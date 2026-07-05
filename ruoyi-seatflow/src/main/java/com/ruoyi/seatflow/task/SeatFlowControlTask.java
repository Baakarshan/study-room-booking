package com.ruoyi.seatflow.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.ruoyi.seatflow.service.ISeatFlowControlService;

/** 可由 RuoYi Quartz 以 seatFlowControlTask.releaseExpiredReservations() 调用。 */
@Component("seatFlowControlTask")
public class SeatFlowControlTask
{
    private static final Logger log = LoggerFactory.getLogger(SeatFlowControlTask.class);
    private static final int BATCH_SIZE = 200;
    private static final int MAX_BATCHES = 50;
    private final ISeatFlowControlService controlService;

    public SeatFlowControlTask(ISeatFlowControlService controlService)
    {
        this.controlService = controlService;
    }

    public void releaseExpiredReservations()
    {
        int total = 0;
        for (int i = 0; i < MAX_BATCHES; i++)
        {
            int handled = controlService.releaseExpiredBatch(BATCH_SIZE);
            total += handled;
            if (handled < BATCH_SIZE)
            {
                break;
            }
        }
        log.info("SeatFlow超时未签到处理完成，本次释放{}条预约", total);
    }
}
