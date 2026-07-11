package com.ruoyi.seatflow.domain.vo;

/** 预约状态及其记录数。 */
public class ReservationStatusCountVo {
  private String status;
  private Long count;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }
}
