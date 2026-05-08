package com.zhongjia.web.push;

import com.zhongjia.biz.entity.QzEducationPushRule;
import com.zhongjia.biz.service.WechatPushSuccessRecordService;
import com.zhongjia.web.vo.qz.QzDiagnosisItem;
import com.zhongjia.web.vo.qz.QzHpDiagnosisEventRequest;
import com.zhongjia.web.vo.qz.QzHpMedicineItem;
import com.zhongjia.web.vo.qz.QzHpPrescriptionRequest;
import com.zhongjia.web.vo.qz.QzOperationItem;
import com.zhongjia.web.vo.qz.QzSurgeryBaseEventRequest;
import com.zhongjia.web.vo.qz.QzSurgeryCompletionRequest;
import com.zhongjia.web.vo.qz.QzSurgeryConfirmationRequest;
import com.zhongjia.web.vo.wechat.WechatMessageRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EducationPushCoordinator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EducationPushCoordinator.class);
    private static final ZoneId SHANGHAI_ZONE_ID = ZoneId.of("Asia/Shanghai");

    private final EducationPushRuleEngine ruleEngine;
    private final DelayedPushTaskService delayedPushTaskService;
    private final WechatPushExecutor wechatPushExecutor;
    private final WechatPushSuccessRecordService wechatPushSuccessRecordService;

    public EducationPushCoordinator(
            EducationPushRuleEngine ruleEngine,
            DelayedPushTaskService delayedPushTaskService,
            WechatPushExecutor wechatPushExecutor,
            WechatPushSuccessRecordService wechatPushSuccessRecordService
    ) {
        this.ruleEngine = ruleEngine;
        this.delayedPushTaskService = delayedPushTaskService;
        this.wechatPushExecutor = wechatPushExecutor;
        this.wechatPushSuccessRecordService = wechatPushSuccessRecordService;
    }

    public void handleDiagnosisEvent(QzHpDiagnosisEventRequest request) {
        LocalDateTime decisionTime = LocalDateTime.now(SHANGHAI_ZONE_ID);
        if (!isIcd10(request.getDiagnosisCodeSystem())) {
            LOGGER.info("病历确诊事件已跳过: patientId={}, reason=diagnosisCodeSystem_not_icd10, diagnosisCodeSystem={}",
                    request.getPatientId(), defaultString(request.getDiagnosisCodeSystem()));
            return;
        }
        if (defaultString(request.getDiagnosisCode()).isBlank()) {
            LOGGER.info("病历确诊事件已跳过: patientId={}, reason=diagnosisCode_blank", request.getPatientId());
            return;
        }

        EducationPushEventContext context = new EducationPushEventContext();
        context.setEventType(EducationPushRuleConstants.EVENT_TYPE_DIAGNOSIS);
        context.setPatientId(delayedPushTaskService.resolvePatientIdForPush(request.getPatientId()));
        context.setPatientName(request.getPatientName());
        context.setGender(request.getGender());
        context.setAge(request.getAge());
        context.setDiagnosis(request.getDiagnosis());
        context.setDiagnosisCodeSystem(request.getDiagnosisCodeSystem());
        context.setDiagnosisCode(request.getDiagnosisCode());
        context.setPrescription("");
        context.setExamTime(firstNonBlank(request.getDate(), toIsoOffset(decisionTime)));
        context.setSourceNo(firstNonBlank(request.getDate(), request.getDoctor()));
        processMatchedRules(context, request, decisionTime);
    }

    public void handlePrescriptionEvent(QzHpPrescriptionRequest request) {
        LocalDateTime decisionTime = LocalDateTime.now(SHANGHAI_ZONE_ID);
        if (!isIcd10(request.getDiagnosisCodeSystem())) {
            LOGGER.info("处方开具事件已跳过: patientId={}, visitNo={}, reason=diagnosisCodeSystem_not_icd10, diagnosisCodeSystem={}",
                    request.getPatientId(), defaultString(request.getVisitNo()), defaultString(request.getDiagnosisCodeSystem()));
            return;
        }
        if (defaultString(request.getDiagnosisCode()).isBlank()) {
            LOGGER.info("处方开具事件已跳过: patientId={}, visitNo={}, reason=diagnosisCode_blank",
                    request.getPatientId(), defaultString(request.getVisitNo()));
            return;
        }

        List<String> medicineNames = resolveMedicineNames(request);
        if (medicineNames.isEmpty()) {
            LOGGER.info("处方开具事件已跳过: patientId={}, visitNo={}, reason=medicineName_blank",
                    request.getPatientId(), defaultString(request.getVisitNo()));
            return;
        }

        EducationPushEventContext context = new EducationPushEventContext();
        context.setEventType(EducationPushRuleConstants.EVENT_TYPE_PRESCRIPTION);
        context.setPatientId(delayedPushTaskService.resolvePatientIdForPush(request.getPatientId()));
        context.setPatientName(request.getPatientName());
        context.setGender(request.getGender());
        context.setAge(request.getAge());
        context.setDiagnosis(request.getDiagnosis());
        context.setDiagnosisCodeSystem(request.getDiagnosisCodeSystem());
        context.setDiagnosisCode(request.getDiagnosisCode());
        context.setPrescription(String.join("；", medicineNames));
        context.setExamTime(firstNonBlank(request.getPrescriptionDate(), toIsoOffset(decisionTime)));
        context.setSourceNo(request.getVisitNo());
        context.setMedicineNames(medicineNames);
        processMatchedRules(context, request, decisionTime);
    }

    public void handleSurgeryConfirmationEvent(QzSurgeryConfirmationRequest request) {
        LocalDateTime decisionTime = LocalDateTime.now(SHANGHAI_ZONE_ID);
        EducationPushEventContext context = buildSurgeryContext(
                request,
                EducationPushRuleConstants.EVENT_TYPE_SURGERY_CONFIRMATION,
                extractDiagnosisCodes(request.getPreoperativeDiagnosisList()),
                extractOperationCodes(request.getPlannedOperationList()),
                List.of(),
                extractDiagnosisNames(request.getPreoperativeDiagnosisList()),
                request.getPlannedStartTime(),
                firstNonBlank(request.getSurgeryNo(), request.getScheduleNo(), request.getApplyNo(), request.getVisitNo(), request.getEventId()),
                decisionTime
        );
        putBusinessTime(context, EducationPushRuleConstants.FIELD_PLANNED_START_TIME, request.getPlannedStartTime());
        processConfiguredEntryRulesSafely(context, request, decisionTime);
    }

    public void handleSurgeryCompletionEvent(QzSurgeryCompletionRequest request) {
        LocalDateTime decisionTime = LocalDateTime.now(SHANGHAI_ZONE_ID);
        EducationPushEventContext context = buildSurgeryContext(
                request,
                EducationPushRuleConstants.EVENT_TYPE_SURGERY_COMPLETION,
                extractDiagnosisCodes(request.getPreoperativeDiagnosisList()),
                List.of(),
                extractOperationCodes(request.getPerformedOperationList()),
                extractDiagnosisNames(request.getPreoperativeDiagnosisList()),
                request.getActualEndTime(),
                firstNonBlank(request.getSurgeryNo(), request.getApplyNo(), request.getVisitNo(), request.getEventId()),
                decisionTime
        );
        putBusinessTime(context, EducationPushRuleConstants.FIELD_ACTUAL_END_TIME, request.getActualEndTime());
        processConfiguredEntryRulesSafely(context, request, decisionTime);
    }

    private void processMatchedRules(EducationPushEventContext context, Object rawRequest, LocalDateTime decisionTime) {
        List<QzEducationPushRule> matchedRules = ruleEngine.matchImmediateRules(context);
        if (matchedRules.isEmpty()) {
            LOGGER.info("宣教推送规则未命中: eventType={}, patientId={}, diagnosisCode={}",
                    context.getEventType(), context.getPatientId(), defaultString(context.getDiagnosisCode()));
            return;
        }

        for (QzEducationPushRule rule : matchedRules) {
            createDelayedTasks(rule, context, decisionTime);
            WechatMessageRequest messageRequest = buildWechatRequest(rule.getTag(), context, context.getExamTime());
            wechatPushExecutor.execute(messageRequest, rawRequest, rule.getRuleCode(), null);
            LOGGER.info("宣教即时推送处理完成: ruleCode={}, patientId={}, tag={}",
                    rule.getRuleCode(), context.getPatientId(), rule.getTag());
        }
    }

    private void processConfiguredEntryRulesSafely(
            EducationPushEventContext context,
            Object rawRequest,
            LocalDateTime decisionTime
    ) {
        List<QzEducationPushRule> matchedRules;
        try {
            matchedRules = ruleEngine.matchEntryRules(context);
        } catch (Exception ex) {
            LOGGER.error("宣教推送规则匹配失败: eventType={}, patientId={}",
                    context.getEventType(), context.getPatientId(), ex);
            return;
        }
        if (matchedRules.isEmpty()) {
            LOGGER.info("宣教推送规则未命中: eventType={}, patientId={}",
                    context.getEventType(), context.getPatientId());
            return;
        }

        for (QzEducationPushRule rule : matchedRules) {
            try {
                processConfiguredRule(rule, context, rawRequest, decisionTime);
            } catch (Exception ex) {
                LOGGER.error("宣教推送规则处理失败，接口继续返回成功: ruleCode={}, patientId={}, tag={}",
                        rule.getRuleCode(), context.getPatientId(), rule.getTag(), ex);
            }
        }
    }

    private void processConfiguredRule(
            QzEducationPushRule rule,
            EducationPushEventContext context,
            Object rawRequest,
            LocalDateTime decisionTime
    ) {
        if (wechatPushSuccessRecordService.hasSuccess(context.getPatientId(), rule.getTag())) {
            LOGGER.info("宣教推送已成功过，不再处理: ruleCode={}, patientId={}, tag={}",
                    rule.getRuleCode(), context.getPatientId(), rule.getTag());
            return;
        }

        EducationPushScheduleDecision decision = EducationPushScheduleCalculator.decide(rule, context, decisionTime);
        if (EducationPushScheduleDecision.Action.SKIP.equals(decision.action())) {
            LOGGER.info("宣教推送规则已跳过: ruleCode={}, patientId={}, tag={}, reason={}",
                    rule.getRuleCode(), context.getPatientId(), rule.getTag(), decision.reason());
            return;
        }

        WechatMessageRequest messageRequest = buildWechatRequest(rule.getTag(), context, resolveExamTime(rule, context, decisionTime));
        if (EducationPushScheduleDecision.Action.CREATE_DELAYED_TASK.equals(decision.action())) {
            Long taskId = delayedPushTaskService.createConfiguredTask(
                    rule.getRuleCode(),
                    context.getPatientId(),
                    rule.getTag(),
                    firstNonBlank(context.getSourceNo(), rule.getRuleCode()),
                    resolveBaseTime(rule, context, decisionTime),
                    decision.triggerTime(),
                    messageRequest
            );
            LOGGER.info("宣教延时推送任务已创建: taskId={}, ruleCode={}, patientId={}, tag={}, triggerTime={}, reason={}",
                    taskId, rule.getRuleCode(), context.getPatientId(), rule.getTag(), decision.triggerTime(), decision.reason());
            return;
        }

        wechatPushExecutor.execute(messageRequest, rawRequest, rule.getRuleCode(), null);
        LOGGER.info("宣教即时推送处理完成: ruleCode={}, patientId={}, tag={}, reason={}",
                rule.getRuleCode(), context.getPatientId(), rule.getTag(), decision.reason());
    }

    private void createDelayedTasks(QzEducationPushRule immediateRule, EducationPushEventContext context, LocalDateTime baseTime) {
        List<QzEducationPushRule> delayedRules = ruleEngine.findDelayedRules(immediateRule.getRuleCode());
        if (delayedRules.isEmpty()) {
            return;
        }

        for (QzEducationPushRule delayedRule : delayedRules) {
            if (wechatPushSuccessRecordService.hasSuccess(context.getPatientId(), delayedRule.getTag())) {
                LOGGER.info("延时宣教推送已成功过，不再创建任务: patientId={}, tag={}, ruleCode={}",
                        context.getPatientId(), delayedRule.getTag(), delayedRule.getRuleCode());
                continue;
            }
            LocalDateTime triggerTime = calculateTriggerTime(baseTime, delayedRule);
            WechatMessageRequest messageRequest = buildWechatRequest(delayedRule.getTag(), context, toIsoOffset(baseTime));
            Long taskId = delayedPushTaskService.createConfiguredTask(
                    delayedRule.getRuleCode(),
                    context.getPatientId(),
                    delayedRule.getTag(),
                    firstNonBlank(context.getSourceNo(), immediateRule.getRuleCode()),
                    baseTime,
                    triggerTime,
                    messageRequest
            );
            LOGGER.info("延时宣教推送任务已创建: taskId={}, ruleCode={}, patientId={}, tag={}, triggerTime={}",
                    taskId, delayedRule.getRuleCode(), context.getPatientId(), delayedRule.getTag(), triggerTime);
        }
    }

    private LocalDateTime calculateTriggerTime(LocalDateTime baseTime, QzEducationPushRule delayedRule) {
        int delayAmount = delayedRule.getDelayAmount() == null ? 0 : Math.max(delayedRule.getDelayAmount(), 0);
        String delayUnit = defaultString(delayedRule.getDelayUnit()).toUpperCase(Locale.ROOT);
        if (EducationPushRuleConstants.DELAY_UNIT_HOURS.equals(delayUnit)) {
            return baseTime.plusHours(delayAmount);
        }
        if (EducationPushRuleConstants.DELAY_UNIT_DAYS.equals(delayUnit)) {
            return baseTime.plusDays(delayAmount);
        }
        if (EducationPushRuleConstants.DELAY_UNIT_MONTHS.equals(delayUnit)) {
            return baseTime.plusMonths(delayAmount);
        }
        LOGGER.warn("延时规则单位非法，回退为立即可执行: ruleCode={}, delayUnit={}",
                delayedRule.getRuleCode(), defaultString(delayedRule.getDelayUnit()));
        return baseTime;
    }

    private WechatMessageRequest buildWechatRequest(String tag, EducationPushEventContext context, String examTime) {
        WechatMessageRequest request = new WechatMessageRequest();
        request.setTag(defaultString(tag));
        request.setPatientId(defaultString(context.getPatientId()));
        request.setPatientName(defaultString(context.getPatientName()));
        request.setGender(defaultString(context.getGender()));
        request.setAge(context.getAge());
        request.setDiagnosis(defaultString(context.getDiagnosis()));
        request.setPrescription(defaultString(context.getPrescription()));
        request.setExamTime(defaultString(examTime));
        request.setReminderContent("");
        return request;
    }

    private EducationPushEventContext buildSurgeryContext(
            QzSurgeryBaseEventRequest request,
            String eventType,
            List<String> preoperativeDiagnosisCodes,
            List<String> plannedOperationCodes,
            List<String> performedOperationCodes,
            String diagnosis,
            String examTime,
            String sourceNo,
            LocalDateTime decisionTime
    ) {
        EducationPushEventContext context = new EducationPushEventContext();
        context.setEventType(eventType);
        context.setPatientId(delayedPushTaskService.resolvePatientIdForPush(request.getPatientId()));
        context.setPatientName(request.getPatientName());
        context.setGender(request.getGender());
        context.setAge(request.getAge());
        context.setDiagnosis(diagnosis);
        context.setDiagnosisCodeSystem("");
        context.setDiagnosisCode("");
        context.setPrescription("");
        context.setExamTime(firstNonBlank(examTime, toIsoOffset(decisionTime)));
        context.setSourceNo(sourceNo);
        context.setPreoperativeDiagnosisCodes(preoperativeDiagnosisCodes);
        context.setPlannedOperationCodes(plannedOperationCodes);
        context.setPerformedOperationCodes(performedOperationCodes);
        return context;
    }

    private void putBusinessTime(EducationPushEventContext context, String fieldName, String value) {
        LocalDateTime businessTime = EducationPushScheduleCalculator.parseBusinessTime(value);
        if (businessTime == null) {
            if (!defaultString(value).isBlank()) {
                LOGGER.warn("业务时间解析失败: eventType={}, patientId={}, fieldName={}, value={}",
                        context.getEventType(), context.getPatientId(), fieldName, value);
            }
            return;
        }
        context.putBusinessTime(fieldName, businessTime);
    }

    private List<String> extractDiagnosisCodes(List<QzDiagnosisItem> diagnoses) {
        if (diagnoses == null) {
            return List.of();
        }
        return diagnoses.stream()
                .filter(Objects::nonNull)
                .map(QzDiagnosisItem::getDiagnosisCode)
                .map(this::defaultString)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    private String extractDiagnosisNames(List<QzDiagnosisItem> diagnoses) {
        if (diagnoses == null) {
            return "";
        }
        return diagnoses.stream()
                .filter(Objects::nonNull)
                .map(QzDiagnosisItem::getDiagnosisName)
                .map(this::defaultString)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .distinct()
                .collect(Collectors.joining("；"));
    }

    private List<String> extractOperationCodes(List<QzOperationItem> operations) {
        if (operations == null) {
            return List.of();
        }
        return operations.stream()
                .filter(Objects::nonNull)
                .map(QzOperationItem::getOperationCode)
                .map(this::defaultString)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }

    private String resolveExamTime(QzEducationPushRule rule, EducationPushEventContext context, LocalDateTime decisionTime) {
        LocalDateTime anchorTime = context.getBusinessTime(rule.getAnchorField());
        if (anchorTime != null) {
            return toIsoOffset(anchorTime);
        }
        return firstNonBlank(context.getExamTime(), toIsoOffset(decisionTime));
    }

    private LocalDateTime resolveBaseTime(QzEducationPushRule rule, EducationPushEventContext context, LocalDateTime decisionTime) {
        LocalDateTime anchorTime = context.getBusinessTime(rule.getAnchorField());
        return anchorTime == null ? decisionTime : anchorTime;
    }

    private List<String> resolveMedicineNames(QzHpPrescriptionRequest request) {
        List<String> result = new ArrayList<>();
        if (request.getMedicineItem() != null) {
            result.addAll(request.getMedicineItem().stream()
                    .filter(Objects::nonNull)
                    .map(QzHpMedicineItem::getMedicineName)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(item -> !item.isBlank())
                    .toList());
        }
        if (request.getMedicines() != null) {
            result.addAll(request.getMedicines().stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(item -> !item.isBlank())
                    .toList());
        }
        return result.stream().distinct().collect(Collectors.toList());
    }

    private boolean isIcd10(String diagnosisCodeSystem) {
        return "ICD-10".equalsIgnoreCase(defaultString(diagnosisCodeSystem).trim());
    }

    private String toIsoOffset(LocalDateTime time) {
        return time.atZone(SHANGHAI_ZONE_ID).toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private String firstNonBlank(String primary, String fallback) {
        String normalizedPrimary = defaultString(primary).trim();
        if (!normalizedPrimary.isEmpty()) {
            return normalizedPrimary;
        }
        return defaultString(fallback).trim();
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            String normalizedValue = defaultString(value).trim();
            if (!normalizedValue.isEmpty()) {
                return normalizedValue;
            }
        }
        return "";
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
