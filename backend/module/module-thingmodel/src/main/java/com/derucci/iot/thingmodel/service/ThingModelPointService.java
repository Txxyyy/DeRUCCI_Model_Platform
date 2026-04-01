package com.derucci.iot.thingmodel.service;

import com.derucci.iot.thingmodel.entity.ThingModelPoint;
import com.derucci.iot.thingmodel.repository.ThingModelPointRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 物模型功能点服务
 * 按照PRD实现功能点管理
 */
@Service
public class ThingModelPointService {

    private static final List<String> VALID_DATA_TYPES = List.of("int", "float", "bool", "string", "enum", "array");
    private static final List<String> VALID_POINT_TYPES = List.of("PROPERTY", "EVENT", "COMMAND");
    private static final List<String> VALID_ACCESS_TYPES = List.of("readOnly", "readWrite", "writeOnly");
    private static final List<String> VALID_ELEMENT_TYPES = List.of("int", "float", "string", "bool");
    private static final Pattern POINT_ID_PATTERN = Pattern.compile("^[a-z][a-z0-9_]{1,49}$");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ThingModelPointRepository pointRepository;

    public ThingModelPointService(ThingModelPointRepository pointRepository) {
        this.pointRepository = pointRepository;
    }

    /**
     * 根据物模型ID获取所有功能点
     *
     * @param thingModelId 物模型ID
     * @return 该物模型下的功能点列表
     */
    public List<ThingModelPoint> findByThingModelId(Long thingModelId) {
        return pointRepository.findByThingModelId(thingModelId);
    }

    /**
     * 根据ID获取功能点
     *
     * @param id 功能点ID
     * @return 功能点Optional，不存在时为空
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
     *
     * @param point 功能点数据（需包含thingModelId、pointId、name、pointType、dataType等必填字段）
     * @return 创建后的功能点（含生成的ID）
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
        validatePointId(point.getPointId());

        // 验证功能名称
        if (point.getName() == null || point.getName().isEmpty()) {
            throw new IllegalArgumentException("功能名称不能为空");
        }
        validateName(point.getName());

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
        // 但如果填了，校验格式
        if (isNumericType(point.getDataType()) && point.getRangeJson() != null && !point.getRangeJson().isEmpty()) {
            validateRange(point.getRangeJson());
        }

        // 验证枚举值（enum类型时必填）- 改为可选，允许不填
        // 但如果填了，校验格式
        if ("enum".equals(point.getDataType()) && point.getEnumValuesJson() != null && !point.getEnumValuesJson().isEmpty()) {
            validateEnumValues(point.getEnumValuesJson());
        }

        // 验证数组类型专属字段（array类型时必填）
        if ("array".equals(point.getDataType())) {
            validateArrayValues(point);
        }

        // 验证物模型内ID唯一
        if (pointRepository.existsByThingModelIdAndPointId(point.getThingModelId(), point.getPointId())) {
            throw new IllegalArgumentException("标识符 \"" + point.getPointId() + "\" 已存在，请使用其他标识符");
        }
        
        return pointRepository.save(point);
    }

    /**
     * 更新功能点
     *
     * @param id 功能点ID
     * @param updateData 待更新的字段数据（null字段不覆盖）
     * @return 更新后的功能点
     */
    @Transactional
    public ThingModelPoint update(Long id, ThingModelPoint updateData) {
        ThingModelPoint point = pointRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("功能点不存在"));
        
        // 如果更新ID，检查格式和唯一性
        if (updateData.getPointId() != null && !updateData.getPointId().equals(point.getPointId())) {
            validatePointId(updateData.getPointId());
            if (pointRepository.existsByThingModelIdAndPointId(point.getThingModelId(), updateData.getPointId())) {
                throw new IllegalArgumentException("物模型ID已存在");
            }
            point.setPointId(updateData.getPointId());
        }

        if (updateData.getName() != null) {
            validateName(updateData.getName());
            point.setName(updateData.getName());
        }
        if (updateData.getDataType() != null) point.setDataType(updateData.getDataType());
        if (updateData.getAccess() != null) point.setAccess(updateData.getAccess());
        if (updateData.getUnit() != null) point.setUnit(updateData.getUnit());
        if (updateData.getRangeJson() != null) {
            if (!updateData.getRangeJson().isEmpty()) validateRange(updateData.getRangeJson());
            point.setRangeJson(updateData.getRangeJson());
        }
        if (updateData.getEnumValuesJson() != null) {
            if (!updateData.getEnumValuesJson().isEmpty()) validateEnumValues(updateData.getEnumValuesJson());
            point.setEnumValuesJson(updateData.getEnumValuesJson());
        }
        if (updateData.getElementType() != null) {
            if (!updateData.getElementType().isEmpty() && !VALID_ELEMENT_TYPES.contains(updateData.getElementType())) {
                throw new IllegalArgumentException("数组元素类型必须是int/float/string/bool");
            }
            point.setElementType(updateData.getElementType());
        }
        if (updateData.getMaxLength() != null) {
            if (updateData.getMaxLength() < 1) {
                throw new IllegalArgumentException("数组最大长度必须 >= 1");
            }
            point.setMaxLength(updateData.getMaxLength());
        }
        if (updateData.getDefaultValue() != null) point.setDefaultValue(updateData.getDefaultValue());
        if (updateData.getDescription() != null) point.setDescription(updateData.getDescription());
        
