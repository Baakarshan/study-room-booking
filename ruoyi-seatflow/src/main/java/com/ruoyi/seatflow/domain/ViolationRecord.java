package com.ruoyi.seatflow.domain;

import java.util.Date;

/** 爽约记录查询模型。 */
public class ViolationRecord
{
    private Long violationId;
    private Long reservationId;
    private Long userId;
    private String userName;
    private String studentNo;
    private String reason;
    private Date violationTime;
    private String status;

    public Long getViolationId() { return violationId; }
    public void setViolationId(Long violationId) { this.violationId = violationId; }
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Date getViolationTime() { return violationTime; }
    public void setViolationTime(Date violationTime) { this.violationTime = violationTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
