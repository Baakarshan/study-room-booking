package com.ruoyi.seatflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.seatflow.common.SeatFlowReservationStatus;
import com.ruoyi.seatflow.domain.dto.ReservationCreateRequest;
import com.ruoyi.seatflow.domain.dto.ReservationManageQuery;
import com.ruoyi.seatflow.domain.vo.LockedSeatVo;
import com.ruoyi.seatflow.domain.vo.ReservationVo;
import com.ruoyi.seatflow.mapper.SeatFlowReservationMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeatFlowReservationServiceImplTest {
  @Mock private SeatFlowReservationMapper mapper;

  private SeatFlowReservationServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new SeatFlowReservationServiceImpl(mapper);
  }

  @Test
  void createReservationWritesFifteenMinuteDeadline() {
    ReservationCreateRequest request = validRequest();
    when(mapper.selectSeatForUpdate(10L)).thenReturn(enabledSeat());
    when(mapper.selectUserForUpdate(20L)).thenReturn(20L);
    when(mapper.insertReservation(eq(20L), eq(request), any(Date.class), eq("student01")))
        .thenReturn(1);

    assertEquals(1, service.createReservation(20L, "student01", request));

    ArgumentCaptor<Date> deadline = ArgumentCaptor.forClass(Date.class);
    verify(mapper).insertReservation(eq(20L), eq(request), deadline.capture(), eq("student01"));
    assertEquals(request.getStartTime().getTime() + 15 * 60_000L, deadline.getValue().getTime());
  }

  @Test
  void createReservationRejectsTimeOutsideOpeningHours() {
    ReservationCreateRequest request = requestAt(LocalDate.now().plusDays(1), 7, 0, 8, 0);
    when(mapper.selectSeatForUpdate(10L)).thenReturn(enabledSeat());

    ServiceException exception =
        assertThrows(
            ServiceException.class,
            () -> service.createReservation(20L, "student01", request));

    assertEquals("预约时间必须在自习室开放时间内", exception.getMessage());
    verify(mapper, never()).selectUserForUpdate(any());
  }

  @Test
  void createReservationRejectsPastStartTime() {
    ReservationCreateRequest request =
        requestAt(LocalDate.now().minusDays(1), 10, 0, 11, 0);

    ServiceException exception =
        assertThrows(
            ServiceException.class,
            () -> service.createReservation(20L, "student01", request));

    assertEquals("预约开始时间必须晚于当前时间", exception.getMessage());
    verify(mapper, never()).selectSeatForUpdate(any());
  }

  @Test
  void createReservationRejectsBlacklistedStudent() {
    ReservationCreateRequest request = validRequest();
    stubSeatAndUser(request);
    when(mapper.countBlacklist(20L)).thenReturn(1);

    ServiceException exception =
        assertThrows(
            ServiceException.class,
            () -> service.createReservation(20L, "student01", request));

    assertEquals("当前用户已进入黑名单，无法预约", exception.getMessage());
    verify(mapper, never()).insertReservation(any(), any(), any(), any());
  }

  @Test
  void createReservationRejectsDailyLimit() {
    ReservationCreateRequest request = validRequest();
    stubSeatAndUser(request);
    when(mapper.countDailyReservations(20L, request.getStartTime())).thenReturn(1);

    ServiceException exception =
        assertThrows(
            ServiceException.class,
            () -> service.createReservation(20L, "student01", request));

    assertEquals("每天最多预约1次", exception.getMessage());
    verify(mapper, never()).insertReservation(any(), any(), any(), any());
  }

  @Test
  void createReservationRejectsUserConflict() {
    ReservationCreateRequest request = validRequest();
    stubSeatAndUser(request);
    when(mapper.countUserConflicts(20L, request.getStartTime(), request.getEndTime()))
        .thenReturn(1);

    ServiceException exception =
        assertThrows(
            ServiceException.class,
            () -> service.createReservation(20L, "student01", request));

    assertEquals("您在所选时间已有有效预约", exception.getMessage());
    verify(mapper, never()).insertReservation(any(), any(), any(), any());
  }

  @Test
  void createReservationRejectsSeatConflict() {
    ReservationCreateRequest request = validRequest();
    stubSeatAndUser(request);
    when(mapper.countSeatConflicts(10L, request.getStartTime(), request.getEndTime()))
        .thenReturn(1);

    ServiceException exception =
        assertThrows(
            ServiceException.class,
            () -> service.createReservation(20L, "student01", request));

    assertEquals("该座位在所选时间已被预约", exception.getMessage());
    verify(mapper, never()).insertReservation(any(), any(), any(), any());
  }

  @Test
  void cancelReservationUpdatesPendingFutureReservation() {
    ReservationVo reservation = new ReservationVo();
    reservation.setReservationId(30L);
    reservation.setStatus(SeatFlowReservationStatus.PENDING_CHECKIN);
    reservation.setStartTime(toDate(LocalDateTime.now().plusHours(2)));
    when(mapper.selectOwnedReservationForUpdate(30L, 20L)).thenReturn(reservation);
    when(mapper.cancelReservation(eq(30L), eq(20L), any(Date.class), eq("student01")))
        .thenReturn(1);

    assertEquals(1, service.cancelReservation(30L, 20L, "student01"));

    verify(mapper).cancelReservation(eq(30L), eq(20L), any(Date.class), eq("student01"));
  }

  @Test
  void cancelReservationRejectsStartedReservation() {
    ReservationVo reservation = new ReservationVo();
    reservation.setReservationId(30L);
    reservation.setStatus(SeatFlowReservationStatus.PENDING_CHECKIN);
    reservation.setStartTime(toDate(LocalDateTime.now().minusMinutes(1)));
    when(mapper.selectOwnedReservationForUpdate(30L, 20L)).thenReturn(reservation);

    ServiceException exception =
        assertThrows(
            ServiceException.class,
            () -> service.cancelReservation(30L, 20L, "student01"));

    assertEquals("预约开始后不能取消", exception.getMessage());
    verify(mapper, never()).cancelReservation(any(), any(), any(), any());
  }

  @Test
  void selectManagedReservationsRejectsInvalidStatus() {
    ReservationManageQuery query = new ReservationManageQuery();
    query.setStatus("unknown");

    ServiceException exception =
        assertThrows(ServiceException.class, () -> service.selectManagedReservations(query));

    assertEquals("预约状态不合法", exception.getMessage());
    verify(mapper, never()).selectManagedReservations(any());
  }

  @Test
  void selectManagedReservationsRejectsReversedTimeRange() {
    ReservationManageQuery query = new ReservationManageQuery();
    query.setBeginTime(toDate(LocalDateTime.now().plusHours(1)));
    query.setEndTime(toDate(LocalDateTime.now()));

    ServiceException exception =
        assertThrows(ServiceException.class, () -> service.selectManagedReservations(query));

    assertEquals("开始时间不能晚于结束时间", exception.getMessage());
    verify(mapper, never()).selectManagedReservations(any());
  }

  private void stubSeatAndUser(ReservationCreateRequest request) {
    when(mapper.selectSeatForUpdate(request.getSeatId())).thenReturn(enabledSeat());
    when(mapper.selectUserForUpdate(20L)).thenReturn(20L);
  }

  private ReservationCreateRequest validRequest() {
    return requestAt(LocalDate.now().plusDays(1), 10, 0, 11, 0);
  }

  private ReservationCreateRequest requestAt(
      LocalDate date, int startHour, int startMinute, int endHour, int endMinute) {
    ReservationCreateRequest request = new ReservationCreateRequest();
    request.setRoomId(1L);
    request.setSeatId(10L);
    request.setStartTime(toDate(LocalDateTime.of(date, LocalTime.of(startHour, startMinute))));
    request.setEndTime(toDate(LocalDateTime.of(date, LocalTime.of(endHour, endMinute))));
    return request;
  }

  private LockedSeatVo enabledSeat() {
    LockedSeatVo seat = new LockedSeatVo();
    seat.setSeatId(10L);
    seat.setRoomId(1L);
    seat.setSeatStatus("enabled");
    seat.setRoomStatus("enabled");
    seat.setOpenTime("08:00:00");
    seat.setCloseTime("22:00:00");
    return seat;
  }

  private Date toDate(LocalDateTime value) {
    return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
  }
}
