package com.ruoyi.seatflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.test.util.ReflectionTestUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.seatflow.domain.dto.ReservationCreateRequest;
import com.ruoyi.seatflow.domain.vo.LockedSeatVo;
import com.ruoyi.seatflow.mapper.SeatFlowReservationMapper;

@ExtendWith(MockitoExtension.class)
class SeatFlowReservationServiceImplTest
{
    @Mock
    private SeatFlowReservationMapper mapper;

    private SeatFlowReservationServiceImpl service;

    @BeforeEach
    void setUp()
    {
        service = new SeatFlowReservationServiceImpl();
        ReflectionTestUtils.setField(service, "reservationMapper", mapper);
    }

    @Test
    void createReservationWritesFifteenMinuteDeadline()
    {
        ReservationCreateRequest request = validRequest();
        when(mapper.selectSeatForUpdate(10L)).thenReturn(enabledSeat());
        when(mapper.selectUserForUpdate(20L)).thenReturn(20L);
        when(mapper.insertReservation(eq(20L), eq(request), any(Date.class), eq("student01"))).thenReturn(1);

        assertEquals(1, service.createReservation(20L, "student01", request));

        ArgumentCaptor<Date> deadline = ArgumentCaptor.forClass(Date.class);
        verify(mapper).insertReservation(eq(20L), eq(request), deadline.capture(), eq("student01"));
        assertEquals(request.getStartTime().getTime() + 15 * 60_000L, deadline.getValue().getTime());
    }

    @Test
    void createReservationRejectsBlacklistedStudent()
    {
        ReservationCreateRequest request = validRequest();
        when(mapper.selectSeatForUpdate(10L)).thenReturn(enabledSeat());
        when(mapper.selectUserForUpdate(20L)).thenReturn(20L);
        when(mapper.countBlacklist(20L)).thenReturn(1);

        assertThrows(ServiceException.class, () -> service.createReservation(20L, "student01", request));

        verify(mapper, never()).insertReservation(any(), any(), any(), any());
    }

    @Test
    void createReservationRejectsSeatConflict()
    {
        ReservationCreateRequest request = validRequest();
        when(mapper.selectSeatForUpdate(10L)).thenReturn(enabledSeat());
        when(mapper.selectUserForUpdate(20L)).thenReturn(20L);
        when(mapper.countSeatConflicts(10L, request.getStartTime(), request.getEndTime())).thenReturn(1);

        assertThrows(ServiceException.class, () -> service.createReservation(20L, "student01", request));

        verify(mapper, never()).insertReservation(any(), any(), any(), any());
    }

    private ReservationCreateRequest validRequest()
    {
        LocalDate date = LocalDate.now().plusDays(1);
        ReservationCreateRequest request = new ReservationCreateRequest();
        request.setRoomId(1L);
        request.setSeatId(10L);
        request.setStartTime(toDate(LocalDateTime.of(date, LocalTime.of(10, 0))));
        request.setEndTime(toDate(LocalDateTime.of(date, LocalTime.of(11, 0))));
        return request;
    }

    private LockedSeatVo enabledSeat()
    {
        LockedSeatVo seat = new LockedSeatVo();
        seat.setSeatId(10L);
        seat.setRoomId(1L);
        seat.setSeatStatus("enabled");
        seat.setRoomStatus("enabled");
        seat.setOpenTime("08:00:00");
        seat.setCloseTime("22:00:00");
        return seat;
    }

    private Date toDate(LocalDateTime value)
    {
        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }
}
