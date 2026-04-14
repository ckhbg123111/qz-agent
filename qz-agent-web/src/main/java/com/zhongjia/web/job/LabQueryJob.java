package com.zhongjia.web.job;

import com.zhongjia.biz.entity.LabQueryCursor;
import com.zhongjia.biz.entity.LabQueryRecord;
import com.zhongjia.biz.service.LabQueryCursorService;
import com.zhongjia.biz.service.LabQueryRecordService;
import com.zhongjia.web.config.LabQueryProperties;
import com.zhongjia.web.integration.lab.LabQueryClient;
import com.zhongjia.web.integration.lab.LabQueryRequest;
import com.zhongjia.web.integration.lab.LabQueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Component
public class LabQueryJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(LabQueryJob.class);
    private static final ZoneId SHANGHAI_ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";

    private final LabQueryProperties properties;
    private final LabQueryClient labQueryClient;
    private final LabQueryCursorService labQueryCursorService;
    private final LabQueryRecordService labQueryRecordService;
    private final Clock clock;

    @Autowired
    public LabQueryJob(
            LabQueryProperties properties,
            LabQueryClient labQueryClient,
            LabQueryCursorService labQueryCursorService,
            LabQueryRecordService labQueryRecordService
    ) {
        this(properties, labQueryClient, labQueryCursorService, labQueryRecordService, Clock.system(SHANGHAI_ZONE_ID));
    }

    LabQueryJob(
            LabQueryProperties properties,
            LabQueryClient labQueryClient,
            LabQueryCursorService labQueryCursorService,
            LabQueryRecordService labQueryRecordService,
            Clock clock
    ) {
        this.properties = properties;
        this.labQueryClient = labQueryClient;
        this.labQueryCursorService = labQueryCursorService;
        this.labQueryRecordService = labQueryRecordService;
        this.clock = clock;
    }

    @Scheduled(fixedDelayString = "${lab.query.fixed-delay-ms:600000}")
    public void queryOutpatientLabApplications() {
        if (!properties.isEnabled()) {
            return;
        }
        List<String> patientIds = properties.getPatientIdList();
        if (patientIds.isEmpty()) {
            LOGGER.warn("门诊检验定时查询已启用，但未配置 patientIds，已跳过执行");
            return;
        }

        LabQueryCursor cursor = loadOrCreateCursor();
        LocalDateTime queryEndTime = LocalDateTime.now(clock);
        LocalDateTime queryStartTime = cursor.getLastQueryEndTime() != null
                ? cursor.getLastQueryEndTime()
                : queryEndTime.minusMinutes(properties.getInitialLookbackMinutes());
        String currentPatientId = null;

        try {
            for (String patientId : patientIds) {
                currentPatientId = patientId;
                LabQueryRequest request = buildRequest(patientId, queryStartTime, queryEndTime);
                LabQueryResponse response = labQueryClient.queryApplications(request);
                saveSuccessRecord(request, response, queryStartTime, queryEndTime);
                if (!response.isSuccess()) {
                    updateCursor(cursor, cursor.getLastQueryStartTime(), cursor.getLastQueryEndTime(), STATUS_FAILED, response.resultDesc());
                    LOGGER.warn("门诊检验定时查询返回业务失败: taskName={}, patientId={}, resultCode={}, resultDesc={}",
                            properties.getTaskName(), patientId, response.resultCode(), response.resultDesc());
                    return;
                }
            }
            updateCursor(cursor, queryStartTime, queryEndTime, STATUS_SUCCESS, "");
            LOGGER.info("门诊检验定时查询完成: taskName={}, patientCount={}, windowStart={}, windowEnd={}",
                    properties.getTaskName(), patientIds.size(), queryStartTime, queryEndTime);
        } catch (Exception ex) {
            saveFailureRecord(currentPatientId, queryStartTime, queryEndTime, ex.getMessage());
            updateCursor(cursor, cursor.getLastQueryStartTime(), cursor.getLastQueryEndTime(), STATUS_FAILED, ex.getMessage());
            LOGGER.error("门诊检验定时查询失败: taskName={}, windowStart={}, windowEnd={}",
                    properties.getTaskName(), queryStartTime, queryEndTime, ex);
        }
    }

    LabQueryRequest buildRequest(String patientId, LocalDateTime queryStartTime, LocalDateTime queryEndTime) {
        return new LabQueryRequest(
                properties.getSender(),
                properties.getReceiver(),
                UUID.randomUUID().toString(),
                properties.getMessageQueryName(),
                properties.getQueryTag(),
                properties.getPatientType(),
                patientId,
                properties.getVisitId(),
                properties.getOrgIdExec(),
                queryStartTime,
                queryEndTime,
                properties.getIsFilter()
        );
    }

    private LabQueryCursor loadOrCreateCursor() {
        LabQueryCursor cursor = labQueryCursorService.lambdaQuery()
                .eq(LabQueryCursor::getTaskName, properties.getTaskName())
                .one();
        if (cursor != null) {
            return cursor;
        }
        LocalDateTime now = LocalDateTime.now(clock);
        LabQueryCursor created = new LabQueryCursor();
        created.setTaskName(properties.getTaskName());
        created.setLastStatus("INIT");
        created.setLastErrorMessage("");
        created.setCreateTime(now);
        created.setUpdateTime(now);
        labQueryCursorService.save(created);
        return created;
    }

    private void saveSuccessRecord(
            LabQueryRequest request,
            LabQueryResponse response,
            LocalDateTime queryStartTime,
            LocalDateTime queryEndTime
    ) {
        LabQueryRecord record = new LabQueryRecord();
        record.setTaskName(properties.getTaskName());
        record.setPatientId(request.patientId());
        record.setQueryTag(request.queryTag());
        record.setQueryStartTime(queryStartTime);
        record.setQueryEndTime(queryEndTime);
        record.setMsgId(response.msgId());
        record.setRequestXml(response.requestXml());
        record.setResponseXml(response.responseXml());
        record.setResultCode(response.resultCode());
        record.setResultDesc(response.resultDesc());
        record.setOrderCount(response.orderCount());
        record.setStatus(response.isSuccess() ? STATUS_SUCCESS : STATUS_FAILED);
        record.setErrorMessage(response.isSuccess() ? "" : response.resultDesc());
        record.setCreateTime(LocalDateTime.now(clock));
        labQueryRecordService.save(record);
    }

    private void saveFailureRecord(
            String patientId,
            LocalDateTime queryStartTime,
            LocalDateTime queryEndTime,
            String errorMessage
    ) {
        LabQueryRecord record = new LabQueryRecord();
        record.setTaskName(properties.getTaskName());
        record.setPatientId(patientId == null ? "" : patientId);
        record.setQueryTag(properties.getQueryTag());
        record.setQueryStartTime(queryStartTime);
        record.setQueryEndTime(queryEndTime);
        record.setMsgId("");
        record.setRequestXml("");
        record.setResponseXml("");
        record.setResultCode("");
        record.setResultDesc("");
        record.setOrderCount(0);
        record.setStatus(STATUS_FAILED);
        record.setErrorMessage(trimErrorMessage(errorMessage));
        record.setCreateTime(LocalDateTime.now(clock));
        labQueryRecordService.save(record);
    }

    private void updateCursor(
            LabQueryCursor cursor,
            LocalDateTime queryStartTime,
            LocalDateTime queryEndTime,
            String status,
            String errorMessage
    ) {
        LocalDateTime now = LocalDateTime.now(clock);
        cursor.setLastQueryStartTime(queryStartTime);
        cursor.setLastQueryEndTime(queryEndTime);
        cursor.setLastStatus(status);
        cursor.setLastErrorMessage(trimErrorMessage(errorMessage));
        if (cursor.getCreateTime() == null) {
            cursor.setCreateTime(now);
        }
        cursor.setUpdateTime(now);
        labQueryCursorService.saveOrUpdate(cursor);
    }

    private String trimErrorMessage(String errorMessage) {
        if (errorMessage == null) {
            return "";
        }
        return errorMessage.length() <= 255 ? errorMessage : errorMessage.substring(0, 255);
    }
}
