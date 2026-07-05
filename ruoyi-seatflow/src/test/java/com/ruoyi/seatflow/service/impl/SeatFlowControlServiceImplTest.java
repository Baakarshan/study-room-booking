package com.ruoyi.seatflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.seatflow.common.SeatFlowReservationStatus;
import com.ruoyi.seatflow.domain.ControlReservation;
import com.ruoyi.seatflow.mapper.SeatFlowControlMapper;

@ExtendWith(MockitoExtension.class)
class SeatFlowControlServiceImplTest
{
    private static final Date NOW = new Date(1_800_000_000_000L);

    @Mock
    private SeatFlowControlMapper mapper;

    private SeatFlowControlServiceImpl service;

    @BeforeEach
    void setUp()
    {
        service = new SeatFlowControlServiceImpl(mapper)
        {
            @Override
            protected Date currentTime()
            {
                return NOW;
            }
        };
    }

    @Test
    void checkinUpdatesReservationAndWritesRecord()
    {
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
    void checkinRejectsAnotherUsersReservation()
    {
        when(mapper.selectReservationForUpdate(10L)).thenReturn(pendingReservation(10L, 99L));

        assertThrows(ServiceException.class, () -> service.checkin(10L, 20L));

        verify(mapper, never()).markCheckedIn(any(), any());
    }

    @Test
    void thirdViolationCreatesPermanentBlacklist()
    {
        ControlReservation reservation = pendingReservation(10L, 20L);
        when(mapper.selectExpiredForUpdate(NOW, 200)).thenReturn(Collections.singletonList(reservation));
        when(mapper.markNoShow(10L, NOW)).thenReturn(1);
        when(mapper.insertViolation(eq(10L), eq(20L), any(), eq(NOW))).thenReturn(1);
        when(mapper.incrementViolationCount(20L)).thenReturn(1);
        when(mapper.selectViolationCountForUpdate(20L)).thenReturn(3);
        when(mapper.selectViolationIdByReservation(10L)).thenReturn(30L);

        service.releaseExpiredBatch(200);

        verify(mapper).markProfileBlacklisted(20L);
        verify(mapper).insertBlacklist(eq(20L), eq(30L), any(), eq(NOW));
    }

    @Test
    void lostConditionalUpdateDoesNotDuplicateViolation()
    {
        ControlReservation reservation = pendingReservation(10L, 20L);
        when(mapper.selectExpiredForUpdate(NOW, 200)).thenReturn(Collections.singletonList(reservation));
        when(mapper.markNoShow(10L, NOW)).thenReturn(0);

        service.releaseExpiredBatch(200);

        verify(mapper, never()).insertViolation(any(), any(), any(), any());
        verify(mapper, never()).incrementViolationCount(any());
    }

    private ControlReservation pendingReservation(Long reservationId, Long userId)
    {
        ControlReservation reservation = new ControlReservation();
        reservation.setReservationId(reservationId);
        reservation.setUserId(userId);
        reservation.setStatus(SeatFlowReservationStatus.PENDING_CHECKIN);
        return reservation;
    }
}
