package com.derucci.iot.ota.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/** 固件实体，存储OTA升级所需的固件包信息 */
@Entity
@Table(name = "t_firmware")
public class Firmware implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 固件ID（主键，自增） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 固件名称（最长100字符） */
    @Column(nullable = false, length = 100)
    private String name;

    /** 固件版本号（如 1.0.0，最长50字符） */
    @Column(nullable = false, length = 50)
    private String version;

    /** 所属产品ID */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** 固件包下载地址 */
    @Column(name = "file_url", length = 500)
    private String fileUrl;

    /** 固件包大小（字节） */
    @Column(name = "file_size")
    private Long fileSize;

    /** 固件包MD5校验值，用于完整性验证 */
    @Column(name = "file_md5", length = 64)
    private String fileMd5;

    /** 固件描述信息 */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** 固件状态（默认DRAFT） */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FirmwareStatus status = FirmwareStatus.DRAFT;

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

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFileMd5() { return fileMd5; }
    public void setFileMd5(String fileMd5) { this.fileMd5 = fileMd5; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public FirmwareStatus getStatus() { return status; }
    public void setStatus(FirmwareStatus status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
