package com.ruoyi.seatflow.controller;

import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.seatflow.domain.dto.CheckinRequest;
import com.ruoyi.seatflow.domain.dto.ControlQuery;
import com.ruoyi.seatflow.service.ISeatFlowControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "签到与黑名单管控")
@RestController
@RequestMapping("/seatflow/control")
public class SeatFlowControlController extends BaseController
{
    private final ISeatFlowControlService controlService;

    public SeatFlowControlController(ISeatFlowControlService controlService)
    {
        this.controlService = controlService;
    }

    @PreAuthorize("@ss.hasPermi('seatflow:control:checkin')")
    @Operation(summary = "学生预约签到")
    @PostMapping("/checkin")
    public AjaxResult checkin(@Valid @RequestBody CheckinRequest request)
    {
        controlService.checkin(request.getReservationId(), SecurityUtils.getUserId());
        return AjaxResult.success("签到成功");
    }

    @PreAuthorize("@ss.hasPermi('seatflow:control:checkin')")
    @Operation(summary = "查询本人当前可签到预约")
    @GetMapping("/checkin/available")
    public AjaxResult availableCheckins()
    {
        return AjaxResult.success(controlService.selectAvailableCheckins(SecurityUtils.getUserId()));
    }

    @PreAuthorize("@ss.hasPermi('seatflow:control:checkin')")
    @Operation(summary = "查询本人爽约记录")
    @GetMapping("/violations/mine")
    public AjaxResult myViolations()
    {
        return AjaxResult.success(controlService.selectMyViolations(SecurityUtils.getUserId()));
    }

    @PreAuthorize("@ss.hasPermi('seatflow:control:blacklist:list')")
    @Operation(summary = "管理员查询爽约记录")
    @GetMapping("/violations")
    public TableDataInfo violations(@ParameterObject ControlQuery query)
    {
        startPage();
        return getDataTable(controlService.selectViolations(query));
    }

    @PreAuthorize("@ss.hasPermi('seatflow:control:blacklist:list')")
    @Operation(summary = "管理员查询永久黑名单")
    @GetMapping("/blacklist")
    public TableDataInfo blacklist(@ParameterObject ControlQuery query)
    {
        startPage();
        return getDataTable(controlService.selectBlacklist(query));
    }
}
