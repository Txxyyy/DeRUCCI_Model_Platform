package com.derucci.iot.thingmodel.repository;

import com.derucci.iot.thingmodel.entity.ThingModel;
import com.derucci.iot.thingmodel.entity.ThingModelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThingModelRepository extends JpaRepository<ThingModel, Long> {

    Optional<ThingModel> findByCode(String code);

    boolean existsByCode(String code);

    List<ThingModel> findByCategory(String category);

    List<ThingModel> findByStatus(ThingModelStatus status);
}
