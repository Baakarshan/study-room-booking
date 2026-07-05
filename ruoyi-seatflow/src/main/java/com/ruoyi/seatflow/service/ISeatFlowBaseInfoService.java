package com.ruoyi.seatflow.service;

import java.util.List;
import com.ruoyi.seatflow.domain.base.*;

/** 基础信息服务。 */
public interface ISeatFlowBaseInfoService
{
    List<SeatFlowCampus> selectCampusList(SeatFlowCampus query);
    SeatFlowCampus selectCampusById(Long id);
    int saveCampus(SeatFlowCampus entity);
    int deleteCampus(Long id);
    List<SeatFlowBuilding> selectBuildingList(SeatFlowBuilding query);
    SeatFlowBuilding selectBuildingById(Long id);
    int saveBuilding(SeatFlowBuilding entity);
    int deleteBuilding(Long id);
    List<SeatFlowFloor> selectFloorList(SeatFlowFloor query);
    SeatFlowFloor selectFloorById(Long id);
    int saveFloor(SeatFlowFloor entity);
    int deleteFloor(Long id);
    List<SeatFlowRoom> selectRoomList(SeatFlowRoom query);
    SeatFlowRoom selectRoomById(Long id);
    int saveRoom(SeatFlowRoom entity);
    int deleteRoom(Long id);
    List<SeatFlowSeat> selectSeatList(SeatFlowSeat query);
    int generateSeats(Long roomId, String operator);
    int updateSeatStatus(Long seatId, String status, String operator);
}
