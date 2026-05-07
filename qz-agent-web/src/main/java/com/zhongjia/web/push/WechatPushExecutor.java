package com.zhongjia.web.push;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongjia.biz.entity.WechatPushLog;
import com.zhongjia.biz.service.WechatPushLogService;
import com.zhongjia.biz.service.WechatPushSuccessRecordService;
import com.zhongjia.web.config.QzHpProperties;
import com.zhongjia.web.config.WechatPushProperties;
import com.zhongjia.web.exception.BizException;
import com.zhongjia.web.integration.wechat.WechatMessageClient;
import com.zhongjia.web.integration.wechat.WechatPushClient;
import com.zhongjia.web.vo.wechat.WechatMessageRequest;
import com.zhongjia.web.vo.wechat.WechatMessageResponse;
import com.zhongjia.web.vo.wechat.WechatMessageResponseData;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WechatPushExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(WechatPushExecutor.class);
    private static final ZoneId SHANGHAI_ZONE_ID = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter PUSH_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final WechatMessageClient wechatMessageClient;
    private final WechatPushClient wechatPushClient;
    private final QzHpProperties qzHpProperties;
    private final WechatPushProperties wechatPushProperties;
    private final WechatPushLogService wechatPushLogService;
    private final WechatPushSuccessRecordService wechatPushSuccessRecordService;
    private final ObjectMapper objectMapper;

    public WechatPushExecutor(
            WechatMessageClient wechatMessageClient,
            WechatPushClient wechatPushClient,
            QzHpProperties qzHpProperties,
            WechatPushProperties wechatPushProperties,
            WechatPushLogService wechatPushLogService,
            WechatPushSuccessRecordService wechatPushSuccessRecordService,
            ObjectMapper objectMapper
    ) {
        this.wechatMessageClient = wechatMessageClient;
        this.wechatPushClient = wechatPushClient;
        this.qzHpProperties = qzHpProperties;
        this.wechatPushProperties = wechatPushProperties;
        this.wechatPushLogService = wechatPushLogService;
        this.wechatPushSuccessRecordService = wechatPushSuccessRecordService;
        this.objectMapper = objectMapper;
    }

    public PushExecutionResult execute(
            WechatMessageRequest request,
            Object rawRequest,
            String sourceRuleCode,
            Long taskId
    ) {
        return executeSerialized(request, toRequestJson(rawRequest), sourceRuleCode, taskId);
    }

    public PushExecutionResult executeSerialized(
            WechatMessageRequest request,
            String requestJson,
            String sourceRuleCode,
            Long taskId
    ) {
        String patientId = request == null ? "" : defaultString(request.getPatientId()).trim();
        String tag = request == null ? "" : defaultString(request.getTag()).trim();
        if (wechatPushSuccessRecordService.hasSuccess(patientId, tag)) {
            LOGGER.info("推送已成功过，跳过重复推送: patientId={}, tag={}, sourceRuleCode={}, taskId={}",
                    patientId, tag, defaultString(sourceRuleCode), taskId);
            return PushExecutionResult.skipped();
        }

        WechatPushLog log = buildInitPushLog(patientId, tag, requestJson);
        try {
            if (request == null) {
                throw new BizException(400, "微信推送请求不能为空");
            }
            if (patientId.isBlank()) {
                throw new BizException(400, "patientId不能为空");
            }
            if (tag.isBlank()) {
                throw new BizException(400, "tag不能为空");
            }

            WechatMessageResponse response = wechatMessageClient.fetchMessage(request);
            WechatMessageResponseData data = response.getData();
            String messageXml = buildPushMessageXml(data);
            pushSoapMessageIfEnabled(patientId, tag, messageXml);

            log.setWechatApiCode(response.getCode());
            log.setWechatApiMessage(response.getMessage());
            log.setJumpLink(data.getJumpLink());
            log.setMessage(messageXml);
            log.setPushStatus(PushTaskConstants.TASK_STATUS_SUCCESS);
            wechatPushLogService.save(log);
            wechatPushSuccessRecordService.recordSuccess(patientId, tag, sourceRuleCode, taskId, log.getId());
            return PushExecutionResult.pushed(data.getJumpLink());
        } catch (BizException ex) {
            if (log.getWechatApiCode() == null) {
                log.setWechatApiCode(ex.getCode());
            }
            if (log.getWechatApiMessage() == null) {
                log.setWechatApiMessage(ex.getMessage());
            }
            log.setErrorMessage(trimErrorMessage(ex.getMessage()));
            wechatPushLogService.save(log);
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("微信推送处理失败: patientId={}, tag={}, sourceRuleCode={}, taskId={}",
                    patientId, tag, defaultString(sourceRuleCode), taskId, ex);
            log.setErrorMessage("微信推送处理失败");
            wechatPushLogService.save(log);
            throw new BizException(500, "微信推送处理失败");
        }
    }

    private WechatPushLog buildInitPushLog(String patientId, String tag, String requestJson) {
        WechatPushLog log = new WechatPushLog();
        log.setBizcode(resolveBizcode());
        log.setPatientId(defaultString(patientId));
        log.setTag(defaultString(tag));
        log.setPushStatus(PushTaskConstants.TASK_STATUS_FAILED);
        log.setMessage("");
        log.setRequestJson(defaultString(requestJson));
        log.setCreateTime(LocalDateTime.now(SHANGHAI_ZONE_ID));
        return log;
    }

    private void pushSoapMessageIfEnabled(String patientId, String tag, String messageXml) {
        if (qzHpProperties.isMaskReturnLink()) {
            LOGGER.info("QZ_HP_MASK_RETURN_LINK=true，跳过SOAP真实调用: patientId={}, tag={}, bizcode={}",
                    patientId, tag, resolveBizcode());
            return;
        }
        wechatPushClient.pushMessage(resolveBizcode(), patientId, messageXml);
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
        String pushTime = LocalDateTime.now(SHANGHAI_ZONE_ID).format(PUSH_TIME_FORMATTER);
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
