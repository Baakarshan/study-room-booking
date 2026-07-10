package com.ruoyi.seatflow.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.seatflow.common.SeatFlowReservationStatus;
import com.ruoyi.seatflow.domain.dto.MyReservationQuery;
import com.ruoyi.seatflow.domain.dto.ReservationCreateRequest;
import com.ruoyi.seatflow.domain.dto.ReservationManageQuery;
import com.ruoyi.seatflow.domain.dto.SeatAvailabilityQuery;
import com.ruoyi.seatflow.domain.vo.LockedSeatVo;
import com.ruoyi.seatflow.domain.vo.ReservationManageVo;
import com.ruoyi.seatflow.domain.vo.ReservationSeatVo;
import com.ruoyi.seatflow.domain.vo.ReservationSpaceVo;
import com.ruoyi.seatflow.domain.vo.ReservationVo;
import com.ruoyi.seatflow.mapper.SeatFlowReservationMapper;
import com.ruoyi.seatflow.service.ISeatFlowReservationService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatFlowReservationServiceImpl implements ISeatFlowReservationService {
  private static final int DAILY_LIMIT = 1;
  private static final long CHECKIN_GRACE_SECONDS = 15 * 60L;
  private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
  private static final Set<String> RESERVATION_STATUSES =
      Set.of(
          SeatFlowReservationStatus.PENDING_CHECKIN,
          SeatFlowReservationStatus.IN_USE,
          SeatFlowReservationStatus.CANCELLED,
          SeatFlowReservationStatus.NO_SHOW,
          SeatFlowReservationStatus.COMPLETED);

  private final SeatFlowReservationMapper reservationMapper;

  public SeatFlowReservationServiceImpl(SeatFlowReservationMapper reservationMapper) {
    this.reservationMapper = reservationMapper;
  }

  @Override
  public List<ReservationSpaceVo> selectCampuses() {
    return reservationMapper.selectCampuses();
  }

  @Override
  public List<ReservationSpaceVo> selectBuildings(Long campusId) {
    requirePositiveId(campusId, "请选择校区");
    return reservationMapper.selectBuildings(campusId);
  }

  @Override
  public List<ReservationSpaceVo> selectFloors(Long buildingId) {
    requirePositiveId(buildingId, "请选择楼栋");
    return reservationMapper.selectFloors(buildingId);
  }

  @Override
  public List<ReservationSpaceVo> selectRooms(Long floorId) {
    requirePositiveId(floorId, "请选择楼层");
    return reservationMapper.selectRooms(floorId);
  }

  @Override
  public List<ReservationSeatVo> selectSeatAvailability(SeatAvailabilityQuery query) {
    if (query == null) {
      throw new ServiceException("座位查询参数不能为空");
    }
    requirePositiveId(query.getRoomId(), "请选择自习室");
    validateRange(query.getStartTime(), query.getEndTime());
    validateFutureStart(query.getStartTime());
    return reservationMapper.selectSeatAvailability(query);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int createReservation(Long userId, String username, ReservationCreateRequest request) {
    requirePositiveId(userId, "当前用户不存在");
    if (request == null) {
      throw new ServiceException("预约参数不能为空");
    }
    requirePositiveId(request.getRoomId(), "请选择自习室");
    requirePositiveId(request.getSeatId(), "请选择座位");
    validateRange(request.getStartTime(), request.getEndTime());
    validateFutureStart(request.getStartTime());

    LockedSeatVo seat = reservationMapper.selectSeatForUpdate(request.getSeatId());
    validateSeat(request, seat);
    validateOpeningHours(request.getStartTime(), request.getEndTime(), seat);

    // 锁定用户行，使同一学生并发选择不同座位时，每日限次与时间冲突校验仍串行执行。
    if (reservationMapper.selectUserForUpdate(userId) == null) {
      throw new ServiceException("当前用户不存在");
    }
    if (reservationMapper.countBlacklist(userId) > 0) {
      throw new ServiceException("当前用户已进入黑名单，无法预约");
    }
    if (reservationMapper.countDailyReservations(userId, request.getStartTime()) >= DAILY_LIMIT) {
      throw new ServiceException("每天最多预约1次");
    }
    if (reservationMapper.countUserConflicts(userId, request.getStartTime(), request.getEndTime())
        > 0) {
      throw new ServiceException("您在所选时间已有有效预约");
    }
    if (reservationMapper.countSeatConflicts(
            request.getSeatId(), request.getStartTime(), request.getEndTime())
        > 0) {
      throw new ServiceException("该座位在所选时间已被预约");
    }

    Date checkDeadline =
        Date.from(request.getStartTime().toInstant().plusSeconds(CHECKIN_GRACE_SECONDS));
    return reservationMapper.insertReservation(userId, request, checkDeadline, username);
  }

  @Override
  public List<ReservationVo> selectMyReservations(MyReservationQuery query) {
    if (query == null || query.getUserId() == null) {
      throw new ServiceException("当前用户不存在");
    }
    validateStatus(query.getStatus());
    return reservationMapper.selectMyReservations(query);
  }

  @Override
  public List<ReservationManageVo> selectManagedReservations(ReservationManageQuery query) {
    if (query == null) {
      throw new ServiceException("查询参数不能为空");
    }
    validateOptionalId(query.getCampusId());
    validateOptionalId(query.getBuildingId());
    validateOptionalId(query.getFloorId());
    validateOptionalId(query.getRoomId());
    validateStatus(query.getStatus());
    if (query.getBeginTime() != null
        && query.getEndTime() != null
        && query.getBeginTime().after(query.getEndTime())) {
      throw new ServiceException("开始时间不能晚于结束时间");
    }
    return reservationMapper.selectManagedReservations(query);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int cancelReservation(Long reservationId, Long userId, String username) {
    requirePositiveId(reservationId, "请选择预约记录");
    requirePositiveId(userId, "当前用户不存在");
    ReservationVo reservation =
        reservationMapper.selectOwnedReservationForUpdate(reservationId, userId);
    if (reservation == null) {
      throw new ServiceException("预约不存在");
    }
    if (!SeatFlowReservationStatus.PENDING_CHECKIN.equals(reservation.getStatus())) {
      throw new ServiceException("当前预约状态不允许取消");
    }

    Date now = new Date();
    if (reservation.getStartTime() == null || !now.before(reservation.getStartTime())) {
      throw new ServiceException("预约开始后不能取消");
    }
    int affectedRows = reservationMapper.cancelReservation(reservationId, userId, now, username);
    if (affectedRows != 1) {
      throw new ServiceException("预约状态已变化，请刷新后重试");
    }
    return affectedRows;
  }

  private void validateSeat(ReservationCreateRequest request, LockedSeatVo seat) {
    if (seat == null || !request.getRoomId().equals(seat.getRoomId())) {
      throw new ServiceException("座位不存在或不属于所选自习室");
    }
    if (!"enabled".equals(seat.getSeatStatus())) {
      throw new ServiceException("该座位已停用");
    }
    if (!"enabled".equals(seat.getRoomStatus())) {
      throw new ServiceException("该自习室已停用");
    }
  }

  private void validateRange(Date startTime, Date endTime) {
    if (startTime == null || endTime == null || !startTime.before(endTime)) {
      throw new ServiceException("开始时间必须早于结束时间");
    }
  }

  private void validateFutureStart(Date startTime) {
    if (!startTime.after(new Date())) {
      throw new ServiceException("预约开始时间必须晚于当前时间");
    }
  }

  private void validateOpeningHours(Date startTime, Date endTime, LockedSeatVo seat) {
    ZoneId zoneId = ZoneId.systemDefault();
    LocalDateTime start = LocalDateTime.ofInstant(startTime.toInstant(), zoneId);
    LocalDateTime end = LocalDateTime.ofInstant(endTime.toInstant(), zoneId);
    if (!start.toLocalDate().equals(end.toLocalDate())) {
      throw new ServiceException("预约必须在同一自然日内完成");
    }

    try {
      LocalDate date = start.toLocalDate();
      LocalDateTime openTime =
          LocalDateTime.of(date, LocalTime.parse(seat.getOpenTime(), TIME_FORMAT));
      LocalDateTime closeTime =
          LocalDateTime.of(date, LocalTime.parse(seat.getCloseTime(), TIME_FORMAT));
      if (start.isBefore(openTime) || end.isAfter(closeTime)) {
        throw new ServiceException("预约时间必须在自习室开放时间内");
      }
    } catch (DateTimeParseException | NullPointerException exception) {
      throw new ServiceException("自习室开放时间配置错误");
    }
  }

  private void validateStatus(String status) {
    if (status != null && !status.isBlank() && !RESERVATION_STATUSES.contains(status)) {
      throw new ServiceException("预约状态不合法");
    }
  }

  private void requirePositiveId(Long id, String message) {
    if (id == null || id <= 0) {
      throw new ServiceException(message);
    }
  }

  private void validateOptionalId(Long id) {
    if (id != null && id <= 0) {
      throw new ServiceException("空间编号必须为正整数");
    }
  }
}
