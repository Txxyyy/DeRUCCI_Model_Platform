package com.derucci.iot.thingmodel.service;

import com.derucci.iot.common.core.exception.BusinessException;
import com.derucci.iot.thingmodel.entity.ThingModel;
import com.derucci.iot.thingmodel.entity.ThingModelPoint;
import com.derucci.iot.thingmodel.entity.ThingModelStatus;
import com.derucci.iot.thingmodel.entity.ThingModelTemplate;
import com.derucci.iot.thingmodel.repository.ThingModelRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ThingModelService {

    private final ThingModelRepository thingModelRepository;
    private final ThingModelPointService pointService;
    private final ThingModelTemplateService templateService;
    private final ObjectMapper objectMapper;

    public ThingModelService(ThingModelRepository thingModelRepository,
                            ThingModelPointService pointService,
                            ThingModelTemplateService templateService,
                            ObjectMapper objectMapper) {
        this.thingModelRepository = thingModelRepository;
        this.pointService = pointService;
        this.templateService = templateService;
        this.objectMapper = objectMapper;
    }

    public Optional<ThingModel> findById(Long id) {
        return thingModelRepository.findById(id);
    }

    public Optional<ThingModel> findByCode(String code) {
        return thingModelRepository.findByCode(code);
    }

    public List<ThingModel> findAll() {
        return thingModelRepository.findAll();
    }

    public List<ThingModel> findByCategory(String category) {
        return thingModelRepository.findByCategory(category);
    }

    public List<ThingModel> findByStatus(ThingModelStatus status) {
        return thingModelRepository.findByStatus(status);
    }

    @Transactional
    public ThingModel create(ThingModel thingModel) {
        if (thingModelRepository.existsByCode(thingModel.getCode())) {
            throw BusinessException.parameterError("物模型编码已存在");
        }
        if (thingModel.getStatus() == null) {
            thingModel.setStatus(ThingModelStatus.DRAFT);
        }
        return thingModelRepository.save(thingModel);
    }

    @Transactional
    public ThingModel update(Long id, ThingModel updateData) {
        ThingModel thingModel = thingModelRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("物模型不存在"));

        if (updateData.getName() != null) thingModel.setName(updateData.getName());
        if (updateData.getDescription() != null) thingModel.setDescription(updateData.getDescription());
        if (updateData.getCategory() != null) thingModel.setCategory(updateData.getCategory());
        if (updateData.getPropertiesJson() != null) thingModel.setPropertiesJson(updateData.getPropertiesJson());
        if (updateData.getEventsJson() != null) thingModel.setEventsJson(updateData.getEventsJson());
        if (updateData.getCommandsJson() != null) thingModel.setCommandsJson(updateData.getCommandsJson());

        return thingModelRepository.save(thingModel);
    }

    @Transactional
    public void delete(Long id) {
        if (!thingModelRepository.existsById(id)) {
            throw BusinessException.notFound("物模型不存在");
        }
        thingModelRepository.deleteById(id);
    }

    @Transactional
    public ThingModel publish(Long id) {
        ThingModel thingModel = thingModelRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("物模型不存在"));
        thingModel.setStatus(ThingModelStatus.PUBLISHED);
        return thingModelRepository.save(thingModel);
    }

    @Transactional
    public ThingModel deprecate(Long id) {
        ThingModel thingModel = thingModelRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("物模型不存在"));
        thingModel.setStatus(ThingModelStatus.DEPRECATED);
        return thingModelRepository.save(thingModel);
    }

    /**
     * 应用模板
     * PRD: 选择模板后清空当前功能点，添加模板的功能点
     */
    @Transactional
    public ThingModel applyTemplate(Long thingModelId, Long templateId) {
        // 获取物模型
        ThingModel thingModel = thingModelRepository.findById(thingModelId)
                .orElseThrow(() -> BusinessException.notFound("物模型不存在"));
        
        // 获取模板
        ThingModelTemplate template = templateService.findById(templateId)
                .orElseThrow(() -> BusinessException.notFound("模板不存在"));
        
        // 清空当前功能点
        pointService.deleteByThingModelId(thingModelId);
        
        // 从模板JSON创建功能点
        try {
            // 处理属性
            if (template.getPropertiesJson() != null && !template.getPropertiesJson().isEmpty()) {
                List<Map<String, Object>> properties = objectMapper.readValue(
                    template.getPropertiesJson(), 
                    new TypeReference<List<Map<String, Object>>>() {});
                for (Map<String, Object> prop : properties) {
                    ThingModelPoint point = createPointFromMap(thingModelId, "PROPERTY", prop);
                    pointService.create(point);
                }
            }
            
            // 处理事件
            if (template.getEventsJson() != null && !template.getEventsJson().isEmpty()) {
                List<Map<String, Object>> events = objectMapper.readValue(
                    template.getEventsJson(),
                    new TypeReference<List<Map<String, Object>>>() {});
                for (Map<String, Object> event : events) {
                    ThingModelPoint point = createPointFromMap(thingModelId, "EVENT", event);
                    pointService.create(point);
                }
            }
            
            // 处理命令
            if (template.getCommandsJson() != null && !template.getCommandsJson().isEmpty()) {
                List<Map<String, Object>> commands = objectMapper.readValue(
                    template.getCommandsJson(),
                    new TypeReference<List<Map<String, Object>>>() {});
                for (Map<String, Object> cmd : commands) {
                    ThingModelPoint point = createPointFromMap(thingModelId, "COMMAND", cmd);
                    pointService.create(point);
                }
            }
        } catch (Exception e) {
            throw BusinessException.serverError("解析模板数据失败: " + e.getMessage());
        }
        
        return thingModel;
    }

    /**
     * 从Map创建功能点
     */
    private ThingModelPoint createPointFromMap(Long thingModelId, String pointType, Map<String, Object> data) {
        ThingModelPoint point = new ThingModelPoint();
        point.setThingModelId(thingModelId);
        point.setPointType(pointType);
        point.setPointId((String) data.get("id"));
        point.setName((String) data.get("name"));
        point.setDataType((String) data.get("dataType"));
        point.setAccess((String) data.get("access"));
        point.setUnit((String) data.get("unit"));
        point.setDescription((String) data.get("description"));
        
        if (data.get("range") != null) {
            point.setRangeJson(data.get("range").toString());
        }
        if (data.get("enumValues") != null) {
            point.setEnumValuesJson(data.get("enumValues").toString());
        }
        if (data.get("defaultValue") != null) {
            point.setDefaultValue(data.get("defaultValue").toString());
        }
        
        return point;
    }
}
