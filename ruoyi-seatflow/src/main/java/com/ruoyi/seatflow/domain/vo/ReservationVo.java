package com.ruoyi.seatflow.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/** 我的预约列表项。 */
public class ReservationVo
{
    private Long reservationId;
    private Long roomId;
    private String roomName;
    private Long seatId;
    private String seatNo;
    private String campusName;
    private String buildingName;
    private String floorName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private Date startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private Date endTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private Date checkDeadline;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private Date cancelTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") private Date createTime;

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
    public String getSeatNo() { return seatNo; }
    public void setSeatNo(String seatNo) { this.seatNo = seatNo; }
    public String getCampusName() { return campusName; }
    public void setCampusName(String campusName) { this.campusName = campusName; }
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    public String getFloorName() { return floorName; }
    public void setFloorName(String floorName) { this.floorName = floorName; }
    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }
    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }
    public Date getCheckDeadline() { return checkDeadline; }
    public void setCheckDeadline(Date checkDeadline) { this.checkDeadline = checkDeadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCancelTime() { return cancelTime; }
    public void setCancelTime(Date cancelTime) { this.cancelTime = cancelTime; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
