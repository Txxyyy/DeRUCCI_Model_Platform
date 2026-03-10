package com.derucci.iot.device.repository;

import com.derucci.iot.device.entity.Device;
import com.derucci.iot.device.entity.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceKey(String deviceKey);

    boolean existsByDeviceKey(String deviceKey);

    List<Device> findByProductId(Long productId);

    List<Device> findByStatus(DeviceStatus status);

    List<Device> findByProductIdAndStatus(Long productId, DeviceStatus status);

    long countByProductIdAndDeviceType(Long productId, String deviceType);

    List<Device> findByDeviceType(String deviceType);
}
