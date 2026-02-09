package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "QzHpLabAppointmentRequest", description = "幽门螺杆菌检验预约请求")
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

    @Schema(description = "申请科室")
    private String applyDepartment;

    @Schema(description = "执行科室")
    private String executeDepartment;

    @Schema(description = "检验项目")
    private String labItem;

    @Schema(description = "主诉")
    private String chiefComplaint;

    @Schema(description = "诊断")
    private String diagnosis;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "申请日期（ISO-8601 或 yyyy-MM-dd）")
    private String applyDate;

    @Schema(description = "申请医生")
    private String applyDoctor;
}
