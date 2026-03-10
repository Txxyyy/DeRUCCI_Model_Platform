package com.derucci.iot.thingmodel.service;

import com.derucci.iot.thingmodel.entity.ThingModelPoint;
import com.derucci.iot.thingmodel.repository.ThingModelPointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 物模型功能点服务
 * 按照PRD实现功能点管理
 */
@Service
public class ThingModelPointService {

    private static final List<String> VALID_DATA_TYPES = List.of("int", "float", "bool", "string", "enum");
    private static final List<String> VALID_POINT_TYPES = List.of("PROPERTY", "EVENT", "COMMAND");
    private static final List<String> VALID_ACCESS_TYPES = List.of("readOnly", "readWrite", "writeOnly");

    private final ThingModelPointRepository pointRepository;

    public ThingModelPointService(ThingModelPointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    /**
     * 根据物模型ID获取所有功能点
     */
    public List<ThingModelPoint> findByThingModelId(Long thingModelId) {
        return pointRepository.findByThingModelId(thingModelId);
    }

    /**
     * 根据ID获取功能点
     */
    public Optional<ThingModelPoint> findById(Long id) {
        return pointRepository.findById(id);
    }

    /**
     * 创建功能点
     * PRD要求:
     * - 物模型ID唯一
     * - 数据类型必须是int/float/bool/string/enum
     * - 属性类型时读写类型必填
     * - 数值类型时取值范围必填
     * - enum类型时枚举值必填
     */
    @Transactional
    public ThingModelPoint create(ThingModelPoint point) {
        // 验证物模型ID
        if (point.getThingModelId() == null) {
            throw new IllegalArgumentException("物模型ID不能为空");
        }

        // 验证功能点标识符
        if (point.getPointId() == null || point.getPointId().isEmpty()) {
            throw new IllegalArgumentException("功能点标识符不能为空");
        }

        // 验证功能名称
        if (point.getName() == null || point.getName().isEmpty()) {
            throw new IllegalArgumentException("功能名称不能为空");
        }

        // 验证功能点类型
        validatePointType(point);
        
        // 验证数据类型
        validateDataType(point);
        
        // 验证读写类型（属性时必填）
        if ("PROPERTY".equals(point.getPointType())) {
            if (point.getAccess() == null || point.getAccess().isEmpty()) {
                throw new IllegalArgumentException("属性类型功能点必须设置读写类型");
            }
            if (!VALID_ACCESS_TYPES.contains(point.getAccess())) {
                throw new IllegalArgumentException("读写类型必须是readOnly/readWrite/writeOnly");
            }
        }
        
        // 验证取值范围（数值类型时必填）- 改为可选，允许不填
        // if (isNumericType(point.getDataType())) {
        //     if (point.getRangeJson() == null || point.getRangeJson().isEmpty()) {
        //         throw new IllegalArgumentException("数值类型功能点必须设置取值范围");
        //     }
        // }
        
        // 验证枚举值（enum类型时必填）- 改为可选，允许不填
        // if ("enum".equals(point.getDataType())) {
        //     if (point.getEnumValuesJson() == null || point.getEnumValuesJson().isEmpty()) {
        //         throw new IllegalArgumentException("枚举类型功能点必须设置枚举值");
        //     }
        // }
        
        // 验证物模型内ID唯一
        if (pointRepository.existsByThingModelIdAndPointId(point.getThingModelId(), point.getPointId())) {
            throw new IllegalArgumentException("物模型ID已存在，请使用其他ID");
        }
        
        return pointRepository.save(point);
    }

    /**
     * 更新功能点
     */
    @Transactional
    public ThingModelPoint update(Long id, ThingModelPoint updateData) {
        ThingModelPoint point = pointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("功能点不存在"));
        
        // 如果更新ID，检查唯一性
        if (updateData.getPointId() != null && !updateData.getPointId().equals(point.getPointId())) {
            if (pointRepository.existsByThingModelIdAndPointId(point.getThingModelId(), updateData.getPointId())) {
                throw new IllegalArgumentException("物模型ID已存在");
            }
            point.setPointId(updateData.getPointId());
        }
        
        if (updateData.getName() != null) point.setName(updateData.getName());
        if (updateData.getDataType() != null) point.setDataType(updateData.getDataType());
        if (updateData.getAccess() != null) point.setAccess(updateData.getAccess());
        if (updateData.getUnit() != null) point.setUnit(updateData.getUnit());
        if (updateData.getRangeJson() != null) point.setRangeJson(updateData.getRangeJson());
        if (updateData.getEnumValuesJson() != null) point.setEnumValuesJson(updateData.getEnumValuesJson());
        if (updateData.getDefaultValue() != null) point.setDefaultValue(updateData.getDefaultValue());
        if (updateData.getDescription() != null) point.setDescription(updateData.getDescription());
        
        return pointRepository.save(point);
    }

    /**
     * 删除功能点
     */
    @Transactional
    public void delete(Long id) {
        if (!pointRepository.existsById(id)) {
            throw new IllegalArgumentException("功能点不存在");
        }
        pointRepository.deleteById(id);
    }

    /**
     * 删除物模型的所有功能点
     */
    @Transactional
    public void deleteByThingModelId(Long thingModelId) {
        pointRepository.deleteByThingModelId(thingModelId);
    }

    /**
     * 验证功能点类型
     */
    private void validatePointType(ThingModelPoint point) {
        if (point.getPointType() == null || point.getPointType().isEmpty()) {
            throw new IllegalArgumentException("功能点类型不能为空");
        }
        if (!VALID_POINT_TYPES.contains(point.getPointType())) {
            throw new IllegalArgumentException("功能类型必须是PROPERTY/EVENT/COMMAND");
        }
    }

    /**
     * 验证数据类型
     */
    private void validateDataType(ThingModelPoint point) {
        if (point.getDataType() == null || point.getDataType().isEmpty()) {
            throw new IllegalArgumentException("数据类型不能为空");
        }
        if (!VALID_DATA_TYPES.contains(point.getDataType())) {
            throw new IllegalArgumentException("数据类型必须是int/float/bool/string/enum");
        }
    }

    /**
     * 判断是否为数值类型
     */
    private boolean isNumericType(String dataType) {
        return "int".equals(dataType) || "float".equals(dataType);
    }
}
