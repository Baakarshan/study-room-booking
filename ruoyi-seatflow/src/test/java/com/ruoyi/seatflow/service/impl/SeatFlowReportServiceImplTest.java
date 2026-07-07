package com.ruoyi.seatflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.seatflow.domain.dto.ReportQuery;
import com.ruoyi.seatflow.mapper.SeatFlowReportMapper;

@ExtendWith(MockitoExtension.class)
class SeatFlowReportServiceImplTest
{
    @Mock
    private SeatFlowReportMapper mapper;

    private SeatFlowReportServiceImpl service;

    @BeforeEach
    void setUp()
    {
        service = new SeatFlowReportServiceImpl();
        ReflectionTestUtils.setField(service, "reportMapper", mapper);
    }

    @Test
    void selectPopularSlotsAcceptsHalfHourSlotType()
    {
        ReportQuery query = validQuery();
        query.setSlotType(SeatFlowReportServiceImpl.SLOT_TYPE_HALF_HOUR);
        when(mapper.selectPopularSlots(query)).thenReturn(Collections.emptyList());

        assertEquals(Collections.emptyList(), service.selectPopularSlots(query));

        verify(mapper).selectPopularSlots(query);
        assertEquals(SeatFlowReportServiceImpl.SLOT_TYPE_HALF_HOUR, query.getSlotType());
    }

    @Test
    void selectPopularSlotsRejectsUnknownSlotType()
    {
        ReportQuery query = validQuery();
        query.setSlotType("day");

        assertThrows(ServiceException.class, () -> service.selectPopularSlots(query));
    }

    private ReportQuery validQuery()
    {
        ReportQuery query = new ReportQuery();
        query.setBeginTime(toDate(LocalDateTime.of(2026, 7, 1, 0, 0)));
        query.setEndTime(toDate(LocalDateTime.of(2026, 7, 7, 23, 59, 59)));
        return query;
    }

    private Date toDate(LocalDateTime value)
    {
        return Date.from(value.atZone(ZoneId.systemDefault()).toInstant());
    }
}
