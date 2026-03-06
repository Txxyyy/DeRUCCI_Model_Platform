package com.derucci.iot.product.repository;

import com.derucci.iot.product.entity.Product;
import com.derucci.iot.product.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByCode(String code);

    boolean existsByCode(String code);

    // PRD要求：按产品型号查询
    Optional<Product> findByModel(String model);
    boolean existsByModel(String model);
    
    // PRD要求：按产品名称查询
    Optional<Product> findByName(String name);
    boolean existsByName(String name);
    
    // PRD要求：按PID查询
    Optional<Product> findByPid(String pid);
    boolean existsByPid(String pid);

    List<Product> findByCategory(String category);

    List<Product> findByStatus(ProductStatus status);
    
    // PRD要求：按通信方式查询
    List<Product> findByProtocol(String protocol);

    // PRD要求：组合查询支持
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
