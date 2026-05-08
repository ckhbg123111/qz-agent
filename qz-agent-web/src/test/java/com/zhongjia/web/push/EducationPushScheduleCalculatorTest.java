package com.zhongjia.web.push;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zhongjia.biz.entity.QzEducationPushRule;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class EducationPushScheduleCalculatorTest {

    @Test
    void parseBusinessTimeSupportsNormalDateTimeFormat() {
        LocalDateTime result = EducationPushScheduleCalculator.parseBusinessTime("2026-05-04 14:00:00");

        assertEquals(LocalDateTime.of(2026, 5, 4, 14, 0), result);
    }

    @Test
    void preoperativeRuleCreatesDelayedTaskBeforeWindow() {
        EducationPushEventContext context = new EducationPushEventContext();
        context.putBusinessTime(
                EducationPushRuleConstants.FIELD_PLANNED_START_TIME,
                LocalDateTime.of(2026, 5, 4, 14, 0)
        );

        EducationPushScheduleDecision decision = EducationPushScheduleCalculator.decide(
                preoperativeRule(),
                context,
                LocalDateTime.of(2026, 5, 3, 18, 0)
        );

        assertEquals(EducationPushScheduleDecision.Action.CREATE_DELAYED_TASK, decision.action());
        assertEquals(LocalDateTime.of(2026, 5, 3, 19, 0), decision.triggerTime());
    }

    @Test
    void preoperativeRulePushesImmediatelyWithinWindow() {
        EducationPushEventContext context = new EducationPushEventContext();
        context.putBusinessTime(
                EducationPushRuleConstants.FIELD_PLANNED_START_TIME,
                LocalDateTime.of(2026, 5, 4, 14, 0)
        );

        EducationPushScheduleDecision decision = EducationPushScheduleCalculator.decide(
                preoperativeRule(),
                context,
                LocalDateTime.of(2026, 5, 4, 10, 0)
        );

        assertEquals(EducationPushScheduleDecision.Action.PUSH_NOW, decision.action());
        assertEquals("within_push_window", decision.reason());
    }

    @Test
    void preoperativeRuleSkipsAfterWindow() {
        EducationPushEventContext context = new EducationPushEventContext();
        context.putBusinessTime(
                EducationPushRuleConstants.FIELD_PLANNED_START_TIME,
                LocalDateTime.of(2026, 5, 4, 14, 0)
        );

        EducationPushScheduleDecision decision = EducationPushScheduleCalculator.decide(
                preoperativeRule(),
                context,
                LocalDateTime.of(2026, 5, 4, 15, 0)
        );

        assertEquals(EducationPushScheduleDecision.Action.SKIP, decision.action());
        assertEquals("late_policy_skip", decision.reason());
    }

    @Test
    void postoperativeSecondDayRulePushesImmediatelyWhenLate() {
        EducationPushEventContext context = new EducationPushEventContext();
        context.putBusinessTime(
                EducationPushRuleConstants.FIELD_ACTUAL_END_TIME,
                LocalDateTime.of(2026, 5, 4, 15, 46, 56)
        );

        EducationPushScheduleDecision decision = EducationPushScheduleCalculator.decide(
                postoperativeSecondDayRule(),
                context,
                LocalDateTime.of(2026, 5, 5, 21, 0)
        );

        assertEquals(EducationPushScheduleDecision.Action.PUSH_NOW, decision.action());
        assertEquals("late_policy_immediate", decision.reason());
    }

    @Test
    void surgeryContextReturnsConfiguredMultiValues() {
        EducationPushEventContext context = new EducationPushEventContext();
        context.setPreoperativeDiagnosisCodes(List.of("H52.701"));
        context.setPlannedOperationCodes(List.of("11.7904"));
        context.setPerformedOperationCodes(List.of("11.7904"));

        assertEquals(List.of("H52.701"), context.getValues(EducationPushRuleConstants.FIELD_PREOPERATIVE_DIAGNOSIS_CODE));
        assertEquals(List.of("11.7904"), context.getValues(EducationPushRuleConstants.FIELD_PLANNED_OPERATION_CODE));
        assertEquals(List.of("11.7904"), context.getValues(EducationPushRuleConstants.FIELD_PERFORMED_OPERATION_CODE));
    }

    private QzEducationPushRule preoperativeRule() {
        QzEducationPushRule rule = new QzEducationPushRule();
        rule.setTriggerType(EducationPushRuleConstants.TRIGGER_TYPE_DELAYED);
        rule.setTriggerTimeStrategy(EducationPushRuleConstants.TRIGGER_TIME_STRATEGY_ANCHOR_TIME);
        rule.setAnchorField(EducationPushRuleConstants.FIELD_PLANNED_START_TIME);
        rule.setAnchorDayOffset(-1);
        rule.setAnchorTime("19:00:00");
        rule.setWindowEndField(EducationPushRuleConstants.FIELD_PLANNED_START_TIME);
        rule.setLatePolicy(EducationPushRuleConstants.LATE_POLICY_SKIP);
        return rule;
    }

    private QzEducationPushRule postoperativeSecondDayRule() {
        QzEducationPushRule rule = new QzEducationPushRule();
        rule.setTriggerType(EducationPushRuleConstants.TRIGGER_TYPE_DELAYED);
        rule.setTriggerTimeStrategy(EducationPushRuleConstants.TRIGGER_TIME_STRATEGY_ANCHOR_TIME);
        rule.setAnchorField(EducationPushRuleConstants.FIELD_ACTUAL_END_TIME);
        rule.setAnchorDayOffset(1);
        rule.setAnchorTime("20:00:00");
        rule.setLatePolicy(EducationPushRuleConstants.LATE_POLICY_IMMEDIATE);
        return rule;
    }
}
