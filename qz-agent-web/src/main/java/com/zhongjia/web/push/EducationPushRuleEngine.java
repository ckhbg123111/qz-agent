package com.zhongjia.web.push;

import com.zhongjia.biz.entity.QzEducationPushRule;
import com.zhongjia.biz.entity.QzEducationPushRuleCondition;
import com.zhongjia.biz.service.QzEducationPushRuleConditionService;
import com.zhongjia.biz.service.QzEducationPushRuleService;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class EducationPushRuleEngine {

    private final QzEducationPushRuleService ruleService;
    private final QzEducationPushRuleConditionService conditionService;

    public EducationPushRuleEngine(
            QzEducationPushRuleService ruleService,
            QzEducationPushRuleConditionService conditionService
    ) {
        this.ruleService = ruleService;
        this.conditionService = conditionService;
    }

    public List<QzEducationPushRule> matchImmediateRules(EducationPushEventContext context) {
        List<QzEducationPushRule> rules = ruleService.lambdaQuery()
                .eq(QzEducationPushRule::getEventType, context.getEventType())
                .eq(QzEducationPushRule::getTriggerType, EducationPushRuleConstants.TRIGGER_TYPE_IMMEDIATE)
                .eq(QzEducationPushRule::getEnabled, 1)
                .orderByAsc(QzEducationPushRule::getSortOrder)
                .list();
        if (rules == null || rules.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, List<QzEducationPushRuleCondition>> conditionsByRuleId = loadConditions(rules);
        return rules.stream()
                .filter(rule -> matchesRule(context, conditionsByRuleId.get(rule.getId())))
                .toList();
    }

    public List<QzEducationPushRule> findDelayedRules(String previousRuleCode) {
        String normalizedPreviousRuleCode = defaultString(previousRuleCode).trim();
        if (normalizedPreviousRuleCode.isEmpty()) {
            return Collections.emptyList();
        }
        List<QzEducationPushRule> rules = ruleService.lambdaQuery()
                .eq(QzEducationPushRule::getPreviousRuleCode, normalizedPreviousRuleCode)
                .eq(QzEducationPushRule::getTriggerType, EducationPushRuleConstants.TRIGGER_TYPE_DELAYED)
                .eq(QzEducationPushRule::getEnabled, 1)
                .orderByAsc(QzEducationPushRule::getSortOrder)
                .list();
        return rules == null ? Collections.emptyList() : rules;
    }

    private Map<Long, List<QzEducationPushRuleCondition>> loadConditions(List<QzEducationPushRule> rules) {
        List<Long> ruleIds = rules.stream()
                .map(QzEducationPushRule::getId)
                .filter(id -> id != null)
                .toList();
        if (ruleIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<QzEducationPushRuleCondition> conditions = conditionService.lambdaQuery()
                .in(QzEducationPushRuleCondition::getRuleId, ruleIds)
                .eq(QzEducationPushRuleCondition::getEnabled, 1)
                .orderByAsc(QzEducationPushRuleCondition::getSortOrder)
                .list();
        if (conditions == null || conditions.isEmpty()) {
            return Collections.emptyMap();
        }
        return conditions.stream().collect(Collectors.groupingBy(QzEducationPushRuleCondition::getRuleId));
    }

    private boolean matchesRule(EducationPushEventContext context, List<QzEducationPushRuleCondition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }
        Map<String, List<QzEducationPushRuleCondition>> conditionsByField = conditions.stream()
                .collect(Collectors.groupingBy(QzEducationPushRuleCondition::getFieldName));
        for (Map.Entry<String, List<QzEducationPushRuleCondition>> entry : conditionsByField.entrySet()) {
            List<String> values = context.getValues(entry.getKey());
            if (values.isEmpty()) {
                return false;
            }
            boolean fieldMatched = entry.getValue().stream()
                    .anyMatch(condition -> values.stream().anyMatch(value -> matchesCondition(value, condition)));
            if (!fieldMatched) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesCondition(String value, QzEducationPushRuleCondition condition) {
        String normalizedValue = defaultString(value);
        String matchValue = defaultString(condition.getMatchValue());
        if (EducationPushRuleConstants.MATCH_EQUALS_IGNORE_CASE.equals(condition.getMatchType())) {
            return normalizedValue.equalsIgnoreCase(matchValue);
        }
        if (EducationPushRuleConstants.MATCH_CONTAINS_IGNORE_CASE.equals(condition.getMatchType())) {
            return normalizedValue.toLowerCase(Locale.ROOT).contains(matchValue.toLowerCase(Locale.ROOT));
        }
        return false;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
