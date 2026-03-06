package com.derucci.iot.thingmodel.repository;

import com.derucci.iot.thingmodel.entity.ThingModelPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThingModelPointRepository extends JpaRepository<ThingModelPoint, Long> {

    List<ThingModelPoint> findByThingModelId(Long thingModelId);

    Optional<ThingModelPoint> findByThingModelIdAndPointId(Long thingModelId, String pointId);

    boolean existsByThingModelIdAndPointId(Long thingModelId, String pointId);

    void deleteByThingModelId(Long thingModelId);

    long countByThingModelId(Long thingModelId);
}
