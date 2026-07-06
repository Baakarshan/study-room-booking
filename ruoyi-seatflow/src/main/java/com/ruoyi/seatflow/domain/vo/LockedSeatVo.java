package com.ruoyi.seatflow.domain.vo;

/** 预约事务中锁定的座位及其自习室信息。 */
public class LockedSeatVo {
  private Long seatId;
  private Long roomId;
  private String seatStatus;
  private String roomStatus;
  private String openTime;
  private String closeTime;

  public Long getSeatId() {
    return seatId;
  }

  public void setSeatId(Long seatId) {
    this.seatId = seatId;
  }

  public Long getRoomId() {
    return roomId;
  }

  public void setRoomId(Long roomId) {
    this.roomId = roomId;
  }

  public String getSeatStatus() {
    return seatStatus;
  }

  public void setSeatStatus(String seatStatus) {
    this.seatStatus = seatStatus;
  }

  public String getRoomStatus() {
    return roomStatus;
  }

  public void setRoomStatus(String roomStatus) {
    this.roomStatus = roomStatus;
  }

  public String getOpenTime() {
    return openTime;
  }

  public void setOpenTime(String openTime) {
    this.openTime = openTime;
  }

  public String getCloseTime() {
    return closeTime;
  }

  public void setCloseTime(String closeTime) {
    this.closeTime = closeTime;
  }
}
