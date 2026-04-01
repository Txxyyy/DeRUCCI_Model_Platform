package com.derucci.iot.thingmodel.repository;

import com.derucci.iot.thingmodel.entity.ThingModel;
import com.derucci.iot.thingmodel.entity.ThingModelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** 物模型数据访问层，提供物模型的持久化操作 */
@Repository
public interface ThingModelRepository extends JpaRepository<ThingModel, Long> {

    /**
     * 根据编码查询物模型
     *
     * @param code 物模型唯一编码
     * @return 物模型Optional，不存在时为空
     */
    Optional<ThingModel> findByCode(String code);

    /**
     * 判断指定编码的物模型是否存在
     *
     * @param code 物模型唯一编码
     * @return true表示编码已存在
     */
    boolean existsByCode(String code);

    /**
     * 按品类查询物模型列表
     *
     * @param category 产品品类名称
     * @return 该品类下的物模型列表
     */
    List<ThingModel> findByCategory(String category);

    /**
     * 按状态查询物模型列表
     *
     * @param status 物模型状态（DRAFT/PUBLISHED/DEPRECATED）
     * @return 该状态下的物模型列表
     */
    List<ThingModel> findByStatus(ThingModelStatus status);
}
