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
@Schema(name = "QzSurgeryAdmissionRequest", description = "办理入院事件请求")
public class QzSurgeryAdmissionRequest extends QzSurgeryBaseEventRequest {

    @Valid
    @NotEmpty(message = "入院诊断列表不能为空")
    @Schema(description = "入院诊断列表")
    private List<QzDiagnosisItem> admissionDiagnosisList;

    @Valid
    @Schema(description = "拟手术列表，优先传 ICD-9-CM3 或院内手术编码；示例：肾穿刺活检 55.2300，屈光手术可传院内编码 EYE_REFRACT_001")
    private List<QzOperationItem> plannedOperationList;

    @Schema(description = "入院时间")
    private String admissionTime;

    @Schema(description = "入院科室编码")
    private String admissionDepartmentCode;

    @Schema(description = "入院科室名称")
    private String admissionDepartmentName;

    @Schema(description = "病区编码")
    private String wardCode;

    @Schema(description = "病区名称")
    private String wardName;

    @Schema(description = "床号")
    private String bedNo;

    @Schema(description = "入院类型，如 NORMAL、EMERGENCY、TRANSFER_IN")
    private String admissionType;

    @Schema(description = "主治医生工号")
    private String attendingDoctorId;

    @Schema(description = "主治医生姓名")
    private String attendingDoctorName;

    @Schema(description = "住院医生工号")
    private String residentDoctorId;

    @Schema(description = "住院医生姓名")
    private String residentDoctorName;

    @Schema(description = "护理级别")
    private String nursingLevel;

    @Schema(description = "入院病情")
    private String admissionCondition;

    @Schema(description = "是否拟行手术")
    private Boolean plannedOperationFlag;

    @Schema(description = "预计手术日期")
    private String plannedOperationDate;

    @Schema(description = "血型")
    private String bloodType;

    @Schema(description = "过敏史")
    private String allergyHistory;

    @Schema(description = "是否感染性病例")
    private Boolean infectionFlag;

    @Schema(description = "备注")
    private String remark;
}
