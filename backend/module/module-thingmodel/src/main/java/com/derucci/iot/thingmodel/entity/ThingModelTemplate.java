package com.derucci.iot.thingmodel.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 物模型模板实体
 * 对应PRD中的物模型模板功能
 */
@Entity
@Table(name = "t_thing_model_template")
public class ThingModelTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 模板名称（如 智能床垫基础模板）
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 模板编码
     */
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    /**
     * 适用产品品类（智能床垫/电动床/智能枕头）
     */
    @Column(nullable = false, length = 50)
    private String category;

    /**
     * 模板描述
     */
    @Column(length = 500)
    private String description;

    /**
     * 属性JSON
     */
    @Column(name = "properties_json", columnDefinition = "TEXT")
    private String propertiesJson;

    /**
     * 事件JSON
     */
    @Column(name = "events_json", columnDefinition = "TEXT")
    private String eventsJson;

    /**
     * 命令JSON
     */
    @Column(name = "commands_json", columnDefinition = "TEXT")
    private String commandsJson;

    /**
     * 属性数量
     */
    @Column(name = "property_count")
    private Integer propertyCount;

    /**
     * 事件数量
     */
    @Column(name = "event_count")
    private Integer eventCount;

    /**
     * 命令数量
     */
    @Column(name = "command_count")
    private Integer commandCount;

    /**
     * 版本号
     */
    @Column(length = 20)
    private String version;

    /**
     * 是否为系统预置模板
     */
    @Column(nullable = false)
    private Boolean system = false;

    /**
     * 模板状态
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TemplateStatus status = TemplateStatus.DRAFT;

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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPropertiesJson() { return propertiesJson; }
    public void setPropertiesJson(String propertiesJson) { this.propertiesJson = propertiesJson; }

    public String getEventsJson() { return eventsJson; }
    public void setEventsJson(String eventsJson) { this.eventsJson = eventsJson; }

    public String getCommandsJson() { return commandsJson; }
    public void setCommandsJson(String commandsJson) { this.commandsJson = commandsJson; }

    public Integer getPropertyCount() { return propertyCount; }
    public void setPropertyCount(Integer propertyCount) { this.propertyCount = propertyCount; }

    public Integer getEventCount() { return eventCount; }
    public void setEventCount(Integer eventCount) { this.eventCount = eventCount; }

    public Integer getCommandCount() { return commandCount; }
    public void setCommandCount(Integer commandCount) { this.commandCount = commandCount; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public Boolean getSystem() { return system; }
    public void setSystem(Boolean system) { this.system = system; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }

    public TemplateStatus getStatus() { return status; }
    public void setStatus(TemplateStatus status) { this.status = status; }
}
