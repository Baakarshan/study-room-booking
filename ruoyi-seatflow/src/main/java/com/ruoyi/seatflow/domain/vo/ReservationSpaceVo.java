package com.ruoyi.seatflow.domain.vo;

/** 预约页空间级联选项。 */
public class ReservationSpaceVo
{
    private Long id;
    private Long parentId;
    private String name;
    private String openTime;
    private String closeTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }
    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }
}
