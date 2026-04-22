package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "QzSurgeryDischargeRequest", description = "办理出院事件请求")
public class QzSurgeryDischargeRequest extends QzSurgeryBaseEventRequest {

    @NotBlank(message = "出院时间不能为空")
    @Schema(description = "出院时间")
    private String dischargeTime;

    @NotBlank(message = "出院科室编码不能为空")
    @Schema(description = "出院科室编码")
    private String dischargeDepartmentCode;

    @NotBlank(message = "出院科室名称不能为空")
    @Schema(description = "出院科室名称")
    private String dischargeDepartmentName;

    @Schema(description = "出院病区编码")
    private String wardCode;

    @Schema(description = "出院病区名称")
    private String wardName;

    @Schema(description = "出院床号")
    private String bedNo;

    @Schema(description = "住院天数")
    private Integer inpatientDays;

    @NotBlank(message = "出院去向不能为空")
    @Schema(description = "出院去向或转归")
    private String dischargeDisposition;

    @Schema(description = "出院时情况")
    private String dischargeCondition;

    @Valid
    @NotEmpty(message = "出院诊断列表不能为空")
    @Schema(description = "出院诊断列表")
    private List<QzDiagnosisItem> dischargeDiagnosisList;

    @Valid
    @Schema(description = "本次住院完成手术列表，优先传 ICD-9-CM3 或院内手术编码；示例：肾穿刺活检 55.2300，屈光手术可传院内编码 EYE_REFRACT_001")
    private List<QzOperationItem> performedOperationList;

    @Schema(description = "出院经治医生工号")
    private String dischargeDoctorId;

    @Schema(description = "出院经治医生姓名")
    private String dischargeDoctorName;

    @Schema(description = "出院小结摘要")
    private String dischargeSummary;

    @Schema(description = "出院医嘱")
    private String dischargeAdvice;

    @Schema(description = "用药指导")
    private String medicationAdvice;

    @Schema(description = "复诊或随访建议")
    private String followUpAdvice;

    @Schema(description = "建议复诊日期")
    private String followUpDate;

    @Schema(description = "是否已出病理结果")
    private Boolean pathologyResultFlag;

    @Schema(description = "病理结果摘要")
    private String pathologySummary;

    @Schema(description = "手术恢复情况")
    private String surgeryRecoveryStatus;

    @Schema(description = "是否发生院感或切口感染")
    private Boolean infectionFlag;

    @Schema(description = "非计划再入院风险提示")
    private String unplannedReadmissionRisk;

    @Schema(description = "是否死亡出院")
    private Boolean deathFlag;

    @Schema(description = "备注")
    private String remark;
}
