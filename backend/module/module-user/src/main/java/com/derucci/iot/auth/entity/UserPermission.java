package com.derucci.iot.auth.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户权限实体，记录用户对各模块的访问权限
 */
@Entity
@Table(name = "t_user_permission")
public class UserPermission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 模块：PRODUCT / DEVICE / OTA / USER */
    @Column(nullable = false, length = 20)
    private String module;

    /** 访问级别：R（只读）/ RW（读写） */
    @Column(nullable = false, length = 5)
    private String access;

    /** 操作权限点：PUBLISH / DELETE / OFFLINE / PUSH，为null表示基础R/RW权限 */
    @Column(length = 30)
    private String action;

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
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getAccess() { return access; }
    public void setAccess(String access) { this.access = access; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getCreateTime() { return createTime; }
}
