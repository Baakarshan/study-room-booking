package com.ruoyi.seatflow.domain.base;

import com.ruoyi.common.core.domain.BaseEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 楼层。 */
public class SeatFlowFloor extends BaseEntity {
  private static final long serialVersionUID = 1L;
  private Long floorId;
  private Long buildingId;
  private String buildingName;
  private Integer floorNumber;
  private String floorName;
  private String status;

  public Long getFloorId() {
    return floorId;
  }

  public void setFloorId(Long floorId) {
    this.floorId = floorId;
  }

  @NotNull(message = "所属楼栋不能为空")
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

  @NotNull(message = "楼层编号不能为空")
  @Min(value = -9, message = "楼层编号不能小于-9")
  @Max(value = 99, message = "楼层编号不能超过99")
  public Integer getFloorNumber() {
    return floorNumber;
  }

  public void setFloorNumber(Integer floorNumber) {
    this.floorNumber = floorNumber;
  }

  @NotBlank(message = "楼层名称不能为空")
  @Size(max = 64, message = "楼层名称不能超过64个字符")
  public String getFloorName() {
    return floorName;
  }

  public void setFloorName(String floorName) {
    this.floorName = floorName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
