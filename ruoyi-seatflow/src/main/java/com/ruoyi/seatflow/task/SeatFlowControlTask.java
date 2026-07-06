package com.ruoyi.seatflow.task;

import com.ruoyi.seatflow.service.ISeatFlowControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** 可由 RuoYi Quartz 以 seatFlowControlTask.releaseExpiredReservations() 调用。 */
@Component("seatFlowControlTask")
public class SeatFlowControlTask {
  private static final Logger log = LoggerFactory.getLogger(SeatFlowControlTask.class);
  private static final int BATCH_SIZE = 200;
  private static final int MAX_BATCHES = 50;
  private final ISeatFlowControlService controlService;

  public SeatFlowControlTask(ISeatFlowControlService controlService) {
    this.controlService = controlService;
  }

  public void releaseExpiredReservations() {
    int noShowTotal = processNoShows();
    int completedTotal = processCompleted();
    log.info("SeatFlow预约维护完成：爽约{}条，自动完成{}条", noShowTotal, completedTotal);
  }

  private int processNoShows() {
    int total = 0;
    for (int i = 0; i < MAX_BATCHES; i++) {
      int handled = controlService.releaseExpiredBatch(BATCH_SIZE);
      total += handled;
      if (handled < BATCH_SIZE) {
        break;
      }
    }
    return total;
  }

  private int processCompleted() {
    int total = 0;
    for (int i = 0; i < MAX_BATCHES; i++) {
      int handled = controlService.completeExpiredBatch(BATCH_SIZE);
      total += handled;
      if (handled < BATCH_SIZE) {
        break;
      }
    }
    return total;
  }
}
