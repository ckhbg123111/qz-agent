package com.zhongjia.web.controller;

import com.zhongjia.biz.entity.WechatPushLog;
import com.zhongjia.biz.service.WechatPushLogService;
import com.zhongjia.web.config.WechatPushProperties;
import com.zhongjia.web.exception.BizException;
import com.zhongjia.web.integration.wechat.WechatMessageClient;
import com.zhongjia.web.vo.Result;
import com.zhongjia.web.vo.qz.QzHpC13ReportRequest;
import com.zhongjia.web.vo.qz.QzHpLabAppointmentRequest;
import com.zhongjia.web.vo.qz.QzHpLinkVO;
import com.zhongjia.web.vo.qz.QzHpPrescriptionRequest;
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

@RestController
@Tag(name = "依据检查信息返回宣教落地页对接接口（幽门螺杆菌例）")
@RequestMapping("/api/b2b/qz/hp")
public class QzHpInterfaceController {

    private static final String TAG_LAB_APPOINTMENT = "UUID_EXAMPLE_1";
    private static final String TAG_REPORT = "UUID_EXAMPLE_2";
    private static final String TAG_PRESCRIPTION = "UUID_EXAMPLE_3";
    private static final Logger LOGGER = LoggerFactory.getLogger(QzHpInterfaceController.class);

    private static final DateTimeFormatter PUSH_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final WechatMessageClient wechatMessageClient;
    private final WechatPushProperties wechatPushProperties;
    private final WechatPushLogService wechatPushLogService;

    public QzHpInterfaceController(
            WechatMessageClient wechatMessageClient,
            WechatPushProperties wechatPushProperties,
            WechatPushLogService wechatPushLogService
    ) {
        this.wechatMessageClient = wechatMessageClient;
        this.wechatPushProperties = wechatPushProperties;
        this.wechatPushLogService = wechatPushLogService;
    }

    @PostMapping("/lab-appointment")
    @Operation(summary = "检验预约（推送）")
    public Result<QzHpLinkVO> labAppointment(@RequestBody @Valid QzHpLabAppointmentRequest request) {
        WechatMessageRequest wechatRequest = buildWechatRequest(
                TAG_LAB_APPOINTMENT,
                request.getPatientId(),
                request.getPatientName(),
                request.getGender(),
                request.getAge(),
                request.getDiagnosis(),
                "",
                request.getApplyDate(),
                ""
        );
        String jumpLink = pushAndLog(TAG_LAB_APPOINTMENT, request.getPatientId(), wechatRequest);
        return Result.success(QzHpLinkVO.of(jumpLink));
    }

    @PostMapping("/report")
    @Operation(summary = "检验报告（推送）")
    public Result<QzHpLinkVO> report(@RequestBody @Valid QzHpC13ReportRequest request) {
        WechatMessageRequest wechatRequest = buildWechatRequest(
                TAG_REPORT,
                request.getPatientId(),
                request.getPatientName(),
                request.getGender(),
                request.getAge(),
                "",
                "",
                request.getTestDate(),
                ""
        );
        String jumpLink = pushAndLog(TAG_REPORT, request.getPatientId(), wechatRequest);
        return Result.success(QzHpLinkVO.of(jumpLink));
    }

    @PostMapping("/prescription")
    @Operation(summary = "处方开具（推送）")
    public Result<QzHpLinkVO> prescription(@RequestBody @Valid QzHpPrescriptionRequest request) {
        WechatMessageRequest wechatRequest = buildWechatRequest(
                TAG_PRESCRIPTION,
                request.getPatientId(),
                request.getPatientName(),
                request.getGender(),
                request.getAge(),
                request.getDiagnosis(),
                request.getTherapy(),
                request.getPrescriptionDate(),
                ""
        );
        String jumpLink = pushAndLog(TAG_PRESCRIPTION, request.getPatientId(), wechatRequest);
        return Result.success(QzHpLinkVO.of(jumpLink));
    }

    private String pushAndLog(String tag, String patientId, WechatMessageRequest wechatRequest) {
        String bizcode = resolveBizcode();
        WechatPushLog log = new WechatPushLog();
        log.setBizcode(bizcode);
        log.setPatientId(defaultString(patientId));
        log.setTag(defaultString(tag));
        log.setPushStatus("FAIL");
        log.setMessage("");
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
}
