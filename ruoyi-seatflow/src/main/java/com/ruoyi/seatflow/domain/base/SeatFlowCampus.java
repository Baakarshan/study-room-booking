package com.ruoyi.seatflow.domain.base;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.ruoyi.common.core.domain.BaseEntity;

/** 校区。 */
public class SeatFlowCampus extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long campusId;
    private String campusName;
    private String address;
    private String status;

    public Long getCampusId() { return campusId; }
    public void setCampusId(Long campusId) { this.campusId = campusId; }
    @NotBlank(message = "校区名称不能为空")
    @Size(max = 100, message = "校区名称不能超过100个字符")
    public String getCampusName() { return campusName; }
    public void setCampusName(String campusName) { this.campusName = campusName; }
    @Size(max = 255, message = "地址不能超过255个字符")
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
