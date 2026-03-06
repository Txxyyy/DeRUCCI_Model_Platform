package com.derucci.iot.thingmodel.service;

import com.derucci.iot.thingmodel.entity.ThingModelTemplate;
import com.derucci.iot.thingmodel.repository.ThingModelTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 物模型模板服务
 * 按照PRD实现模板管理功能
 */
@Service
public class ThingModelTemplateService {

    private final ThingModelTemplateRepository templateRepository;

    public ThingModelTemplateService(ThingModelTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    /**
     * 获取所有模板
     */
    public List<ThingModelTemplate> findAll() {
        return templateRepository.findAll();
    }

    /**
     * 根据ID获取模板
     */
    public Optional<ThingModelTemplate> findById(Long id) {
        return templateRepository.findById(id);
    }

    /**
     * 根据产品品类获取推荐模板
     * PRD: 根据产品品类推荐对应模板
     */
    public List<ThingModelTemplate> findByCategory(String category) {
        return templateRepository.findByCategory(category);
    }

    /**
     * 根据模板编码获取模板
     */
    public Optional<ThingModelTemplate> findByCode(String code) {
        return templateRepository.findByCode(code);
    }

    /**
     * 创建模板
     */
    @Transactional
    public ThingModelTemplate create(ThingModelTemplate template) {
        if (templateRepository.existsByCode(template.getCode())) {
            throw new IllegalArgumentException("模板编码已存在");
        }
        return templateRepository.save(template);
    }

    /**
     * 更新模板
     */
    @Transactional
    public ThingModelTemplate update(Long id, ThingModelTemplate updateData) {
        ThingModelTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("模板不存在"));
        
        if (updateData.getName() != null) {
            template.setName(updateData.getName());
        }
        if (updateData.getDescription() != null) {
            template.setDescription(updateData.getDescription());
        }
        if (updateData.getPropertiesJson() != null) {
            template.setPropertiesJson(updateData.getPropertiesJson());
        }
        if (updateData.getEventsJson() != null) {
            template.setEventsJson(updateData.getEventsJson());
        }
        if (updateData.getCommandsJson() != null) {
            template.setCommandsJson(updateData.getCommandsJson());
        }
        
        return templateRepository.save(template);
    }

    /**
     * 删除模板
     */
    @Transactional
    public void delete(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new IllegalArgumentException("模板不存在");
        }
        templateRepository.deleteById(id);
    }
}
