package com.ruoyi.seatflow.service;

import java.util.List;
import com.ruoyi.seatflow.domain.dto.MyReservationQuery;
import com.ruoyi.seatflow.domain.dto.ReservationCreateRequest;
import com.ruoyi.seatflow.domain.dto.SeatAvailabilityQuery;
import com.ruoyi.seatflow.domain.vo.ReservationSeatVo;
import com.ruoyi.seatflow.domain.vo.ReservationSpaceVo;
import com.ruoyi.seatflow.domain.vo.ReservationVo;

public interface ISeatFlowReservationService
{
    List<ReservationSpaceVo> selectCampuses();
    List<ReservationSpaceVo> selectBuildings(Long campusId);
    List<ReservationSpaceVo> selectFloors(Long buildingId);
    List<ReservationSpaceVo> selectRooms(Long floorId);
    List<ReservationSeatVo> selectSeatAvailability(SeatAvailabilityQuery query);
    int createReservation(Long userId, String username, ReservationCreateRequest request);
    List<ReservationVo> selectMyReservations(MyReservationQuery query);
    int cancelReservation(Long reservationId, Long userId, String username);
}
