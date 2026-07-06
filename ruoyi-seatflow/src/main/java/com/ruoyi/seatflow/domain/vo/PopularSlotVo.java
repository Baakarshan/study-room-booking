package com.ruoyi.seatflow.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/** 热门时段数据。 */
@Schema(description = "热门时段数据")
public class PopularSlotVo {
  @Schema(description = "时段标签，格式：HH:00")
  private String slotLabel;

  @Schema(description = "预约数")
  private Long reservationCount;

  @Schema(description = "签到数")
  private Long checkinCount;

  public String getSlotLabel() {
    return slotLabel;
  }

  public void setSlotLabel(String slotLabel) {
    this.slotLabel = slotLabel;
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
}
