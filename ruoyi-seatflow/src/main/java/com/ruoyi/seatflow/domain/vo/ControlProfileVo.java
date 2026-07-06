package com.ruoyi.seatflow.domain.vo;

/** 学生签到管控状态。 */
public class ControlProfileVo {
  private Integer violationCount;
  private Boolean blacklisted;
  private Integer threshold;

  public Integer getViolationCount() {
    return violationCount;
  }

  public void setViolationCount(Integer violationCount) {
    this.violationCount = violationCount;
  }

  public Boolean getBlacklisted() {
    return blacklisted;
  }

  public void setBlacklisted(Boolean blacklisted) {
    this.blacklisted = blacklisted;
  }

  public Integer getThreshold() {
    return threshold;
  }

  public void setThreshold(Integer threshold) {
    this.threshold = threshold;
  }
}
