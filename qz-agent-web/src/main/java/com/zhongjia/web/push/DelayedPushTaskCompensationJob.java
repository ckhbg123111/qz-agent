package com.zhongjia.web.push;

import com.zhongjia.biz.entity.WechatPushTask;
import com.zhongjia.biz.service.WechatPushTaskService;
import com.zhongjia.web.config.PushTaskProperties;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DelayedPushTaskCompensationJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelayedPushTaskCompensationJob.class);
    private static final ZoneId SHANGHAI_ZONE_ID = ZoneId.of("Asia/Shanghai");

    private final WechatPushTaskService wechatPushTaskService;
    private final DelayedPushTaskService delayedPushTaskService;
    private final PushTaskProperties pushTaskProperties;
    private final MeterRegistry meterRegistry;

    public DelayedPushTaskCompensationJob(
            WechatPushTaskService wechatPushTaskService,
            DelayedPushTaskService delayedPushTaskService,
            PushTaskProperties pushTaskProperties,
            MeterRegistry meterRegistry
    ) {
        this.wechatPushTaskService = wechatPushTaskService;
        this.delayedPushTaskService = delayedPushTaskService;
        this.pushTaskProperties = pushTaskProperties;
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedDelayString = "${push.task.compensation-fixed-delay-ms:60000}")
    public void compensateDueTasks() {
        LocalDateTime now = LocalDateTime.now(SHANGHAI_ZONE_ID);
        List<WechatPushTask> dueTasks = wechatPushTaskService.lambdaQuery()
                .in(WechatPushTask::getStatus, PushTaskConstants.TASK_STATUS_PENDING, PushTaskConstants.TASK_STATUS_FAILED)
                .le(WechatPushTask::getNextRetryTime, now)
                .last("limit " + pushTaskProperties.getBatchSize())
                .list();
        if (dueTasks == null || dueTasks.isEmpty()) {
            return;
        }

        for (WechatPushTask task : dueTasks) {
            boolean enqueued = delayedPushTaskService.enqueueTask(task.getId(), task.getNextRetryTime());
            if (!enqueued) {
                meterRegistry.counter("push.task.compensation.enqueue.failed").increment();
                continue;
            }
            wechatPushTaskService.lambdaUpdate()
                    .eq(WechatPushTask::getId, task.getId())
                    .set(WechatPushTask::getEnqueueStatus, PushTaskConstants.ENQUEUE_STATUS_ENQUEUED)
                    .set(WechatPushTask::getUpdateTime, now)
                    .update();
            meterRegistry.counter("push.task.compensation.enqueue.success").increment();
        }
        LOGGER.info("推送任务补偿完成: batchSize={}", dueTasks.size());
    }
}
