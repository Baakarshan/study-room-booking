package com.ruoyi.seatflow.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 座位热力图数据。
 */
@Schema(description = "座位热力图数据")
public class SeatHeatmapVo
{
    @Schema(description = "座位ID")
    private Long seatId;

    @Schema(description = "自习室ID")
    private Long roomId;

    @Schema(description = "自习室名称")
    private String roomName;

    @Schema(description = "座位号")
    private String seatNo;

    @Schema(description = "座位行号")
    private Integer rowNum;

    @Schema(description = "座位列号")
    private Integer colNum;

    @Schema(description = "预约次数")
    private Long reservationCount;

    @Schema(description = "有效使用分钟数")
    private Long usageMinutes;

    public Long getSeatId()
    {
        return seatId;
    }

    public void setSeatId(Long seatId)
    {
        this.seatId = seatId;
    }

    public Long getRoomId()
    {
        return roomId;
    }

    public void setRoomId(Long roomId)
    {
        this.roomId = roomId;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public void setRoomName(String roomName)
    {
        this.roomName = roomName;
    }

    public String getSeatNo()
    {
        return seatNo;
    }

    public void setSeatNo(String seatNo)
    {
        this.seatNo = seatNo;
    }

    public Integer getRowNum()
    {
        return rowNum;
    }

    public void setRowNum(Integer rowNum)
    {
        this.rowNum = rowNum;
    }

    public Integer getColNum()
    {
        return colNum;
    }

    public void setColNum(Integer colNum)
    {
        this.colNum = colNum;
    }

    public Long getReservationCount()
    {
        return reservationCount;
    }

    public void setReservationCount(Long reservationCount)
    {
        this.reservationCount = reservationCount;
    }

    public Long getUsageMinutes()
    {
        return usageMinutes;
    }

    public void setUsageMinutes(Long usageMinutes)
    {
        this.usageMinutes = usageMinutes;
    }
}
