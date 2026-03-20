package com.derucci.iot.thingmodel.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.common.auth.RequirePermission;
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

    /**
     * 根据ID查询物模型详情
     *
     * @param id 物模型ID
     * @return 物模型详情，不存在时返回404
     */
    @GetMapping("/thing-models/{id}")
    @RequirePermission(module = "PRODUCT_MODEL", access = "R")
    public Result<ThingModel> getThingModel(@PathVariable Long id) {
        return thingModelService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("物模型不存在"));
    }

    /**
     * 根据编码查询物模型
     *
     * @param code 物模型唯一编码
     * @return 物模型详情，不存在时返回404
     */
    @GetMapping("/thing-models/code/{code}")
    @RequirePermission(module = "PRODUCT_MODEL", access = "R")
    public Result<ThingModel> getThingModelByCode(@PathVariable String code) {
        return thingModelService.findByCode(code)
                .map(Result::success)
                .orElse(Result.notFound("物模型不存在"));
    }

    /**
     * 查询物模型列表，支持按品类或状态筛选
     *
     * @param category 产品品类（可选）
     * @param status 物模型状态（可选）
     * @return 物模型列表
     */
    @GetMapping("/thing-models")
    @RequirePermission(module = "PRODUCT_MODEL", access = "R")
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

    /**
     * 创建物模型
     *
     * @param thingModel 物模型数据
     * @return 创建后的物模型（含生成的ID）
     */
    @PostMapping("/thing-models")
    @RequirePermission(module = "PRODUCT_MODEL", access = "RW")
    public Result<ThingModel> createThingModel(@RequestBody ThingModel thingModel) {
        ThingModel created = thingModelService.create(thingModel);
        return Result.success(created);
    }

    /**
     * 更新物模型信息
     *
     * @param id 物模型ID
     * @param thingModel 待更新的字段数据
     * @return 更新后的物模型
     */
    @PutMapping("/thing-models/{id}")
    @RequirePermission(module = "PRODUCT_MODEL", access = "RW")
    public Result<ThingModel> updateThingModel(@PathVariable Long id, @RequestBody ThingModel thingModel) {
        ThingModel updated = thingModelService.update(id, thingModel);
        return Result.success(updated);
    }

    /**
     * 删除物模型
     *
     * @param id 物模型ID
     * @return 空结果
     */
    @DeleteMapping("/thing-models/{id}")
    @RequirePermission(module = "PRODUCT_MODEL", access = "RW")
    public Result<Void> deleteThingModel(@PathVariable Long id) {
        thingModelService.delete(id);
        return Result.success();
    }

    /**
     * 发布物模型，状态变更为PUBLISHED
     *
     * @param id 物模型ID
     * @return 发布后的物模型
     */
    @PutMapping("/thing-models/{id}/publish")
    @RequirePermission(module = "PRODUCT_MODEL", access = "RW")
    public Result<ThingModel> publishThingModel(@PathVariable Long id) {
        ThingModel thingModel = thingModelService.publish(id);
        return Result.success(thingModel);
    }

    // ==================== 物模型模板管理 ====================

    /**
     * PRD: 获取所有物模型模板列表
     *
     * @return 模板列表
     */
    @GetMapping("/thing-model/templates")
    @RequirePermission(module = "CATEGORY_MODEL", access = "R")
    public Result<List<ThingModelTemplate>> getAllTemplates() {
        return Result.success(templateService.findAll());
    }

    /**
     * PRD: 按品类获取推荐模板
     *
     * @param category 产品品类名称
     * @return 该品类下的推荐模板列表
     */
    @GetMapping("/thing-model/templates/{category}")
    @RequirePermission(module = "CATEGORY_MODEL", access = "R")
    public Result<List<ThingModelTemplate>> getTemplatesByCategory(@PathVariable String category) {
        return Result.success(templateService.findByCategory(category));
    }

    /**
     * PRD: 获取模板详情
     *
     * @param id 模板ID
     * @return 模板详情，不存在时返回404
     */
    @GetMapping("/thing-model/template/{id}")
    @RequirePermission(module = "CATEGORY_MODEL", access = "R")
    public Result<ThingModelTemplate> getTemplateById(@PathVariable Long id) {
        return templateService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("模板不存在"));
    }

    /**
     * 创建物模型模板
     *
     * @param template 模板数据
     * @return 创建后的模板（含生成的ID）
     */
    @PostMapping("/thing-model/templates")
    @RequirePermission(module = "CATEGORY_MODEL", access = "RW")
    public Result<ThingModelTemplate> createTemplate(@RequestBody ThingModelTemplate template) {
        ThingModelTemplate created = templateService.create(template);
        return Result.success(created);
    }

    /**
     * 更新物模型模板
     *
     * @param id 模板ID
     * @param template 待更新的字段数据
     * @return 更新后的模板
     */
    @PutMapping("/thing-model/templates/{id}")
    @RequirePermission(module = "CATEGORY_MODEL", access = "RW")
    public Result<ThingModelTemplate> updateTemplate(@PathVariable Long id, @RequestBody ThingModelTemplate template) {
        ThingModelTemplate updated = templateService.update(id, template);
        return Result.success(updated);
    }

    /**
     * 删除物模型模板
     *
     * @param id 模板ID
     * @return 空结果
     */
    @DeleteMapping("/thing-model/templates/{id}")
    @RequirePermission(module = "CATEGORY_MODEL", access = "RW")
    public Result<Void> deleteTemplate(@PathVariable Long id) {
        templateService.delete(id);
        return Result.success();
    }

    // ==================== 物模型功能点管理 ====================

    /**
     * 获取物模型的所有功能点
     *
     * @param id 物模型ID
     * @return 该物模型下的功能点列表
     */
    @GetMapping("/thing-models/{id}/points")
    @RequirePermission(module = "PRODUCT_MODEL", access = "R")
    public Result<List<ThingModelPoint>> getPoints(@PathVariable Long id) {
        return Result.success(pointService.findByThingModelId(id));
    }

    /**
     * 获取功能点详情
     *
     * @param id 功能点ID
     * @return 功能点详情，不存在时返回404
     */
    @GetMapping("/thing-model/points/{id}")
    @RequirePermission(module = "PRODUCT_MODEL", access = "R")
    public Result<ThingModelPoint> getPointById(@PathVariable Long id) {
        return pointService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("功能点不存在"));
    }

    /**
     * PRD: 新增功能点
     *
     * @param point 功能点数据（需包含thingModelId、pointType等必填字段）
     * @return 创建后的功能点（含生成的ID）
     */
    @PostMapping("/thing-model/points")
    @RequirePermission(module = "PRODUCT_MODEL", access = "RW")
    public Result<ThingModelPoint> createPoint(@RequestBody ThingModelPoint point) {
        ThingModelPoint created = pointService.create(point);
        return Result.success(created);
    }

    /**
     * PRD: 编辑功能点
     *
     * @param id 功能点ID
     * @param point 待更新的字段数据
     * @return 更新后的功能点
     */
    @PutMapping("/thing-model/points/{id}")
    @RequirePermission(module = "PRODUCT_MODEL", access = "RW")
    public Result<ThingModelPoint> updatePoint(@PathVariable Long id, @RequestBody ThingModelPoint point) {
        ThingModelPoint updated = pointService.update(id, point);
        return Result.success(updated);
    }

    /**
     * PRD: 删除功能点
     *
     * @param id 功能点ID
     * @return 空结果
     */
    @DeleteMapping("/thing-model/points/{id}")
    @RequirePermission(module = "PRODUCT_MODEL", access = "RW")
    public Result<Void> deletePoint(@PathVariable Long id) {
        pointService.delete(id);
        return Result.success();
    }

    /**
     * 选择模板 - 清空当前功能点并添加模板功能点
     *
     * @param id 目标物模型ID
     * @param templateId 品类模板ID
     * @return 空结果
     */
    @PostMapping("/thing-models/{id}/apply-template/{templateId}")
    @RequirePermission(module = "PRODUCT_MODEL", access = "RW")
    public Result<Void> applyTemplate(@PathVariable Long id, @PathVariable Long templateId) {
        thingModelService.applyTemplate(id, templateId);
        return Result.success();
    }
}