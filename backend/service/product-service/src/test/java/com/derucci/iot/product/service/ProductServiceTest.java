package com.derucci.iot.product.service;

import com.derucci.iot.product.entity.Product;
import com.derucci.iot.product.entity.ProductStatus;
import com.derucci.iot.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 产品服务测试
 * 按照TDD流程开发
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("产品管理服务测试")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PidGeneratorService pidGenerator;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setPid("PID_A1B2C3");
        testProduct.setName("慕思智能床垫X1");
        testProduct.setModel("DR-M001");
        testProduct.setBrand("慕思");
        testProduct.setCategory("智能床垫");
        testProduct.setProtocol("MQTT");
        testProduct.setStatus(ProductStatus.DRAFT);
    }

    @Test
    @DisplayName("创建产品时应自动生成PID")
    void testCreateProductGeneratesPid() {
        // Given: 产品信息
        Product newProduct = new Product();
        newProduct.setName("测试产品");
        newProduct.setModel("DR-M002");
        newProduct.setBrand("慕思");
        newProduct.setCategory("智能床垫");
        newProduct.setProtocol("MQTT");
        
        when(pidGenerator.generate()).thenReturn("PID_X1Y2Z3");
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });

        // When: 创建产品
        Product result = productService.create(newProduct);

        // Then: 验证PID已生成
        assertNotNull(result.getPid());
        assertEquals("PID_X1Y2Z3", result.getPid());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("产品型号应该唯一")
    void testProductModelMustBeUnique() {
        // Given: 已存在的产品型号
        String model = "DR-M001";
        when(productRepository.existsByModel(model)).thenReturn(true);

        // When & Then: 尝试创建重复型号产品应抛出异常
        Product duplicateModelProduct = new Product();
        duplicateModelProduct.setModel(model);
        
        assertThrows(IllegalArgumentException.class, 
            () -> productService.create(duplicateModelProduct),
            "产品型号已存在时应抛出异常");
    }

    @Test
    @DisplayName("产品名称应该唯一")
    void testProductNameMustBeUnique() {
        // Given: 已存在的产品名称
        String name = "慕思智能床垫X1";
        when(productRepository.existsByName(name)).thenReturn(true);

        // When & Then: 
        Product duplicateNameProduct = new Product();
        duplicateNameProduct.setName(name);
        
        assertThrows(IllegalArgumentException.class,
            () -> productService.create(duplicateNameProduct),
            "产品名称已存在时应抛出异常");
    }

    @Test
    @DisplayName("产品品类应为必填")
    void testCategoryIsRequired() {
        // Given: 没有品类的产品
        Product productWithoutCategory = new Product();
        productWithoutCategory.setName("测试产品");
        productWithoutCategory.setModel("DR-TEST");
        productWithoutCategory.setBrand("慕思");
        productWithoutCategory.setCategory(null);
        
        // When & Then: 应验证失败
        assertThrows(IllegalArgumentException.class,
            () -> productService.create(productWithoutCategory),
            "产品品类为空时应抛出异常");
    }

    @Test
    @DisplayName("通信方式应为MQTT或BLE")
    void testProtocolShouldBeMqttOrBle() {
        // Given: 无效的通信协议
        Product productWithInvalidProtocol = new Product();
        productWithInvalidProtocol.setName("测试产品");
        productWithInvalidProtocol.setModel("DR-TEST");
        productWithInvalidProtocol.setBrand("慕思");
        productWithInvalidProtocol.setCategory("智能床垫");
        productWithInvalidProtocol.setProtocol("HTTP");
        
        // When & Then: 
        assertThrows(IllegalArgumentException.class,
            () -> productService.create(productWithInvalidProtocol),
            "通信协议不是MQTT或BLE时应抛出异常");
    }

    @Test
    @DisplayName("产品型号格式验证")
    void testProductModelFormat() {
        // Given: 无效的型号格式
        Product productWithInvalidModel = new Product();
        productWithInvalidModel.setName("测试产品");
        productWithInvalidModel.setModel("invalid model!@#");
        productWithInvalidModel.setBrand("慕思");
        productWithInvalidModel.setCategory("智能床垫");
        productWithInvalidModel.setProtocol("MQTT");
        
        // When & Then: 
        assertThrows(IllegalArgumentException.class,
            () -> productService.create(productWithInvalidModel),
            "产品型号格式不正确时应抛出异常");
    }

    @Test
    @DisplayName("查询产品列表支持分类筛选")
    void testListProductsWithCategoryFilter() {
        // Given: 
        List<Product> products = Arrays.asList(
            createProduct("智能床垫"),
            createProduct("智能床垫"),
            createProduct("电动床")
        );
        when(productRepository.findAll()).thenReturn(products);
        
        // When: 按分类查询
        List<Product> result = productService.list("智能床垫", null, null);
        
        // Then: 
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> "智能床垫".equals(p.getCategory())));
    }

    @Test
    @DisplayName("查询产品列表支持通信方式筛选")
    void testListProductsWithProtocolFilter() {
        // Given:
        List<Product> products = Arrays.asList(
            createProductWithProtocol("智能床垫", "MQTT"),
            createProductWithProtocol("智能床垫", "BLE"),
            createProductWithProtocol("电动床", "MQTT")
        );
        when(productRepository.findAll()).thenReturn(products);
        
        // When: 按通信方式查询
        List<Product> result = productService.list(null, "MQTT", null);
        
        // Then:
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> "MQTT".equals(p.getProtocol())));
    }

    @Test
    @DisplayName("查询产品列表支持名称/型号搜索")
    void testListProductsWithKeywordSearch() {
        // Given:
        List<Product> products = Arrays.asList(
            createProductWithNameAndModel("慕思智能床垫X1", "DR-M001"),
            createProductWithNameAndModel("慕思智能枕头P1", "DR-P001"),
            createProductWithNameAndModel("测试产品", "DR-TEST")
        );
        when(productRepository.findAll()).thenReturn(products);
        
        // When: 按关键字搜索
        List<Product> result = productService.list(null, null, "床垫");
        
        // Then:
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().contains("床垫"));
    }

    @Test
    @DisplayName("发布产品应更新状态为已发布")
    void testPublishProduct() {
        // Given:
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When:
        Product result = productService.publish(1L);
        
        // Then:
        assertEquals(ProductStatus.PUBLISHED, result.getStatus());
    }

    @Test
    @DisplayName("只能删除草稿状态的产品")
    void testCanOnlyDeleteDraftProduct() {
        // Given: 已发布的产品
        testProduct.setStatus(ProductStatus.PUBLISHED);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // When & Then: 删除应失败
        assertThrows(IllegalStateException.class,
            () -> productService.delete(1L),
            "只能删除草稿状态的产品");
    }

    private Product createProduct(String category) {
        Product p = new Product();
        p.setCategory(category);
        return p;
    }

    private Product createProductWithProtocol(String category, String protocol) {
        Product p = new Product();
        p.setCategory(category);
        p.setProtocol(protocol);
        return p;
    }

    private Product createProductWithNameAndModel(String name, String model) {
        Product p = new Product();
        p.setName(name);
        p.setModel(model);
        return p;
    }
}
