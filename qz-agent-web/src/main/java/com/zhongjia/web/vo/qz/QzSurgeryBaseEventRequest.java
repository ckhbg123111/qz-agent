package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "QzSurgeryBaseEventRequest", description = "手术全流程事件公共请求字段")
public class QzSurgeryBaseEventRequest {

    @NotBlank(message = "患者ID不能为空")
    @Schema(description = "患者唯一标识")
    private String patientId;

    @Schema(description = "事件唯一标识，建议作为幂等键")
    private String eventId;

    @Schema(description = "链路追踪号")
    private String traceId;

    @Schema(description = "事件发生时间，ISO-8601")
    private String eventTime;

    @Schema(description = "事件类型")
    private String eventType;

    @Schema(description = "平台侧医院唯一标识")
    private String hospitalId;

    @Schema(description = "医疗机构编码")
    private String orgCode;

    @Schema(description = "院区编码")
    private String campusCode;

    @Schema(description = "来源系统，如 HIS、EMR、ORMS、ANESTHESIA")
    private String sourceSystem;

    @Schema(description = "来源系统实例标识")
    private String sourceSystemInstance;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "患者性别（男/女/未知）")
    private String gender;

    @Schema(description = "患者年龄")
    private Integer age;

    @Schema(description = "身份证号")
    private String idCardNo;

    @Schema(description = "联系电话")
    private String mobile;

    @Schema(description = "本次住院/就诊唯一标识")
    private String visitId;

    @Schema(description = "住院号/就诊号")
    private String visitNo;

    @Schema(description = "病案号")
    private String caseNo;

    @Schema(description = "就诊类型，如 INPATIENT")
    private String encounterType;

    @Schema(description = "入院登记号")
    private String admissionNo;
}
