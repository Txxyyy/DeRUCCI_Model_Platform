package com.derucci.iot.ota.repository;

import com.derucci.iot.ota.entity.Firmware;
import com.derucci.iot.ota.entity.FirmwareStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** 固件数据访问层，提供固件的持久化操作 */
@Repository
public interface FirmwareRepository extends JpaRepository<Firmware, Long> {

    /**
     * 根据产品ID和版本号查找固件（唯一约束）
     *
     * @param productId 产品ID
     * @param version 固件版本号，如 "1.0.0"
     * @return 匹配的固件，不存在时返回空 Optional
     */
    Optional<Firmware> findByProductIdAndVersion(Long productId, String version);

    /**
     * 查询指定产品下的所有固件
     *
     * @param productId 产品ID
     * @return 该产品关联的固件列表，无结果时返回空列表
     */
    List<Firmware> findByProductId(Long productId);

    /**
     * 按状态查询固件列表
     *
     * @param status 固件状态（DRAFT / PUBLISHED）
     * @return 符合状态条件的固件列表，无结果时返回空列表
     */
    List<Firmware> findByStatus(FirmwareStatus status);
}
