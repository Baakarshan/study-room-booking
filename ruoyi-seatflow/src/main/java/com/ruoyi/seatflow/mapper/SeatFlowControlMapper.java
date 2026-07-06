package com.ruoyi.seatflow.mapper;

import com.ruoyi.seatflow.domain.BlacklistEntry;
import com.ruoyi.seatflow.domain.ControlReservation;
import com.ruoyi.seatflow.domain.ViolationRecord;
import com.ruoyi.seatflow.domain.dto.ControlQuery;
import com.ruoyi.seatflow.domain.vo.ControlProfileVo;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SeatFlowControlMapper {
  ControlReservation selectReservationForUpdate(Long reservationId);

  List<ControlReservation> selectAvailableCheckins(
      @Param("userId") Long userId, @Param("now") Date now);

  int markCheckedIn(@Param("reservationId") Long reservationId, @Param("now") Date now);

  int insertCheckin(
      @Param("reservationId") Long reservationId,
      @Param("userId") Long userId,
      @Param("now") Date now);

  int markCompleted(@Param("reservationId") Long reservationId, @Param("now") Date now);

  List<ControlReservation> selectEndedForUpdate(@Param("now") Date now, @Param("limit") int limit);

  int markAutomaticallyCompleted(
      @Param("reservationId") Long reservationId, @Param("now") Date now);

  List<ControlReservation> selectExpiredForUpdate(
      @Param("now") Date now, @Param("limit") int limit);

  int markNoShow(@Param("reservationId") Long reservationId, @Param("now") Date now);

  int insertViolation(
      @Param("reservationId") Long reservationId,
      @Param("userId") Long userId,
      @Param("reason") String reason,
      @Param("now") Date now);

  Long selectViolationIdByReservation(Long reservationId);

  int incrementViolationCount(Long userId);

  Integer selectViolationCountForUpdate(Long userId);

  int markProfileBlacklisted(Long userId);

  int insertBlacklist(
      @Param("userId") Long userId,
      @Param("violationId") Long violationId,
      @Param("reason") String reason,
      @Param("now") Date now);

  List<ViolationRecord> selectViolations(
      @Param("userId") Long userId, @Param("query") ControlQuery query);

  List<BlacklistEntry> selectBlacklist(ControlQuery query);

  BlacklistEntry selectBlacklistForUpdate(Long blacklistId);

  int deactivateBlacklist(@Param("blacklistId") Long blacklistId, @Param("now") Date now);

  int resetProfileControlState(Long userId);

  ControlProfileVo selectProfile(Long userId);
}
