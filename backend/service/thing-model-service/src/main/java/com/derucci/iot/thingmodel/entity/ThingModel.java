package com.derucci.iot.thingmodel.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/** 物模型实体，定义产品的属性、事件和命令能力 */
@Entity
@Table(name = "t_thing_model")
public class ThingModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 物模型名称（最长100字符） */
    @Column(nullable = false, length = 100)
    private String name;

    /** 物模型编码（唯一标识，最长50字符） */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /** 物模型描述（最长500字符） */
    @Column(length = 500)
    private String description;

    /** 所属品类（如智能床垫、电动床等） */
    @Column(length = 50)
    private String category;

    /** 属性定义（JSON格式） */
    @Column(name = "properties_json", columnDefinition = "TEXT")
    private String propertiesJson;

    /** 事件定义（JSON格式） */
    @Column(name = "events_json", columnDefinition = "TEXT")
    private String eventsJson;

    /** 命令定义（JSON格式） */
    @Column(name = "commands_json", columnDefinition = "TEXT")
    private String commandsJson;

    /** 物模型状态（DRAFT/PUBLISHED/DEPRECATED） */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ThingModelStatus status = ThingModelStatus.DRAFT;

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

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getPropertiesJson() { return propertiesJson; }
    public void setPropertiesJson(String propertiesJson) { this.propertiesJson = propertiesJson; }

    public String getEventsJson() { return eventsJson; }
    public void setEventsJson(String eventsJson) { this.eventsJson = eventsJson; }

    public String getCommandsJson() { return commandsJson; }
    public void setCommandsJson(String commandsJson) { this.commandsJson = commandsJson; }

    public ThingModelStatus getStatus() { return status; }
    public void setStatus(ThingModelStatus status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    /** 判断物模型是否已发布 */
    public boolean isPublished() {
        return status == ThingModelStatus.PUBLISHED;
    }
}
