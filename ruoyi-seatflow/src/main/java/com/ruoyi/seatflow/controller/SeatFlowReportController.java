package com.ruoyi.seatflow.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.seatflow.domain.dto.ReportQuery;
import com.ruoyi.seatflow.service.ISeatFlowReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** SeatFlow报表统计。 */
@Tag(name = "报表统计管理")
@RestController
@RequestMapping("/seatflow/report")
public class SeatFlowReportController {
  @Autowired private ISeatFlowReportService reportService;

  @PreAuthorize("@ss.hasPermi('seatflow:report:view')")
  @Operation(summary = "获取报表总览")
  @GetMapping("/summary")
  public AjaxResult summary(@ParameterObject ReportQuery query) {
    return AjaxResult.success(reportService.selectSummary(query));
  }

  @PreAuthorize("@ss.hasPermi('seatflow:report:view')")
  @Operation(summary = "获取座位热力图")
  @GetMapping("/heatmap")
  public AjaxResult heatmap(@ParameterObject ReportQuery query) {
    return AjaxResult.success(reportService.selectHeatmap(query));
  }

  @PreAuthorize("@ss.hasPermi('seatflow:report:view')")
  @Operation(summary = "获取使用率统计")
  @GetMapping("/usage-rate")
  public AjaxResult usageRate(@ParameterObject ReportQuery query) {
    return AjaxResult.success(reportService.selectUsageRate(query));
  }

  @PreAuthorize("@ss.hasPermi('seatflow:report:view')")
  @Operation(summary = "获取热门时段统计")
  @GetMapping("/popular-slots")
  public AjaxResult popularSlots(@ParameterObject ReportQuery query) {
    return AjaxResult.success(reportService.selectPopularSlots(query));
  }

  @PreAuthorize("@ss.hasPermi('seatflow:report:view')")
  @Operation(summary = "获取自习室排行")
  @GetMapping("/room-ranking")
  public AjaxResult roomRanking(@ParameterObject ReportQuery query) {
    return AjaxResult.success(reportService.selectRoomRanking(query));
  }
}
