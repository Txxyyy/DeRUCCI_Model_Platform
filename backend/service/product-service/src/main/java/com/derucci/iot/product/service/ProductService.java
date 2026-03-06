package com.derucci.iot.product.service;

import com.derucci.iot.common.core.exception.BusinessException;
import com.derucci.iot.product.entity.Product;
import com.derucci.iot.product.entity.ProductStatus;
import com.derucci.iot.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 产品管理服务
 * 按照PRD文档实现产品创建、查询、更新、删除功能
 */
@Service
public class ProductService {

    private static final Set<String> VALID_CATEGORIES = Set.of("智能床垫", "电动床", "智能枕头");
    private static final Set<String> VALID_PROTOCOLS = Set.of("MQTT", "BLE");
    private static final String MODEL_PATTERN = "^[A-Za-z0-9-]+$";

    private final ProductRepository productRepository;
    private final PidGeneratorService pidGenerator;

    public ProductService(ProductRepository productRepository, PidGeneratorService pidGenerator) {
        this.productRepository = productRepository;
        this.pidGenerator = pidGenerator;
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> findByPid(String pid) {
        return productRepository.findByPid(pid);
    }

    public Optional<Product> findByCode(String code) {
        // 兼容旧接口：通过code查询实际使用pid
        return productRepository.findByPid(code);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * 查询产品列表（支持筛选）
     * PRD: 产品列表支持分类、通信方式、状态、名称/型号搜索
     */
    public List<Product> list(String category, String protocol, ProductStatus status, String keyword) {
        return productRepository.searchProducts(category, protocol, status, keyword);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }

    /**
     * 创建产品
     * PRD要求:
     * - 自动生成唯一PID
     * - 产品型号唯一
     * - 产品名称唯一
     * - 产品品类必填
     * - 通信方式只能是MQTT或BLE
     * - 创建后状态为开发中
     */
    @Transactional
    public Product create(Product product) {
        // 验证必填字段
        validateRequiredFields(product);

        // 验证产品型号格式
        validateModelFormat(product.getModel());

        // 验证产品型号唯一性
        if (productRepository.existsByModel(product.getModel())) {
            throw BusinessException.parameterError("产品型号已存在，请使用其他型号");
        }

        // 验证产品名称唯一性
        if (productRepository.existsByName(product.getName())) {
            throw BusinessException.parameterError("产品名称已存在，请使用其他名称");
        }

        // 验证通信协议
        if (!VALID_PROTOCOLS.contains(product.getProtocol())) {
            throw BusinessException.parameterError("通信方式只能是MQTT或BLE");
        }

        // 生成唯一PID
        String pid = generateUniquePid();
        product.setPid(pid);

        // 设置默认状态为开发中
        product.setStatus(ProductStatus.DEVELOPING);

        return productRepository.save(product);
    }

    /**
     * 更新产品
     * PRD: 只能在开发中状态更新，已发布后锁定
     */
    @Transactional
    public Product update(Long id, Product updateData) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("产品不存在"));

        // 检查产品状态，只能在开发中状态更新
        if (product.getStatus() != ProductStatus.DEVELOPING) {
            throw BusinessException.parameterError("已发布的产品信息不可修改");
        }

        // 如果更新型号，检查唯一性
        if (updateData.getModel() != null && !updateData.getModel().equals(product.getModel())) {
            validateModelFormat(updateData.getModel());
            if (productRepository.existsByModel(updateData.getModel())) {
                throw BusinessException.parameterError("产品型号已存在");
            }
            product.setModel(updateData.getModel());
        }

        // 如果更新名称，检查唯一性
        if (updateData.getName() != null && !updateData.getName().equals(product.getName())) {
            if (productRepository.existsByName(updateData.getName())) {
                throw BusinessException.parameterError("产品名称已存在");
            }
            product.setName(updateData.getName());
        }

        if (updateData.getBrand() != null) product.setBrand(updateData.getBrand());
        if (updateData.getCategory() != null) product.setCategory(updateData.getCategory());
        if (updateData.getDescription() != null) product.setDescription(updateData.getDescription());
        if (updateData.getImageUrl() != null) product.setImageUrl(updateData.getImageUrl());

        // 验证通信协议
        if (updateData.getProtocol() != null) {
            if (!VALID_PROTOCOLS.contains(updateData.getProtocol())) {
                throw BusinessException.parameterError("通信方式只能是MQTT或BLE");
            }
            product.setProtocol(updateData.getProtocol());
        }

        if (updateData.getThingModelId() != null) {
            product.setThingModelId(updateData.getThingModelId());
        }

        return productRepository.save(product);
    }

    /**
     * 删除产品
     * PRD: 只能删除开发中状态的产品
     */
    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("产品不存在"));

        if (product.getStatus() != ProductStatus.DEVELOPING) {
            throw BusinessException.parameterError("只能删除开发中的产品");
        }

        productRepository.delete(product);
    }

    /**
     * 发布产品
     * PRD: 检查物模型是否已配置
     */
    @Transactional
    public Product publish(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("产品不存在"));

        // 检查产品状态
        if (product.getStatus() == ProductStatus.PUBLISHED) {
            throw BusinessException.parameterError("产品已经是发布状态");
        }

        // 检查是否已配置物模型（至少有一个功能点）
        if (product.getThingModelId() == null) {
            throw BusinessException.parameterError("请先配置物模型后再发布");
        }

        product.setStatus(ProductStatus.PUBLISHED);
        return productRepository.save(product);
    }

    /**
     * 下线产品
     */
    @Transactional
    public Product offline(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("产品不存在"));
        product.setStatus(ProductStatus.OFFLINE);
        return productRepository.save(product);
    }

    /**
     * 生成唯一PID
     */
    private String generateUniquePid() {
        String pid;
        do {
            pid = pidGenerator.generate();
        } while (productRepository.existsByPid(pid));
        return pid;
    }

    /**
     * 验证必填字段
     */
    private void validateRequiredFields(Product product) {
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw BusinessException.parameterError("产品名称不能为空");
        }
        if (product.getModel() == null || product.getModel().trim().isEmpty()) {
            throw BusinessException.parameterError("产品型号不能为空");
        }
        if (product.getBrand() == null || product.getBrand().trim().isEmpty()) {
            throw BusinessException.parameterError("产品品牌不能为空");
        }
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw BusinessException.parameterError("产品品类不能为空");
        }
        if (!VALID_CATEGORIES.contains(product.getCategory())) {
            throw BusinessException.parameterError("产品品类必须是：智能床垫/电动床/智能枕头");
        }
    }

    /**
     * 验证产品型号格式
     * 格式: 只能包含英文字母、数字、连字符
     */
    private void validateModelFormat(String model) {
        if (model != null && !model.matches(MODEL_PATTERN)) {
            throw BusinessException.parameterError("产品型号格式不正确，只能包含字母、数字和连字符");
        }
        if (model != null && (model.startsWith("-") || model.endsWith("-"))) {
            throw BusinessException.parameterError("产品型号不能以连字符开头或结尾");
        }
    }
}
