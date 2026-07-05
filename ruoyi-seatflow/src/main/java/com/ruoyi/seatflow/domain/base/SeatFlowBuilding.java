package com.ruoyi.seatflow.domain.base;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.ruoyi.common.core.domain.BaseEntity;

/** 楼栋。 */
public class SeatFlowBuilding extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long buildingId;
    private Long campusId;
    private String campusName;
    private String buildingName;
    private Integer floorCount;
    private String status;

    public Long getBuildingId() { return buildingId; }
    public void setBuildingId(Long buildingId) { this.buildingId = buildingId; }
    @NotNull(message = "所属校区不能为空")
    public Long getCampusId() { return campusId; }
    public void setCampusId(Long campusId) { this.campusId = campusId; }
    public String getCampusName() { return campusName; }
    public void setCampusName(String campusName) { this.campusName = campusName; }
    @NotBlank(message = "楼栋名称不能为空") @Size(max = 100, message = "楼栋名称不能超过100个字符")
    public String getBuildingName() { return buildingName; }
    public void setBuildingName(String buildingName) { this.buildingName = buildingName; }
    @Min(value = 1, message = "楼层数至少为1")
    public Integer getFloorCount() { return floorCount; }
    public void setFloorCount(Integer floorCount) { this.floorCount = floorCount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
