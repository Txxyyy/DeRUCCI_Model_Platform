package com.derucci.iot.product.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.common.auth.RequirePermission;
import com.derucci.iot.common.auth.AuthContext;
import com.derucci.iot.common.auth.AuthUser;
import com.derucci.iot.product.entity.Product;
import com.derucci.iot.product.entity.ProductStatus;
import com.derucci.iot.product.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 产品管理控制器，提供产品的CRUD及发布/下线操作接口 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 根据ID查询产品详情
     *
     * @param id 产品主键ID
     * @return 产品详情，不存在时返回 404
     */
    @GetMapping("/{id}")
    @RequirePermission(module = "PRODUCT", access = "R")
    public Result<Product> getProduct(@PathVariable Long id) {
        checkProductScope(id);
        return productService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("产品不存在"));
    }

    /**
     * 根据产品编码查询产品详情
     *
     * @param code 产品编码（即PID，如 "PID_A1B2C3"）
     * @return 产品详情，不存在时返回 404
     */
    @GetMapping("/code/{code}")
    @RequirePermission(module = "PRODUCT", access = "R")
    public Result<Product> getProductByCode(@PathVariable String code) {
        return productService.findByCode(code)
                .map(p -> {
                    checkProductScope(p.getId());
                    return Result.success(p);
                })
                .orElse(Result.notFound("产品不存在"));
    }

    /**
     * 查询产品列表，支持按品类、协议、状态和关键词筛选
     *
     * @param category 品类名称（如 "智能床垫"），可选
     * @param protocol 通信协议（MQTT/BLE），可选
     * @param status   产品状态（DEVELOPING/PUBLISHED），可选
     * @param keyword  搜索关键词，匹配产品名称或型号，可选
     * @return 符合条件的产品列表
     */
    @GetMapping
    @RequirePermission(module = "PRODUCT", access = "R")
    public Result<List<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String protocol,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) String keyword) {
        List<Product> products = productService.list(category, protocol, status, keyword);
        AuthUser currentUser = AuthContext.getUser();
        if (currentUser != null && currentUser.isDeveloper() && currentUser.getAssignedProductIds() != null) {
            products = products.stream()
                    .filter(p -> currentUser.getAssignedProductIds().contains(p.getId()))
                    .collect(java.util.stream.Collectors.toList());
        }
        return Result.success(products);
    }

    /**
     * 创建新产品
     *
     * @param product 产品信息（名称、型号、品类、协议等）
     * @return 创建成功的产品（含自动生成的PID）
     */
    @PostMapping
    @RequirePermission(module = "PRODUCT", access = "RW")
    public Result<Product> createProduct(@RequestBody Product product) {
        Product created = productService.create(product);
        return Result.success(created);
    }

    /**
     * 更新指定产品信息
     *
     * @param id      产品主键ID
     * @param product 需要更新的字段（仅非null字段会被更新）
     * @return 更新后的产品信息
     */
    @PutMapping("/{id}")
    @RequirePermission(module = "PRODUCT", access = "RW")
    public Result<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        checkProductScope(id);
        Product updated = productService.update(id, product);
        return Result.success(updated);
    }

    /**
     * 删除指定产品
     *
     * @param id 产品主键ID
     */
    @DeleteMapping("/{id}")
    @RequirePermission(module = "PRODUCT", access = "RW")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        checkProductScope(id);
        productService.delete(id);
        return Result.success();
    }

    /**
     * 发布产品，状态变更为已发布
     *
     * @param id 产品主键ID
     * @return 发布后的产品信息
     */
    @PutMapping("/{id}/publish")
    @RequirePermission(module = "PRODUCT", action = "PUBLISH")
    public Result<Product> publishProduct(@PathVariable Long id) {
        checkProductScope(id);
        Product product = productService.publish(id);
        return Result.success(product);
    }

    /**
     * 下线产品，状态变更为开发中
     *
     * @param id 产品主键ID
     * @return 下线后的产品信息
     */
    @PutMapping("/{id}/offline")
    @RequirePermission(module = "PRODUCT", access = "RW")
    public Result<Product> offlineProduct(@PathVariable Long id) {
        checkProductScope(id);
        Product product = productService.offline(id);
        return Result.success(product);
    }

    /**
     * DEVELOPER产品范围检查，非分配产品抛403
     *
     * @param productId 产品ID
     */
    private void checkProductScope(Long productId) {
        AuthUser user = AuthContext.getUser();
        if (user != null && !user.hasProductAccess(productId)) {
            throw new SecurityException("无权访问该产品");
        }
    }
}