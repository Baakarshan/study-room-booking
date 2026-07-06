package com.ruoyi.seatflow.domain.dto;

/** 管控列表筛选条件。 */
public class ControlQuery {
  private String userName;
  private String studentNo;

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getStudentNo() {
    return studentNo;
  }

  public void setStudentNo(String studentNo) {
    this.studentNo = studentNo;
  }
}