        return pointRepository.save(point);
    }

    /**
     * 删除功能点
     *
     * @param id 功能点ID
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
     *
     * @param thingModelId 物模型ID
     */
    @Transactional
    public void deleteByThingModelId(Long thingModelId) {
        pointRepository.deleteByThingModelId(thingModelId);
    }

    /**
     * 验证功能点类型
     *
     * @param point 待验证的功能点
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
     *
     * @param point 待验证的功能点
     */
    private void validateDataType(ThingModelPoint point) {
        if (point.getDataType() == null || point.getDataType().isEmpty()) {
            throw new IllegalArgumentException("数据类型不能为空");
        }
        if (!VALID_DATA_TYPES.contains(point.getDataType())) {
            throw new IllegalArgumentException("数据类型必须是int/float/bool/string/enum/array");
        }
    }

    /**
     * 判断是否为数值类型
     *
     * @param dataType 数据类型字符串
     * @return true表示int或float类型
     */
    private boolean isNumericType(String dataType) {
        return "int".equals(dataType) || "float".equals(dataType);
    }

    /**
     * 验证功能点标识符格式
     *
     * @param pointId 功能点标识符
     */
    private void validatePointId(String pointId) {
        String trimmed = pointId.trim();
        if (!POINT_ID_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("标识符格式错误：以小写字母开头，仅支持小写字母、数字和下划线，2-50字符");
        }
        if (trimmed.contains("__")) {
            throw new IllegalArgumentException("标识符不允许连续下划线");
        }
        if (trimmed.endsWith("_")) {
            throw new IllegalArgumentException("标识符不允许以下划线结尾");
        }
    }

    /**
     * 验证功能名称
     *
     * @param name 功能名称
     */
    private void validateName(String name) {
        String trimmed = name.trim();
        if (trimmed.isEmpty() || trimmed.length() > 50) {
            throw new IllegalArgumentException("功能名称长度必须在1-50字符之间");
        }
    }

    /**
     * 验证取值范围JSON
     *
     * @param rangeJson 取值范围JSON字符串，需包含min和max字段
     */
    private void validateRange(String rangeJson) {
        try {
            JsonNode node = objectMapper.readTree(rangeJson);
            if (!node.has("min") || !node.has("max")) {
                throw new IllegalArgumentException("取值范围必须包含 min 和 max");
            }
            double min = node.get("min").asDouble();
            double max = node.get("max").asDouble();
            if (min >= max) {
                throw new IllegalArgumentException("取值范围 min 必须小于 max");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("取值范围JSON格式错误");
        }
    }

    /**
     * 验证枚举值JSON
     *
     * @param enumValuesJson 枚举值JSON数组字符串，每项需包含value字段
     */
    private void validateEnumValues(String enumValuesJson) {
        try {
            JsonNode node = objectMapper.readTree(enumValuesJson);
            if (!node.isArray()) {
                throw new IllegalArgumentException("枚举值必须是数组格式");
            }
            if (node.isEmpty()) {
                throw new IllegalArgumentException("枚举类型至少需要1个枚举值");
            }
            if (node.size() > 20) {
                throw new IllegalArgumentException("枚举值最多20个");
            }
            Set<String> values = new HashSet<>();
            for (JsonNode item : node) {
                String val = item.has("value") ? item.get("value").asText() : "";
                if (val.isEmpty()) {
                    throw new IllegalArgumentException("枚举值的 value 不能为空");
                }
                if (!values.add(val)) {
                    throw new IllegalArgumentException("枚举值 \"" + val + "\" 重复");
                }
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("枚举值JSON格式错误");
        }
    }

    /**
     * 验证数组类型专属字段
     */
    private void validateArrayValues(ThingModelPoint point) {
        // elementType 必填
        if (point.getElementType() == null || point.getElementType().isEmpty()) {
            throw new IllegalArgumentException("数组类型必须指定元素类型");
        }
        if (!VALID_ELEMENT_TYPES.contains(point.getElementType())) {
            throw new IllegalArgumentException("数组元素类型必须是int/float/string/bool");
        }
        // maxLength 必填且 >= 1
        if (point.getMaxLength() == null || point.getMaxLength() < 1) {
            throw new IllegalArgumentException("数组类型必须指定最大长度，且 >= 1");
        }
        // defaultValue 格式校验（如果提供了）
        if (point.getDefaultValue() != null && !point.getDefaultValue().isEmpty()) {
            try {
                JsonNode node = objectMapper.readTree(point.getDefaultValue());
                if (!node.isArray()) {
                    throw new IllegalArgumentException("数组默认值必须是JSON数组格式");
                }
                if (node.size() > point.getMaxLength()) {
                    throw new IllegalArgumentException("数组默认值的元素个数不能超过最大长度");
                }
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalArgumentException("数组默认值JSON格式错误");
            }
        }
    }
}
