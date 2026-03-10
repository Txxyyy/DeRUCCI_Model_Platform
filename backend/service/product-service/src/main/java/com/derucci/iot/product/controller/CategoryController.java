package com.derucci.iot.product.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.product.entity.ProductCategory;
import com.derucci.iot.product.repository.ProductCategoryRepository;
import com.derucci.iot.product.repository.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @GetMapping
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

    @PostMapping
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

    @PutMapping("/{id}")
    public Result<ProductCategory> updateCategory(@PathVariable Long id, @RequestBody ProductCategory data) {
        ProductCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        if (data.getName() != null) category.setName(data.getName());
        if (data.getDescription() != null) category.setDescription(data.getDescription());
        return Result.success(categoryRepository.save(category));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return Result.error("分类不存在");
        }
        categoryRepository.deleteById(id);
        return Result.success();
    }
}
