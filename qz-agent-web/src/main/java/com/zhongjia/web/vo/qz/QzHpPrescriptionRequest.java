package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "QzHpPrescriptionRequest", description = "幽门螺杆菌处方推送请求")
public class QzHpPrescriptionRequest {

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

    @Schema(description = "诊断")
    private String diagnosis;

    @Schema(description = "处方日期（ISO-8601 或 yyyy-MM-dd）")
    private String prescriptionDate;

    @Schema(description = "治疗方法")
    private String therapy;

    @Schema(description = "医院")
    private String hospital;

    @Schema(description = "科室")
    private String department;

    @Schema(description = "处方医师")
    private String doctor;

    @Schema(description = "审核药师")
    private String pharmacist;
}
