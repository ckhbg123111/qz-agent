package com.zhongjia.web.push;

import java.time.LocalDateTime;

public record EducationPushScheduleDecision(
        Action action,
        LocalDateTime triggerTime,
        String reason
) {

    public enum Action {
        PUSH_NOW,
        CREATE_DELAYED_TASK,
        SKIP
    }

    public static EducationPushScheduleDecision pushNow(String reason) {
        return new EducationPushScheduleDecision(Action.PUSH_NOW, null, reason);
    }

    public static EducationPushScheduleDecision createDelayedTask(LocalDateTime triggerTime, String reason) {
        return new EducationPushScheduleDecision(Action.CREATE_DELAYED_TASK, triggerTime, reason);
    }

    public static EducationPushScheduleDecision skip(String reason) {
        return new EducationPushScheduleDecision(Action.SKIP, null, reason);
    }
}
