package com.zhongjia.web.controller;

import com.zhongjia.web.config.WechatPushProperties;
import com.zhongjia.web.exception.BizException;
import com.zhongjia.web.integration.wechat.WechatPushClient;
import com.zhongjia.web.push.DelayedPushTaskService;
import com.zhongjia.web.vo.Result;
import com.zhongjia.web.vo.test.TestWechatCurlRequest;
import com.zhongjia.web.vo.test.TestWechatPushRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "联调测试接口")
public class TestController {

    private static final String CURL_URL = "http://192.168.50.19/csp/hsb/DHC.Published.PUB0039.BS.PUB0039.cls";
    private static final String SOAP_ACTION = "http://www.dhcc.com.cn/DHC.Published.PUB0039.BS.PUB0039.HIPMessageServer";
    private static final String MESSAGE_TIME = "2026-03-17 10:00:00";
    private static final String HIS_URL_PREFIX = "https://hp.aiqikang.com/h5/chat?code=";
    private static final String HIS_URL_SUFFIX = "&amp;amp;cid=qzhospital&amp;amp;exp=1800930903&amp;amp;sign=PaXwbtjrQbbxRccUrCn0tpNz7wIR9nb0loejqk1joyU";

    private final WechatPushClient wechatPushClient;
    private final WechatPushProperties wechatPushProperties;
    private final DelayedPushTaskService delayedPushTaskService;

    public TestController(
            WechatPushClient wechatPushClient,
            WechatPushProperties wechatPushProperties,
            DelayedPushTaskService delayedPushTaskService
    ) {
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
        WechatCurlTemplate template = resolveCurlTemplate(tag);
        String escapedTag = escapeXml(tag);
        String messageXml = """
                &lt;message&gt;&lt;first&gt;%s&lt;/first&gt;&lt;keyword1&gt;%s&lt;/keyword1&gt;&lt;keyword2&gt;%s&lt;/keyword2&gt;&lt;remark/&gt;&lt;hisURL&gt;%s%s%s&lt;/hisURL&gt;&lt;/message&gt;"""
                .formatted(
                        escapeXml(template.first()),
                        MESSAGE_TIME,
                        escapeXml(template.keyword2()),
                        HIS_URL_PREFIX,
                        escapedTag,
                        HIS_URL_SUFFIX
                );

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
                        escapeXml(patientId)
                );
    }

    private WechatCurlTemplate resolveCurlTemplate(String tag) {
        return switch (tag) {
            case "UUID_EXAMPLE_7" -> new WechatCurlTemplate(
                    "二联疗法处方",
                    "为您开具二联疗法处方，内含相关用药指导，点击卡片查看详情"
            );
            case "UUID_EXAMPLE_6" -> new WechatCurlTemplate(
                    "四联疗法处方",
                    "为您开具四联疗法处方，内含相关用药指导，点击卡片查看详情"
            );
            case "UUID_EXAMPLE_10" -> new WechatCurlTemplate(
                    "复查提醒",
                    "【幽门螺杆菌】您的复查时间将至，内含复查相关指引，点击卡片查看详情"
            );
            case "UUID_EXAMPLE_1" -> new WechatCurlTemplate(
                    "已确诊幽门螺杆菌",
                    "幽门螺杆菌检测前注意事项指南"
            );
            default -> throw new BizException(400, "不支持的tag: " + tag);
        };
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

    private record WechatCurlTemplate(String first, String keyword2) {
    }
}
