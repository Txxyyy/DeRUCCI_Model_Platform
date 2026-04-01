package com.derucci.iot.product.repository;

import com.derucci.iot.product.entity.Product;
import com.derucci.iot.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** 产品数据访问层，提供产品的持久化查询操作 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据产品型号查询
     *
     * @param model 产品型号（如 "DRC-M01"）
     * @return 匹配的产品，未找到时返回 empty
     */
    Optional<Product> findByModel(String model);
    /**
     * 判断指定型号是否已存在
     *
     * @param model 产品型号
     * @return 存在返回 true
     */
    boolean existsByModel(String model);

    /**
     * 根据产品名称查询
     *
     * @param name 产品名称
     * @return 匹配的产品，未找到时返回 empty
     */
    Optional<Product> findByName(String name);
    /**
     * 判断指定名称是否已存在
     *
     * @param name 产品名称
     * @return 存在返回 true
     */
    boolean existsByName(String name);

    /**
     * 根据PID查询产品
     *
     * @param pid 产品PID（如 "PID_A1B2C3"）
     * @return 匹配的产品，未找到时返回 empty
     */
    Optional<Product> findByPid(String pid);
    /**
     * 判断指定PID是否已存在
     *
     * @param pid 产品PID
     * @return 存在返回 true
     */
    boolean existsByPid(String pid);

    /**
     * 根据品类名称查询产品列表
     *
     * @param category 品类名称（如 "智能床垫"）
     * @return 该品类下的产品列表
     */
    List<Product> findByCategory(String category);

    /**
     * 根据产品状态查询产品列表
     *
     * @param status 产品状态（DEVELOPING/PUBLISHED/OFFLINE）
     * @return 该状态下的产品列表
     */
    List<Product> findByStatus(ProductStatus status);

    /**
     * 根据通信协议查询产品列表
     *
     * @param protocol 通信协议（MQTT/BLE）
     * @return 使用该协议的产品列表
     */
    List<Product> findByProtocol(String protocol);

    /**
     * 多条件组合查询产品，支持品类、协议、状态和关键词筛选
     *
     * @param category 品类名称，传 null 表示不限
     * @param protocol 通信协议，传 null 表示不限
     * @param status   产品状态，传 null 表示不限
     * @param keyword  搜索关键词，匹配名称或型号，传 null 表示不限
     * @return 符合所有条件的产品列表
     */
    @Query("SELECT p FROM Product p WHERE " +
           "(:category IS NULL OR p.category = :category) AND " +
           "(:protocol IS NULL OR p.protocol = :protocol) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:keyword IS NULL OR p.name LIKE %:keyword% OR p.model LIKE %:keyword%)")
    List<Product> searchProducts(@Param("category") String category,
                                  @Param("protocol") String protocol,
                                  @Param("status") ProductStatus status,
                                  @Param("keyword") String keyword);
}
