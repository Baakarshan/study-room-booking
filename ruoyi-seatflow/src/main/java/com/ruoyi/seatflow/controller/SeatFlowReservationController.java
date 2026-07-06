package com.ruoyi.seatflow.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.seatflow.domain.dto.MyReservationQuery;
import com.ruoyi.seatflow.domain.dto.ReservationCreateRequest;
import com.ruoyi.seatflow.domain.dto.ReservationManageQuery;
import com.ruoyi.seatflow.domain.dto.SeatAvailabilityQuery;
import com.ruoyi.seatflow.service.ISeatFlowReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "座位预约")
@RestController
@RequestMapping("/seatflow/reservation")
public class SeatFlowReservationController extends BaseController {
  private final ISeatFlowReservationService reservationService;

  public SeatFlowReservationController(ISeatFlowReservationService reservationService) {
    this.reservationService = reservationService;
  }

  @PreAuthorize("@ss.hasPermi('seatflow:reservation:create')")
  @GetMapping("/campuses")
  public AjaxResult campuses() {
    return AjaxResult.success(reservationService.selectCampuses());
  }

  @PreAuthorize("@ss.hasPermi('seatflow:reservation:create')")
  @GetMapping("/buildings")
  public AjaxResult buildings(@RequestParam Long campusId) {
    return AjaxResult.success(reservationService.selectBuildings(campusId));
  }

  @PreAuthorize("@ss.hasPermi('seatflow:reservation:create')")
  @GetMapping("/floors")
  public AjaxResult floors(@RequestParam Long buildingId) {
    return AjaxResult.success(reservationService.selectFloors(buildingId));
  }

  @PreAuthorize("@ss.hasPermi('seatflow:reservation:create')")
  @GetMapping("/rooms")
  public AjaxResult rooms(@RequestParam Long floorId) {
    return AjaxResult.success(reservationService.selectRooms(floorId));
  }

  @Operation(summary = "查询时间段内座位状态")
  @PreAuthorize("@ss.hasPermi('seatflow:reservation:create')")
  @GetMapping("/seats")
  public AjaxResult seats(@Valid @ParameterObject SeatAvailabilityQuery query) {
    return AjaxResult.success(reservationService.selectSeatAvailability(query));
  }

  @Operation(summary = "提交预约")
  @PreAuthorize("@ss.hasPermi('seatflow:reservation:create')")
  @PostMapping
  public AjaxResult create(@Valid @RequestBody ReservationCreateRequest request) {
    return toAjax(
        reservationService.createReservation(
            SecurityUtils.getUserId(), SecurityUtils.getUsername(), request));
  }

  @Operation(summary = "查询我的预约")
  @PreAuthorize("@ss.hasPermi('seatflow:reservation:mine')")
  @GetMapping("/mine")
  public TableDataInfo mine(@ParameterObject MyReservationQuery query) {
    query.setUserId(SecurityUtils.getUserId());
    startPage();
    return getDataTable(reservationService.selectMyReservations(query));
  }

  @Operation(summary = "管理端分页查询全部预约")
  @PreAuthorize("@ss.hasPermi('seatflow:reservation:list')")
  @GetMapping("/manage")
  public TableDataInfo manage(@ParameterObject ReservationManageQuery query) {
    startPage();
    return getDataTable(reservationService.selectManagedReservations(query));
  }

  @Operation(summary = "取消我的预约")
  @PreAuthorize("@ss.hasPermi('seatflow:reservation:mine')")
  @DeleteMapping("/{reservationId}")
  public AjaxResult cancel(@PathVariable Long reservationId) {
    return toAjax(
        reservationService.cancelReservation(
            reservationId, SecurityUtils.getUserId(), SecurityUtils.getUsername()));
  }
}
