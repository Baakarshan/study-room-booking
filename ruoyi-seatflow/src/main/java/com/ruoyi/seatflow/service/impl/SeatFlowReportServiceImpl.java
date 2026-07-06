package com.ruoyi.seatflow.service.impl;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.seatflow.domain.dto.ReportQuery;
import com.ruoyi.seatflow.domain.vo.PopularSlotVo;
import com.ruoyi.seatflow.domain.vo.ReportSummaryVo;
import com.ruoyi.seatflow.domain.vo.RoomRankingVo;
import com.ruoyi.seatflow.domain.vo.SeatHeatmapVo;
import com.ruoyi.seatflow.domain.vo.UsageRateVo;
import com.ruoyi.seatflow.mapper.SeatFlowReportMapper;
import com.ruoyi.seatflow.service.ISeatFlowReportService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** 报表统计服务实现。 */
@Service
public class SeatFlowReportServiceImpl implements ISeatFlowReportService {
  private static final int MAX_RANGE_DAYS = 31;

  private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

  public static final String METRIC_RESERVATION_COUNT = "reservation_count";

  public static final String METRIC_USAGE_MINUTES = "usage_minutes";

  public static final String METRIC_CHECKIN_COUNT = "checkin_count";

  public static final String METRIC_USAGE_RATE = "usage_rate";

  public static final String SLOT_TYPE_HOUR = "hour";

  private static final Set<String> ALL_METRICS =
      new HashSet<String>(
          Arrays.asList(
              METRIC_RESERVATION_COUNT,
              METRIC_USAGE_MINUTES,
              METRIC_CHECKIN_COUNT,
              METRIC_USAGE_RATE));

  private static final Set<String> HEATMAP_METRICS =
      new HashSet<String>(Arrays.asList(METRIC_RESERVATION_COUNT, METRIC_USAGE_MINUTES));

  private static final Set<String> ROOM_RANKING_METRICS =
      new HashSet<String>(
          Arrays.asList(METRIC_RESERVATION_COUNT, METRIC_CHECKIN_COUNT, METRIC_USAGE_RATE));

  @Autowired private SeatFlowReportMapper reportMapper;

  @Override
  public ReportSummaryVo selectSummary(ReportQuery query) {
    validateQuery(query);
    return reportMapper.selectSummary(query);
  }

  @Override
  public List<SeatHeatmapVo> selectHeatmap(ReportQuery query) {
    validateQuery(query);
    normalizeMetric(query, METRIC_RESERVATION_COUNT);
    validateMetric(
        query.getMetric(), HEATMAP_METRICS, "热力图指标只支持 reservation_count 或 usage_minutes");
    return reportMapper.selectHeatmap(query);
  }

  @Override
  public List<UsageRateVo> selectUsageRate(ReportQuery query) {
    validateQuery(query);
    return reportMapper.selectUsageRate(query);
  }

  @Override
  public List<PopularSlotVo> selectPopularSlots(ReportQuery query) {
    validateQuery(query);
    normalizeSlotType(query);
    return reportMapper.selectPopularSlots(query);
  }

  @Override
  public List<RoomRankingVo> selectRoomRanking(ReportQuery query) {
    validateQuery(query);
    normalizeMetric(query, METRIC_RESERVATION_COUNT);
    validateMetric(
        query.getMetric(),
        ROOM_RANKING_METRICS,
        "自习室排行指标只支持 reservation_count、checkin_count 或 usage_rate");
    return reportMapper.selectRoomRanking(query);
  }

  private void validateQuery(ReportQuery query) {
    if (query == null) {
      throw new ServiceException("查询参数不能为空");
    }
    trimQuery(query);

    Date beginTime = query.getBeginTime();
    Date endTime = query.getEndTime();
    if (beginTime == null) {
      throw new ServiceException("开始时间不能为空");
    }
    if (endTime == null) {
      throw new ServiceException("结束时间不能为空");
    }
    if (beginTime.after(endTime)) {
      throw new ServiceException("开始时间不能晚于结束时间");
    }
    if (countInclusiveDays(beginTime, endTime) > MAX_RANGE_DAYS) {
      throw new ServiceException("查询时间范围不能超过31天");
    }
    if (StringUtils.isNotBlank(query.getMetric()) && !ALL_METRICS.contains(query.getMetric())) {
      throw new ServiceException("报表指标参数不合法");
    }
    if (StringUtils.isNotBlank(query.getSlotType())
        && !SLOT_TYPE_HOUR.equals(query.getSlotType())) {
      throw new ServiceException("热门时段第一版只支持按小时统计");
    }
  }

  private void trimQuery(ReportQuery query) {
    if (query.getMetric() != null) {
      query.setMetric(query.getMetric().trim());
    }
    if (query.getSlotType() != null) {
      query.setSlotType(query.getSlotType().trim());
    }
  }

  private long countInclusiveDays(Date beginTime, Date endTime) {
    LocalDate beginDate = beginTime.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
    LocalDate endDate = endTime.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDate();
    return ChronoUnit.DAYS.between(beginDate, endDate) + 1;
  }

  private void normalizeMetric(ReportQuery query, String defaultMetric) {
    if (StringUtils.isBlank(query.getMetric())) {
      query.setMetric(defaultMetric);
    }
  }

  private void normalizeSlotType(ReportQuery query) {
    if (StringUtils.isBlank(query.getSlotType())) {
      query.setSlotType(SLOT_TYPE_HOUR);
    }
  }

  private void validateMetric(String metric, Set<String> allowedMetrics, String message) {
    if (!allowedMetrics.contains(metric)) {
      throw new ServiceException(message);
    }
  }
}
