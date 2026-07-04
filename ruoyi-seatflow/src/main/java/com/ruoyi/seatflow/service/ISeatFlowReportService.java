package com.ruoyi.seatflow.service;

import java.util.List;
import com.ruoyi.seatflow.domain.dto.ReportQuery;
import com.ruoyi.seatflow.domain.vo.PopularSlotVo;
import com.ruoyi.seatflow.domain.vo.ReportSummaryVo;
import com.ruoyi.seatflow.domain.vo.RoomRankingVo;
import com.ruoyi.seatflow.domain.vo.SeatHeatmapVo;
import com.ruoyi.seatflow.domain.vo.UsageRateVo;

/**
 * 报表统计服务。
 */
public interface ISeatFlowReportService
{
    public ReportSummaryVo selectSummary(ReportQuery query);

    public List<SeatHeatmapVo> selectHeatmap(ReportQuery query);

    public List<UsageRateVo> selectUsageRate(ReportQuery query);

    public List<PopularSlotVo> selectPopularSlots(ReportQuery query);

    public List<RoomRankingVo> selectRoomRanking(ReportQuery query);
}
