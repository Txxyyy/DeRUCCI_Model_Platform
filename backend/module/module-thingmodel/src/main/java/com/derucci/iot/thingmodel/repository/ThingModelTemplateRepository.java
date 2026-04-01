package com.derucci.iot.thingmodel.repository;

import com.derucci.iot.thingmodel.entity.ThingModelTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThingModelTemplateRepository extends JpaRepository<ThingModelTemplate, Long> {

    Optional<ThingModelTemplate> findByCode(String code);

    boolean existsByCode(String code);

    /**
     * 根据产品品类查询推荐模板
     */
    List<ThingModelTemplate> findByCategory(String category);

    /**
     * 查询所有系统预置模板
     */
    List<ThingModelTemplate> findBySystemTrue();
}
