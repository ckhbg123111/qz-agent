package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "QzHpLabOrderEventRequest", description = "检验开单事件请求")
public class QzHpLabOrderEventRequest {

    @NotBlank(message = "事件ID不能为空")
    @Schema(description = "事件唯一标识，建议作为幂等键")
    private String eventId;

    @Schema(description = "链路追踪号")
    private String traceId;

    @NotBlank(message = "事件时间不能为空")
    @Schema(description = "事件发生时间，ISO-8601")
    private String eventTime;

    @NotBlank(message = "来源系统不能为空")
    @Schema(description = "来源系统，如 HIS、EMR、CPOE")
    private String sourceSystem;

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者唯一标识")
    private String patientId;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "患者性别（男/女/未知）")
    private String gender;

    @Schema(description = "患者年龄")
    private Integer age;

    @Schema(description = "本次就诊唯一标识")
    private String visitId;

    @Schema(description = "就诊号/门诊号/住院号")
    private String visitNo;

    @Schema(description = "就诊类型，如 OUTPATIENT、EMERGENCY、INPATIENT")
    private String encounterType;

    @Schema(description = "申请科室编码")
    private String departmentCode;

    @Schema(description = "申请科室名称")
    private String departmentName;

    @NotBlank(message = "检验申请单号不能为空")
    @Schema(description = "检验申请单号，示例：LIS202604220001")
    private String labApplyNo;

    @Schema(description = "医嘱号/开单号，示例：ORD202604220015")
    private String orderNo;

    @NotBlank(message = "开单时间不能为空")
    @Schema(description = "开单时间，ISO-8601")
    private String orderTime;

    @Schema(description = "申请医生工号")
    private String applyDoctorId;

    @Schema(description = "申请医生姓名")
    private String applyDoctorName;

    @Schema(description = "执行科室编码")
    private String executeDepartmentCode;

    @Schema(description = "执行科室名称")
    private String executeDepartmentName;

    @Schema(description = "申请优先级，如 ROUTINE、URGENT、STAT")
    private String priority;

    @Schema(description = "主诊断编码，优先传 ICD-10；糖尿病示例 E14.90")
    private String diagnosisCode;

    @Schema(description = "诊断编码体系，默认 ICD-10")
    private String diagnosisCodeSystem;

    @Schema(description = "主诊断名称")
    private String diagnosis;

    @Schema(description = "标本类型编码，示例：BLOOD、SERUM、BREATH")
    private String specimenTypeCode;

    @Schema(description = "标本类型名称，如全血、血清、呼气")
    private String specimenTypeName;

    @Schema(description = "采样部位，如静脉血、末梢血、呼气")
    private String specimenCollectionSite;

    @Schema(description = "临床诊断依据或申请目的")
    private String clinicalPurpose;

    @Schema(description = "主诉")
    private String chiefComplaint;

    @Valid
    @NotEmpty(message = "检验项目列表不能为空")
    @Schema(description = "检验项目列表，优先传院内 LIS 项目编码；示例：GLU_FAST（空腹血糖）、HBA1C（糖化血红蛋白）、C13BT（13C/14C 呼气试验）")
    private List<QzHpLabItem> labItems;

    @Schema(description = "备注")
    private String remark;
}
