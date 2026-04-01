package com.derucci.iot.device.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.common.auth.RequirePermission;
import com.derucci.iot.common.auth.AuthContext;
import com.derucci.iot.common.auth.AuthUser;
import com.derucci.iot.device.entity.Device;
import com.derucci.iot.device.entity.DeviceStatus;
import com.derucci.iot.device.service.DeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 设备管理控制器，提供设备CRUD及状态变更的REST接口 */
@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 根据ID获取设备详情
     *
     * @param id 设备ID
     * @return 设备详情，不存在时返回404
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "DEVICE", access = "R")
    public Result<Device> getDevice(@PathVariable Long id) {
        return deviceService.findById(id)
                .map(d -> {
                    checkDeviceProductScope(d);
                    return Result.success(d);
                })
                .orElse(Result.notFound("设备不存在"));
    }

    /**
     * 根据设备Key获取设备详情
     *
     * @param deviceKey 设备唯一标识Key
     * @return 设备详情，不存在时返回404
     */
    @GetMapping("/key/{deviceKey}")
    @RequirePermission(module = "DEVICE", access = "R")
    public Result<Device> getDeviceByKey(@PathVariable String deviceKey) {
        return deviceService.findByDeviceKey(deviceKey)
                .map(d -> {
                    checkDeviceProductScope(d);
                    return Result.success(d);
                })
                .orElse(Result.notFound("设备不存在"));
    }

    /**
     * 根据序列号获取设备详情
     *
     * @param serialNumber 设备序列号（SN）
     * @return 设备详情，不存在时返回404
     */
    @GetMapping("/sn/{serialNumber}")
    @RequirePermission(module = "DEVICE", access = "R")
    public Result<Device> getDeviceBySn(@PathVariable String serialNumber) {
        return deviceService.findBySerialNumber(serialNumber)
                .map(d -> {
                    checkDeviceProductScope(d);
                    return Result.success(d);
                })
                .orElse(Result.notFound("设备不存在"));
    }

    /**
     * 查询设备列表，支持按产品ID、状态、设备类型筛选
     *
     * @param productId 产品ID（可选）
     * @param status 设备状态（可选）
     * @param deviceType 设备类型（可选，如 TEST、PRODUCTION）
     * @return 符合筛选条件的设备列表
     */
    @GetMapping
    @RequirePermission(module = "DEVICE", access = "R")
    public Result<List<Device>> getAllDevices(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) DeviceStatus status,
            @RequestParam(required = false) String deviceType) {
        List<Device> devices;
        if (productId != null) {
            devices = deviceService.findByProductId(productId);
        } else if (status != null) {
            devices = deviceService.findByStatus(status);
        } else if (deviceType != null && !deviceType.isBlank()) {
            devices = deviceService.findByDeviceType(deviceType);
        } else {
            devices = deviceService.findAll();
        }
        return Result.success(filterByProductScope(devices));
    }

    /**
     * 注册新设备
     *
     * @param device 待注册的设备信息
     * @return 注册成功的设备实体
     */
    @PostMapping
    @RequirePermission(module = "DEVICE", access = "RW")
    public Result<Device> createDevice(@RequestBody Device device) {
        checkProductScope(device.getProductId());
        Device created = deviceService.create(device);
        return Result.success(created);
    }

    /**
     * 更新设备信息
     *
     * @param id 待更新的设备ID
     * @param device 包含更新字段的设备对象
     * @return 更新后的设备实体
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "DEVICE", access = "RW")
    public Result<Device> updateDevice(@PathVariable Long id, @RequestBody Device device) {
        deviceService.findById(id).ifPresent(this::checkDeviceProductScope);
        Device updated = deviceService.update(id, device);
        return Result.success(updated);
    }

    /**
     * 删除设备
     *
     * @param id 待删除的设备ID
     * @return 空响应体
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "DEVICE", action = "DELETE")
    public Result<Void> deleteDevice(@PathVariable Long id) {
        deviceService.findById(id).ifPresent(this::checkDeviceProductScope);
        deviceService.delete(id);
        return Result.success();
    }

    /**
     * 将设备标记为上线状态
     *
     * @param id 设备ID
     * @return 状态变更后的设备实体
     */
    @PutMapping("/{id}/online")
    @RequirePermission(module = "DEVICE", access = "RW")
    public Result<Device> onlineDevice(@PathVariable Long id) {
        deviceService.findById(id).ifPresent(this::checkDeviceProductScope);
        Device device = deviceService.online(id);
        return Result.success(device);
    }

    /**
     * 将设备标记为离线状态
     *
     * @param id 设备ID
     * @return 状态变更后的设备实体
     */
    @PutMapping("/{id}/offline")
    @RequirePermission(module = "DEVICE", action = "OFFLINE")
    public Result<Device> offlineDevice(@PathVariable Long id) {
        deviceService.findById(id).ifPresent(this::checkDeviceProductScope);
        Device device = deviceService.offline(id);
        return Result.success(device);
    }

    /**
     * 更新设备属性快照
     *
     * @param id 设备ID
     * @param properties 新的属性快照JSON字符串
     * @return 更新后的设备实体
     */
    @PutMapping("/{id}/properties")
    @RequirePermission(module = "DEVICE", access = "RW")
    public Result<Device> updateProperties(@PathVariable Long id, @RequestBody String properties) {
        deviceService.findById(id).ifPresent(this::checkDeviceProductScope);
        Device device = deviceService.updateProperties(id, properties);
        return Result.success(device);
    }

    // --- 产品范围过滤 ---

    private List<Device> filterByProductScope(List<Device> devices) {
        AuthUser currentUser = AuthContext.getUser();
        if (currentUser != null && currentUser.isDeveloper() && currentUser.getAssignedProductIds() != null) {
            return devices.stream()
                    .filter(d -> currentUser.getAssignedProductIds().contains(d.getProductId()))
                    .collect(java.util.stream.Collectors.toList());
        }
        return devices;
    }

    private void checkProductScope(Long productId) {
        AuthUser user = AuthContext.getUser();
        if (user != null && !user.hasProductAccess(productId)) {
            throw new SecurityException("无权访问该产品下的设备");
        }
    }

    private void checkDeviceProductScope(Device device) {
        if (device.getProductId() != null) {
            checkProductScope(device.getProductId());
        }
    }
}