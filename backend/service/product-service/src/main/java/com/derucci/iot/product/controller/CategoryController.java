package com.derucci.iot.product.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.common.auth.RequirePermission;
import com.derucci.iot.product.entity.ProductCategory;
import com.derucci.iot.product.repository.ProductCategoryRepository;
import com.derucci.iot.product.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 产品品类控制器，提供品类的CRUD接口 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryController(ProductCategoryRepository categoryRepository,
                               ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    /**
     * 查询所有品类列表，附带每个品类下的产品数量
     *
     * @return 品类列表（含 id、name、code、description、productCount、createTime）
     */
    @GetMapping
    @RequirePermission(module = "PRODUCT", access = "R")
    public Result<List<Map<String, Object>>> getAllCategories() {
        List<ProductCategory> categories = categoryRepository.findAll();
        // 统计每个品类的产品数量
        Map<String, Long> countMap = productRepository.findAll().stream()
                .collect(Collectors.groupingBy(p -> p.getCategory() != null ? p.getCategory() : "", Collectors.counting()));

        List<Map<String, Object>> result = categories.stream().map(c -> {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("code", c.getCode());
            map.put("description", c.getDescription());
            map.put("productCount", countMap.getOrDefault(c.getName(), 0L));
            map.put("createTime", c.getCreateTime());
            return map;
        }).collect(Collectors.toList());

        return Result.success(result);
    }

    /**
     * 创建新品类，校验名称和编码的必填性及唯一性
     *
     * @param category 品类信息（name、code、description）
     * @return 创建成功的品类实体
     */
    @PostMapping
    @RequirePermission(module = "PRODUCT", access = "RW")
    public Result<ProductCategory> createCategory(@RequestBody ProductCategory category) {
        if (category.getName() == null || category.getName().isBlank()) {
            return Result.error("分类名称不能为空");
        }
        if (category.getCode() == null || category.getCode().isBlank()) {
            return Result.error("分类编码不能为空");
        }
        if (categoryRepository.existsByCode(category.getCode())) {
            return Result.error("分类编码已存在");
        }
        return Result.success(categoryRepository.save(category));
    }

    /**
     * 更新指定品类的名称和描述
     *
     * @param id   品类主键ID
     * @param data 需要更新的字段（name、description）
     * @return 更新后的品类实体
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "PRODUCT", access = "RW")
    public Result<ProductCategory> updateCategory(@PathVariable Long id, @RequestBody ProductCategory data) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        if (data.getName() != null) category.setName(data.getName());
        if (data.getDescription() != null) category.setDescription(data.getDescription());
        return Result.success(categoryRepository.save(category));
    }

    /**
     * 删除指定品类
     *
     * @param id 品类主键ID
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "PRODUCT", access = "RW")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return Result.error("分类不存在");
        }
        categoryRepository.deleteById(id);
        return Result.success();
    }
}
