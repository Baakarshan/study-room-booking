package com.ruoyi.seatflow.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.seatflow.domain.base.SeatFlowBuilding;
import com.ruoyi.seatflow.domain.base.SeatFlowCampus;
import com.ruoyi.seatflow.domain.base.SeatFlowFloor;
import com.ruoyi.seatflow.domain.base.SeatFlowRoom;
import com.ruoyi.seatflow.domain.base.SeatFlowSeat;

/** 基础信息数据层。 */
public interface SeatFlowBaseInfoMapper
{
    List<SeatFlowCampus> selectCampusList(SeatFlowCampus query);
    SeatFlowCampus selectCampusById(Long id);
    int insertCampus(SeatFlowCampus entity);
    int updateCampus(SeatFlowCampus entity);
    int deleteCampusById(Long id);
    int countBuildingByCampusId(Long id);

    List<SeatFlowBuilding> selectBuildingList(SeatFlowBuilding query);
    SeatFlowBuilding selectBuildingById(Long id);
    int insertBuilding(SeatFlowBuilding entity);
    int updateBuilding(SeatFlowBuilding entity);
    int deleteBuildingById(Long id);
    int countFloorByBuildingId(Long id);

    List<SeatFlowFloor> selectFloorList(SeatFlowFloor query);
    SeatFlowFloor selectFloorById(Long id);
    int insertFloor(SeatFlowFloor entity);
    int updateFloor(SeatFlowFloor entity);
    int deleteFloorById(Long id);
    int countRoomByFloorId(Long id);

    List<SeatFlowRoom> selectRoomList(SeatFlowRoom query);
    SeatFlowRoom selectRoomById(Long id);
    int insertRoom(SeatFlowRoom entity);
    int updateRoom(SeatFlowRoom entity);
    int deleteRoomById(Long id);
    int countSeatByRoomId(Long id);

    List<SeatFlowSeat> selectSeatList(SeatFlowSeat query);
    SeatFlowSeat selectSeatById(Long id);
    int insertSeatBatch(@Param("seats") List<SeatFlowSeat> seats);
    int updateSeatStatus(@Param("seatId") Long seatId, @Param("status") String status,
            @Param("updateBy") String updateBy);
}
