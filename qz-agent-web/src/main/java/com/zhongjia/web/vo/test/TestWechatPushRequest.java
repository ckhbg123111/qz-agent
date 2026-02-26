package com.zhongjia.web.vo.test;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "TestWechatPushRequest", description = "微信推送测试请求")
public class TestWechatPushRequest {

    @Schema(description = "业务编码，留空则使用配置项 wechat.push.bizcode")
    private String bizcode;

    @NotBlank(message = "patientId不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @NotBlank(message = "messageXml不能为空")
    @Schema(description = "微信模板消息XML字符串")
    private String messageXml;
}
