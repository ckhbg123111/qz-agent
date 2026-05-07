package com.zhongjia.web.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongjia.biz.entity.WechatPushTask;
import com.zhongjia.biz.service.WechatPushTaskService;
import com.zhongjia.web.config.PushTaskProperties;
import com.zhongjia.web.vo.wechat.WechatMessageRequest;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DelayedPushTaskDispatcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedPushTaskDispatcher.class);
    private static final ZoneId SHANGHAI_ZONE_ID = ZoneId.of("Asia/Shanghai");

    private final StringRedisTemplate stringRedisTemplate;
    private final PushTaskProperties pushTaskProperties;
    private final WechatPushTaskService wechatPushTaskService;
    private final ObjectMapper objectMapper;
    private final DelayedPushTaskService delayedPushTaskService;
    private final WechatPushExecutor wechatPushExecutor;
    private final MeterRegistry meterRegistry;

    public DelayedPushTaskDispatcher(
            StringRedisTemplate stringRedisTemplate,
            PushTaskProperties pushTaskProperties,
            WechatPushTaskService wechatPushTaskService,
            ObjectMapper objectMapper,
            DelayedPushTaskService delayedPushTaskService,
            WechatPushExecutor wechatPushExecutor,
            MeterRegistry meterRegistry
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.pushTaskProperties = pushTaskProperties;
        this.wechatPushTaskService = wechatPushTaskService;
        this.objectMapper = objectMapper;
        this.delayedPushTaskService = delayedPushTaskService;
        this.wechatPushExecutor = wechatPushExecutor;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedDelayString = "${push.task.consumer-fixed-delay-ms:5000}")
    public void dispatchDueTasks() {
        long nowEpochMillis = System.currentTimeMillis();
        Set<String> dueMembers = stringRedisTemplate.opsForZSet().rangeByScore(
                pushTaskProperties.getZsetKey(),
                0,
                nowEpochMillis,
                0,
                pushTaskProperties.getBatchSize()
        );
        if (dueMembers == null || dueMembers.isEmpty()) {
            return;
        }
        for (String member : dueMembers) {
            if (member == null || member.isBlank()) {
                continue;
            }
            Long removed = stringRedisTemplate.opsForZSet().remove(pushTaskProperties.getZsetKey(), member);
            if (removed == null || removed == 0) {
                continue;
            }
            processTaskMember(member);
        }
    }

    private void processTaskMember(String member) {
        Long taskId;
        try {
            taskId = Long.valueOf(member);
        } catch (NumberFormatException ex) {
            LOGGER.warn("无效推送任务成员: {}", member);
            return;
        }

        LocalDateTime now = LocalDateTime.now(SHANGHAI_ZONE_ID);
        boolean claimed = wechatPushTaskService.lambdaUpdate()
                .eq(WechatPushTask::getId, taskId)
                .in(WechatPushTask::getStatus, PushTaskConstants.TASK_STATUS_PENDING, PushTaskConstants.TASK_STATUS_FAILED)
                .le(WechatPushTask::getNextRetryTime, now)
                .set(WechatPushTask::getStatus, PushTaskConstants.TASK_STATUS_SENDING)
                .set(WechatPushTask::getUpdateTime, now)
                .update();
        if (!claimed) {
            return;
        }

        WechatPushTask task = wechatPushTaskService.getById(taskId);
        if (task == null) {
            return;
        }
        executeTask(task);
    }

    private void executeTask(WechatPushTask task) {
        LocalDateTime now = LocalDateTime.now(SHANGHAI_ZONE_ID);
        try {
            WechatMessageRequest messageRequest = objectMapper.readValue(task.getRequestJson(), WechatMessageRequest.class);
            PushExecutionResult result = wechatPushExecutor.executeSerialized(
                    messageRequest,
                    task.getRequestJson(),
                    task.getTaskType(),
                    task.getId()
            );

            wechatPushTaskService.lambdaUpdate()
                    .eq(WechatPushTask::getId, task.getId())
                    .set(WechatPushTask::getStatus, PushTaskConstants.TASK_STATUS_SUCCESS)
                    .set(WechatPushTask::getEnqueueStatus, PushTaskConstants.ENQUEUE_STATUS_ENQUEUED)
                    .set(WechatPushTask::getLastErrorMessage, "")
                    .set(WechatPushTask::getUpdateTime, now)
                    .update();
            String resultTag = result.isSkipped() ? "skipped_success_record" : "pushed";
            meterRegistry.counter("push.task.dispatch.success", "taskType", task.getTaskType(), "result", resultTag).increment();
            LOGGER.info("延时推送处理完成: taskId={}, taskType={}, patientId={}, retryCount={}, result={}",
                    task.getId(), task.getTaskType(), task.getPatientId(), task.getRetryCount(), resultTag);
        } catch (Exception ex) {
            LOGGER.error("延时推送执行失败: taskId={}, patientId={}, tag={}", task.getId(), task.getPatientId(), task.getTag(), ex);
            handleTaskFailure(task, ex, now);
        }
    }

    private void handleTaskFailure(WechatPushTask task, Exception ex, LocalDateTime now) {
        int currentRetry = task.getRetryCount() == null ? 0 : task.getRetryCount();
        int nextRetryCount = currentRetry + 1;
        int maxRetryCount = task.getMaxRetryCount() == null ? pushTaskProperties.getMaxRetryCount() : task.getMaxRetryCount();
        if (nextRetryCount > maxRetryCount) {
            wechatPushTaskService.lambdaUpdate()
                    .eq(WechatPushTask::getId, task.getId())
                    .set(WechatPushTask::getStatus, PushTaskConstants.TASK_STATUS_DEAD)
                    .set(WechatPushTask::getEnqueueStatus, PushTaskConstants.ENQUEUE_STATUS_DEAD)
                    .set(WechatPushTask::getRetryCount, nextRetryCount)
                    .set(WechatPushTask::getLastErrorMessage, trimErrorMessage(ex.getMessage()))
                    .set(WechatPushTask::getUpdateTime, now)
                    .update();
            meterRegistry.counter("push.task.dispatch.dead", "taskType", task.getTaskType()).increment();
            return;
        }

        LocalDateTime nextRetryTime = now.plusMinutes(calculateRetryDelayMinutes(nextRetryCount));
        boolean enqueueSuccess = delayedPushTaskService.enqueueTask(task.getId(), nextRetryTime);
        wechatPushTaskService.lambdaUpdate()
                .eq(WechatPushTask::getId, task.getId())
                .set(WechatPushTask::getStatus, PushTaskConstants.TASK_STATUS_FAILED)
                .set(WechatPushTask::getRetryCount, nextRetryCount)
                .set(WechatPushTask::getNextRetryTime, nextRetryTime)
                .set(WechatPushTask::getLastErrorMessage, trimErrorMessage(ex.getMessage()))
                .set(WechatPushTask::getEnqueueStatus, enqueueSuccess
                        ? PushTaskConstants.ENQUEUE_STATUS_ENQUEUED
                        : PushTaskConstants.ENQUEUE_STATUS_WAITING)
                .set(WechatPushTask::getUpdateTime, now)
                .update();
        meterRegistry.counter("push.task.dispatch.failed", "taskType", task.getTaskType()).increment();
    }

    private int calculateRetryDelayMinutes(int retryCount) {
        int cappedRetry = Math.min(retryCount, 6);
        int multiplier = 1 << Math.max(cappedRetry - 1, 0);
        return pushTaskProperties.getBaseRetryDelayMinutes() * multiplier;
    }

    private String trimErrorMessage(String value) {
        String message = defaultString(value);
        if (message.length() <= 255) {
            return message;
        }
        return message.substring(0, 255);
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
