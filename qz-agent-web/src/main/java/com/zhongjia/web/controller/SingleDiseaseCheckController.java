package com.zhongjia.web.controller;

import com.zhongjia.web.vo.singleDisease.CheckMessageModels;
import com.zhongjia.web.vo.singleDisease.CommonXmlModels;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "单病种消息接口-检查")
@RequestMapping("/api/b2b/single-disease/check")
public class SingleDiseaseCheckController {

    @PostMapping(
            value = "/orm-o01",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @Operation(summary = "检查申请单推送（开立、作废）")
    public ResponseEntity<CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody>> checkApply(
            @RequestBody CommonXmlModels.RequestEnvelope<CheckMessageModels.CheckApplyBody> request
    ) {
        return buildAckResponse(request, "ORM^O01^ORM_O01", "ACK_ORM^O01^ORM_O01");
    }

    @PostMapping(
            value = "/reservation/siu-s12",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @Operation(summary = "检查预约推送")
    public ResponseEntity<CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody>> reservationPush(
            @RequestBody CommonXmlModels.RequestEnvelope<CheckMessageModels.CheckReservationPushBody> request
    ) {
        return buildAckResponse(request, "SIU^S12^SIU_S12", "ACK_ SIU^S12^SIU_S12");
    }

    @PostMapping(
            value = "/status",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @Operation(summary = "医技状态回传（检查）")
    public ResponseEntity<CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody>> checkStatus(
            @RequestBody CommonXmlModels.RequestEnvelope<CheckMessageModels.CheckStatusBody> request
    ) {
        return buildAckResponse(request, "ORM^O01^JCZTGB_XML", "ACK^O01");
    }

    @PostMapping(
            value = "/report",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @Operation(summary = "检查报告发布/危急值回传")
    public ResponseEntity<CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody>> checkReport(
            @RequestBody CommonXmlModels.RequestEnvelope<CheckMessageModels.CheckReportBody> request
    ) {
        return buildAckResponse(request, "ORU^R01^JCBGHC_XML", "ACK^O01");
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
