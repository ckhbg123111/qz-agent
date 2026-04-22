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
@Schema(name = "QzSurgeryCompletionRequest", description = "手术完成单事件请求")
public class QzSurgeryCompletionRequest extends QzSurgeryBaseEventRequest {

    @Valid
    @NotEmpty(message = "实施手术列表不能为空")
    @Schema(description = "实施手术列表")
    private List<QzOperationItem> performedOperationList;

    @Valid
    @Schema(description = "术前诊断列表")
    private List<QzDiagnosisItem> preoperativeDiagnosisList;

    @Schema(description = "手术单号或手术流水号")
    private String surgeryNo;

    @Schema(description = "手术申请单号")
    private String applyNo;

    @Schema(description = "手术状态，完成场景建议为 COMPLETED")
    private String surgeryStatus;

    @Schema(description = "实际开始时间")
    private String actualStartTime;

    @NotBlank(message = "实际结束时间不能为空")
    @Schema(description = "实际结束时间")
    private String actualEndTime;

    @Schema(description = "手术时长，单位分钟")
    private Integer operationDurationMinutes;

    @Valid
    @Schema(description = "术中诊断列表")
    private List<QzDiagnosisItem> intraoperativeDiagnosisList;

    @Valid
    @Schema(description = "术后诊断列表")
    private List<QzDiagnosisItem> postoperativeDiagnosisList;


    @Schema(description = "主刀医生工号")
    private String surgeonId;

    @Schema(description = "主刀医生姓名")
    private String surgeonName;

    @Valid
    @Schema(description = "助手医生列表")
    private List<QzDoctorRoleItem> assistantDoctorList;

    @Schema(description = "麻醉医生工号")
    private String anesthesiologistId;

    @Schema(description = "麻醉医生姓名")
    private String anesthesiologistName;

    @Schema(description = "麻醉方式编码")
    private String anesthesiaMethodCode;

    @Schema(description = "麻醉方式名称")
    private String anesthesiaMethodName;

    @Schema(description = "麻醉开始时间")
    private String anesthesiaStartTime;

    @Schema(description = "麻醉结束时间")
    private String anesthesiaEndTime;

    @Schema(description = "ASA 分级")
    private String asaGrade;

    @Schema(description = "切口等级")
    private String incisionGrade;

    @Schema(description = "切口愈合等级")
    private String incisionHealingGrade;

    @Schema(description = "麻醉反应或麻醉效果")
    private String anesthesiaReaction;

    @Schema(description = "出血量，单位 ml")
    private Integer bloodLossMl;

    @Schema(description = "输血量，单位 ml")
    private Integer transfusionVolumeMl;

    @Schema(description = "尿量，单位 ml")
    private Integer urineVolumeMl;

    @Schema(description = "是否放置引流")
    private Boolean drainageFlag;

    @Schema(description = "引流说明")
    private String drainageDescription;

    @Schema(description = "是否送病理或送检")
    private Boolean specimenFlag;

    @Valid
    @Schema(description = "标本列表")
    private List<QzSpecimenItem> specimenList;

    @Schema(description = "是否植入耗材或器械")
    private Boolean implantFlag;

    @Valid
    @Schema(description = "植入物或耗材列表")
    private List<QzImplantItem> implantList;

    @Schema(description = "是否发生并发症")
    private Boolean complicationFlag;

    @Valid
    @Schema(description = "并发症列表")
    private List<QzComplicationItem> complicationList;

    @Schema(description = "手术结果或术毕情况")
    private String operationResult;

    @Schema(description = "手术经过摘要")
    private String operationDescription;

    @Schema(description = "术后去向")
    private String postoperativeDestination;

    @Schema(description = "返回病房时间")
    private String returnWardTime;

    @Schema(description = "是否术中或术后死亡")
    private Boolean deathFlag;

    @Schema(description = "手术者签名姓名")
    private String operatorSignatureName;

    @Schema(description = "记录人姓名")
    private String recorderName;

    @Schema(description = "记录时间")
    private String recordTime;

    @Schema(description = "备注")
    private String remark;
}
