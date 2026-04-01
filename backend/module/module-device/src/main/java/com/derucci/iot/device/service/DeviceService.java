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

/** 设备业务服务，封装设备注册、状态流转、属性更新等核心逻辑 */
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    /**
     * 根据ID查找设备
     *
     * @param id 设备ID
     * @return 匹配的设备，不存在时返回空Optional
     */
    public Optional<Device> findById(Long id) {
        return deviceRepository.findById(id);
    }

    /**
     * 根据设备Key查找设备
     *
     * @param deviceKey 设备唯一标识Key
     * @return 匹配的设备，不存在时返回空Optional
     */
    public Optional<Device> findByDeviceKey(String deviceKey) {
        return deviceRepository.findByDeviceKey(deviceKey);
    }

    /**
     * 根据序列号查找设备
     *
     * @param serialNumber 设备序列号（SN）
     * @return 匹配的设备，不存在时返回空Optional
     */
    public Optional<Device> findBySerialNumber(String serialNumber) {
        return deviceRepository.findBySerialNumber(serialNumber);
    }

    /**
     * 查询所有设备
     *
     * @return 全部设备列表
     */
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    /**
     * 查询指定产品下的所有设备
     *
     * @param productId 产品ID
     * @return 该产品关联的设备列表
     */
    public List<Device> findByProductId(Long productId) {
        return deviceRepository.findByProductId(productId);
    }

    /**
     * 按状态查询设备列表
     *
     * @param status 设备状态（UNACTIVE/ONLINE/OFFLINE/DISABLED）
     * @return 符合状态条件的设备列表
     */
    public List<Device> findByStatus(DeviceStatus status) {
        return deviceRepository.findByStatus(status);
    }

    /**
     * 按设备类型查询设备列表
     *
     * @param deviceType 设备类型（如 TEST、PRODUCTION）
     * @return 该类型下的设备列表
     */
    public List<Device> findByDeviceType(String deviceType) {
        return deviceRepository.findByDeviceType(deviceType);
    }

    /**
     * 注册新设备，校验Key唯一性并限制测试设备数量（每产品最多10台）
     *
     * @param device 待注册的设备信息，需包含 deviceKey
     * @return 注册成功的设备实体（含生成的ID）
     */
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

    /**
     * 更新设备信息，仅覆盖非null字段
     *
     * @param id 待更新的设备ID
     * @param updateData 包含待更新字段的设备对象，null字段不覆盖
     * @return 更新后的设备实体
     */
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

    /**
     * 删除设备
     *
     * @param id 待删除的设备ID
     */
    @Transactional
    public void delete(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw BusinessException.notFound("设备不存在");
        }
        deviceRepository.deleteById(id);
    }

    /**
     * 将设备置为上线状态并记录上线时间
     *
     * @param id 设备ID
     * @return 状态变更后的设备实体
     */
    @Transactional
    public Device online(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("设备不存在"));
        device.setStatus(DeviceStatus.ONLINE);
        device.setLastOnlineTime(LocalDateTime.now());
        return deviceRepository.save(device);
    }

    /**
     * 将设备置为离线状态并记录离线时间
     *
     * @param id 设备ID
     * @return 状态变更后的设备实体
     */
    @Transactional
    public Device offline(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("设备不存在"));
        device.setStatus(DeviceStatus.OFFLINE);
        device.setLastOfflineTime(LocalDateTime.now());
        return deviceRepository.save(device);
    }

    /**
     * 更新设备属性快照（覆盖写入）
     *
     * @param id 设备ID
     * @param properties 新的属性快照JSON字符串
     * @return 更新后的设备实体
     */
    @Transactional
    public Device updateProperties(Long id, String properties) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("设备不存在"));
        device.setProperties(properties);
        return deviceRepository.save(device);
    }
}
