package com.derucci.iot.product.repository;

import com.derucci.iot.product.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** 产品品类数据访问层，提供品类的持久化查询操作 */
@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    /**
     * 判断指定编码的品类是否已存在
     *
     * @param code 品类编码（如 "smart_mattress"）
     * @return 存在返回 true
     */
    boolean existsByCode(String code);
}
