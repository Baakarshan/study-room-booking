package com.ruoyi.seatflow.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.seatflow.domain.base.SeatFlowCampus;
import com.ruoyi.seatflow.domain.base.SeatFlowRoom;
import com.ruoyi.seatflow.domain.base.SeatFlowSeat;
import com.ruoyi.seatflow.mapper.SeatFlowBaseInfoMapper;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
class SeatFlowBaseInfoServiceImplTest {
  @Mock private SeatFlowBaseInfoMapper mapper;

  private SeatFlowBaseInfoServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new SeatFlowBaseInfoServiceImpl(mapper);
  }

  @Test
  void saveCampusReturnsReadableDuplicateMessage() {
    SeatFlowCampus campus = new SeatFlowCampus();
    campus.setCampusName(" 主校区 ");
    when(mapper.insertCampus(any())).thenThrow(new DuplicateKeyException("duplicate"));

    ServiceException exception =
        assertThrows(ServiceException.class, () -> service.saveCampus(campus));

    assertEquals("校区名称已存在", exception.getMessage());
    assertEquals("主校区", campus.getCampusName());
  }

  @Test
  void deleteCampusRejectsExistingBuildings() {
    SeatFlowCampus campus = new SeatFlowCampus();
    campus.setCampusId(1L);
    when(mapper.selectCampusById(1L)).thenReturn(campus);
    when(mapper.countBuildingByCampusId(1L)).thenReturn(1);

    ServiceException exception =
        assertThrows(ServiceException.class, () -> service.deleteCampus(1L));

    assertEquals("该校区下存在楼栋，不能删除", exception.getMessage());
  }

  @Test
  void generateSeatsUsesStableGridNumbers() {
    SeatFlowRoom room = validRoom();
    when(mapper.selectRoomById(1L)).thenReturn(room);
    when(mapper.countSeatByRoomId(1L)).thenReturn(0);
    when(mapper.insertSeatBatch(any())).thenReturn(6);

    assertEquals(6, service.generateSeats(1L, "admin"));

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<SeatFlowSeat>> captor = ArgumentCaptor.forClass(List.class);
    verify(mapper).insertSeatBatch(captor.capture());
    List<SeatFlowSeat> seats = captor.getValue();
    assertEquals("A01", seats.get(0).getSeatNo());
    assertEquals("B03", seats.get(5).getSeatNo());
  }

  @Test
  void saveRoomRejectsInvalidOpeningHours() {
    SeatFlowRoom room = validRoom();
    room.setOpenTime(LocalTime.of(22, 0));
    room.setCloseTime(LocalTime.of(8, 0));
    when(mapper.selectFloorById(1L)).thenReturn(new com.ruoyi.seatflow.domain.base.SeatFlowFloor());

    ServiceException exception = assertThrows(ServiceException.class, () -> service.saveRoom(room));

    assertEquals("开放时间必须早于关闭时间", exception.getMessage());
  }

  private SeatFlowRoom validRoom() {
    SeatFlowRoom room = new SeatFlowRoom();
    room.setRoomId(1L);
    room.setFloorId(1L);
    room.setRoomName("测试自习室");
    room.setRowCount(2);
    room.setColCount(3);
    room.setOpenTime(LocalTime.of(8, 0));
    room.setCloseTime(LocalTime.of(22, 0));
    room.setStatus("enabled");
    return room;
  }
}
