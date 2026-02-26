package com.zhongjia.web.controller;

import com.zhongjia.web.config.WechatPushProperties;
import com.zhongjia.web.integration.wechat.WechatPushClient;
import com.zhongjia.web.vo.Result;
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

    private final WechatPushClient wechatPushClient;
    private final WechatPushProperties wechatPushProperties;

    public TestController(WechatPushClient wechatPushClient, WechatPushProperties wechatPushProperties) {
        this.wechatPushClient = wechatPushClient;
        this.wechatPushProperties = wechatPushProperties;
    }

    @PostMapping("/wechat/push")
    @Operation(summary = "手工触发微信SOAP推送")
    public Result<String> testWechatPush(@RequestBody @Valid TestWechatPushRequest request) {
        String bizcode = resolveBizcode(request.getBizcode());
        wechatPushClient.pushMessage(bizcode, request.getPatientId(), request.getMessageXml());
        return Result.success("推送已触发");
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
}
