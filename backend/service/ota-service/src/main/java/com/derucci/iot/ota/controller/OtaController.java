package com.derucci.iot.ota.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.ota.entity.Firmware;
import com.derucci.iot.ota.entity.OtaTask;
import com.derucci.iot.ota.entity.OtaTaskStatus;
import com.derucci.iot.ota.service.OtaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ota")
public class OtaController {

    private final OtaService otaService;

    public OtaController(OtaService otaService) {
        this.otaService = otaService;
    }

    // Firmware APIs
    @GetMapping("/firmwares/{id}")
    public Result<Firmware> getFirmware(@PathVariable Long id) {
        return otaService.findFirmwareById(id)
                .map(Result::success)
                .orElse(Result.notFound("固件不存在"));
    }

    @GetMapping("/firmwares")
    public Result<List<Firmware>> getAllFirmwares(@RequestParam(required = false) Long productId) {
        List<Firmware> firmwares = productId != null
                ? otaService.findFirmwaresByProductId(productId)
                : otaService.findAllFirmwares();
        return Result.success(firmwares);
    }

    @PostMapping("/firmwares")
    public Result<Firmware> createFirmware(@RequestBody Firmware firmware) {
        Firmware created = otaService.createFirmware(firmware);
        return Result.success(created);
    }

    @PutMapping("/firmwares/{id}/publish")
    public Result<Firmware> publishFirmware(@PathVariable Long id) {
        Firmware firmware = otaService.publishFirmware(id);
        return Result.success(firmware);
    }

    // OTA Task APIs
    @GetMapping("/tasks/{id}")
    public Result<OtaTask> getTask(@PathVariable Long id) {
        return otaService.findTaskById(id)
                .map(Result::success)
                .orElse(Result.notFound("OTA任务不存在"));
    }

    @GetMapping("/tasks")
    public Result<List<OtaTask>> getAllTasks(@RequestParam(required = false) Long firmwareId) {
        List<OtaTask> tasks = firmwareId != null
                ? otaService.findTasksByFirmwareId(firmwareId)
                : otaService.findAllTasks();
        return Result.success(tasks);
    }

    @PostMapping("/tasks")
    public Result<OtaTask> createTask(@RequestBody OtaTask task) {
        OtaTask created = otaService.createTask(task);
        return Result.success(created);
    }

    @PutMapping("/tasks/{id}/start")
    public Result<OtaTask> startTask(@PathVariable Long id) {
        OtaTask task = otaService.startTask(id);
        return Result.success(task);
    }

    @PutMapping("/tasks/{id}/complete")
    public Result<OtaTask> completeTask(@PathVariable Long id) {
        OtaTask task = otaService.completeTask(id);
        return Result.success(task);
    }

    @PutMapping("/tasks/{id}/cancel")
    public Result<OtaTask> cancelTask(@PathVariable Long id) {
        OtaTask task = otaService.cancelTask(id);
        return Result.success(task);
    }
}
