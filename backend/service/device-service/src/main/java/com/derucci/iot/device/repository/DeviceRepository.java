package com.derucci.iot.device.repository;

import com.derucci.iot.device.entity.Device;
import com.derucci.iot.device.entity.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** 设备数据访问层，提供设备的持久化查询操作 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    /**
     * 根据设备Key查找设备
     *
     * @param deviceKey 设备唯一标识Key
     * @return 匹配的设备，不存在时返回空Optional
     */
    Optional<Device> findByDeviceKey(String deviceKey);

    /**
     * 判断指定设备Key是否已存在
     *
     * @param deviceKey 设备唯一标识Key
     * @return 存在返回true，否则false
     */
    boolean existsByDeviceKey(String deviceKey);

    /**
     * 查询指定产品下的所有设备
     *
     * @param productId 产品ID
     * @return 该产品关联的设备列表
     */
    List<Device> findByProductId(Long productId);

    /**
     * 按设备状态查询设备列表
     *
     * @param status 设备状态（UNACTIVE/ONLINE/OFFLINE/DISABLED）
     * @return 符合状态条件的设备列表
     */
    List<Device> findByStatus(DeviceStatus status);

    /**
     * 按产品ID和状态联合查询设备
     *
     * @param productId 产品ID
     * @param status 设备状态
     * @return 同时满足产品和状态条件的设备列表
     */
    List<Device> findByProductIdAndStatus(Long productId, DeviceStatus status);

    /**
     * 统计指定产品下某类型设备的数量（用于测试设备上限校验）
     *
     * @param productId 产品ID
     * @param deviceType 设备类型（如 TEST、PRODUCTION）
     * @return 符合条件的设备数量
     */
    long countByProductIdAndDeviceType(Long productId, String deviceType);

    /**
     * 按设备类型查询设备列表
     *
     * @param deviceType 设备类型（如 TEST、PRODUCTION）
     * @return 该类型下的设备列表
     */
    List<Device> findByDeviceType(String deviceType);

    /**
     * 根据序列号查找设备
     *
     * @param serialNumber 设备序列号（SN）
     * @return 匹配的设备，不存在时返回空Optional
     */
    Optional<Device> findBySerialNumber(String serialNumber);
}
