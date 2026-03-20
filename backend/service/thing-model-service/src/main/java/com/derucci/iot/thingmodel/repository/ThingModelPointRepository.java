package com.derucci.iot.thingmodel.repository;

import com.derucci.iot.thingmodel.entity.ThingModelPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** 物模型功能点数据访问层，提供功能点的持久化操作 */
@Repository
public interface ThingModelPointRepository extends JpaRepository<ThingModelPoint, Long> {

    /**
     * 查询指定物模型下的所有功能点
     *
     * @param thingModelId 物模型ID
     * @return 功能点列表
     */
    List<ThingModelPoint> findByThingModelId(Long thingModelId);

    /**
     * 根据物模型ID和功能点标识查询功能点
     *
     * @param thingModelId 物模型ID
     * @param pointId 功能点标识符
     * @return 功能点Optional，不存在时为空
     */
    Optional<ThingModelPoint> findByThingModelIdAndPointId(Long thingModelId, String pointId);

    /**
     * 判断指定物模型下是否存在该功能点标识
     *
     * @param thingModelId 物模型ID
     * @param pointId 功能点标识符
     * @return true表示标识已存在
     */
    boolean existsByThingModelIdAndPointId(Long thingModelId, String pointId);

    /**
     * 删除指定物模型下的所有功能点
     *
     * @param thingModelId 物模型ID
     */
    void deleteByThingModelId(Long thingModelId);

    /**
     * 统计指定物模型下的功能点数量
     *
     * @param thingModelId 物模型ID
     * @return 功能点数量
     */
    long countByThingModelId(Long thingModelId);
}
