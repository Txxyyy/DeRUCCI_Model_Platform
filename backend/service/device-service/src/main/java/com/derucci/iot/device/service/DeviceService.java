package com.derucci.iot.device.service;

import com.derucci.iot.common.core.exception.BusinessException;
import com.derucci.iot.device.entity.Device;
import com.derucci.iot.device.entity.DeviceStatus;
import com.derucci.iot.device.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Optional<Device> findById(Long id) {
        return deviceRepository.findById(id);
    }

    public Optional<Device> findByDeviceKey(String deviceKey) {
        return deviceRepository.findByDeviceKey(deviceKey);
    }

    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    public List<Device> findByProductId(Long productId) {
        return deviceRepository.findByProductId(productId);
    }

    public List<Device> findByStatus(DeviceStatus status) {
        return deviceRepository.findByStatus(status);
    }

    public List<Device> findByDeviceType(String deviceType) {
        return deviceRepository.findByDeviceType(deviceType);
    }

    @Transactional
    public Device create(Device device) {
        if (deviceRepository.existsByDeviceKey(device.getDeviceKey())) {
            throw BusinessException.parameterError("设备Key已存在");
        }
        if (device.getStatus() == null) {
            device.setStatus(DeviceStatus.UNACTIVE);
        }
        String deviceType = device.getDeviceType() == null ? "PRODUCTION" : device.getDeviceType();
        device.setDeviceType(deviceType);
        if ("TEST".equals(deviceType) && device.getProductId() != null) {
            long testCount = deviceRepository.countByProductIdAndDeviceType(device.getProductId(), "TEST");
            if (testCount >= 10) {
                throw BusinessException.parameterError("该产品测试设备已达上限（最多10台）");
            }
        }
        return deviceRepository.save(device);
    }

    @Transactional
    public Device update(Long id, Device updateData) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("设备不存在"));

        if (updateData.getName() != null)         device.setName(updateData.getName());
        if (updateData.getTags() != null)         device.setTags(updateData.getTags());
        if (updateData.getProperties() != null)   device.setProperties(updateData.getProperties());
        if (updateData.getSerialNumber() != null) device.setSerialNumber(updateData.getSerialNumber());
        if (updateData.getDeviceKey() != null)    device.setDeviceKey(updateData.getDeviceKey());
        if (updateData.getProductId() != null)    device.setProductId(updateData.getProductId());
        if (updateData.getProductName() != null)  device.setProductName(updateData.getProductName());
        if (updateData.getThingModelId() != null) device.setThingModelId(updateData.getThingModelId());

        return deviceRepository.save(device);
    }

    @Transactional
    public void delete(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw BusinessException.notFound("设备不存在");
        }
        deviceRepository.deleteById(id);
    }

    @Transactional
    public Device online(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("设备不存在"));
        device.setStatus(DeviceStatus.ONLINE);
        device.setLastOnlineTime(LocalDateTime.now());
        return deviceRepository.save(device);
    }

    @Transactional
    public Device offline(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("设备不存在"));
        device.setStatus(DeviceStatus.OFFLINE);
        device.setLastOfflineTime(LocalDateTime.now());
        return deviceRepository.save(device);
    }

    @Transactional
    public Device updateProperties(Long id, String properties) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("设备不存在"));
        device.setProperties(properties);
        return deviceRepository.save(device);
    }
}
