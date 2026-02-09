package com.zhongjia.web.controller;

import com.zhongjia.web.vo.Result;
import com.zhongjia.web.vo.qz.QzHpC13ReportRequest;
import com.zhongjia.web.vo.qz.QzHpLabAppointmentRequest;
import com.zhongjia.web.vo.qz.QzHpLinkVO;
import com.zhongjia.web.vo.qz.QzHpPrescriptionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "依据检查信息返回宣教落地页对接接口（幽门螺杆菌例）")
@RequestMapping("/api/b2b/qz/hp")
public class QzHpInterfaceController {

    @PostMapping("/lab-appointment")
    @Operation(summary = "检验预约（推送）")
    public Result<QzHpLinkVO> labAppointment(@RequestBody @Valid QzHpLabAppointmentRequest request) {
        return Result.success(QzHpLinkVO.of(buildMockLink("UUID_EXAMPLE_1")));
    }

    @PostMapping("/report")
    @Operation(summary = "检验报告（推送）")
    public Result<QzHpLinkVO> report(@RequestBody @Valid QzHpC13ReportRequest request) {
        return Result.success(QzHpLinkVO.of(buildMockLink("UUID_EXAMPLE_2")));
    }

    @PostMapping("/prescription")
    @Operation(summary = "处方开具（推送）")
    public Result<QzHpLinkVO> prescription(@RequestBody @Valid QzHpPrescriptionRequest request) {
        return Result.success(QzHpLinkVO.of(buildMockLink("UUID_EXAMPLE_3")));
    }

    private String buildMockLink(String type) {
        return "https://test.aiqikang.com:8086/h5/chat?code=" + type;
    }
}
