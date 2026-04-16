package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "QzHpRegistrationEventRequest", description = "挂号事件请求")
public class QzHpRegistrationEventRequest {

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者名称")
    private String patientName;

    @Schema(description = "患者性别（男/女/未知）")
    private String gender;

    @Schema(description = "患者年龄")
    private Integer age;

    @NotBlank(message = "科室不能为空")
    @Schema(description = "科室")
    private String department;
}
