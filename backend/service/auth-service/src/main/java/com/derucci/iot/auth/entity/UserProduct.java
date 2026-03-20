package com.derucci.iot.auth.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户-产品关联实体，记录DEVELOPER角色分配的产品范围
 */
@Entity
@Table(name = "t_user_product", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"userId", "productId"})
})
public class UserProduct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 产品ID */
    @Column(nullable = false)
    private Long productId;

    /** 创建时间 */
    @Column(updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public LocalDateTime getCreateTime() { return createTime; }
}
