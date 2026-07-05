package com.ruoyi.seatflow.domain.dto;

import jakarta.validation.constraints.NotNull;

/** 签到请求。 */
public class CheckinRequest
{
    @NotNull(message = "预约ID不能为空")
    private Long reservationId;

    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
}
