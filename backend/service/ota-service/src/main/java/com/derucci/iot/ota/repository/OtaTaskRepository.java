package com.derucci.iot.ota.repository;

import com.derucci.iot.ota.entity.OtaTask;
import com.derucci.iot.ota.entity.OtaTaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** OTA任务数据访问层，提供升级任务的持久化操作 */
@Repository
public interface OtaTaskRepository extends JpaRepository<OtaTask, Long> {

    /**
     * 查询指定固件关联的所有升级任务
     *
     * @param firmwareId 固件ID
     * @return 该固件关联的任务列表，无结果时返回空列表
     */
    List<OtaTask> findByFirmwareId(Long firmwareId);

    /**
     * 按状态查询升级任务列表
     *
     * @param status 任务状态（PENDING / RUNNING / COMPLETED / CANCELLED）
     * @return 符合状态条件的任务列表，无结果时返回空列表
     */
    List<OtaTask> findByStatus(OtaTaskStatus status);
}
