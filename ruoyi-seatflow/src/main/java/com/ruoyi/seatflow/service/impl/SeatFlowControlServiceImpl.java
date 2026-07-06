package com.ruoyi.seatflow.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.seatflow.common.SeatFlowReservationStatus;
import com.ruoyi.seatflow.domain.BlacklistEntry;
import com.ruoyi.seatflow.domain.ControlReservation;
import com.ruoyi.seatflow.domain.ViolationRecord;
import com.ruoyi.seatflow.domain.dto.ControlQuery;
import com.ruoyi.seatflow.domain.vo.ControlProfileVo;
import com.ruoyi.seatflow.mapper.SeatFlowControlMapper;
import com.ruoyi.seatflow.service.ISeatFlowControlService;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatFlowControlServiceImpl implements ISeatFlowControlService {
  static final String RECORD_ACTIVE = "active";
  static final String NO_SHOW_REASON = "预约开始后15分钟内未签到";
  static final String BLACKLIST_REASON = "累计爽约满3次";
  private static final int BLACKLIST_THRESHOLD = 3;
  private static final int MAX_BATCH_SIZE = 500;

  private final SeatFlowControlMapper controlMapper;

  public SeatFlowControlServiceImpl(SeatFlowControlMapper controlMapper) {
    this.controlMapper = controlMapper;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void checkin(Long reservationId, Long userId) {
    if (reservationId == null || userId == null) {
      throw new ServiceException("预约ID和用户ID不能为空");
    }
    ControlReservation reservation = controlMapper.selectReservationForUpdate(reservationId);
    if (reservation == null) {
      throw new ServiceException("预约不存在");
    }
    if (!userId.equals(reservation.getUserId())) {
      throw new ServiceException("只能签到本人的预约");
    }
    if (!SeatFlowReservationStatus.PENDING_CHECKIN.equals(reservation.getStatus())) {
      throw new ServiceException("当前预约状态不允许签到");
    }
    Date now = currentTime();
    if (reservation.getStartTime() == null || now.before(reservation.getStartTime())) {
      throw new ServiceException("预约尚未开始，暂不能签到");
    }
    if (reservation.getCheckDeadline() == null || now.after(reservation.getCheckDeadline())) {
      throw new ServiceException("已超过签到截止时间");
    }
    if (controlMapper.markCheckedIn(reservationId, now) != 1) {
      throw new ServiceException("预约状态已变化，请刷新后重试");
    }
    if (controlMapper.insertCheckin(reservationId, userId, now) != 1) {
      throw new ServiceException("签到记录写入失败");
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void complete(Long reservationId, Long userId) {
    requireIds(reservationId, userId);
    ControlReservation reservation = controlMapper.selectReservationForUpdate(reservationId);
    if (reservation == null) {
      throw new ServiceException("预约不存在");
    }
    if (!userId.equals(reservation.getUserId())) {
      throw new ServiceException("只能结束本人的预约");
    }
    if (!SeatFlowReservationStatus.IN_USE.equals(reservation.getStatus())) {
      throw new ServiceException("只有使用中的预约可以提前结束");
    }
    if (controlMapper.markCompleted(reservationId, currentTime()) != 1) {
      throw new ServiceException("预约状态已变化，请刷新后重试");
    }
  }

  @Override
  public List<ControlReservation> selectAvailableCheckins(Long userId) {
    return controlMapper.selectAvailableCheckins(userId, currentTime());
  }

  @Override
  public ControlProfileVo selectProfile(Long userId) {
    if (userId == null) {
      throw new ServiceException("用户ID不能为空");
    }
    ControlProfileVo profile = controlMapper.selectProfile(userId);
    if (profile == null) {
      profile = new ControlProfileVo();
      profile.setViolationCount(0);
      profile.setBlacklisted(false);
    }
    profile.setThreshold(BLACKLIST_THRESHOLD);
    return profile;
  }

  @Override
  public List<ViolationRecord> selectMyViolations(Long userId) {
    return controlMapper.selectViolations(userId, null);
  }

  @Override
  public List<ViolationRecord> selectViolations(ControlQuery query) {
    return controlMapper.selectViolations(null, normalize(query));
  }

  @Override
  public List<BlacklistEntry> selectBlacklist(ControlQuery query) {
    return controlMapper.selectBlacklist(normalize(query));
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void releaseBlacklist(Long blacklistId) {
    if (blacklistId == null) {
      throw new ServiceException("黑名单ID不能为空");
    }
    BlacklistEntry entry = controlMapper.selectBlacklistForUpdate(blacklistId);
    if (entry == null) {
      throw new ServiceException("有效黑名单记录不存在");
    }
    Date now = currentTime();
    if (controlMapper.deactivateBlacklist(blacklistId, now) != 1) {
      throw new ServiceException("黑名单状态已变化，请刷新后重试");
    }
    // 解除只重置学生当前管控状态，历史爽约记录用于实验统计和审计展示。
    if (controlMapper.resetProfileControlState(entry.getUserId()) != 1) {
      throw new ServiceException("学生档案不存在，无法解除黑名单");
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int releaseExpiredBatch(int batchSize) {
    if (batchSize < 1 || batchSize > MAX_BATCH_SIZE) {
      throw new ServiceException("单批处理数量必须在1到500之间");
    }
    Date now = currentTime();
    List<ControlReservation> expired = controlMapper.selectExpiredForUpdate(now, batchSize);
    if (expired == null) {
      expired = Collections.emptyList();
    }
    int handled = 0;
    for (ControlReservation reservation : expired) {
      // 条件更新和唯一索引共同保证 Quartz 重入不会重复记爽约。
      if (controlMapper.markNoShow(reservation.getReservationId(), now) != 1) {
        continue;
      }
      if (controlMapper.insertViolation(
              reservation.getReservationId(), reservation.getUserId(), NO_SHOW_REASON, now)
          != 1) {
        throw new ServiceException("爽约记录写入失败");
      }
      if (controlMapper.incrementViolationCount(reservation.getUserId()) != 1) {
        throw new ServiceException("学生档案不存在，无法累计爽约次数");
      }
      Integer violationCount = controlMapper.selectViolationCountForUpdate(reservation.getUserId());
      if (violationCount != null && violationCount >= BLACKLIST_THRESHOLD) {
        controlMapper.markProfileBlacklisted(reservation.getUserId());
        Long violationId =
            controlMapper.selectViolationIdByReservation(reservation.getReservationId());
        controlMapper.insertBlacklist(reservation.getUserId(), violationId, BLACKLIST_REASON, now);
      }
      handled++;
    }
    return handled;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int completeExpiredBatch(int batchSize) {
    validateBatchSize(batchSize);
    Date now = currentTime();
    List<ControlReservation> ended = controlMapper.selectEndedForUpdate(now, batchSize);
    if (ended == null) {
      ended = Collections.emptyList();
    }
    int handled = 0;
    for (ControlReservation reservation : ended) {
      // 条件更新使并发任务只能有一个实例完成同一条预约。
      if (controlMapper.markAutomaticallyCompleted(reservation.getReservationId(), now) == 1) {
        handled++;
      }
    }
    return handled;
  }

  protected Date currentTime() {
    return new Date();
  }

  private void requireIds(Long reservationId, Long userId) {
    if (reservationId == null || userId == null) {
      throw new ServiceException("预约ID和用户ID不能为空");
    }
  }

  private void validateBatchSize(int batchSize) {
    if (batchSize < 1 || batchSize > MAX_BATCH_SIZE) {
      throw new ServiceException("单批处理数量必须在1到500之间");
    }
  }

  private ControlQuery normalize(ControlQuery query) {
    if (query == null) {
      return new ControlQuery();
    }
    if (query.getUserName() != null) {
      query.setUserName(query.getUserName().trim());
    }
    if (query.getStudentNo() != null) {
      query.setStudentNo(query.getStudentNo().trim());
    }
    return query;
  }
}
