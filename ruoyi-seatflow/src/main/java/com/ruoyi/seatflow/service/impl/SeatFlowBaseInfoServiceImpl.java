package com.ruoyi.seatflow.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.seatflow.domain.base.*;
import com.ruoyi.seatflow.mapper.SeatFlowBaseInfoMapper;
import com.ruoyi.seatflow.service.ISeatFlowBaseInfoService;

/** 基础信息服务实现。 */
@Service
public class SeatFlowBaseInfoServiceImpl implements ISeatFlowBaseInfoService
{
    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disabled";

    private final SeatFlowBaseInfoMapper mapper;

    public SeatFlowBaseInfoServiceImpl(SeatFlowBaseInfoMapper mapper)
    {
        this.mapper = mapper;
    }

    public List<SeatFlowCampus> selectCampusList(SeatFlowCampus q) { return mapper.selectCampusList(q); }
    public SeatFlowCampus selectCampusById(Long id) { return mapper.selectCampusById(id); }
    public List<SeatFlowBuilding> selectBuildingList(SeatFlowBuilding q) { return mapper.selectBuildingList(q); }
    public SeatFlowBuilding selectBuildingById(Long id) { return mapper.selectBuildingById(id); }
    public List<SeatFlowFloor> selectFloorList(SeatFlowFloor q) { return mapper.selectFloorList(q); }
    public SeatFlowFloor selectFloorById(Long id) { return mapper.selectFloorById(id); }
    public List<SeatFlowRoom> selectRoomList(SeatFlowRoom q) { return mapper.selectRoomList(q); }
    public SeatFlowRoom selectRoomById(Long id) { return mapper.selectRoomById(id); }
    public List<SeatFlowSeat> selectSeatList(SeatFlowSeat q) { return mapper.selectSeatList(q); }

    @Transactional
    public int saveCampus(SeatFlowCampus e)
    {
        normalize(e);
        try
        {
            return e.getCampusId() == null ? mapper.insertCampus(e) : mapper.updateCampus(e);
        }
        catch (DuplicateKeyException ex)
        {
            throw new ServiceException("校区名称已存在");
        }
    }

    @Transactional
    public int deleteCampus(Long id)
    {
        requireExists(mapper.selectCampusById(id), "校区不存在");
        if (mapper.countBuildingByCampusId(id) > 0) throw new ServiceException("该校区下存在楼栋，不能删除");
        return mapper.deleteCampusById(id);
    }

    @Transactional
    public int saveBuilding(SeatFlowBuilding e)
    {
        normalize(e);
        requireExists(mapper.selectCampusById(e.getCampusId()), "所属校区不存在");
        try
        {
            return e.getBuildingId() == null ? mapper.insertBuilding(e) : mapper.updateBuilding(e);
        }
        catch (DuplicateKeyException ex)
        {
            throw new ServiceException("同一校区内楼栋名称不能重复");
        }
    }

    @Transactional
    public int deleteBuilding(Long id)
    {
        requireExists(mapper.selectBuildingById(id), "楼栋不存在");
        if (mapper.countFloorByBuildingId(id) > 0) throw new ServiceException("该楼栋下存在楼层，不能删除");
        return mapper.deleteBuildingById(id);
    }

    @Transactional
    public int saveFloor(SeatFlowFloor e)
    {
        normalize(e);
        requireExists(mapper.selectBuildingById(e.getBuildingId()), "所属楼栋不存在");
        try
        {
            return e.getFloorId() == null ? mapper.insertFloor(e) : mapper.updateFloor(e);
        }
        catch (DuplicateKeyException ex)
        {
            throw new ServiceException("同一楼栋内楼层编号不能重复");
        }
    }

    @Transactional
    public int deleteFloor(Long id)
    {
        requireExists(mapper.selectFloorById(id), "楼层不存在");
        if (mapper.countRoomByFloorId(id) > 0) throw new ServiceException("该楼层下存在自习室，不能删除");
        return mapper.deleteFloorById(id);
    }

    @Transactional
    public int saveRoom(SeatFlowRoom e)
    {
        normalize(e);
        requireExists(mapper.selectFloorById(e.getFloorId()), "所属楼层不存在");
        if (!e.getOpenTime().isBefore(e.getCloseTime())) throw new ServiceException("开放时间必须早于关闭时间");
        e.setTotalSeats(e.getRowCount() * e.getColCount());
        if (e.getRoomId() != null && mapper.countSeatByRoomId(e.getRoomId()) > 0)
        {
            SeatFlowRoom old = mapper.selectRoomById(e.getRoomId());
            requireExists(old, "自习室不存在");
            if (!old.getRowCount().equals(e.getRowCount()) || !old.getColCount().equals(e.getColCount()))
                throw new ServiceException("自习室已有座位，不能修改行列数");
        }
        try
        {
            return e.getRoomId() == null ? mapper.insertRoom(e) : mapper.updateRoom(e);
        }
        catch (DuplicateKeyException ex)
        {
            throw new ServiceException("同一楼层内自习室名称不能重复");
        }
    }

    @Transactional
    public int deleteRoom(Long id)
    {
        requireExists(mapper.selectRoomById(id), "自习室不存在");
        if (mapper.countSeatByRoomId(id) > 0) throw new ServiceException("该自习室下存在座位，不能删除");
        return mapper.deleteRoomById(id);
    }

    @Transactional
    public int generateSeats(Long roomId, String operator)
    {
        SeatFlowRoom room = mapper.selectRoomById(roomId);
        requireExists(room, "自习室不存在");
        if (mapper.countSeatByRoomId(roomId) > 0) throw new ServiceException("该自习室已生成座位，不能重复生成");
        List<SeatFlowSeat> seats = new ArrayList<>();
        for (int row = 1; row <= room.getRowCount(); row++)
        {
            for (int col = 1; col <= room.getColCount(); col++)
            {
                SeatFlowSeat seat = new SeatFlowSeat();
                seat.setRoomId(roomId);
                seat.setSeatNo(String.format("%c%02d", 'A' + row - 1, col));
                seat.setRowNum(row);
                seat.setColNum(col);
                seat.setStatus(ENABLED);
                seat.setCreateBy(operator);
                seats.add(seat);
            }
        }
        return mapper.insertSeatBatch(seats);
    }

    @Transactional
    public int updateSeatStatus(Long seatId, String status, String operator)
    {
        requireExists(mapper.selectSeatById(seatId), "座位不存在");
        validateStatus(status);
        return mapper.updateSeatStatus(seatId, status, operator);
    }

    private void normalize(SeatFlowCampus e) { e.setCampusName(e.getCampusName().trim()); e.setStatus(normalizeStatus(e.getStatus())); }
    private void normalize(SeatFlowBuilding e) { e.setBuildingName(e.getBuildingName().trim()); e.setStatus(normalizeStatus(e.getStatus())); }
    private void normalize(SeatFlowFloor e) { e.setFloorName(e.getFloorName().trim()); e.setStatus(normalizeStatus(e.getStatus())); }
    private void normalize(SeatFlowRoom e) { e.setRoomName(e.getRoomName().trim()); e.setStatus(normalizeStatus(e.getStatus())); }
    private String normalizeStatus(String status)
    {
        status = StringUtils.isBlank(status) ? ENABLED : status.trim();
        validateStatus(status);
        return status;
    }
    private void validateStatus(String status)
    {
        if (!ENABLED.equals(status) && !DISABLED.equals(status)) throw new ServiceException("状态只支持 enabled 或 disabled");
    }
    private void requireExists(Object value, String message)
    {
        if (value == null) throw new ServiceException(message);
    }
}
