package com.zhongjia.web.controller;

import com.zhongjia.web.vo.singleDisease.CommonXmlModels;
import com.zhongjia.web.vo.singleDisease.OrderMessageModels;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "单病种消息接口-医嘱")
@RequestMapping("/api/b2b/sdhp/order")
public class SingleDiseaseOrderController {

    @PostMapping(
            value = "/omp-o09",
            consumes = MediaType.APPLICATION_XML_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    @Operation(summary = "药品治疗医嘱信息推送")
    public ResponseEntity<CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody>> ompO09(
            @RequestBody CommonXmlModels.RequestEnvelope<OrderMessageModels.OmpO09Body> request
    ) {
        String validationError = SingleDiseaseMessageSupport.validateCommonRequest(request, "OMP^O09^OMP_O09");
        if (validationError != null) {
            return ResponseEntity.ok(
                    SingleDiseaseMessageSupport.errorAck(
                            request == null ? null : request.getMessageHeader(),
                            "ACK_OMP^O09^OMP_O09",
                            validationError
                    )
            );
        }

        return ResponseEntity.ok(SingleDiseaseMessageSupport.successAck(request.getMessageHeader(), "ACK_OMP^O09^OMP_O09"));
    }
}
