package com.derucci.iot.thingmodel.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.thingmodel.entity.ThingModel;
import com.derucci.iot.thingmodel.entity.ThingModelPoint;
import com.derucci.iot.thingmodel.entity.ThingModelStatus;
import com.derucci.iot.thingmodel.entity.ThingModelTemplate;
import com.derucci.iot.thingmodel.service.ThingModelPointService;
import com.derucci.iot.thingmodel.service.ThingModelService;
import com.derucci.iot.thingmodel.service.ThingModelTemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物模型管理控制器
 * 按照PRD实现API接口
 */
@RestController
@RequestMapping("/api")
public class ThingModelController {

    private final ThingModelService thingModelService;
    private final ThingModelTemplateService templateService;
    private final ThingModelPointService pointService;

    public ThingModelController(ThingModelService thingModelService,
                                ThingModelTemplateService templateService,
                                ThingModelPointService pointService) {
        this.thingModelService = thingModelService;
        this.templateService = templateService;
        this.pointService = pointService;
    }

    // ==================== 物模型管理 ====================

    @GetMapping("/thing-models/{id}")
    public Result<ThingModel> getThingModel(@PathVariable Long id) {
        return thingModelService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("物模型不存在"));
    }

    @GetMapping("/thing-models/code/{code}")
    public Result<ThingModel> getThingModelByCode(@PathVariable String code) {
        return thingModelService.findByCode(code)
                .map(Result::success)
                .orElse(Result.notFound("物模型不存在"));
    }

    @GetMapping("/thing-models")
    public Result<List<ThingModel>> getAllThingModels(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) ThingModelStatus status) {
        List<ThingModel> models;
        if (category != null) {
            models = thingModelService.findByCategory(category);
        } else if (status != null) {
            models = thingModelService.findByStatus(status);
        } else {
            models = thingModelService.findAll();
        }
        return Result.success(models);
    }

    @PostMapping("/thing-models")
    public Result<ThingModel> createThingModel(@RequestBody ThingModel thingModel) {
        ThingModel created = thingModelService.create(thingModel);
        return Result.success(created);
    }

    @PutMapping("/thing-models/{id}")
    public Result<ThingModel> updateThingModel(@PathVariable Long id, @RequestBody ThingModel thingModel) {
        ThingModel updated = thingModelService.update(id, thingModel);
        return Result.success(updated);
    }

    @DeleteMapping("/thing-models/{id}")
    public Result<Void> deleteThingModel(@PathVariable Long id) {
        thingModelService.delete(id);
        return Result.success();
    }

    @PutMapping("/thing-models/{id}/publish")
    public Result<ThingModel> publishThingModel(@PathVariable Long id) {
        ThingModel thingModel = thingModelService.publish(id);
        return Result.success(thingModel);
    }

    // ==================== 物模型模板管理 ====================

    /**
     * PRD: 获取所有物模型模板列表
     */
    @GetMapping("/thing-model/templates")
    public Result<List<ThingModelTemplate>> getAllTemplates() {
        return Result.success(templateService.findAll());
    }

    /**
     * PRD: 按品类获取推荐模板
     */
    @GetMapping("/thing-model/templates/{category}")
    public Result<List<ThingModelTemplate>> getTemplatesByCategory(@PathVariable String category) {
        return Result.success(templateService.findByCategory(category));
    }

    /**
     * PRD: 获取模板详情
     */
    @GetMapping("/thing-model/template/{id}")
    public Result<ThingModelTemplate> getTemplateById(@PathVariable Long id) {
        return templateService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("模板不存在"));
    }

    @PostMapping("/thing-model/templates")
    public Result<ThingModelTemplate> createTemplate(@RequestBody ThingModelTemplate template) {
        ThingModelTemplate created = templateService.create(template);
        return Result.success(created);
    }

    @PutMapping("/thing-model/templates/{id}")
    public Result<ThingModelTemplate> updateTemplate(@PathVariable Long id, @RequestBody ThingModelTemplate template) {
        ThingModelTemplate updated = templateService.update(id, template);
        return Result.success(updated);
    }

    @DeleteMapping("/thing-model/templates/{id}")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        templateService.delete(id);
        return Result.success();
    }

    // ==================== 物模型功能点管理 ====================

    /**
     * 获取物模型的所有功能点
     */
    @GetMapping("/thing-models/{id}/points")
    public Result<List<ThingModelPoint>> getPoints(@PathVariable Long id) {
        return Result.success(pointService.findByThingModelId(id));
    }

    /**
     * 获取功能点详情
     */
    @GetMapping("/thing-model/points/{id}")
    public Result<ThingModelPoint> getPointById(@PathVariable Long id) {
        return pointService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("功能点不存在"));
    }

    /**
     * PRD: 新增功能点
     */
    @PostMapping("/thing-model/points")
    public Result<ThingModelPoint> createPoint(@RequestBody ThingModelPoint point) {
        ThingModelPoint created = pointService.create(point);
        return Result.success(created);
    }

    /**
     * PRD: 编辑功能点
     */
    @PutMapping("/thing-model/points/{id}")
    public Result<ThingModelPoint> updatePoint(@PathVariable Long id, @RequestBody ThingModelPoint point) {
        ThingModelPoint updated = pointService.update(id, point);
        return Result.success(updated);
    }

    /**
     * PRD: 删除功能点
     */
    @DeleteMapping("/thing-model/points/{id}")
    public Result<Void> deletePoint(@PathVariable Long id) {
        pointService.delete(id);
        return Result.success();
    }

    /**
     * 选择模板 - 清空当前功能点并添加模板功能点
     */
    @PostMapping("/thing-models/{id}/apply-template/{templateId}")
    public Result<Void> applyTemplate(@PathVariable Long id, @PathVariable Long templateId) {
        // TODO: 实现模板应用逻辑
        return Result.success();
    }
}
