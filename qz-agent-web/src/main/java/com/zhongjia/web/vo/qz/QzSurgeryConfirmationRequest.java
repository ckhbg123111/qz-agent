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
@Schema(name = "QzSurgeryConfirmationRequest", description = "手术确认事件请求")
public class QzSurgeryConfirmationRequest extends QzSurgeryBaseEventRequest {

    @Valid
    @NotEmpty(message = "术前诊断列表不能为空")
    @Schema(description = "术前诊断列表")
    private List<QzDiagnosisItem> preoperativeDiagnosisList;

    @Valid
    @Schema(description = "拟手术列表")
    private List<QzOperationItem> plannedOperationList;
    @Schema(description = "手术单号或手术流水号")
    private String surgeryNo;

    @Schema(description = "手术申请单号")
    private String applyNo;

    @Schema(description = "手术排程号")
    private String scheduleNo;

    @Schema(description = "手术状态，确认场景建议为 CONFIRMED")
    private String surgeryStatus;

    @Schema(description = "是否急诊手术")
    private Boolean emergencyFlag;

    @Schema(description = "是否日间手术")
    private Boolean daySurgeryFlag;

    @Schema(description = "手术级别")
    private String operationLevel;

    @Schema(description = "手术类别，如 ELECTIVE、LIMITED、EMERGENCY")
    private String operationCategory;

    @Valid
    @Schema(description = "确认手术列表")
    private List<QzOperationItem> confirmedOperationList;

    @NotBlank(message = "计划开始时间不能为空")
    @Schema(description = "计划开始时间")
    private String plannedStartTime;

    @Schema(description = "计划结束时间")
    private String plannedEndTime;

    @Schema(description = "手术室编码")
    private String operatingRoomCode;

    @Schema(description = "手术室名称")
    private String operatingRoomName;

    @Schema(description = "手术间号")
    private String operatingRoomNo;

    @Schema(description = "台次")
    private String tableNo;

    @Schema(description = "申请科室编码")
    private String departmentCode;

    @Schema(description = "申请科室名称")
    private String departmentName;

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

    @Schema(description = "ASA 麻醉分级")
    private String asaGrade;

    @Schema(description = "手术体位")
    private String operationPosition;

    @Schema(description = "手术部位")
    private String operationSite;

    @Schema(description = "切口类型或入路说明")
    private String incisionType;

    @Schema(description = "是否需要特殊设备")
    private Boolean specialEquipmentFlag;

    @Schema(description = "是否隔离手术")
    private Boolean isolationFlag;

    @Schema(description = "是否备血")
    private Boolean bloodPreparationFlag;

    @Schema(description = "备注")
    private String remark;
}
