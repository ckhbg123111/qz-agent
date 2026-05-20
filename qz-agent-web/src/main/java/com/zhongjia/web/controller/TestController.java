package com.zhongjia.web.controller;

import com.zhongjia.web.config.WechatPushProperties;
import com.zhongjia.web.integration.wechat.WechatMessageClient;
import com.zhongjia.web.integration.wechat.WechatPushClient;
import com.zhongjia.web.push.DelayedPushTaskService;
import com.zhongjia.web.vo.Result;
import com.zhongjia.web.vo.test.TestWechatCurlRequest;
import com.zhongjia.web.vo.test.TestWechatPushRequest;
import com.zhongjia.web.vo.wechat.WechatMessageRequest;
import com.zhongjia.web.vo.wechat.WechatMessageResponse;
import com.zhongjia.web.vo.wechat.WechatMessageResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "联调测试接口")
public class TestController {

    private static final String CURL_URL = "http://192.168.50.19/csp/hsb/DHC.Published.PUB0039.BS.PUB0039.cls";
    private static final String SOAP_ACTION = "http://www.dhcc.com.cn/DHC.Published.PUB0039.BS.PUB0039.HIPMessageServer";
    private static final DateTimeFormatter PUSH_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final WechatMessageClient wechatMessageClient;
    private final WechatPushClient wechatPushClient;
    private final WechatPushProperties wechatPushProperties;
    private final DelayedPushTaskService delayedPushTaskService;

    public TestController(
            WechatMessageClient wechatMessageClient,
            WechatPushClient wechatPushClient,
            WechatPushProperties wechatPushProperties,
            DelayedPushTaskService delayedPushTaskService
    ) {
        this.wechatMessageClient = wechatMessageClient;
        this.wechatPushClient = wechatPushClient;
        this.wechatPushProperties = wechatPushProperties;
        this.delayedPushTaskService = delayedPushTaskService;
    }

    @PostMapping("/wechat/push")
    @Operation(summary = "手工触发微信SOAP推送")
    public Result<String> testWechatPush(@RequestBody @Valid TestWechatPushRequest request) {
        String bizcode = resolveBizcode(request.getBizcode());
        String effectivePatientId = delayedPushTaskService.resolvePatientIdForPush(request.getPatientId());
        wechatPushClient.pushMessage(bizcode, effectivePatientId, request.getMessageXml());
        return Result.success("推送已触发");
    }

    @PostMapping("/wechat/curl")
    @Operation(summary = "生成微信SOAP推送curl指令")
    public Result<String> buildWechatCurl(@RequestBody @Valid TestWechatCurlRequest request) {
        return Result.success(buildCurlCommand(request.getTag(), request.getPatientId()));
    }

    private String buildCurlCommand(String tag, String patientId) {
        String effectivePatientId = delayedPushTaskService.resolvePatientIdForPush(patientId);
        WechatMessageResponse response = wechatMessageClient.fetchMessage(buildWechatRequest(tag, effectivePatientId));
        String messageXml = buildPushMessageXml(response.getData());

        return """
                curl -X POST "%s" \\
                  -H "Content-Type: text/xml; charset=utf-8" \\
                  -H "SOAPAction: %s" \\
                  --data-binary @- <<'EOF'
                <?xml version="1.0" encoding="utf-8"?>
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:dhc="http://www.dhcc.com.cn">
                  <soapenv:Header/>
                  <soapenv:Body>
                    <dhc:HIPMessageServer>
                      <dhc:bizcode>%s</dhc:bizcode>
                      <dhc:message>%s</dhc:message>
                      <dhc:patientId>%s</dhc:patientId>
                    </dhc:HIPMessageServer>
                  </soapenv:Body>
                </soapenv:Envelope>
                EOF"""
                .formatted(
                        CURL_URL,
                        SOAP_ACTION,
                        escapeXml(resolveBizcode(null)),
                        messageXml,
                        escapeXml(effectivePatientId)
                );
    }

    private WechatMessageRequest buildWechatRequest(String tag, String patientId) {
        WechatMessageRequest request = new WechatMessageRequest();
        request.setTag(defaultString(tag));
        request.setPatientId(defaultString(patientId));
        request.setPatientName("");
        request.setGender("");
        request.setDiagnosis("");
        request.setPrescription("");
        request.setExamTime("");
        request.setReminderContent("");
        return request;
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
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String resolveBizcode(String requestBizcode) {
        if (StringUtils.hasText(requestBizcode)) {
            return requestBizcode;
        }
        if (StringUtils.hasText(wechatPushProperties.getBizcode())) {
            return wechatPushProperties.getBizcode();
        }
        return "yytz";
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
