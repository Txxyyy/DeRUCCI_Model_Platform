package com.derucci.iot.product.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 产品实体
 * 对应PRD中的产品管理功能
 */
@Entity
@Table(name = "t_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * PID - 系统自动生成的产品唯一标识
     * 格式: PID_ + 6位大写字母数字
     */
    @Column(nullable = false, unique = true, length = 50)
    private String pid;

    /**
     * 产品名称
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 产品型号
     * 格式: 品牌缩写-品类缩写-序号，如 DR-M001
     */
    @Column(nullable = false, unique = true, length = 50)
    private String model;

    /**
     * 产品品牌
     */
    @Column(nullable = false, length = 50)
    private String brand;

    /**
     * 产品分类（智能床垫/电动床/智能枕头）
     */
    @Column(nullable = false, length = 50)
    private String category;

    /**
     * 产品描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 产品图片URL
     */
    @Column(length = 500)
    private String imageUrl;

    /**
     * 通信协议 (MQTT/BLE)
     */
    @Column(nullable = false, length = 20)
    private String protocol;

    /**
     * 物模型ID
     */
    @Column(name = "thing_model_id")
    private Long thingModelId;

    /**
     * 状态
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status = ProductStatus.DEVELOPING;

    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
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

    public String getPid() { return pid; }
    public void setPid(String pid) { this.pid = pid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Long getThingModelId() { return thingModelId; }
    public void setThingModelId(Long thingModelId) { this.thingModelId = thingModelId; }

    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public boolean isPublished() {
        return status == ProductStatus.PUBLISHED;
    }
}
