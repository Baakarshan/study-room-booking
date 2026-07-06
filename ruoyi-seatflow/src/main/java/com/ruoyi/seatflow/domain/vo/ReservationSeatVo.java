package com.ruoyi.seatflow.domain.vo;

/** 座位图中的座位状态。 */
public class ReservationSeatVo {
  private Long seatId;
  private Long roomId;
  private String seatNo;
  private Integer rowNum;
  private Integer colNum;
  private String status;

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

  public String getSeatNo() {
    return seatNo;
  }

  public void setSeatNo(String seatNo) {
    this.seatNo = seatNo;
  }

  public Integer getRowNum() {
    return rowNum;
  }

  public void setRowNum(Integer rowNum) {
    this.rowNum = rowNum;
  }

  public Integer getColNum() {
    return colNum;
  }

  public void setColNum(Integer colNum) {
    this.colNum = colNum;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
