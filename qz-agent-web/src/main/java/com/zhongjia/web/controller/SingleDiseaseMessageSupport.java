package com.zhongjia.web.controller;

import com.zhongjia.web.vo.singleDisease.CommonXmlModels;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

final class SingleDiseaseMessageSupport {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final String DEFAULT_SENDER = "ZJ_AGENT";

    private SingleDiseaseMessageSupport() {
    }

    static <T> CommonXmlModels.ResponseEnvelope<T> successResponse(
            CommonXmlModels.MessageHeader requestHeader,
            String ackEventType,
            T body
    ) {
        return buildResponse(requestHeader, ackEventType, body);
    }

    static CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody> successAck(
            CommonXmlModels.MessageHeader requestHeader,
            String ackEventType
    ) {
        return buildAck(requestHeader, ackEventType, "CA", "");
    }

    static CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody> errorAck(
            CommonXmlModels.MessageHeader requestHeader,
            String ackEventType,
            String desc
    ) {
        return buildAck(requestHeader, ackEventType, "CE", desc);
    }

    static String validateCommonRequest(
            CommonXmlModels.RequestEnvelope<?> request,
            String expectedEventType
    ) {
        if (request == null) {
            return "请求报文不能为空";
        }
        if (request.getMessageHeader() == null) {
            return "MessageHeader不能为空";
        }
        if (request.getMessageBody() == null) {
            return "MessageBody不能为空";
        }

        CommonXmlModels.MessageHeader messageHeader = request.getMessageHeader();
        if (isBlank(messageHeader.getEventType())) {
            return "EventType不能为空";
        }
        if (!expectedEventType.equals(messageHeader.getEventType())) {
            return "EventType不匹配，期望值为" + expectedEventType;
        }
        if (isBlank(messageHeader.getMsgId())) {
            return "MsgId不能为空";
        }
        return null;
    }

    static String defaultIfBlank(String value, String defaultValue) {
        return isBlank(value) ? defaultValue : value;
    }

    static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static CommonXmlModels.ResponseEnvelope<CommonXmlModels.AckBody> buildAck(
            CommonXmlModels.MessageHeader requestHeader,
            String ackEventType,
            String code,
            String desc
    ) {
        CommonXmlModels.Result result = new CommonXmlModels.Result();
        result.setCode(code);
        result.setDesc(desc);

        CommonXmlModels.AckBody ackBody = new CommonXmlModels.AckBody();
        ackBody.setResult(result);
        return buildResponse(requestHeader, ackEventType, ackBody);
    }

    private static <T> CommonXmlModels.ResponseEnvelope<T> buildResponse(
            CommonXmlModels.MessageHeader requestHeader,
            String ackEventType,
            T body
    ) {
        CommonXmlModels.MessageHeader responseHeader = new CommonXmlModels.MessageHeader();
        responseHeader.setSender(DEFAULT_SENDER);
        if (requestHeader != null) {
            responseHeader.setReceiver(defaultIfBlank(requestHeader.getSender(), ""));
            responseHeader.setMsgId(defaultIfBlank(requestHeader.getMsgId(), ""));
        } else {
            responseHeader.setReceiver("");
            responseHeader.setMsgId("");
        }
        responseHeader.setSendTime(LocalDateTime.now().format(FORMATTER));
        responseHeader.setEventType(ackEventType);

        CommonXmlModels.ResponseEnvelope<T> response = new CommonXmlModels.ResponseEnvelope<>();
        response.setMessageHeader(responseHeader);
        response.setMessageBody(body);
        return response;
    }
}
