package com.zhongjia.web.push;

import com.zhongjia.biz.entity.QzEducationPushRule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class EducationPushScheduleCalculator {

    private static final ZoneId SHANGHAI_ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter NORMAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private EducationPushScheduleCalculator() {
    }

    public static EducationPushScheduleDecision decide(
            QzEducationPushRule rule,
            EducationPushEventContext context,
            LocalDateTime now
    ) {
        String strategy = resolveStrategy(rule);
        if (EducationPushRuleConstants.TRIGGER_TIME_STRATEGY_IMMEDIATE.equals(strategy)) {
            return EducationPushScheduleDecision.pushNow("immediate_rule");
        }
        if (EducationPushRuleConstants.TRIGGER_TIME_STRATEGY_ANCHOR_TIME.equals(strategy)) {
            return decideAnchorTime(rule, context, now);
        }
        if (EducationPushRuleConstants.TRIGGER_TIME_STRATEGY_RELATIVE_OFFSET.equals(strategy)) {
            return decideRelativeOffset(rule, now);
        }
        return EducationPushScheduleDecision.skip("unsupported_trigger_time_strategy");
    }

    public static LocalDateTime parseBusinessTime(String value) {
        String normalizedValue = defaultString(value).trim();
        if (normalizedValue.isEmpty()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(normalizedValue).atZoneSameInstant(SHANGHAI_ZONE_ID).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            // Try the next supported format.
        }
        try {
            return LocalDateTime.parse(normalizedValue, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException ignored) {
            // Try the next supported format.
        }
        try {
            return LocalDateTime.parse(normalizedValue, NORMAL_DATE_TIME_FORMATTER);
        } catch (DateTimeParseException ignored) {
            // Try the next supported format.
        }
        try {
            return LocalDate.parse(normalizedValue, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private static EducationPushScheduleDecision decideAnchorTime(
            QzEducationPushRule rule,
            EducationPushEventContext context,
            LocalDateTime now
    ) {
        LocalDateTime anchorBaseTime = context.getBusinessTime(rule.getAnchorField());
        if (anchorBaseTime == null) {
            return EducationPushScheduleDecision.skip("anchor_time_missing");
        }

        LocalTime configuredTime = parseAnchorTime(rule.getAnchorTime());
        if (configuredTime == null) {
            return EducationPushScheduleDecision.skip("anchor_time_invalid");
        }

        int dayOffset = rule.getAnchorDayOffset() == null ? 0 : rule.getAnchorDayOffset();
        LocalDateTime triggerTime = anchorBaseTime.toLocalDate().plusDays(dayOffset).atTime(configuredTime);
        if (now.isBefore(triggerTime)) {
            return EducationPushScheduleDecision.createDelayedTask(triggerTime, "before_trigger_time");
        }

        LocalDateTime windowEndTime = context.getBusinessTime(rule.getWindowEndField());
        if (windowEndTime != null && !now.isAfter(windowEndTime)) {
            return EducationPushScheduleDecision.pushNow("within_push_window");
        }

        if (EducationPushRuleConstants.LATE_POLICY_IMMEDIATE.equals(resolveLatePolicy(rule))) {
            return EducationPushScheduleDecision.pushNow("late_policy_immediate");
        }
        return EducationPushScheduleDecision.skip("late_policy_skip");
    }

    private static EducationPushScheduleDecision decideRelativeOffset(QzEducationPushRule rule, LocalDateTime now) {
        LocalDateTime triggerTime = calculateRelativeTriggerTime(now, rule);
        if (now.isBefore(triggerTime)) {
            return EducationPushScheduleDecision.createDelayedTask(triggerTime, "relative_offset");
        }
        return EducationPushScheduleDecision.pushNow("relative_offset_due");
    }

    private static LocalDateTime calculateRelativeTriggerTime(LocalDateTime baseTime, QzEducationPushRule rule) {
        int delayAmount = rule.getDelayAmount() == null ? 0 : Math.max(rule.getDelayAmount(), 0);
        String delayUnit = defaultString(rule.getDelayUnit()).toUpperCase(Locale.ROOT);
        if (EducationPushRuleConstants.DELAY_UNIT_HOURS.equals(delayUnit)) {
            return baseTime.plusHours(delayAmount);
        }
        if (EducationPushRuleConstants.DELAY_UNIT_DAYS.equals(delayUnit)) {
            return baseTime.plusDays(delayAmount);
        }
        if (EducationPushRuleConstants.DELAY_UNIT_MONTHS.equals(delayUnit)) {
            return baseTime.plusMonths(delayAmount);
        }
        return baseTime;
    }

    private static String resolveStrategy(QzEducationPushRule rule) {
        String configuredStrategy = defaultString(rule.getTriggerTimeStrategy()).trim().toUpperCase(Locale.ROOT);
        if (!configuredStrategy.isEmpty()) {
            return configuredStrategy;
        }
        String triggerType = defaultString(rule.getTriggerType()).trim().toUpperCase(Locale.ROOT);
        if (EducationPushRuleConstants.TRIGGER_TYPE_DELAYED.equals(triggerType)) {
            return EducationPushRuleConstants.TRIGGER_TIME_STRATEGY_RELATIVE_OFFSET;
        }
        return EducationPushRuleConstants.TRIGGER_TIME_STRATEGY_IMMEDIATE;
    }

    private static String resolveLatePolicy(QzEducationPushRule rule) {
        return defaultString(rule.getLatePolicy()).trim().toUpperCase(Locale.ROOT);
    }

    private static LocalTime parseAnchorTime(String value) {
        String normalizedValue = defaultString(value).trim();
        if (normalizedValue.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(normalizedValue, DateTimeFormatter.ISO_LOCAL_TIME);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private static String defaultString(String value) {
        return value == null ? "" : value;
    }
}
