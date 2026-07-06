package com.ruoyi.seatflow.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

/** 报表查询参数。 */
@Schema(description = "报表查询参数")
public class ReportQuery {
  @Schema(
      description = "开始时间，格式：yyyy-MM-dd HH:mm:ss",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "2026-07-01 00:00:00")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date beginTime;

  @Schema(
      description = "结束时间，格式：yyyy-MM-dd HH:mm:ss",
      requiredMode = Schema.RequiredMode.REQUIRED,
      example = "2026-07-07 23:59:59")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date endTime;

  @Schema(description = "校区ID")
  private Long campusId;

  @Schema(description = "楼栋ID")
  private Long buildingId;

  @Schema(description = "楼层ID")
  private Long floorId;

  @Schema(description = "自习室ID")
  private Long roomId;

  @Schema(
      description =
          "统计指标：reservation_count 预约数，usage_minutes 使用分钟数，checkin_count 签到数，usage_rate 使用率",
      allowableValues = {"reservation_count", "usage_minutes", "checkin_count", "usage_rate"})
  private String metric;

  @Schema(
      description = "时段类型，目前仅支持 hour",
      allowableValues = {"hour"},
      example = "hour")
  private String slotType;

  public Date getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(Date beginTime) {
    this.beginTime = beginTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public Long getCampusId() {
    return campusId;
  }

  public void setCampusId(Long campusId) {
    this.campusId = campusId;
  }

  public Long getBuildingId() {
    return buildingId;
  }

  public void setBuildingId(Long buildingId) {
    this.buildingId = buildingId;
  }

  public Long getFloorId() {
    return floorId;
  }

  public void setFloorId(Long floorId) {
    this.floorId = floorId;
  }

  public Long getRoomId() {
    return roomId;
  }

  public void setRoomId(Long roomId) {
    this.roomId = roomId;
  }

  public String getMetric() {
    return metric;
  }

  public void setMetric(String metric) {
    this.metric = metric;
  }

  public String getSlotType() {
    return slotType;
  }

  public void setSlotType(String slotType) {
    this.slotType = slotType;
  }
}
