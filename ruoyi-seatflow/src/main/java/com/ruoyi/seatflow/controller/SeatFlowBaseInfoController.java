package com.ruoyi.seatflow.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.seatflow.domain.base.*;
import com.ruoyi.seatflow.service.ISeatFlowBaseInfoService;

/** 校区、楼栋、楼层、自习室和座位管理。 */
@RestController
@RequestMapping("/seatflow/base")
public class SeatFlowBaseInfoController extends BaseController
{
    @Autowired
    private ISeatFlowBaseInfoService service;

    @PreAuthorize("@ss.hasPermi('seatflow:base:campus:list')")
    @GetMapping("/campus/list")
    public TableDataInfo campusList(SeatFlowCampus query) { startPage(); return getDataTable(service.selectCampusList(query)); }
    @GetMapping("/campus/options")
    public AjaxResult campusOptions() { return success(service.selectCampusList(new SeatFlowCampus())); }
    @GetMapping("/campus/{id}") public AjaxResult campus(@PathVariable Long id) { return success(service.selectCampusById(id)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:campus:add')") @Log(title="校区管理", businessType=BusinessType.INSERT)
    @PostMapping("/campus") public AjaxResult addCampus(@Validated @RequestBody SeatFlowCampus e) { e.setCreateBy(getUsername()); return toAjax(service.saveCampus(e)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:campus:edit')") @Log(title="校区管理", businessType=BusinessType.UPDATE)
    @PutMapping("/campus") public AjaxResult editCampus(@Validated @RequestBody SeatFlowCampus e) { e.setUpdateBy(getUsername()); return toAjax(service.saveCampus(e)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:campus:remove')") @Log(title="校区管理", businessType=BusinessType.DELETE)
    @DeleteMapping("/campus/{id}") public AjaxResult delCampus(@PathVariable Long id) { return toAjax(service.deleteCampus(id)); }

    @PreAuthorize("@ss.hasPermi('seatflow:base:building:list')") @GetMapping("/building/list")
    public TableDataInfo buildingList(SeatFlowBuilding q) { startPage(); return getDataTable(service.selectBuildingList(q)); }
    @GetMapping("/building/options") public AjaxResult buildingOptions(SeatFlowBuilding q) { return success(service.selectBuildingList(q)); }
    @GetMapping("/building/{id}") public AjaxResult building(@PathVariable Long id) { return success(service.selectBuildingById(id)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:building:add')") @Log(title="楼栋管理", businessType=BusinessType.INSERT)
    @PostMapping("/building") public AjaxResult addBuilding(@Validated @RequestBody SeatFlowBuilding e) { e.setCreateBy(getUsername()); return toAjax(service.saveBuilding(e)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:building:edit')") @Log(title="楼栋管理", businessType=BusinessType.UPDATE)
    @PutMapping("/building") public AjaxResult editBuilding(@Validated @RequestBody SeatFlowBuilding e) { e.setUpdateBy(getUsername()); return toAjax(service.saveBuilding(e)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:building:remove')") @Log(title="楼栋管理", businessType=BusinessType.DELETE)
    @DeleteMapping("/building/{id}") public AjaxResult delBuilding(@PathVariable Long id) { return toAjax(service.deleteBuilding(id)); }

    @PreAuthorize("@ss.hasPermi('seatflow:base:floor:list')") @GetMapping("/floor/list")
    public TableDataInfo floorList(SeatFlowFloor q) { startPage(); return getDataTable(service.selectFloorList(q)); }
    @GetMapping("/floor/options") public AjaxResult floorOptions(SeatFlowFloor q) { return success(service.selectFloorList(q)); }
    @GetMapping("/floor/{id}") public AjaxResult floor(@PathVariable Long id) { return success(service.selectFloorById(id)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:floor:add')") @Log(title="楼层管理", businessType=BusinessType.INSERT)
    @PostMapping("/floor") public AjaxResult addFloor(@Validated @RequestBody SeatFlowFloor e) { e.setCreateBy(getUsername()); return toAjax(service.saveFloor(e)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:floor:edit')") @Log(title="楼层管理", businessType=BusinessType.UPDATE)
    @PutMapping("/floor") public AjaxResult editFloor(@Validated @RequestBody SeatFlowFloor e) { e.setUpdateBy(getUsername()); return toAjax(service.saveFloor(e)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:floor:remove')") @Log(title="楼层管理", businessType=BusinessType.DELETE)
    @DeleteMapping("/floor/{id}") public AjaxResult delFloor(@PathVariable Long id) { return toAjax(service.deleteFloor(id)); }

    @PreAuthorize("@ss.hasPermi('seatflow:base:room:list')") @GetMapping("/room/list")
    public TableDataInfo roomList(SeatFlowRoom q) { startPage(); return getDataTable(service.selectRoomList(q)); }
    @GetMapping("/room/options") public AjaxResult roomOptions(SeatFlowRoom q) { return success(service.selectRoomList(q)); }
    @GetMapping("/room/{id}") public AjaxResult room(@PathVariable Long id) { return success(service.selectRoomById(id)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:room:add')") @Log(title="自习室管理", businessType=BusinessType.INSERT)
    @PostMapping("/room") public AjaxResult addRoom(@Validated @RequestBody SeatFlowRoom e) { e.setCreateBy(getUsername()); return toAjax(service.saveRoom(e)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:room:edit')") @Log(title="自习室管理", businessType=BusinessType.UPDATE)
    @PutMapping("/room") public AjaxResult editRoom(@Validated @RequestBody SeatFlowRoom e) { e.setUpdateBy(getUsername()); return toAjax(service.saveRoom(e)); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:room:remove')") @Log(title="自习室管理", businessType=BusinessType.DELETE)
    @DeleteMapping("/room/{id}") public AjaxResult delRoom(@PathVariable Long id) { return toAjax(service.deleteRoom(id)); }

    @PreAuthorize("@ss.hasPermi('seatflow:base:seat:list')") @GetMapping("/seat/list")
    public TableDataInfo seatList(SeatFlowSeat q) { startPage(); List<SeatFlowSeat> list = service.selectSeatList(q); return getDataTable(list); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:seat:generate')") @Log(title="座位管理", businessType=BusinessType.INSERT)
    @PostMapping("/room/{roomId}/seats/generate") public AjaxResult generate(@PathVariable Long roomId) { return toAjax(service.generateSeats(roomId, getUsername())); }
    @PreAuthorize("@ss.hasPermi('seatflow:base:seat:edit')") @Log(title="座位管理", businessType=BusinessType.UPDATE)
    @PutMapping("/seat/{seatId}/status/{status}") public AjaxResult status(@PathVariable Long seatId, @PathVariable String status) { return toAjax(service.updateSeatStatus(seatId, status, getUsername())); }
}
