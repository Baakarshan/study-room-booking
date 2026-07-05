package com.ruoyi.seatflow.service.impl;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.seatflow.common.SeatFlowReservationStatus;
import com.ruoyi.seatflow.domain.dto.*;
import com.ruoyi.seatflow.domain.vo.*;
import com.ruoyi.seatflow.mapper.SeatFlowReservationMapper;
import com.ruoyi.seatflow.service.ISeatFlowReservationService;

@Service
public class SeatFlowReservationServiceImpl implements ISeatFlowReservationService
{
    private static final int DAILY_LIMIT = 1;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Autowired private SeatFlowReservationMapper reservationMapper;

    @Override public List<ReservationSpaceVo> selectCampuses() { return reservationMapper.selectCampuses(); }
    @Override public List<ReservationSpaceVo> selectBuildings(Long id) { return reservationMapper.selectBuildings(id); }
    @Override public List<ReservationSpaceVo> selectFloors(Long id) { return reservationMapper.selectFloors(id); }
    @Override public List<ReservationSpaceVo> selectRooms(Long id) { return reservationMapper.selectRooms(id); }

    @Override
    public List<ReservationSeatVo> selectSeatAvailability(SeatAvailabilityQuery query)
    {
        validateRange(query.getStartTime(), query.getEndTime());
        return reservationMapper.selectSeatAvailability(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createReservation(Long userId, String username, ReservationCreateRequest request)
    {
        validateRange(request.getStartTime(), request.getEndTime());
        if (!request.getStartTime().after(new Date())) throw new ServiceException("预约开始时间必须晚于当前时间");

        LockedSeatVo seat = reservationMapper.selectSeatForUpdate(request.getSeatId());
        if (seat == null || !request.getRoomId().equals(seat.getRoomId())) throw new ServiceException("座位不存在或不属于所选自习室");
        if (!"enabled".equals(seat.getSeatStatus())) throw new ServiceException("该座位已停用");
        if (!"enabled".equals(seat.getRoomStatus())) throw new ServiceException("该自习室已停用");
        validateOpeningHours(request.getStartTime(), request.getEndTime(), seat);

        // 用户行锁保证同一用户并发选择不同座位时，当日限次和重叠校验仍串行执行。
        if (reservationMapper.selectUserForUpdate(userId) == null) throw new ServiceException("当前用户不存在");
        if (reservationMapper.countBlacklist(userId) > 0) throw new ServiceException("当前用户已进入黑名单，无法预约");
        if (reservationMapper.countDailyReservations(userId, request.getStartTime()) >= DAILY_LIMIT) throw new ServiceException("每天最多预约1次");
        if (reservationMapper.countUserConflicts(userId, request.getStartTime(), request.getEndTime()) > 0) throw new ServiceException("您在所选时间已有有效预约");
        if (reservationMapper.countSeatConflicts(request.getSeatId(), request.getStartTime(), request.getEndTime()) > 0) throw new ServiceException("该座位在所选时间已被预约");
        Date deadline = Date.from(request.getStartTime().toInstant().plusSeconds(15 * 60L));
        return reservationMapper.insertReservation(userId, request, deadline, username);
    }

    @Override public List<ReservationVo> selectMyReservations(MyReservationQuery query) { return reservationMapper.selectMyReservations(query); }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelReservation(Long reservationId, Long userId, String username)
    {
        ReservationVo value = reservationMapper.selectOwnedReservationForUpdate(reservationId, userId);
        if (value == null) throw new ServiceException("预约不存在");
        if (!SeatFlowReservationStatus.PENDING_CHECKIN.equals(value.getStatus())) throw new ServiceException("当前预约状态不允许取消");
        Date now = new Date();
        if (!now.before(value.getStartTime())) throw new ServiceException("预约开始后不能取消");
        int rows = reservationMapper.cancelReservation(reservationId, userId, now, username);
        if (rows != 1) throw new ServiceException("预约状态已变化，请刷新后重试");
        return rows;
    }

    private void validateRange(Date start, Date end)
    {
        if (start == null || end == null || !start.before(end)) throw new ServiceException("开始时间必须早于结束时间");
    }

    private void validateOpeningHours(Date start, Date end, LockedSeatVo seat)
    {
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime startValue = LocalDateTime.ofInstant(start.toInstant(), zone);
        LocalDateTime endValue = LocalDateTime.ofInstant(end.toInstant(), zone);
        if (!startValue.toLocalDate().equals(endValue.toLocalDate())) throw new ServiceException("预约必须在同一自然日内完成");
        LocalDate date = startValue.toLocalDate();
        LocalDateTime open = LocalDateTime.of(date, LocalTime.parse(seat.getOpenTime(), TIME_FORMAT));
        LocalDateTime close = LocalDateTime.of(date, LocalTime.parse(seat.getCloseTime(), TIME_FORMAT));
        if (startValue.isBefore(open) || endValue.isAfter(close)) throw new ServiceException("预约时间必须在自习室开放时间内");
    }
}
