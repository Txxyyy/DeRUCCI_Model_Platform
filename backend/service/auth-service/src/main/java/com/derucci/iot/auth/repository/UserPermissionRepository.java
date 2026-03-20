package com.derucci.iot.auth.repository;

import com.derucci.iot.auth.entity.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

/**
 * 用户权限Repository
 */
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    List<UserPermission> findByUserId(Long userId);
    @Modifying
    void deleteByUserId(Long userId);
}
