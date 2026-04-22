package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "QzHpDiagnosisEventRequest", description = "病历确诊事件请求")
public class QzHpDiagnosisEventRequest {

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者名称")
    private String patientName;

    @Schema(description = "患者性别（男/女/未知）")
    private String gender;

    @Schema(description = "患者年龄")
    private Integer age;

    @NotNull(message = "科室枚举不能为空")
    @Schema(description = "科室（GASTROENTEROLOGY/ENDOCRINOLOGY）")
    private String department;

    @Schema(description = "诊断")
    private String diagnosis;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "日期（ISO-8601 或 yyyy-MM-dd）")
    private String date;

    @Schema(description = "医生")
    private String doctor;

    @NotNull(message = "病种枚举不能为空")
    @Schema(description = "病种枚举（GASTRITIS/PEPTIC_ULCER/INFLAMMATORY_BOWEL_DISEASE/DIABETES_MELLITUS）")
    private QzHpDiseaseTypeEnum diseaseType;
}
