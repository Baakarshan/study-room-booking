package com.ruoyi.seatflow.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/** 使用率数据。 */
@Schema(description = "使用率数据")
public class UsageRateVo {
  @Schema(description = "统计日期")
  private String reportDate;

  @Schema(description = "自习室ID")
  private Long roomId;

  @Schema(description = "自习室名称")
  private String roomName;

  @Schema(description = "有效使用分钟数")
  private Long usageMinutes;

  @Schema(description = "可用分钟数")
  private Long availableMinutes;

  @Schema(description = "使用率")
  private BigDecimal usageRate;

  public String getReportDate() {
    return reportDate;
  }

  public void setReportDate(String reportDate) {
    this.reportDate = reportDate;
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

  public Long getUsageMinutes() {
    return usageMinutes;
  }

  public void setUsageMinutes(Long usageMinutes) {
    this.usageMinutes = usageMinutes;
  }

  public Long getAvailableMinutes() {
    return availableMinutes;
  }

  public void setAvailableMinutes(Long availableMinutes) {
    this.availableMinutes = availableMinutes;
  }

  public BigDecimal getUsageRate() {
    return usageRate;
  }

  public void setUsageRate(BigDecimal usageRate) {
    this.usageRate = usageRate;
  }
}
