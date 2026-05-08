package com.zhongjia.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongjia.web.vo.Result;
import com.zhongjia.web.vo.qz.QzSurgeryAdmissionRequest;
import com.zhongjia.web.vo.qz.QzSurgeryCompletionRequest;
import com.zhongjia.web.vo.qz.QzSurgeryConfirmationRequest;
import com.zhongjia.web.vo.qz.QzSurgeryDischargeRequest;
import com.zhongjia.web.push.EducationPushCoordinator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "手术全流程信息接收接口")
@RequestMapping("/api/b2b/qz/surgery")
public class QzSurgeryInterfaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QzSurgeryInterfaceController.class);
    private final ObjectMapper objectMapper;
    private final EducationPushCoordinator educationPushCoordinator;

    public QzSurgeryInterfaceController(ObjectMapper objectMapper, EducationPushCoordinator educationPushCoordinator) {
        this.objectMapper = objectMapper;
        this.educationPushCoordinator = educationPushCoordinator;
    }

    @PostMapping("/admission")
    @Operation(summary = "办理入院")
    public Result<Boolean> admission(@RequestBody @Valid QzSurgeryAdmissionRequest request) {
        LOGGER.info("办理入院事件入参: {}", toRequestJson(request));
        return Result.success(Boolean.TRUE);
    }

    @PostMapping("/confirmation")
    @Operation(summary = "手术确认")
    public Result<Boolean> confirmation(@RequestBody @Valid QzSurgeryConfirmationRequest request) {
        LOGGER.info("手术确认事件入参: {}", toRequestJson(request));
        try {
            educationPushCoordinator.handleSurgeryConfirmationEvent(request);
        } catch (Exception ex) {
            LOGGER.error("手术确认宣教推送处理失败，接口继续返回成功: patientId={}, visitNo={}",
                    request.getPatientId(), request.getVisitNo(), ex);
        }
        return Result.success(Boolean.TRUE);
    }

    @PostMapping("/completion")
    @Operation(summary = "手术完成单")
    public Result<Boolean> completion(@RequestBody @Valid QzSurgeryCompletionRequest request) {
        LOGGER.info("手术完成单事件入参: {}", toRequestJson(request));
        try {
            educationPushCoordinator.handleSurgeryCompletionEvent(request);
        } catch (Exception ex) {
            LOGGER.error("手术完成宣教推送处理失败，接口继续返回成功: patientId={}, visitNo={}",
                    request.getPatientId(), request.getVisitNo(), ex);
        }
        return Result.success(Boolean.TRUE);
    }

    @PostMapping("/discharge")
    @Operation(summary = "办理出院")
    public Result<Boolean> discharge(@RequestBody @Valid QzSurgeryDischargeRequest request) {
        LOGGER.info("办理出院事件入参: {}", toRequestJson(request));
        return Result.success(Boolean.TRUE);
    }

    private String toRequestJson(Object request) {
        if (request == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException ex) {
            LOGGER.warn("请求体序列化失败，使用降级内容记录日志", ex);
            return "{\"serializeError\":\"REQUEST_JSON_SERIALIZE_FAILED\"}";
        }
    }
}
