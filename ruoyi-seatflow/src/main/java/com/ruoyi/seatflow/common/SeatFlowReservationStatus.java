package com.ruoyi.seatflow.common;

/** 预约状态语义码。 */
public final class SeatFlowReservationStatus {
  public static final String PENDING_CHECKIN = "pending_checkin";

  public static final String IN_USE = "in_use";

  public static final String CANCELLED = "cancelled";

  public static final String NO_SHOW = "no_show";

  public static final String COMPLETED = "completed";

  private SeatFlowReservationStatus() {}
}
