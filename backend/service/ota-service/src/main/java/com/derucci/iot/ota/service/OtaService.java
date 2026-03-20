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

/** OTA业务服务，封装固件管理和升级任务的核心逻辑 */
@Service
public class OtaService {

    private final FirmwareRepository firmwareRepository;
    private final OtaTaskRepository otaTaskRepository;

    public OtaService(FirmwareRepository firmwareRepository, OtaTaskRepository otaTaskRepository) {
        this.firmwareRepository = firmwareRepository;
        this.otaTaskRepository = otaTaskRepository;
    }

    // Firmware operations

    /**
     * 根据ID查找固件
     *
     * @param id 固件ID
     * @return 匹配的固件，不存在时返回空 Optional
     */
    public Optional<Firmware> findFirmwareById(Long id) {
        return firmwareRepository.findById(id);
    }

    /**
     * 查询所有固件
     *
     * @return 全部固件列表
     */
    public List<Firmware> findAllFirmwares() {
        return firmwareRepository.findAll();
    }

    /**
     * 查询指定产品下的固件列表
     *
     * @param productId 产品ID
     * @return 该产品关联的固件列表
     */
    public List<Firmware> findFirmwaresByProductId(Long productId) {
        return firmwareRepository.findByProductId(productId);
    }

    /**
     * 创建固件，默认状态为DRAFT
     *
     * @param firmware 固件信息，需包含 productId、version、downloadUrl 等必填字段
     * @return 持久化后的固件实体（含生成的ID，状态默认为 DRAFT）
     */
    @Transactional
    public Firmware createFirmware(Firmware firmware) {
        if (firmware.getStatus() == null) {
            firmware.setStatus(FirmwareStatus.DRAFT);
        }
        return firmwareRepository.save(firmware);
    }

    /**
     * 发布固件，将状态变更为PUBLISHED
     *
     * @param id 固件ID
     * @return 状态已更新为 PUBLISHED 的固件实体
     * @throws BusinessException 固件不存在时抛出 404 异常
     */
    @Transactional
    public Firmware publishFirmware(Long id) {
        Firmware firmware = firmwareRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("固件不存在"));
        firmware.setStatus(FirmwareStatus.PUBLISHED);
        return firmwareRepository.save(firmware);
    }

    // OTA Task operations

    /**
     * 根据ID查找OTA任务
     *
     * @param id 任务ID
     * @return 匹配的任务，不存在时返回空 Optional
     */
    public Optional<OtaTask> findTaskById(Long id) {
        return otaTaskRepository.findById(id);
    }

    /**
     * 查询所有OTA任务
     *
     * @return 全部任务列表
     */
    public List<OtaTask> findAllTasks() {
        return otaTaskRepository.findAll();
    }

    /**
     * 查询指定固件关联的任务列表
     *
     * @param firmwareId 固件ID
     * @return 该固件关联的任务列表
     */
    public List<OtaTask> findTasksByFirmwareId(Long firmwareId) {
        return otaTaskRepository.findByFirmwareId(firmwareId);
    }

    /**
     * 创建OTA任务，初始化状态和计数器
     *
     * @param task 任务信息，需包含 firmwareId 和目标设备列表
     * @return 创建成功的任务实体（含生成的ID，初始状态为 PENDING，计数器归零）
     */
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

    /**
     * 启动OTA任务，记录开始时间
     *
     * @param id 任务ID
     * @return 状态已更新为 RUNNING 的任务实体
     * @throws BusinessException 任务不存在时抛出 404 异常
     */
    @Transactional
    public OtaTask startTask(Long id) {
        OtaTask task = otaTaskRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("OTA任务不存在"));
        task.setStatus(OtaTaskStatus.RUNNING);
        task.setStartTime(LocalDateTime.now());
        return otaTaskRepository.save(task);
    }

    /**
     * 完成OTA任务，记录结束时间
     *
     * @param id 任务ID
     * @return 状态已更新为 COMPLETED 的任务实体
     * @throws BusinessException 任务不存在时抛出 404 异常
     */
    @Transactional
    public OtaTask completeTask(Long id) {
        OtaTask task = otaTaskRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("OTA任务不存在"));
        task.setStatus(OtaTaskStatus.COMPLETED);
        task.setEndTime(LocalDateTime.now());
        return otaTaskRepository.save(task);
    }

    /**
     * 取消OTA任务
     *
     * @param id 任务ID
     * @return 状态已更新为 CANCELLED 的任务实体
     * @throws BusinessException 任务不存在时抛出 404 异常
     */
    @Transactional
    public OtaTask cancelTask(Long id) {
        OtaTask task = otaTaskRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("OTA任务不存在"));
        task.setStatus(OtaTaskStatus.CANCELLED);
        return otaTaskRepository.save(task);
    }

    /**
     * 更新任务进度，根据设备升级结果累加成功或失败计数
     *
     * @param id 任务ID
     * @param success true 表示该设备升级成功，false 表示失败
     * @return 计数器已更新的任务实体
     * @throws BusinessException 任务不存在时抛出 404 异常
     */
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
