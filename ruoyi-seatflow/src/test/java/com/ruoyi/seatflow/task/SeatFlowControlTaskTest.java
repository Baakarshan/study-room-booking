package com.ruoyi.seatflow.task;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.seatflow.service.ISeatFlowControlService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeatFlowControlTaskTest {
  @Mock private ISeatFlowControlService controlService;

  @Test
  void maintenanceHandlesNoShowsAndEndedReservations() {
    when(controlService.releaseExpiredBatch(200)).thenReturn(2);
    when(controlService.completeExpiredBatch(200)).thenReturn(3);

    new SeatFlowControlTask(controlService).releaseExpiredReservations();

    verify(controlService).releaseExpiredBatch(200);
    verify(controlService).completeExpiredBatch(200);
  }
}
