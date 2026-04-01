package com.derucci.iot.auth.repository;

import com.derucci.iot.auth.entity.UserProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 用户-产品关联Repository
 */
public interface UserProductRepository extends JpaRepository<UserProduct, Long> {

    /**
     * 查询用户关联的产品列表
     *
     * @param userId 用户ID
     * @return 关联记录列表
     */
    List<UserProduct> findByUserId(Long userId);

    /**
     * 查询用户分配的产品ID列表
     *
     * @param userId 用户ID
     * @return 产品ID列表
     */
    @Query("SELECT up.productId FROM UserProduct up WHERE up.userId = :userId")
    List<Long> findProductIdsByUserId(Long userId);

    /**
     * 删除用户的所有产品分配
     *
     * @param userId 用户ID
     */
    @Modifying
    void deleteByUserId(Long userId);
}
