package com.ruoyi.seatflow.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/** 管理端预约列表项。 */
public class ReservationManageVo {
  private Long reservationId;
  private Long userId;
  private String userName;
  private String nickName;
  private String studentNo;
  private Long campusId;
  private String campusName;
  private Long buildingId;
  private String buildingName;
  private Long floorId;
  private String floorName;
  private Long roomId;
  private String roomName;
  private Long seatId;
  private String seatNo;
  private String status;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date startTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date endTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date checkDeadline;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date cancelTime;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  public Long getReservationId() {
    return reservationId;
  }

  public void setReservationId(Long reservationId) {
    this.reservationId = reservationId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public String getStudentNo() {
    return studentNo;
  }

  public void setStudentNo(String studentNo) {
    this.studentNo = studentNo;
  }

  public Long getCampusId() {
    return campusId;
  }

  public void setCampusId(Long campusId) {
    this.campusId = campusId;
  }

  public String getCampusName() {
    return campusName;
  }

  public void setCampusName(String campusName) {
    this.campusName = campusName;
  }

  public Long getBuildingId() {
    return buildingId;
  }

  public void setBuildingId(Long buildingId) {
    this.buildingId = buildingId;
  }

  public String getBuildingName() {
    return buildingName;
  }

  public void setBuildingName(String buildingName) {
    this.buildingName = buildingName;
  }

  public Long getFloorId() {
    return floorId;
  }

  public void setFloorId(Long floorId) {
    this.floorId = floorId;
  }

  public String getFloorName() {
    return floorName;
  }

  public void setFloorName(String floorName) {
    this.floorName = floorName;
  }

  public Long getRoomId() {
    return roomId;
  }

  public void setRoomId(Long roomId) {
    this.roomId = roomId;
  }

  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public Long getSeatId() {
    return seatId;
  }

  public void setSeatId(Long seatId) {
    this.seatId = seatId;
  }

  public String getSeatNo() {
    return seatNo;
  }

  public void setSeatNo(String seatNo) {
    this.seatNo = seatNo;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public Date getCheckDeadline() {
    return checkDeadline;
  }

  public void setCheckDeadline(Date checkDeadline) {
    this.checkDeadline = checkDeadline;
  }

  public Date getCancelTime() {
    return cancelTime;
  }

  public void setCancelTime(Date cancelTime) {
    this.cancelTime = cancelTime;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }
}
