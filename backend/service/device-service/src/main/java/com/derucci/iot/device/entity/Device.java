package com.derucci.iot.device.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/** 设备实体，对应物理IoT设备的数据模型 */
@Entity
@Table(name = "t_device")
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 设备主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 设备名称（最长100字符） */
    @Column(nullable = false, length = 100)
    private String name;

    /** 设备唯一标识Key（全局唯一，最长50字符） */
    @Column(nullable = false, unique = true, length = 50)
    private String deviceKey;

    /** 设备序列号/SN码（全局唯一） */
    @Column(unique = true, length = 100)
    private String serialNumber;

    /** 所属产品ID */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** 所属产品名称（冗余字段，便于展示） */
    @Column(name = "product_name", length = 100)
    private String productName;

    /** 关联的物模型ID */
    @Column(name = "thing_model_id")
    private Long thingModelId;

    /** 当前固件版本号 */
    @Column(length = 50)
    private String firmwareVersion;

    /** 设备在线状态（默认离线） */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DeviceStatus status = DeviceStatus.OFFLINE;

    /** 最近一次上线时间 */
    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;

    /** 最近一次离线时间 */
    @Column(name = "last_offline_time")
    private LocalDateTime lastOfflineTime;

    /** 设备标签（JSON格式） */
    @Column(columnDefinition = "TEXT")
    private String tags;

    /** 设备属性快照（JSON格式，存储最新上报的属性值） */
    @Column(columnDefinition = "TEXT")
    private String properties;

    /** 设备类型（TEST-测试设备 | PRODUCTION-量产设备） */
    @Column(name = "device_type", length = 20)
    private String deviceType = "PRODUCTION";  // TEST | PRODUCTION

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

    public String getDeviceKey() { return deviceKey; }
    public void setDeviceKey(String deviceKey) { this.deviceKey = deviceKey; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Long getThingModelId() { return thingModelId; }
    public void setThingModelId(Long thingModelId) { this.thingModelId = thingModelId; }

    public String getFirmwareVersion() { return firmwareVersion; }
    public void setFirmwareVersion(String firmwareVersion) { this.firmwareVersion = firmwareVersion; }

    public DeviceStatus getStatus() { return status; }
    public void setStatus(DeviceStatus status) { this.status = status; }

    public LocalDateTime getLastOnlineTime() { return lastOnlineTime; }
    public void setLastOnlineTime(LocalDateTime lastOnlineTime) { this.lastOnlineTime = lastOnlineTime; }

    public LocalDateTime getLastOfflineTime() { return lastOfflineTime; }
    public void setLastOfflineTime(LocalDateTime lastOfflineTime) { this.lastOfflineTime = lastOfflineTime; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getProperties() { return properties; }
    public void setProperties(String properties) { this.properties = properties; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public boolean isOnline() {
        return status == DeviceStatus.ONLINE;
    }
}
