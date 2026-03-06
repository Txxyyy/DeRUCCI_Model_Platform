package com.derucci.iot.ota.repository;

import com.derucci.iot.ota.entity.OtaTask;
import com.derucci.iot.ota.entity.OtaTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OtaTaskRepository extends JpaRepository<OtaTask, Long> {

    List<OtaTask> findByFirmwareId(Long firmwareId);

    List<OtaTask> findByStatus(OtaTaskStatus status);
}
