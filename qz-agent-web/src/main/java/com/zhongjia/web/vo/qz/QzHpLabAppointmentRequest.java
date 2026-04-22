package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "QzHpLabAppointmentRequest", description = "检验预约请求")
public class QzHpLabAppointmentRequest {

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者名称")
    private String patientName;

    @Schema(description = "患者性别（男/女/未知）")
    private String gender;

    @Schema(description = "患者年龄")
    private Integer age;

    @Schema(description = "就诊号/病案号")
    private String visitNo;

    @Schema(description = "申请科室: 消化内科门诊/内分泌科门诊")
    private String applyDepartment;

    @Schema(description = "执行科室")
    private String executeDepartment;

    @Schema(description = "主诊断编码，优先传 ICD-10；糖尿病示例 E14.90")
    private String diagnosisCode;

    @Schema(description = "诊断编码体系，默认 ICD-10")
    private String diagnosisCodeSystem;

    @Valid
    @Schema(description = "检验项目列表，优先按编码识别；糖尿病场景示例可传 GLU_FAST（空腹血糖）、HBA1C（糖化血红蛋白）")
    private List<QzHpLabItem> labItems;

    @Schema(description = "主诉")
    private String chiefComplaint;

    @Schema(description = "主诊断名称")
    private String diagnosis;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "申请日期（ISO-8601 或 yyyy-MM-dd）")
    private String applyDate;

    @Schema(description = "申请医生")
    private String applyDoctor;
}
