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
import com.ruoyi.seatflow.domain.BlacklistEntry;
import com.ruoyi.seatflow.domain.ControlReservation;
import com.ruoyi.seatflow.mapper.SeatFlowControlMapper;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeatFlowControlServiceImplTest {
  private static final Date NOW = new Date(1_800_000_000_000L);

  @Mock private SeatFlowControlMapper mapper;

  private SeatFlowControlServiceImpl service;

  @BeforeEach
  void setUp() {
    service =
        new SeatFlowControlServiceImpl(mapper) {
          @Override
          protected Date currentTime() {
            return NOW;
          }
        };
  }

  @Test
  void checkinUpdatesReservationAndWritesRecord() {
    ControlReservation reservation = pendingReservation(10L, 20L);
    reservation.setStartTime(new Date(NOW.getTime() - 60_000));
    reservation.setCheckDeadline(new Date(NOW.getTime() + 60_000));
    when(mapper.selectReservationForUpdate(10L)).thenReturn(reservation);
    when(mapper.markCheckedIn(10L, NOW)).thenReturn(1);
    when(mapper.insertCheckin(10L, 20L, NOW)).thenReturn(1);

    service.checkin(10L, 20L);

    verify(mapper).markCheckedIn(10L, NOW);
    verify(mapper).insertCheckin(10L, 20L, NOW);
  }

  @Test
  void checkinRejectsAnotherUsersReservation() {
    when(mapper.selectReservationForUpdate(10L)).thenReturn(pendingReservation(10L, 99L));

    assertThrows(ServiceException.class, () -> service.checkin(10L, 20L));

    verify(mapper, never()).markCheckedIn(any(), any());
  }

  @Test
  void completeUpdatesOnlyCurrentUsersInUseReservation() {
    ControlReservation reservation = reservation(10L, 20L, SeatFlowReservationStatus.IN_USE);
    when(mapper.selectReservationForUpdate(10L)).thenReturn(reservation);
    when(mapper.markCompleted(10L, NOW)).thenReturn(1);

    service.complete(10L, 20L);

    verify(mapper).markCompleted(10L, NOW);
  }

  @Test
  void completeRejectsAnotherUsersReservation() {
    ControlReservation reservation = reservation(10L, 99L, SeatFlowReservationStatus.IN_USE);
    when(mapper.selectReservationForUpdate(10L)).thenReturn(reservation);

    assertThrows(ServiceException.class, () -> service.complete(10L, 20L));

    verify(mapper, never()).markCompleted(any(), any());
  }

  @Test
  void completeRejectsIllegalStatus() {
    when(mapper.selectReservationForUpdate(10L)).thenReturn(pendingReservation(10L, 20L));

    assertThrows(ServiceException.class, () -> service.complete(10L, 20L));

    verify(mapper, never()).markCompleted(any(), any());
  }

  @Test
  void thirdViolationCreatesPermanentBlacklist() {
    List<ControlReservation> reservations =
        List.of(
            pendingReservation(8L, 20L), pendingReservation(9L, 20L), pendingReservation(10L, 20L));
    when(mapper.selectExpiredForUpdate(NOW, 200)).thenReturn(reservations);
    when(mapper.markNoShow(8L, NOW)).thenReturn(1);
    when(mapper.markNoShow(9L, NOW)).thenReturn(1);
    when(mapper.markNoShow(10L, NOW)).thenReturn(1);
    when(mapper.insertViolation(any(), eq(20L), any(), eq(NOW))).thenReturn(1);
    when(mapper.incrementViolationCount(20L)).thenReturn(1);
    when(mapper.selectViolationCountForUpdate(20L)).thenReturn(1, 2, 3);
    when(mapper.selectViolationIdByReservation(10L)).thenReturn(30L);

    int handled = service.releaseExpiredBatch(200);

    assertEquals(3, handled);
    verify(mapper).markProfileBlacklisted(20L);
    verify(mapper).insertBlacklist(eq(20L), eq(30L), any(), eq(NOW));
  }

  @Test
  void lostConditionalUpdateDoesNotDuplicateViolation() {
    ControlReservation reservation = pendingReservation(10L, 20L);
    when(mapper.selectExpiredForUpdate(NOW, 200))
        .thenReturn(Collections.singletonList(reservation));
    when(mapper.markNoShow(10L, NOW)).thenReturn(0);

    service.releaseExpiredBatch(200);

    verify(mapper, never()).insertViolation(any(), any(), any(), any());
    verify(mapper, never()).incrementViolationCount(any());
  }

  @Test
  void endedInUseReservationIsAutomaticallyCompleted() {
    ControlReservation reservation = reservation(10L, 20L, SeatFlowReservationStatus.IN_USE);
    when(mapper.selectEndedForUpdate(NOW, 200)).thenReturn(Collections.singletonList(reservation));
    when(mapper.markAutomaticallyCompleted(10L, NOW)).thenReturn(1);

    int handled = service.completeExpiredBatch(200);

    assertEquals(1, handled);
    verify(mapper).markAutomaticallyCompleted(10L, NOW);
  }

  @Test
  void releaseBlacklistDeactivatesEntryAndResetsProfile() {
    BlacklistEntry entry = new BlacklistEntry();
    entry.setBlacklistId(30L);
    entry.setUserId(20L);
    when(mapper.selectBlacklistForUpdate(30L)).thenReturn(entry);
    when(mapper.deactivateBlacklist(30L, NOW)).thenReturn(1);
    when(mapper.resetProfileControlState(20L)).thenReturn(1);

    service.releaseBlacklist(30L);

    verify(mapper).deactivateBlacklist(30L, NOW);
    verify(mapper).resetProfileControlState(20L);
    verify(mapper, never()).insertViolation(any(), any(), any(), any());
  }

  @Test
  void releaseBlacklistRejectsInactiveOrMissingEntry() {
    when(mapper.selectBlacklistForUpdate(30L)).thenReturn(null);

    assertThrows(ServiceException.class, () -> service.releaseBlacklist(30L));

    verify(mapper, never()).deactivateBlacklist(any(), any());
    verify(mapper, never()).resetProfileControlState(any());
  }

  private ControlReservation pendingReservation(Long reservationId, Long userId) {
    return reservation(reservationId, userId, SeatFlowReservationStatus.PENDING_CHECKIN);
  }

  private ControlReservation reservation(Long reservationId, Long userId, String status) {
    ControlReservation reservation = new ControlReservation();
    reservation.setReservationId(reservationId);
    reservation.setUserId(userId);
    reservation.setStatus(status);
    return reservation;
  }
}
