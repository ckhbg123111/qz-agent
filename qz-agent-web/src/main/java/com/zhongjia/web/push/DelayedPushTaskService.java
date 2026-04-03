package com.zhongjia.web.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongjia.biz.entity.WechatPushTask;
import com.zhongjia.biz.service.WechatPushTaskService;
import com.zhongjia.web.config.PushTaskProperties;
import com.zhongjia.web.exception.BizException;
import com.zhongjia.web.vo.qz.QzHpC13ReportRequest;
import com.zhongjia.web.vo.wechat.WechatMessageRequest;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class DelayedPushTaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedPushTaskService.class);
    private static final ZoneId SHANGHAI_ZONE_ID = ZoneId.of("Asia/Shanghai");

    private final WechatPushTaskService wechatPushTaskService;
    private final StringRedisTemplate stringRedisTemplate;
    private final PushTaskProperties pushTaskProperties;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    public DelayedPushTaskService(
            WechatPushTaskService wechatPushTaskService,
            StringRedisTemplate stringRedisTemplate,
            PushTaskProperties pushTaskProperties,
            ObjectMapper objectMapper,
            MeterRegistry meterRegistry
    ) {
        this.wechatPushTaskService = wechatPushTaskService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.pushTaskProperties = pushTaskProperties;
        this.objectMapper = objectMapper;
        this.meterRegistry = meterRegistry;
    }

    public Long createReportWarningTask(QzHpC13ReportRequest request) {
        if (isNegativeReportSuggestion(request.getSuggestion())) {
            meterRegistry.counter("push.task.create.skipped", "taskType",
                    PushTaskConstants.TASK_TYPE_REPORT_WARNING, "reason", "negative_suggestion").increment();
            LOGGER.info("报告推送任务已跳过: taskType={}, patientId={}, reportNo={}, reason=negative_suggestion",
                    PushTaskConstants.TASK_TYPE_REPORT_WARNING,
                    resolvePatientIdForPush(request.getPatientId()),
                    defaultString(request.getReportNo()));
            return null;
        }

        LocalDateTime now = LocalDateTime.now(SHANGHAI_ZONE_ID);
        LocalDateTime baseTime = resolveBusinessTime(request.getTestDate(), now);
        LocalDateTime triggerTime = calculateReportTriggerTime(baseTime, now);
        String effectivePatientId = resolvePatientIdForPush(request.getPatientId());
        WechatMessageRequest wechatRequest = buildWechatRequest(
                PushTaskConstants.TAG_REPORT,
                effectivePatientId,
                request.getPatientName(),
                request.getGender(),
                request.getAge(),
                "",
                "",
                toIsoOffset(baseTime),
                ""
        );
        String sourceNo = firstNonBlank(request.getReportNo(), request.getVisitNo());
        return createTask(
                PushTaskConstants.TASK_TYPE_REPORT_WARNING,
                effectivePatientId,
                PushTaskConstants.TAG_REPORT,
                sourceNo,
                baseTime,
                triggerTime,
                wechatRequest
        );
    }

    public Long createFollowUpTask(QzHpC13ReportRequest request) {
        LocalDateTime now = LocalDateTime.now(SHANGHAI_ZONE_ID);
        LocalDateTime baseTime = resolveBusinessTime(request.getTestDate(), now);
        LocalDateTime triggerTime = calculateFollowUpTriggerTime(baseTime, now);
        String effectivePatientId = resolvePatientIdForPush(request.getPatientId());
        WechatMessageRequest wechatRequest = buildWechatRequest(
                PushTaskConstants.TAG_FOLLOW_UP,
                effectivePatientId,
                request.getPatientName(),
                request.getGender(),
                request.getAge(),
                "",
                "",
                toIsoOffset(baseTime),
                ""
        );
        String sourceNo = firstNonBlank(request.getReportNo(), request.getVisitNo());
        return createTask(
                PushTaskConstants.TASK_TYPE_FOLLOW_UP_REMINDER,
                effectivePatientId,
                PushTaskConstants.TAG_FOLLOW_UP,
                sourceNo,
                baseTime,
                triggerTime,
                wechatRequest
        );
    }

    public boolean enqueueTask(Long taskId, LocalDateTime executeTime) {
        String member = String.valueOf(taskId);
        double score = executeTime.atZone(SHANGHAI_ZONE_ID).toInstant().toEpochMilli();
        try {
            stringRedisTemplate.opsForZSet().add(pushTaskProperties.getZsetKey(), member, score);
            return true;
        } catch (Exception ex) {
            LOGGER.error("推送任务入队失败: taskId={}, executeTime={}", taskId, executeTime, ex);
            return false;
        }
    }

    private Long createTask(
            String taskType,
            String patientId,
            String tag,
            String sourceNo,
            LocalDateTime baseTime,
            LocalDateTime triggerTime,
            WechatMessageRequest wechatRequest
    ) {
        String normalizedPatientId = defaultString(patientId);
        if (normalizedPatientId.isBlank()) {
            throw new BizException(400, "patientId不能为空");
        }

        String idempotentKey = buildIdempotentKey(taskType, normalizedPatientId, sourceNo, triggerTime);
        WechatPushTask existingTask = wechatPushTaskService.lambdaQuery()
                .eq(WechatPushTask::getIdempotentKey, idempotentKey)
                .one();
        if (existingTask != null) {
            meterRegistry.counter("push.task.create.deduplicated", "taskType", taskType).increment();
            return existingTask.getId();
        }

        WechatPushTask task = new WechatPushTask();
        task.setTaskType(taskType);
        task.setPatientId(normalizedPatientId);
        task.setTag(tag);
        task.setSourceNo(defaultString(sourceNo));
        task.setIdempotentKey(idempotentKey);
        task.setBaseTime(baseTime);
        task.setTriggerTime(triggerTime);
        task.setNextRetryTime(triggerTime);
        task.setRetryCount(0);
        task.setMaxRetryCount(pushTaskProperties.getMaxRetryCount());
        task.setStatus(PushTaskConstants.TASK_STATUS_PENDING);
        task.setEnqueueStatus(PushTaskConstants.ENQUEUE_STATUS_WAITING);
        task.setRequestJson(toJson(wechatRequest));
        task.setLastErrorMessage("");
        task.setCreateTime(LocalDateTime.now(SHANGHAI_ZONE_ID));
        task.setUpdateTime(LocalDateTime.now(SHANGHAI_ZONE_ID));

        try {
            wechatPushTaskService.save(task);
        } catch (DuplicateKeyException duplicateKeyException) {
            WechatPushTask duplicated = wechatPushTaskService.lambdaQuery()
                    .eq(WechatPushTask::getIdempotentKey, idempotentKey)
                    .one();
            if (duplicated != null) {
                meterRegistry.counter("push.task.create.deduplicated", "taskType", taskType).increment();
                return duplicated.getId();
            }
            throw duplicateKeyException;
        }
        meterRegistry.counter("push.task.create.success", "taskType", taskType).increment();

        boolean enqueued = enqueueTask(task.getId(), triggerTime);
        wechatPushTaskService.lambdaUpdate()
                .eq(WechatPushTask::getId, task.getId())
                .set(WechatPushTask::getEnqueueStatus, enqueued
                        ? PushTaskConstants.ENQUEUE_STATUS_ENQUEUED
                        : PushTaskConstants.ENQUEUE_STATUS_WAITING)
                .update();
        if (!enqueued) {
            meterRegistry.counter("push.task.enqueue.failed", "taskType", taskType).increment();
        }
        LOGGER.info("推送任务创建完成: taskId={}, taskType={}, patientId={}, triggerTime={}, enqueueStatus={}",
                task.getId(), taskType, normalizedPatientId, triggerTime,
                enqueued ? PushTaskConstants.ENQUEUE_STATUS_ENQUEUED : PushTaskConstants.ENQUEUE_STATUS_WAITING);
        return task.getId();
    }

    public boolean isNegativeReportSuggestion(String suggestion) {
        return containsNegativeSuggestion(suggestion);
    }

    private String buildIdempotentKey(String taskType, String patientId, String sourceNo, LocalDateTime triggerTime) {
        return taskType + "|" + patientId + "|" + defaultString(sourceNo) + "|" + triggerTime;
    }

    public String resolvePatientIdForPush(String patientId) {
        String normalizedPatientId = defaultString(patientId).trim();
        if (!pushTaskProperties.isTestMode()) {
            return normalizedPatientId;
        }
        String fixedPatientId = defaultString(pushTaskProperties.getTestFixedPatientId()).trim();
        if (fixedPatientId.isEmpty()) {
            LOGGER.warn("测试模式已开启但未配置 push.task.test-fixed-patient-id，沿用请求 patientId");
            return normalizedPatientId;
        }
        return fixedPatientId;
    }

    private LocalDateTime calculateReportTriggerTime(LocalDateTime baseTime, LocalDateTime now) {
        if (pushTaskProperties.isTestMode()) {
            return now.plusMinutes(Math.max(pushTaskProperties.getReportDelayMinutesForTest(), 1));
        }
        int delayDays = Math.max(pushTaskProperties.getReportDelayDays(), 0);
        return toConfiguredHour(baseTime.plusDays(delayDays));
    }

    private LocalDateTime calculateFollowUpTriggerTime(LocalDateTime baseTime, LocalDateTime now) {
        if (pushTaskProperties.isTestMode()) {
            return now.plusMinutes(Math.max(pushTaskProperties.getFollowUpDelayMinutesForTest(), 1));
        }
        int delayDays = Math.max(pushTaskProperties.getFollowUpDelayDays(), 0);
        return toConfiguredHour(baseTime.plusDays(delayDays));
    }

    private LocalDateTime toConfiguredHour(LocalDateTime time) {
        int hour = pushTaskProperties.getTriggerHour();
        if (hour < 0 || hour > 23) {
            LOGGER.warn("push.task.trigger-hour={} 非法，回退到10点", hour);
            hour = 10;
        }
        return time.toLocalDate().atTime(hour, 0, 0);
    }

    private String toIsoOffset(LocalDateTime time) {
        return time.atZone(SHANGHAI_ZONE_ID).toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private LocalDateTime resolveBusinessTime(String value, LocalDateTime fallbackTime) {
        if (value == null || value.isBlank()) {
            return fallbackTime;
        }
        try {
            return OffsetDateTime.parse(value).atZoneSameInstant(SHANGHAI_ZONE_ID).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            // ignored
        }
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // ignored
        }
        try {
            return LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // ignored
        }
        throw new BizException(400, "业务时间格式错误");
    }

    private WechatMessageRequest buildWechatRequest(
            String tag,
            String patientId,
            String patientName,
            String gender,
            Integer age,
            String diagnosis,
            String prescription,
            String examTime,
            String reminderContent
    ) {
        WechatMessageRequest wechatRequest = new WechatMessageRequest();
        wechatRequest.setTag(defaultString(tag));
        wechatRequest.setPatientId(defaultString(patientId));
        wechatRequest.setPatientName(defaultString(patientName));
        wechatRequest.setGender(defaultString(gender));
        wechatRequest.setAge(age);
        wechatRequest.setDiagnosis(defaultString(diagnosis));
        wechatRequest.setPrescription(defaultString(prescription));
        wechatRequest.setExamTime(defaultString(examTime));
        wechatRequest.setReminderContent(defaultString(reminderContent));
        return wechatRequest;
    }

    private String toJson(WechatMessageRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            throw new BizException(500, "推送任务序列化失败");
        }
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private String firstNonBlank(String primary, String fallback) {
        String normalizedPrimary = defaultString(primary).trim();
        if (!normalizedPrimary.isEmpty()) {
            return normalizedPrimary;
        }
        return defaultString(fallback).trim();
    }

    private boolean containsNegativeSuggestion(String suggestion) {
        return defaultString(suggestion).trim().contains("阴性");
    }
}
