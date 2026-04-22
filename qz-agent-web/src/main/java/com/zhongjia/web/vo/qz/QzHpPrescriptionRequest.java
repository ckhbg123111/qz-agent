package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "QzHpPrescriptionRequest", description = "处方推送请求")
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

    @Schema(description = "主诊断编码，优先传 ICD-10；糖尿病示例 E14.90")
    private String diagnosisCode;

    @Schema(description = "诊断编码体系，默认 ICD-10")
    private String diagnosisCodeSystem;

    @Schema(description = "主诊断名称，如糖尿病、幽门螺杆菌感染")
    private String diagnosis;

    @Schema(description = "处方日期（ISO-8601 或 yyyy-MM-dd）")
    private String prescriptionDate;

    @Schema(description = "治疗方法")
    private String therapy;

    @Valid
    @Schema(description = "药品信息列表，优先按药品编码识别；糖尿病场景示例可传胰岛素、利拉鲁肽、司美格鲁肽、替尔泊肽对应院内药品字典编码")
    private List<QzHpMedicineItem> medicines;

    @Schema(description = "医院")
    private String hospital;

    @Schema(description = "科室")
    private String department;

    @Schema(description = "处方医师")
    private String doctor;

    @Schema(description = "审核药师")
    private String pharmacist;
}
