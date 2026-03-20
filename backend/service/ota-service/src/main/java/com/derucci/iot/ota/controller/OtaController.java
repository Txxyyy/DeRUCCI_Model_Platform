package com.derucci.iot.ota.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.common.auth.RequirePermission;
import com.derucci.iot.common.auth.AuthContext;
import com.derucci.iot.common.auth.AuthUser;
import com.derucci.iot.ota.entity.Firmware;
import com.derucci.iot.ota.entity.OtaTask;
import com.derucci.iot.ota.entity.OtaTaskStatus;
import com.derucci.iot.ota.service.OtaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** OTA管理控制器，提供固件管理和升级任务的REST接口 */
@RestController
@RequestMapping("/api/ota")
public class OtaController {

    private final OtaService otaService;

    public OtaController(OtaService otaService) {
        this.otaService = otaService;
    }

    // Firmware APIs

    /**
     * 根据ID获取固件详情
     *
     * @param id 固件ID
     * @return 固件详情，不存在时返回 404
     */
    @GetMapping("/firmwares/{id}")
    @RequirePermission(module = "OTA", access = "R")
    public Result<Firmware> getFirmware(@PathVariable Long id) {
        return otaService.findFirmwareById(id)
                .map(f -> {
                    checkFirmwareScope(f);
                    return Result.success(f);
                })
                .orElse(Result.notFound("固件不存在"));
    }

    /**
     * 查询固件列表，支持按产品ID筛选
     *
     * @param productId 产品ID，为 null 时返回全部固件
     * @return 固件列表
     */
    @GetMapping("/firmwares")
    @RequirePermission(module = "OTA", access = "R")
    public Result<List<Firmware>> getAllFirmwares(@RequestParam(required = false) Long productId) {
        List<Firmware> firmwares = productId != null
                ? otaService.findFirmwaresByProductId(productId)
                : otaService.findAllFirmwares();
        AuthUser currentUser = AuthContext.getUser();
        if (currentUser != null && currentUser.isDeveloper() && currentUser.getAssignedProductIds() != null) {
            firmwares = firmwares.stream()
                    .filter(f -> currentUser.getAssignedProductIds().contains(f.getProductId()))
                    .collect(java.util.stream.Collectors.toList());
        }
        return Result.success(firmwares);
    }

    /**
     * 创建新固件
     *
     * @param firmware 固件信息，需包含 productId、version、downloadUrl 等必填字段
     * @return 创建成功的固件实体
     */
    @PostMapping("/firmwares")
    @RequirePermission(module = "OTA", access = "RW")
    public Result<Firmware> createFirmware(@RequestBody Firmware firmware) {
        checkProductScope(firmware.getProductId());
        Firmware created = otaService.createFirmware(firmware);
        return Result.success(created);
    }

    /**
     * 发布固件，状态变更为PUBLISHED
     *
     * @param id 固件ID
     * @return 状态已更新为 PUBLISHED 的固件实体
     */
    @PutMapping("/firmwares/{id}/publish")
    @RequirePermission(module = "OTA", access = "RW")
    public Result<Firmware> publishFirmware(@PathVariable Long id) {
        otaService.findFirmwareById(id).ifPresent(this::checkFirmwareScope);
        Firmware firmware = otaService.publishFirmware(id);
        return Result.success(firmware);
    }

    // OTA Task APIs

    /**
     * 根据ID获取OTA任务详情
     *
     * @param id 任务ID
     * @return 任务详情，不存在时返回 404
     */
    @GetMapping("/tasks/{id}")
    @RequirePermission(module = "OTA", access = "R")
    public Result<OtaTask> getTask(@PathVariable Long id) {
        return otaService.findTaskById(id)
                .map(Result::success)
                .orElse(Result.notFound("OTA任务不存在"));
    }

    /**
     * 查询OTA任务列表，支持按固件ID筛选
     *
     * @param firmwareId 固件ID，为 null 时返回全部任务
     * @return 任务列表
     */
    @GetMapping("/tasks")
    @RequirePermission(module = "OTA", access = "R")
    public Result<List<OtaTask>> getAllTasks(@RequestParam(required = false) Long firmwareId) {
        List<OtaTask> tasks = firmwareId != null
                ? otaService.findTasksByFirmwareId(firmwareId)
                : otaService.findAllTasks();
        return Result.success(tasks);
    }

    /**
     * 创建新的OTA升级任务
     *
     * @param task 任务信息，需包含 firmwareId 和目标设备列表
     * @return 创建成功的任务实体（初始状态为 PENDING）
     */
    @PostMapping("/tasks")
    @RequirePermission(module = "OTA", access = "RW")
    public Result<OtaTask> createTask(@RequestBody OtaTask task) {
        if (task.getFirmwareId() != null) {
            otaService.findFirmwareById(task.getFirmwareId()).ifPresent(this::checkFirmwareScope);
        }
        OtaTask created = otaService.createTask(task);
        return Result.success(created);
    }

    /**
     * 启动OTA任务，开始向设备推送固件
     *
     * @param id 任务ID
     * @return 状态已更新为 RUNNING 的任务实体
     */
    @PutMapping("/tasks/{id}/start")
    @RequirePermission(module = "OTA", action = "PUSH")
    public Result<OtaTask> startTask(@PathVariable Long id) {
        OtaTask task = otaService.startTask(id);
        return Result.success(task);
    }

    /**
     * 标记OTA任务为已完成
     *
     * @param id 任务ID
     * @return 状态已更新为 COMPLETED 的任务实体
     */
    @PutMapping("/tasks/{id}/complete")
    @RequirePermission(module = "OTA", access = "RW")
    public Result<OtaTask> completeTask(@PathVariable Long id) {
        OtaTask task = otaService.completeTask(id);
        return Result.success(task);
    }

    /**
     * 取消OTA任务
     *
     * @param id 任务ID
     * @return 状态已更新为 CANCELLED 的任务实体
     */
    @PutMapping("/tasks/{id}/cancel")
    @RequirePermission(module = "OTA", access = "RW")
    public Result<OtaTask> cancelTask(@PathVariable Long id) {
        OtaTask task = otaService.cancelTask(id);
        return Result.success(task);
    }

    // --- 产品范围检查 ---

    private void checkProductScope(Long productId) {
        AuthUser user = AuthContext.getUser();
        if (user != null && !user.hasProductAccess(productId)) {
            throw new SecurityException("无权访问该产品的OTA资源");
        }
    }

    private void checkFirmwareScope(Firmware firmware) {
        if (firmware.getProductId() != null) {
            checkProductScope(firmware.getProductId());
        }
    }
}