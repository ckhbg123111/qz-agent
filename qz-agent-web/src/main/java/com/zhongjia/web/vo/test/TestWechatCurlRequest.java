package com.zhongjia.web.vo.test;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "TestWechatCurlRequest", description = "微信SOAP curl生成测试请求")
public class TestWechatCurlRequest {

    @NotBlank(message = "tag不能为空")
    @Schema(description = "微信消息标签")
    private String tag;

    @NotBlank(message = "patientId不能为空")
    @Schema(description = "患者ID")
    private String patientId;
}
