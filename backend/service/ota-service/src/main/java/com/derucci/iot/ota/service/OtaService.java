package com.derucci.iot.ota.service;

import com.derucci.iot.common.core.exception.BusinessException;
import com.derucci.iot.ota.entity.Firmware;
import com.derucci.iot.ota.entity.FirmwareStatus;
import com.derucci.iot.ota.entity.OtaTask;
import com.derucci.iot.ota.entity.OtaTaskStatus;
import com.derucci.iot.ota.repository.FirmwareRepository;
import com.derucci.iot.ota.repository.OtaTaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OtaService {

    private final FirmwareRepository firmwareRepository;
    private final OtaTaskRepository otaTaskRepository;

    public OtaService(FirmwareRepository firmwareRepository, OtaTaskRepository otaTaskRepository) {
        this.firmwareRepository = firmwareRepository;
        this.otaTaskRepository = otaTaskRepository;
    }

    // Firmware operations
    public Optional<Firmware> findFirmwareById(Long id) {
        return firmwareRepository.findById(id);
    }

    public List<Firmware> findAllFirmwares() {
        return firmwareRepository.findAll();
    }

    public List<Firmware> findFirmwaresByProductId(Long productId) {
        return firmwareRepository.findByProductId(productId);
    }

    @Transactional
    public Firmware createFirmware(Firmware firmware) {
        if (firmware.getStatus() == null) {
            firmware.setStatus(FirmwareStatus.DRAFT);
        }
        return firmwareRepository.save(firmware);
    }

    @Transactional
    public Firmware publishFirmware(Long id) {
        Firmware firmware = firmwareRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("固件不存在"));
        firmware.setStatus(FirmwareStatus.PUBLISHED);
        return firmwareRepository.save(firmware);
    }

    // OTA Task operations
    public Optional<OtaTask> findTaskById(Long id) {
        return otaTaskRepository.findById(id);
    }

    public List<OtaTask> findAllTasks() {
        return otaTaskRepository.findAll();
    }

    public List<OtaTask> findTasksByFirmwareId(Long firmwareId) {
        return otaTaskRepository.findByFirmwareId(firmwareId);
    }

    @Transactional
    public OtaTask createTask(OtaTask task) {
        if (task.getStatus() == null) {
            task.setStatus(OtaTaskStatus.PENDING);
        }
        if (task.getTotalCount() == null) {
            task.setTotalCount(0);
        }
        if (task.getSuccessCount() == null) {
            task.setSuccessCount(0);
        }
        if (task.getFailCount() == null) {
            task.setFailCount(0);
        }
        return otaTaskRepository.save(task);
    }

    @Transactional
    public OtaTask startTask(Long id) {
        OtaTask task = otaTaskRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("OTA任务不存在"));
        task.setStatus(OtaTaskStatus.RUNNING);
        task.setStartTime(LocalDateTime.now());
        return otaTaskRepository.save(task);
    }

    @Transactional
    public OtaTask completeTask(Long id) {
        OtaTask task = otaTaskRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("OTA任务不存在"));
        task.setStatus(OtaTaskStatus.COMPLETED);
        task.setEndTime(LocalDateTime.now());
        return otaTaskRepository.save(task);
    }

    @Transactional
    public OtaTask cancelTask(Long id) {
        OtaTask task = otaTaskRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("OTA任务不存在"));
        task.setStatus(OtaTaskStatus.CANCELLED);
        return otaTaskRepository.save(task);
    }

    @Transactional
    public OtaTask updateTaskProgress(Long id, boolean success) {
        OtaTask task = otaTaskRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("OTA任务不存在"));
        if (success) {
            task.setSuccessCount(task.getSuccessCount() + 1);
        } else {
            task.setFailCount(task.getFailCount() + 1);
        }
        return otaTaskRepository.save(task);
    }
}
