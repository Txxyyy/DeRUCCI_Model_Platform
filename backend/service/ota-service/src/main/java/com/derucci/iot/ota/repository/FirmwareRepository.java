package com.derucci.iot.ota.repository;

import com.derucci.iot.ota.entity.Firmware;
import com.derucci.iot.ota.entity.FirmwareStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FirmwareRepository extends JpaRepository<Firmware, Long> {

    Optional<Firmware> findByProductIdAndVersion(Long productId, String version);

    List<Firmware> findByProductId(Long productId);

    List<Firmware> findByStatus(FirmwareStatus status);
}
