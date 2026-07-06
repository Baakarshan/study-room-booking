package com.ruoyi.seatflow.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/** 指定自习室和时间段的座位状态查询。 */
public class SeatAvailabilityQuery {
  @NotNull(message = "请选择自习室")
  private Long roomId;

  @NotNull(message = "请选择开始时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date startTime;

  @NotNull(message = "请选择结束时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date endTime;

  public Long getRoomId() {
    return roomId;
  }

  public void setRoomId(Long roomId) {
    this.roomId = roomId;
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
}
