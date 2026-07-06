package com.ruoyi.seatflow.service;

import com.ruoyi.seatflow.domain.BlacklistEntry;
import com.ruoyi.seatflow.domain.ControlReservation;
import com.ruoyi.seatflow.domain.ViolationRecord;
import com.ruoyi.seatflow.domain.dto.ControlQuery;
import com.ruoyi.seatflow.domain.vo.ControlProfileVo;
import java.util.List;

public interface ISeatFlowControlService {
  void checkin(Long reservationId, Long userId);

  void complete(Long reservationId, Long userId);

  List<ControlReservation> selectAvailableCheckins(Long userId);

  ControlProfileVo selectProfile(Long userId);

  List<ViolationRecord> selectMyViolations(Long userId);

  List<ViolationRecord> selectViolations(ControlQuery query);

  List<BlacklistEntry> selectBlacklist(ControlQuery query);

  void releaseBlacklist(Long blacklistId);

  int releaseExpiredBatch(int batchSize);

  int completeExpiredBatch(int batchSize);
}
