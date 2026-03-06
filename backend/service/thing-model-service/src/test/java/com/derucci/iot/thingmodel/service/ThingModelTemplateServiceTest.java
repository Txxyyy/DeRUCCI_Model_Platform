package com.derucci.iot.thingmodel.service;

import com.derucci.iot.thingmodel.entity.ThingModelTemplate;
import com.derucci.iot.thingmodel.repository.ThingModelTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 物模型模板服务测试
 * 按照TDD流程开发
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("物模型模板服务测试")
class ThingModelTemplateServiceTest {

    @Mock
    private ThingModelTemplateRepository templateRepository;

    @InjectMocks
    private ThingModelTemplateService templateService;

    private ThingModelTemplate testTemplate;

    @BeforeEach
    void setUp() {
        testTemplate = new ThingModelTemplate();
        testTemplate.setId(1L);
        testTemplate.setName("智能床垫基础模板");
        testTemplate.setCode("TM_BASIC_MATTRESS");
        testTemplate.setCategory("智能床垫");
        testTemplate.setSystem(true);
        testTemplate.setVersion("1.0.0");
        testTemplate.setPropertyCount(16);
        testTemplate.setEventCount(6);
        testTemplate.setCommandCount(9);
    }

    @Test
    @DisplayName("根据产品品类获取推荐模板")
    void testFindByCategory() {
        // Given: 存在多个模板
        List<ThingModelTemplate> templates = Arrays.asList(
            createTemplate("智能床垫基础模板", "智能床垫"),
            createTemplate("智能床垫进阶模板", "智能床垫")
        );
        when(templateRepository.findByCategory("智能床垫")).thenReturn(templates);

        // When: 按品类查询
        List<ThingModelTemplate> result = templateService.findByCategory("智能床垫");

        // Then: 返回对应品类的模板
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> "智能床垫".equals(t.getCategory())));
    }

    @Test
    @DisplayName("获取模板详情")
    void testFindById() {
        // Given:
        when(templateRepository.findById(1L)).thenReturn(Optional.of(testTemplate));

        // When:
        Optional<ThingModelTemplate> result = templateService.findById(1L);

        // Then:
        assertTrue(result.isPresent());
        assertEquals("智能床垫基础模板", result.get().getName());
    }

    @Test
    @DisplayName("创建模板应检查编码唯一性")
    void testCreateTemplateChecksUniqueness() {
        // Given: 模板编码已存在
        ThingModelTemplate newTemplate = new ThingModelTemplate();
        newTemplate.setName("新模板");
        newTemplate.setCode("TM_BASIC_MATTRESS");
        when(templateRepository.existsByCode("TM_BASIC_MATTRESS")).thenReturn(true);

        // When & Then: 应抛出异常
        assertThrows(IllegalArgumentException.class,
            () -> templateService.create(newTemplate),
            "模板编码已存在时应抛出异常");
    }

    @Test
    @DisplayName("创建系统模板")
    void testCreateSystemTemplate() {
        // Given:
        ThingModelTemplate template = new ThingModelTemplate();
        template.setName("智能床垫基础模板");
        template.setCode("TM_NEW_TEMPLATE");
        template.setCategory("智能床垫");
        template.setSystem(true);
        
        when(templateRepository.existsByCode("TM_NEW_TEMPLATE")).thenReturn(false);
        when(templateRepository.save(any(ThingModelTemplate.class))).thenAnswer(invocation -> {
            ThingModelTemplate t = invocation.getArgument(0);
            t.setId(2L);
            return t;
        });

        // When:
        ThingModelTemplate result = templateService.create(template);

        // Then:
        assertNotNull(result.getId());
        assertTrue(result.getSystem());
    }

    @Test
    @DisplayName("删除模板")
    void testDeleteTemplate() {
        // Given:
        when(templateRepository.existsById(1L)).thenReturn(true);
        doNothing().when(templateRepository).deleteById(1L);

        // When:
        templateService.delete(1L);

        // Then:
        verify(templateRepository).deleteById(1L);
    }

    @Test
    @DisplayName("删除不存在的模板应抛出异常")
    void testDeleteNonExistentTemplate() {
        // Given:
        when(templateRepository.existsById(999L)).thenReturn(false);

        // When & Then:
        assertThrows(IllegalArgumentException.class,
            () -> templateService.delete(999L),
            "模板不存在时应抛出异常");
    }

    private ThingModelTemplate createTemplate(String name, String category) {
        ThingModelTemplate t = new ThingModelTemplate();
        t.setName(name);
        t.setCategory(category);
        return t;
    }
}
