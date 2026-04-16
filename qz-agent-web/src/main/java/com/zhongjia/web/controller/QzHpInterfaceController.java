package com.zhongjia.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongjia.biz.entity.WechatPushLog;
import com.zhongjia.biz.service.WechatPushLogService;
import com.zhongjia.web.config.QzHpProperties;
import com.zhongjia.web.config.WechatPushProperties;
import com.zhongjia.web.exception.BizException;
import com.zhongjia.web.integration.wechat.WechatMessageClient;
import com.zhongjia.web.push.DelayedPushTaskService;
import com.zhongjia.web.push.PushTaskConstants;
import com.zhongjia.web.vo.Result;
import com.zhongjia.web.vo.qz.QzHpC13ReportRequest;
import com.zhongjia.web.vo.qz.QzHpDiagnosisEventRequest;
import com.zhongjia.web.vo.qz.QzHpLabAppointmentRequest;
import com.zhongjia.web.vo.qz.QzHpLinkVO;
import com.zhongjia.web.vo.qz.QzHpPrescriptionRequest;
import com.zhongjia.web.vo.qz.QzHpRegistrationEventRequest;
import com.zhongjia.web.vo.wechat.WechatMessageRequest;
import com.zhongjia.web.vo.wechat.WechatMessageResponse;
import com.zhongjia.web.vo.wechat.WechatMessageResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Tag(name = "依据检查信息返回宣教落地页对接接口（幽门螺杆菌例）")
@RequestMapping("/api/b2b/qz/hp")
public class QzHpInterfaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QzHpInterfaceController.class);

    private static final DateTimeFormatter PUSH_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final WechatMessageClient wechatMessageClient;
    private final QzHpProperties qzHpProperties;
    private final WechatPushProperties wechatPushProperties;
    private final WechatPushLogService wechatPushLogService;
    private final ObjectMapper objectMapper;
    private final DelayedPushTaskService delayedPushTaskService;

    public QzHpInterfaceController(
            WechatMessageClient wechatMessageClient,
            QzHpProperties qzHpProperties,
            WechatPushProperties wechatPushProperties,
            WechatPushLogService wechatPushLogService,
            ObjectMapper objectMapper,
            DelayedPushTaskService delayedPushTaskService
    ) {
        this.wechatMessageClient = wechatMessageClient;
        this.qzHpProperties = qzHpProperties;
        this.wechatPushProperties = wechatPushProperties;
        this.wechatPushLogService = wechatPushLogService;
        this.objectMapper = objectMapper;
        this.delayedPushTaskService = delayedPushTaskService;
    }

    @PostMapping("/lab-appointment")
    @Operation(summary = "检验预约（推送）")
    public Result<QzHpLinkVO> labAppointment(@RequestBody @Valid QzHpLabAppointmentRequest request) {
        String effectivePatientId = delayedPushTaskService.resolvePatientIdForPush(request.getPatientId());
        WechatMessageRequest wechatRequest = buildWechatRequest(
                PushTaskConstants.TAG_LAB_APPOINTMENT,
                effectivePatientId,
                request.getPatientName(),
                request.getGender(),
                request.getAge(),
                request.getDiagnosis(),
                "",
                request.getApplyDate(),
                ""
        );
        String jumpLink = pushAndLog(PushTaskConstants.TAG_LAB_APPOINTMENT, effectivePatientId, wechatRequest, request);
        return Result.success(QzHpLinkVO.of(maskLinkIfNeeded(jumpLink)));
    }

    @PostMapping("/report")
    @Operation(summary = "检验报告（推送）")
    public Result<QzHpLinkVO> report(@RequestBody @Valid QzHpC13ReportRequest request) {
        boolean negativeReport = delayedPushTaskService.isNegativeReportSuggestion(request.getSuggestion());
        if (negativeReport) {
            return Result.success(QzHpLinkVO.of(""));
        }

        delayedPushTaskService.createReportWarningTask(request);
        delayedPushTaskService.createFollowUpTask(request);
        String effectivePatientId = delayedPushTaskService.resolvePatientIdForPush(request.getPatientId());
        WechatMessageRequest wechatRequest = buildWechatRequest(
                PushTaskConstants.TAG_REPORT_TIME_IN,
                effectivePatientId,
                request.getPatientName(),
                request.getGender(),
                request.getAge(),
                "",
                "",
                request.getTestDate(),
                ""
        );
        String jumpLink = pushAndLog(PushTaskConstants.TAG_REPORT_TIME_IN, effectivePatientId, wechatRequest, request);
        return Result.success(QzHpLinkVO.of(jumpLink));
    }

    @PostMapping("/prescription")
    @Operation(summary = "处方开具（推送）")
    public Result<QzHpLinkVO> prescription(@RequestBody @Valid QzHpPrescriptionRequest request) {
        String prescriptionTag = resolvePrescriptionTag(request);
        String prescriptionContent = resolvePrescriptionContent(request);
        String effectivePatientId = delayedPushTaskService.resolvePatientIdForPush(request.getPatientId());
        WechatMessageRequest wechatRequest = buildWechatRequest(
                prescriptionTag,
                effectivePatientId,
                request.getPatientName(),
                request.getGender(),
                request.getAge(),
                request.getDiagnosis(),
                prescriptionContent,
                request.getPrescriptionDate(),
                ""
        );
        pushAndLog(prescriptionTag, effectivePatientId, wechatRequest, request);
        return Result.success(QzHpLinkVO.of(""));
    }

    @PostMapping("/diagnosis-event")
    @Operation(summary = "病历确诊事件")
    public Result<Boolean> diagnosisEvent(@RequestBody @Valid QzHpDiagnosisEventRequest request) {
        LOGGER.info(
                "病历确诊事件入参: patientId={}, patientName={}, gender={}, age={}, department={}, diagnosis={}, remark={}, date={}, doctor={}, diseaseType={}",
                request.getPatientId(),
                request.getPatientName(),
                request.getGender(),
                request.getAge(),
                request.getDepartment(),
                request.getDiagnosis(),
                request.getRemark(),
                request.getDate(),
                request.getDoctor(),
                request.getDiseaseType()
        );
        return Result.success(Boolean.TRUE);
    }

    @PostMapping("/registration-event")
    @Operation(summary = "挂号事件")
    public Result<Boolean> registrationEvent(@RequestBody @Valid QzHpRegistrationEventRequest request) {
        LOGGER.info(
                "挂号事件入参: patientId={}, patientName={}, gender={}, age={}, department={}",
                request.getPatientId(),
                request.getPatientName(),
                request.getGender(),
                request.getAge(),
                request.getDepartment()
        );
        return Result.success(Boolean.TRUE);
    }

    private String resolvePrescriptionTag(QzHpPrescriptionRequest request) {
        String therapy = defaultString(request.getTherapy()).toLowerCase(Locale.ROOT);
        if (therapy.contains("二联") || therapy.contains("2联")) {
            return PushTaskConstants.TAG_PRESCRIPTION_2;
        }
        return PushTaskConstants.TAG_PRESCRIPTION;
    }

    private String resolvePrescriptionContent(QzHpPrescriptionRequest request) {
        String medicines = joinMedicines(request.getMedicines());
        if (!medicines.isBlank()) {
            return medicines;
        }
        return defaultString(request.getTherapy());
    }

    private String joinMedicines(List<String> medicines) {
        if (medicines == null || medicines.isEmpty()) {
            return "";
        }
        return medicines.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .collect(Collectors.joining("；"));
    }

    private String pushAndLog(String tag, String patientId, WechatMessageRequest wechatRequest, Object rawRequest) {
        String bizcode = resolveBizcode();
        WechatPushLog log = new WechatPushLog();
        log.setBizcode(bizcode);
        log.setPatientId(defaultString(patientId));
        log.setTag(defaultString(tag));
        log.setPushStatus("FAIL");
        log.setMessage("");
        log.setRequestJson(toRequestJson(rawRequest));
        log.setCreateTime(LocalDateTime.now());

        try {
            if (log.getPatientId().isBlank()) {
                throw new BizException(400, "patientId不能为空");
            }
            WechatMessageResponse response = wechatMessageClient.fetchMessage(wechatRequest);
            WechatMessageResponseData data = response.getData();
            log.setWechatApiCode(response.getCode());
            log.setWechatApiMessage(response.getMessage());
            log.setJumpLink(data.getJumpLink());
            String messageXml = buildPushMessageXml(data);
            log.setMessage(messageXml);

//            wechatPushClient.pushMessage(bizcode, log.getPatientId(), messageXml);
            log.setPushStatus("WAITING TEST");
            wechatPushLogService.save(log);
            return data.getJumpLink();
        } catch (BizException ex) {
            if (log.getWechatApiCode() == null) {
                log.setWechatApiCode(ex.getCode());
            }
            if (log.getWechatApiMessage() == null) {
                log.setWechatApiMessage(ex.getMessage());
            }
            log.setErrorMessage(ex.getMessage());
            wechatPushLogService.save(log);
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("微信推送处理失败: bizcode={}, patientId={}, tag={}", bizcode, log.getPatientId(), tag, ex);
            log.setErrorMessage("微信推送处理失败");
            wechatPushLogService.save(log);
            throw new BizException(500, "微信推送处理失败");
        }
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
        wechatRequest.setExamTime(defaultExamTime(examTime));
        wechatRequest.setReminderContent(defaultString(reminderContent));
        return wechatRequest;
    }

    private String defaultExamTime(String value) {
        if (value == null || value.isBlank()) {
            return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return value;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private String maskLinkIfNeeded(String jumpLink) {
        if (qzHpProperties.isMaskReturnLink()) {
            return "";
        }
        return jumpLink;
    }

    private String resolveBizcode() {
        String configuredBizcode = defaultString(wechatPushProperties.getBizcode());
        if (configuredBizcode.isBlank()) {
            return "yytz";
        }
        return configuredBizcode;
    }

    private String buildPushMessageXml(WechatMessageResponseData data) {
        String title = defaultString(data.getReplyTitle());
        String description = defaultString(data.getReplyDescription());
        String keyword2 = description.isBlank() ? title : description;
        String pushTime = LocalDateTime.now().format(PUSH_TIME_FORMATTER);
        String jumpLink = defaultString(data.getJumpLink());

        return "<message>"
                + "<first>" + escapeXml(title) + "</first>"
                + "<keyword1>" + escapeXml(pushTime) + "</keyword1>"
                + "<keyword2>" + escapeXml(keyword2) + "</keyword2>"
                + "<remark/>"
                + "<hisURL>" + escapeXml(jumpLink) + "</hisURL>"
                + "</message>";
    }

    private String escapeXml(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String toRequestJson(Object rawRequest) {
        if (rawRequest == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(rawRequest);
        } catch (JsonProcessingException ex) {
            LOGGER.warn("请求体序列化失败，使用降级内容记录日志", ex);
            return "{\"serializeError\":\"REQUEST_JSON_SERIALIZE_FAILED\"}";
        }
    }
}
