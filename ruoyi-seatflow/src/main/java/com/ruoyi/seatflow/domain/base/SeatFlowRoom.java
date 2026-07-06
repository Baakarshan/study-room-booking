package com.ruoyi.seatflow.domain.base;

import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.ruoyi.common.core.domain.BaseEntity;

/** 自习室。 */
public class SeatFlowRoom extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    private Long roomId;
    private Long floorId;
    private String floorName;
    private String roomName;
    private Integer rowCount;
    private Integer colCount;
    private Integer totalSeats;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime openTime;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime closeTime;
    private String status;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    @NotNull(message = "所属楼层不能为空") public Long getFloorId() { return floorId; }
    public void setFloorId(Long floorId) { this.floorId = floorId; }
    public String getFloorName() { return floorName; }
    public void setFloorName(String floorName) { this.floorName = floorName; }
    @NotBlank(message = "自习室名称不能为空") @Size(max = 64, message = "自习室名称不能超过64个字符")
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    @NotNull(message = "座位行数不能为空") @Min(value = 1, message = "座位行数至少为1") @Max(value = 26, message = "座位行数不能超过26")
    public Integer getRowCount() { return rowCount; }
    public void setRowCount(Integer rowCount) { this.rowCount = rowCount; }
    @NotNull(message = "座位列数不能为空") @Min(value = 1, message = "座位列数至少为1") @Max(value = 99, message = "座位列数不能超过99")
    public Integer getColCount() { return colCount; }
    public void setColCount(Integer colCount) { this.colCount = colCount; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    @NotNull(message = "开放时间不能为空") public LocalTime getOpenTime() { return openTime; }
    public void setOpenTime(LocalTime openTime) { this.openTime = openTime; }
    @NotNull(message = "关闭时间不能为空") public LocalTime getCloseTime() { return closeTime; }
    public void setCloseTime(LocalTime closeTime) { this.closeTime = closeTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
