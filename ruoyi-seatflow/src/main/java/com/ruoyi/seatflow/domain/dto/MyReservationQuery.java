package com.ruoyi.seatflow.domain.dto;

/** 我的预约筛选参数。 */
public class MyReservationQuery
{
    private Long userId;
    private String status;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
