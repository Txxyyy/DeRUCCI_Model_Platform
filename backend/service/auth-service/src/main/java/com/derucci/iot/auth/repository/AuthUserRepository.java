package com.derucci.iot.auth.repository;

import com.derucci.iot.auth.entity.AuthUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户Repository（auth-service使用）
 */
public interface AuthUserRepository extends JpaRepository<AuthUserEntity, Long> {
    Optional<AuthUserEntity> findByUsername(String username);
}
