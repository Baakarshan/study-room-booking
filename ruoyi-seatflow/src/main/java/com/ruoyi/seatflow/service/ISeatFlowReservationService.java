package com.ruoyi.seatflow.service;

import com.ruoyi.seatflow.domain.dto.MyReservationQuery;
import com.ruoyi.seatflow.domain.dto.ReservationCreateRequest;
import com.ruoyi.seatflow.domain.dto.ReservationManageQuery;
import com.ruoyi.seatflow.domain.dto.SeatAvailabilityQuery;
import com.ruoyi.seatflow.domain.vo.ReservationManageVo;
import com.ruoyi.seatflow.domain.vo.ReservationSeatVo;
import com.ruoyi.seatflow.domain.vo.ReservationSpaceVo;
import com.ruoyi.seatflow.domain.vo.ReservationStatusCountVo;
import com.ruoyi.seatflow.domain.vo.ReservationVo;
import java.util.List;

public interface ISeatFlowReservationService {
  List<ReservationSpaceVo> selectCampuses();

  List<ReservationSpaceVo> selectBuildings(Long campusId);

  List<ReservationSpaceVo> selectFloors(Long buildingId);

  List<ReservationSpaceVo> selectRooms(Long floorId);

  List<ReservationSeatVo> selectSeatAvailability(SeatAvailabilityQuery query);

  int createReservation(Long userId, String username, ReservationCreateRequest request);

  List<ReservationVo> selectMyReservations(MyReservationQuery query);

  List<ReservationStatusCountVo> selectMyReservationStatusCounts(Long userId);

  List<ReservationManageVo> selectManagedReservations(ReservationManageQuery query);

  List<ReservationStatusCountVo> selectManagedReservationStatusCounts(ReservationManageQuery query);

  int cancelReservation(Long reservationId, Long userId, String username);
}
