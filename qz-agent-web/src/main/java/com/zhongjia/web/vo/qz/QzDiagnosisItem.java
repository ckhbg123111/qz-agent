package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "QzDiagnosisItem", description = "诊断条目")
public class QzDiagnosisItem {

    @Schema(description = "诊断类型，如 PRIMARY、POSTOPERATIVE_PRIMARY")
    private String diagnosisType;

    @Schema(description = "ICD-10 编码")
    private String diagnosisCode;

    @NotBlank(message = "诊断名称不能为空")
    @Schema(description = "诊断名称")
    private String diagnosisName;

    @Schema(description = "诊断时间，ISO-8601")
    private String diagnosisTime;
}
