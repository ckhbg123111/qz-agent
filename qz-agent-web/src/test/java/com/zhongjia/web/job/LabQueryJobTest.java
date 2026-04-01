package com.zhongjia.web.job;

import com.zhongjia.biz.entity.LabQueryCursor;
import com.zhongjia.biz.entity.LabQueryRecord;
import com.zhongjia.biz.service.LabQueryCursorService;
import com.zhongjia.biz.service.LabQueryRecordService;
import com.zhongjia.web.config.LabQueryProperties;
import com.zhongjia.web.exception.BizException;
import com.zhongjia.web.integration.lab.LabQueryClient;
import com.zhongjia.web.integration.lab.LabQueryResponse;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LabQueryJobTest {

    private static final ZoneId SHANGHAI = ZoneId.of("Asia/Shanghai");

    @Mock
    private LabQueryClient labQueryClient;

    @Mock
    private LabQueryCursorService labQueryCursorService;

    @Mock
    private LabQueryRecordService labQueryRecordService;

    @Mock
    private LambdaQueryChainWrapper<LabQueryCursor> cursorQuery;

    private LabQueryProperties properties;
    private LabQueryJob job;

    @BeforeEach
    void setUp() {
        properties = new LabQueryProperties();
        properties.setEnabled(true);
        properties.setTaskName("outpatient-lab-query");
        properties.setPatientIds("P1001");
        properties.setOrgIdExec("2301");
        properties.setInitialLookbackMinutes(10);

        when(labQueryCursorService.lambdaQuery()).thenReturn(cursorQuery);
        when(cursorQuery.eq(any(), any())).thenReturn(cursorQuery);

        Clock clock = Clock.fixed(Instant.parse("2026-04-01T02:10:00Z"), SHANGHAI);
        job = new LabQueryJob(properties, labQueryClient, labQueryCursorService, labQueryRecordService, clock);
    }

    @Test
    void shouldAdvanceCursorWhenQuerySucceeds() {
        LabQueryCursor cursor = new LabQueryCursor();
        cursor.setId(1L);
        cursor.setTaskName("outpatient-lab-query");
        cursor.setLastQueryStartTime(LocalDateTime.of(2026, 4, 1, 9, 40, 0));
        cursor.setLastQueryEndTime(LocalDateTime.of(2026, 4, 1, 10, 0, 0));
        when(cursorQuery.one()).thenReturn(cursor);

        when(labQueryClient.queryApplications(any())).thenReturn(new LabQueryResponse(
                "MSG-1",
                "<Request/>",
                "<SoapRequest/>",
                "<Response/>",
                "<SoapResponse/>",
                "CA",
                "",
                2
        ));

        job.queryOutpatientLabApplications();

        ArgumentCaptor<LabQueryRecord> recordCaptor = ArgumentCaptor.forClass(LabQueryRecord.class);
        verify(labQueryRecordService).save(recordCaptor.capture());
        assertEquals("P1001", recordCaptor.getValue().getPatientId());
        assertEquals("SUCCESS", recordCaptor.getValue().getStatus());
        assertEquals(2, recordCaptor.getValue().getOrderCount());

        ArgumentCaptor<LabQueryCursor> cursorCaptor = ArgumentCaptor.forClass(LabQueryCursor.class);
        verify(labQueryCursorService).saveOrUpdate(cursorCaptor.capture());
        assertEquals(LocalDateTime.of(2026, 4, 1, 10, 0, 0), cursorCaptor.getValue().getLastQueryStartTime());
        assertEquals(LocalDateTime.of(2026, 4, 1, 10, 10, 0), cursorCaptor.getValue().getLastQueryEndTime());
        assertEquals("SUCCESS", cursorCaptor.getValue().getLastStatus());
    }

    @Test
    void shouldKeepCursorWhenQueryFails() {
        LabQueryCursor cursor = new LabQueryCursor();
        cursor.setId(1L);
        cursor.setTaskName("outpatient-lab-query");
        cursor.setLastQueryStartTime(LocalDateTime.of(2026, 4, 1, 9, 40, 0));
        cursor.setLastQueryEndTime(LocalDateTime.of(2026, 4, 1, 10, 0, 0));
        when(cursorQuery.one()).thenReturn(cursor);

        when(labQueryClient.queryApplications(any())).thenThrow(new BizException(502, "调用失败"));

        job.queryOutpatientLabApplications();

        ArgumentCaptor<LabQueryRecord> recordCaptor = ArgumentCaptor.forClass(LabQueryRecord.class);
        verify(labQueryRecordService).save(recordCaptor.capture());
        assertEquals("FAILED", recordCaptor.getValue().getStatus());
        assertEquals("P1001", recordCaptor.getValue().getPatientId());

        ArgumentCaptor<LabQueryCursor> cursorCaptor = ArgumentCaptor.forClass(LabQueryCursor.class);
        verify(labQueryCursorService).saveOrUpdate(cursorCaptor.capture());
        assertEquals(LocalDateTime.of(2026, 4, 1, 9, 40, 0), cursorCaptor.getValue().getLastQueryStartTime());
        assertEquals(LocalDateTime.of(2026, 4, 1, 10, 0, 0), cursorCaptor.getValue().getLastQueryEndTime());
        assertEquals("FAILED", cursorCaptor.getValue().getLastStatus());
    }
}
