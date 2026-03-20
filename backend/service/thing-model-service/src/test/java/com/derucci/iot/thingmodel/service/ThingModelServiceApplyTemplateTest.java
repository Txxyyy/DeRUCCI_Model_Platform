package com.derucci.iot.thingmodel.service;

import com.derucci.iot.thingmodel.entity.ThingModel;
import com.derucci.iot.thingmodel.entity.ThingModelPoint;
import com.derucci.iot.thingmodel.entity.ThingModelTemplate;
import com.derucci.iot.thingmodel.repository.ThingModelRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("物模型模板应用测试")
class ThingModelServiceApplyTemplateTest {

    @Mock
    private ThingModelRepository thingModelRepository;
    @Mock
    private ThingModelPointService pointService;
    @Mock
    private ThingModelTemplateService templateService;

    private ThingModelService thingModelService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        thingModelService = new ThingModelService(
            thingModelRepository, pointService, templateService, objectMapper);
    }

    @Test
    @DisplayName("应用模板应复制功能点到目标物模型")
    void applyTemplate_shouldCopyPoints() {
        ThingModel tm = new ThingModel();
        tm.setId(1L);
        when(thingModelRepository.findById(1L)).thenReturn(Optional.of(tm));

        ThingModelTemplate tpl = new ThingModelTemplate();
        tpl.setId(10L);
        tpl.setPropertiesJson("[{\"id\":\"temperature\",\"name\":\"温度\",\"dataType\":\"float\",\"access\":\"readOnly\"}]");
        tpl.setEventsJson("[{\"id\":\"overheat\",\"name\":\"过热告警\",\"dataType\":\"string\"}]");
        tpl.setCommandsJson("[]");
        when(templateService.findById(10L)).thenReturn(Optional.of(tpl));
        when(pointService.create(any())).thenAnswer(i -> i.getArgument(0));

        thingModelService.applyTemplate(1L, 10L);

        verify(pointService).deleteByThingModelId(1L);
        ArgumentCaptor<ThingModelPoint> captor = ArgumentCaptor.forClass(ThingModelPoint.class);
        verify(pointService, times(2)).create(captor.capture());

        ThingModelPoint prop = captor.getAllValues().get(0);
        assertEquals("temperature", prop.getPointId());
        assertEquals("PROPERTY", prop.getPointType());
        assertEquals("readOnly", prop.getAccess());

        ThingModelPoint evt = captor.getAllValues().get(1);
        assertEquals("overheat", evt.getPointId());
        assertEquals("EVENT", evt.getPointType());
    }

    @Test
    @DisplayName("重复应用模板应先清空再导入")
    void applyTemplate_shouldClearBeforeImport() {
        ThingModel tm = new ThingModel();
        tm.setId(1L);
        when(thingModelRepository.findById(1L)).thenReturn(Optional.of(tm));

        ThingModelTemplate tpl = new ThingModelTemplate();
        tpl.setId(10L);
        tpl.setPropertiesJson("[{\"id\":\"p1\",\"name\":\"属性1\",\"dataType\":\"int\"}]");
        when(templateService.findById(10L)).thenReturn(Optional.of(tpl));
        when(pointService.create(any())).thenAnswer(i -> i.getArgument(0));

        // Apply twice
        thingModelService.applyTemplate(1L, 10L);
        thingModelService.applyTemplate(1L, 10L);

        // deleteByThingModelId called each time
        verify(pointService, times(2)).deleteByThingModelId(1L);
    }
}
