package com.zhongjia.web.controller;

import com.zhongjia.web.vo.singleDisease.CommonXmlModels;
import com.zhongjia.web.vo.singleDisease.LabMessageModels;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "单病种消息接口-检验")
@RequestMapping("/api/b2b/sdhp/lab")
public class SingleDiseaseLabController {

    @PostMapping(
            value = "/oml-o21",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @Operation(summary = "检验申请单推送（开立/作废）")
    public ResponseEntity<CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody>> labApply(
            @RequestBody CommonXmlModels.RequestEnvelope<LabMessageModels.LabApplyBody> request
    ) {
        return buildAckResponse(request, "OML_O21_OML_O21", "ACK_OML_O21_OML_O21");
    }

    @PostMapping(
            value = "/status",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @Operation(summary = "检验状态回传")
    public ResponseEntity<CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody>> labStatus(
            @RequestBody CommonXmlModels.RequestEnvelope<LabMessageModels.LabStatusBody> request
    ) {
        return buildAckResponse(request, "OML^O21^JYZTGB_XML", "ACK^O01");
    }

    @PostMapping(
            value = "/report",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @Operation(summary = "检验文字报告/危急值/耐药菌回传")
    public ResponseEntity<CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody>> labReport(
            @RequestBody CommonXmlModels.RequestEnvelope<LabMessageModels.LabReportBody> request
    ) {
        return buildAckResponse(request, "OUL^R21^JYBGHC_XML", "ACK^O01");
    }

    private <T> ResponseEntity<CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody>> buildAckResponse(
            CommonXmlModels.RequestEnvelope<T> request,
            String requestEventType,
            String ackEventType
    ) {
        String validationError = SingleDiseaseMessageSupport.validateCommonRequest(request, requestEventType);
        if (validationError != null) {
            return ResponseEntity.ok(
                    SingleDiseaseMessageSupport.errorAck(
                            request == null ? null : request.getMessageHeader(),
                            ackEventType,
                            validationError
                    )
            );
        }
        return ResponseEntity.ok(SingleDiseaseMessageSupport.successAck(request.getMessageHeader(), ackEventType));
    }
}
