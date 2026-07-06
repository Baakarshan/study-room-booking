package com.ruoyi.seatflow.domain.dto;

import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;

/** 管理端预约列表筛选参数。 */
@Schema(description = "管理端预约列表筛选参数")
public class ReservationManageQuery
{
    @Schema(description = "登录账号，支持模糊查询")
    private String userName;

    @Schema(description = "学号，支持模糊查询")
    private String studentNo;

    private Long campusId;

    private Long buildingId;

    private Long floorId;

    private Long roomId;

    @Schema(description = "预约状态")
    private String status;

    @Schema(description = "预约开始时间下限，格式：yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;

    @Schema(description = "预约结束时间上限，格式：yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getStudentNo()
    {
        return studentNo;
    }

    public void setStudentNo(String studentNo)
    {
        this.studentNo = studentNo;
    }

    public Long getCampusId()
    {
        return campusId;
    }

    public void setCampusId(Long campusId)
    {
        this.campusId = campusId;
    }

    public Long getBuildingId()
    {
        return buildingId;
    }

    public void setBuildingId(Long buildingId)
    {
        this.buildingId = buildingId;
    }

    public Long getFloorId()
    {
        return floorId;
    }

    public void setFloorId(Long floorId)
    {
        this.floorId = floorId;
    }

    public Long getRoomId()
    {
        return roomId;
    }

    public void setRoomId(Long roomId)
    {
        this.roomId = roomId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Date getBeginTime()
    {
        return beginTime;
    }

    public void setBeginTime(Date beginTime)
    {
        this.beginTime = beginTime;
    }

    public Date getEndTime()
    {
        return endTime;
    }

    public void setEndTime(Date endTime)
    {
        this.endTime = endTime;
    }
}
