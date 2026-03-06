package com.derucci.iot.device.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.device.entity.Device;
import com.derucci.iot.device.entity.DeviceStatus;
import com.derucci.iot.device.service.DeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/{id}")
    public Result<Device> getDevice(@PathVariable Long id) {
        return deviceService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("设备不存在"));
    }

    @GetMapping("/key/{deviceKey}")
    public Result<Device> getDeviceByKey(@PathVariable String deviceKey) {
        return deviceService.findByDeviceKey(deviceKey)
                .map(Result::success)
                .orElse(Result.notFound("设备不存在"));
    }

    @GetMapping
    public Result<List<Device>> getAllDevices(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) DeviceStatus status) {
        List<Device> devices;
        if (productId != null) {
            devices = deviceService.findByProductId(productId);
        } else if (status != null) {
            devices = deviceService.findByStatus(status);
        } else {
            devices = deviceService.findAll();
        }
        return Result.success(devices);
    }

    @PostMapping
    public Result<Device> createDevice(@RequestBody Device device) {
        Device created = deviceService.create(device);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        Device updated = deviceService.update(id, device);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDevice(@PathVariable Long id) {
        deviceService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/online")
    public Result<Device> onlineDevice(@PathVariable Long id) {
        Device device = deviceService.online(id);
        return Result.success(device);
    }

    @PutMapping("/{id}/offline")
    public Result<Device> offlineDevice(@PathVariable Long id) {
        Device device = deviceService.offline(id);
        return Result.success(device);
    }

    @PutMapping("/{id}/properties")
    public Result<Device> updateProperties(@PathVariable Long id, @RequestBody String properties) {
        Device device = deviceService.updateProperties(id, properties);
        return Result.success(device);
    }
}
