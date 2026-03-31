package com.zhongjia.web.vo.singleDisease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

public final class CommonXmlModels {

    private CommonXmlModels() {
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JacksonXmlRootElement(localName = "Request")
    public static class RequestEnvelope<T> {

        @JacksonXmlProperty(localName = "MessageHeader")
        private MessageHeader messageHeader;

        @JacksonXmlProperty(localName = "MessageBody")
        private T messageBody;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JacksonXmlRootElement(localName = "Response")
    public static class ResponseEnvelope<T> {

        @JacksonXmlProperty(localName = "MessageHeader")
        private MessageHeader messageHeader;

        @JacksonXmlProperty(localName = "MessageBody")
        private T messageBody;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class MessageHeader {

        @JacksonXmlProperty(localName = "Sender")
        private String sender;

        @JacksonXmlProperty(localName = "Receiver")
        private String receiver;

        @JacksonXmlProperty(localName = "SendTime")
        private String sendTime;

        @JacksonXmlProperty(localName = "EventType")
        private String eventType;

        @JacksonXmlProperty(localName = "MsgId")
        private String msgId;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class AckBody {

        @JacksonXmlProperty(localName = "Result")
        private Result result;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Result {

        @JacksonXmlProperty(localName = "Code")
        private String code;

        @JacksonXmlProperty(localName = "Desc")
        private String desc;
    }
}
