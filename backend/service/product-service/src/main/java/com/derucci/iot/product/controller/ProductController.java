package com.derucci.iot.product.controller;

import com.derucci.iot.common.core.result.Result;
import com.derucci.iot.product.entity.Product;
import com.derucci.iot.product.entity.ProductStatus;
import com.derucci.iot.product.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        return productService.findById(id)
                .map(Result::success)
                .orElse(Result.notFound("产品不存在"));
    }

    @GetMapping("/code/{code}")
    public Result<Product> getProductByCode(@PathVariable String code) {
        return productService.findByCode(code)
                .map(Result::success)
                .orElse(Result.notFound("产品不存在"));
    }

    @GetMapping
    public Result<List<Product>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String protocol,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false) String keyword) {
        List<Product> products = productService.list(category, protocol, status, keyword);
        return Result.success(products);
    }

    @PostMapping
    public Result<Product> createProduct(@RequestBody Product product) {
        Product created = productService.create(product);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    public Result<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updated = productService.update(id, product);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }

    @PutMapping("/{id}/publish")
    public Result<Product> publishProduct(@PathVariable Long id) {
        Product product = productService.publish(id);
        return Result.success(product);
    }

    @PutMapping("/{id}/offline")
    public Result<Product> offlineProduct(@PathVariable Long id) {
        Product product = productService.offline(id);
        return Result.success(product);
    }
}
