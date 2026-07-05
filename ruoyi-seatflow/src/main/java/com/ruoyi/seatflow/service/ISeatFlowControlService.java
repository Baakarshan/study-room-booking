package com.ruoyi.seatflow.service;

import java.util.List;
import com.ruoyi.seatflow.domain.BlacklistEntry;
import com.ruoyi.seatflow.domain.ControlReservation;
import com.ruoyi.seatflow.domain.ViolationRecord;
import com.ruoyi.seatflow.domain.dto.ControlQuery;

public interface ISeatFlowControlService
{
    void checkin(Long reservationId, Long userId);
    List<ControlReservation> selectAvailableCheckins(Long userId);
    List<ViolationRecord> selectMyViolations(Long userId);
    List<ViolationRecord> selectViolations(ControlQuery query);
    List<BlacklistEntry> selectBlacklist(ControlQuery query);
    int releaseExpiredBatch(int batchSize);
}
