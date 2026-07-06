package com.ruoyi.seatflow.domain;

import java.util.Date;

/** 黑名单查询模型。 */
public class BlacklistEntry {
  private Long blacklistId;
  private Long userId;
  private String userName;
  private String nickName;
  private String studentNo;
  private Integer violationCount;
  private String reason;
  private Date startTime;
  private Date endTime;
  private String status;

  public Long getBlacklistId() {
    return blacklistId;
  }

  public void setBlacklistId(Long blacklistId) {
    this.blacklistId = blacklistId;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  public String getStudentNo() {
    return studentNo;
  }

  public void setStudentNo(String studentNo) {
    this.studentNo = studentNo;
  }

  public Integer getViolationCount() {
    return violationCount;
  }

  public void setViolationCount(Integer violationCount) {
    this.violationCount = violationCount;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setStartTime(Date startTime) {
    this.startTime = startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
