package com.derucci.iot.thingmodel.entity;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 物模型功能点实体
 * 对应PRD中的物模型功能点定义
 * 支持不同数据类型的动态字段编辑
 */
@Entity
@Table(name = "t_thing_model_point")
public class ThingModelPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 物模型ID
     */
    @Column(name = "thing_model_id", nullable = false)
    private Long thingModelId;

    /**
     * 标识符(identifier)：如 temperature, bed_position
     * 同一物模型内唯一
     */
    @Column(nullable = false, length = 50)
    private String pointId;

    /**
     * 功能名称：如 当前温度、睡床位置
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 功能类型：PROPERTY/EVENT/COMMAND
     */
    @Column(nullable = false, length = 20)
    private String pointType;

    /**
     * 数据类型：int/float/bool/string/enum/array
     */
    @Column(nullable = false, length = 20)
    private String dataType;

    /**
     * 读写类型：readOnly/readWrite/writeOnly（仅属性时使用）
     */
    @Column(length = 20)
    private String access;

    /**
     * 单位（如 ℃、%、次）
     */
    @Column(length = 20)
    private String unit;

    /**
     * 取值范围JSON（如 {"min": -40, "max": 80}）
     * int/float类型使用
     */
    @Column(columnDefinition = "JSON")
    private String rangeJson;

    /**
     * 枚举值JSON（如 [{"value":"off","description":"关闭"},{"value":"on","description":"开启"}】）
     * enum类型使用，每个枚举值包含value和description
     */
    @Column(columnDefinition = "JSON")
    private String enumValuesJson;

    /**
     * 字符串最大长度
     * string类型使用
     */
    @Column
    private Integer maxLength;

    /**
     * 数组元素类型（array类型使用）：int/float/string/bool
     */
    @Column(length = 20)
    private String elementType;

    /**
     * 默认值
     */
    @Column(columnDefinition = "TEXT")
    private String defaultValue;

    /**
     * 功能描述
     */
    @Column(length = 200)
    private String description;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getThingModelId() { return thingModelId; }
    public void setThingModelId(Long thingModelId) { this.thingModelId = thingModelId; }

    public String getPointId() { return pointId; }
    public void setPointId(String pointId) { this.pointId = pointId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPointType() { return pointType; }
    public void setPointType(String pointType) { this.pointType = pointType; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public String getAccess() { return access; }
    public void setAccess(String access) { this.access = access; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getRangeJson() { return rangeJson; }
    public void setRangeJson(String rangeJson) { this.rangeJson = rangeJson; }

    public String getEnumValuesJson() { return enumValuesJson; }
    public void setEnumValuesJson(String enumValuesJson) { this.enumValuesJson = enumValuesJson; }

    public Integer getMaxLength() { return maxLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }

    public String getElementType() { return elementType; }
    public void setElementType(String elementType) { this.elementType = elementType; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
