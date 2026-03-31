package com.zhongjia.web.controller;

import com.zhongjia.web.vo.singleDisease.CommonXmlModels;
import com.zhongjia.web.vo.singleDisease.PatientMessageModels;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "单病种消息接口-患者管理")
@RequestMapping("/api/b2b/single-disease/patient")
public class SingleDiseasePatientController {

    private static final String REQUEST_EVENT_TYPE = "ADT_A01_MZ";
    private static final String ACK_EVENT_TYPE = "ACK_ADT_A01_MZ";

    @PostMapping(
            value = "/adt-a01-mz",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @Operation(summary = "患者入出转推送(门诊)")
    public ResponseEntity<CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody>> adtA01Mz(
            @RequestBody CommonXmlModels.RequestEnvelope<PatientMessageModels.AdtA01MzBody> request
    ) {
        String validationError = SingleDiseaseMessageSupport.validateCommonRequest(request, REQUEST_EVENT_TYPE);
        if (validationError != null) {
            return ResponseEntity.ok(
                    SingleDiseaseMessageSupport.errorAck(
                            request == null ? null : request.getMessageHeader(),
                            ACK_EVENT_TYPE,
                            validationError
                    )
            );
        }

        PatientMessageModels.EventInfo eventInfo = request.getMessageBody().getEventInfo();
        if (eventInfo == null || SingleDiseaseMessageSupport.isBlank(eventInfo.getEventTypeCode())) {
            return ResponseEntity.ok(
                    SingleDiseaseMessageSupport.errorAck(request.getMessageHeader(), ACK_EVENT_TYPE, "EventInfo.EventTypeCode不能为空")
            );
        }

        if (!"REG".equals(eventInfo.getEventTypeCode())) {
            return ResponseEntity.ok(
                    SingleDiseaseMessageSupport.errorAck(request.getMessageHeader(), ACK_EVENT_TYPE, "当前接口仅处理挂号成功事件REG")
            );
        }

        return ResponseEntity.ok(SingleDiseaseMessageSupport.successAck(request.getMessageHeader(), ACK_EVENT_TYPE));
    }
}
