package com.ruoyi.seatflow.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.seatflow.domain.dto.MyReservationQuery;
import com.ruoyi.seatflow.domain.dto.ReservationCreateRequest;
import com.ruoyi.seatflow.domain.dto.SeatAvailabilityQuery;
import com.ruoyi.seatflow.domain.vo.LockedSeatVo;
import com.ruoyi.seatflow.domain.vo.ReservationSeatVo;
import com.ruoyi.seatflow.domain.vo.ReservationSpaceVo;
import com.ruoyi.seatflow.domain.vo.ReservationVo;

public interface SeatFlowReservationMapper
{
    List<ReservationSpaceVo> selectCampuses();
    List<ReservationSpaceVo> selectBuildings(@Param("campusId") Long campusId);
    List<ReservationSpaceVo> selectFloors(@Param("buildingId") Long buildingId);
    List<ReservationSpaceVo> selectRooms(@Param("floorId") Long floorId);
    List<ReservationSeatVo> selectSeatAvailability(SeatAvailabilityQuery query);
    LockedSeatVo selectSeatForUpdate(@Param("seatId") Long seatId);
    Long selectUserForUpdate(@Param("userId") Long userId);
    int countBlacklist(@Param("userId") Long userId);
    int countDailyReservations(@Param("userId") Long userId, @Param("startTime") Date startTime);
    int countUserConflicts(@Param("userId") Long userId, @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);
    int countSeatConflicts(@Param("seatId") Long seatId, @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);
    int insertReservation(@Param("userId") Long userId, @Param("request") ReservationCreateRequest request,
            @Param("checkDeadline") Date checkDeadline, @Param("createBy") String createBy);
    List<ReservationVo> selectMyReservations(MyReservationQuery query);
    ReservationVo selectOwnedReservationForUpdate(@Param("reservationId") Long reservationId,
            @Param("userId") Long userId);
    int cancelReservation(@Param("reservationId") Long reservationId, @Param("userId") Long userId,
            @Param("cancelTime") Date cancelTime, @Param("updateBy") String updateBy);
}
