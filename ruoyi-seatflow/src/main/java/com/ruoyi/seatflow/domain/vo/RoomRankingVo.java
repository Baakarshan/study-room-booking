package com.ruoyi.seatflow.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/** 自习室排行数据。 */
@Schema(description = "自习室排行数据")
public class RoomRankingVo {
  @Schema(description = "自习室ID")
  private Long roomId;

  @Schema(description = "自习室名称")
  private String roomName;

  @Schema(description = "预约数")
  private Long reservationCount;

  @Schema(description = "签到数")
  private Long checkinCount;

  @Schema(description = "有效使用分钟数")
  private Long usageMinutes;

  @Schema(description = "使用率")
  private BigDecimal usageRate;

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

  public Long getReservationCount() {
    return reservationCount;
  }

  public void setReservationCount(Long reservationCount) {
    this.reservationCount = reservationCount;
  }

  public Long getCheckinCount() {
    return checkinCount;
  }

  public void setCheckinCount(Long checkinCount) {
    this.checkinCount = checkinCount;
  }

  public Long getUsageMinutes() {
    return usageMinutes;
  }

  public void setUsageMinutes(Long usageMinutes) {
    this.usageMinutes = usageMinutes;
  }

  public BigDecimal getUsageRate() {
    return usageRate;
  }

  public void setUsageRate(BigDecimal usageRate) {
    this.usageRate = usageRate;
  }
}
