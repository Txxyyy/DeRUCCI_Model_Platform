package com.derucci.iot.product.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/** 产品品类实体，定义产品的分类信息（如智能床垫、电动床等） */
@Entity
@Table(name = "t_product_category")
public class ProductCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 品类主键ID（自增） */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 品类名称（最长100字符，不可为空） */
    @Column(nullable = false, length = 100)
    private String name;

    /** 品类编码（最长50字符，全局唯一） */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** 品类描述（最长500字符） */
    @Column(length = 500)
    private String description;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
