package com.ruoyi.seatflow.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/** 报表总览数据。 */
@Schema(description = "报表总览数据")
public class ReportSummaryVo {
  @Schema(description = "预约数")
  private Long reservationCount;

  @Schema(description = "签到数")
  private Long checkinCount;

  @Schema(description = "爽约数")
  private Long noShowCount;

  @Schema(description = "启用座位数")
  private Long activeSeatCount;

  @Schema(description = "有效使用分钟数")
  private Long usageMinutes;

  @Schema(description = "使用率")
  private BigDecimal usageRate;

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

  public Long getNoShowCount() {
    return noShowCount;
  }

  public void setNoShowCount(Long noShowCount) {
    this.noShowCount = noShowCount;
  }

  public Long getActiveSeatCount() {
    return activeSeatCount;
  }

  public void setActiveSeatCount(Long activeSeatCount) {
    this.activeSeatCount = activeSeatCount;
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
