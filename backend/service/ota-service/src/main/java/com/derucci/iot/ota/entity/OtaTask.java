package com.derucci.iot.ota.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/** OTA升级任务实体，记录一次固件升级任务的执行信息和进度 */
@Entity
@Table(name = "t_ota_task")
public class OtaTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 任务ID（主键，自增） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 任务名称 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 关联的固件ID */
    @Column(name = "firmware_id", nullable = false)
    private Long firmwareId;

    /** 目标升级版本号 */
    @Column(name = "target_version", length = 50)
    private String targetVersion;

    /** 任务状态（默认PENDING） */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OtaTaskStatus status = OtaTaskStatus.PENDING;

    /** 目标设备总数 */
    @Column(name = "total_count")
    private Integer totalCount;

    /** 升级成功设备数 */
    @Column(name = "success_count")
    private Integer successCount;

    /** 升级失败设备数 */
    @Column(name = "fail_count")
    private Integer failCount;

    /** 任务开始执行时间 */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /** 任务结束时间 */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /** 创建时间（不可更新） */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    /** 最后更新时间 */
    @Column(nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getFirmwareId() { return firmwareId; }
    public void setFirmwareId(Long firmwareId) { this.firmwareId = firmwareId; }

    public String getTargetVersion() { return targetVersion; }
    public void setTargetVersion(String targetVersion) { this.targetVersion = targetVersion; }

    public OtaTaskStatus getStatus() { return status; }
    public void setStatus(OtaTaskStatus status) { this.status = status; }

    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }

    public Integer getSuccessCount() { return successCount; }
    public void setSuccessCount(Integer successCount) { this.successCount = successCount; }

    public Integer getFailCount() { return failCount; }
    public void setFailCount(Integer failCount) { this.failCount = failCount; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
