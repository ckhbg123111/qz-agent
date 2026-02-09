package com.zhongjia.web.vo.qz;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "QzHpC13ReportRequest", description = "幽门螺杆菌 C13 检验报告请求")
public class QzHpC13ReportRequest {

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

    @Schema(description = "试剂名称")
    private String reagentName;

    @Schema(description = "纯度")
    private String purity;

    @Schema(description = "报告编号")
    private String reportNo;

    @Schema(description = "送检医生")
    private String sendDoctor;

    @Schema(description = "检验人员")
    private String tester;

    @Schema(description = "检验日期（ISO-8601 或 yyyy-MM-dd）")
    private String testDate;

    @Schema(description = "指标")
    private String indicator;

    @Schema(description = "检验值")
    private String testValue;

    @Schema(description = "检验结果")
    private String testResult;

    @Schema(description = "检验标准")
    private String testStandard;

    @Schema(description = "DOB 值")
    private String dobValue;

    @Schema(description = "结论")
    private String conclusion;

    @Schema(description = "建议")
    private String suggestion;

    @Schema(description = "检验人员")
    private String examiner;

    @Schema(description = "审核人员")
    private String reviewer;
}
