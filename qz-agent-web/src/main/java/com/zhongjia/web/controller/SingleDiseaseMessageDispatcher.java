package com.zhongjia.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.zhongjia.web.vo.singleDisease.CheckMessageModels;
import com.zhongjia.web.vo.singleDisease.CommonXmlModels;
import com.zhongjia.web.vo.singleDisease.LabMessageModels;
import com.zhongjia.web.vo.singleDisease.OrderMessageModels;
import com.zhongjia.web.vo.singleDisease.PatientMessageModels;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SingleDiseaseMessageDispatcher {

    private static final String DEFAULT_ACK_EVENT_TYPE = "ACK";

    private final XmlMapper xmlMapper;
    private final Map<String, MessageDefinition<?>> messageDefinitions;

    public SingleDiseaseMessageDispatcher() {
        this.xmlMapper = new XmlMapper();
        this.xmlMapper.findAndRegisterModules();
        this.messageDefinitions = buildDefinitions();
    }

    public String dispatch(String requestXml) {
        if (SingleDiseaseMessageSupport.isBlank(requestXml)) {
            return serializeAck(SingleDiseaseMessageSupport.errorAck(null, DEFAULT_ACK_EVENT_TYPE, "请求报文不能为空"));
        }

        CommonXmlModels.MessageHeader fallbackHeader = tryExtractMessageHeader(requestXml);
        String eventType = fallbackHeader == null ? null : fallbackHeader.getEventType();
        MessageDefinition<?> definition = messageDefinitions.get(eventType);
        if (definition == null) {
            String desc = SingleDiseaseMessageSupport.isBlank(eventType)
                    ? "EventType不能为空"
                    : "暂不支持的EventType: " + eventType;
            return serializeAck(SingleDiseaseMessageSupport.errorAck(
                    fallbackHeader,
                    DEFAULT_ACK_EVENT_TYPE,
                    desc
            ));
        }

        return dispatchTyped(requestXml, fallbackHeader, definition);
    }

    private <T> String dispatchTyped(
            String requestXml,
            CommonXmlModels.MessageHeader fallbackHeader,
            MessageDefinition<T> definition
    ) {
        try {
            CommonXmlModels.RequestEnvelope<T> request = readRequest(requestXml, definition.bodyType());
            String validationError = SingleDiseaseMessageSupport.validateCommonRequest(request, definition.requestEventType());
            if (validationError != null) {
                return serializeAck(SingleDiseaseMessageSupport.errorAck(
                        request == null ? fallbackHeader : request.getMessageHeader(),
                        definition.ackEventType(),
                        validationError
                ));
            }

            if (definition.additionalValidator() != null) {
                String businessError = definition.additionalValidator().validate(request);
                if (businessError != null) {
                    return serializeAck(SingleDiseaseMessageSupport.errorAck(
                            request.getMessageHeader(),
                            definition.ackEventType(),
                            businessError
                    ));
                }
            }

            return serializeAck(SingleDiseaseMessageSupport.successAck(
                    request.getMessageHeader(),
                    definition.ackEventType()
            ));
        } catch (JsonProcessingException ex) {
            return serializeAck(SingleDiseaseMessageSupport.errorAck(
                    fallbackHeader,
                    definition.ackEventType(),
                    "请求报文XML解析失败"
            ));
        }
    }

    private Map<String, MessageDefinition<?>> buildDefinitions() {
        Map<String, MessageDefinition<?>> definitions = new LinkedHashMap<>();
        definitions.put("ADT_A01_MZ", new MessageDefinition<>(
                "ADT_A01_MZ",
                "ACK_ADT_A01_MZ",
                PatientMessageModels.AdtA01MzBody.class,
                this::validatePatientMessage
        ));
        definitions.put("ORM^O01^ORM_O01", new MessageDefinition<>(
                "ORM^O01^ORM_O01",
                "ACK_ORM^O01^ORM_O01",
                CheckMessageModels.CheckApplyBody.class,
                null
        ));
        definitions.put("SIU^S12^SIU_S12", new MessageDefinition<>(
                "SIU^S12^SIU_S12",
                "ACK_SIU^S12^SIU_S12",
                CheckMessageModels.CheckReservationPushBody.class,
                null
        ));
        definitions.put("ORM^O01^JCZTGB_XML", new MessageDefinition<>(
                "ORM^O01^JCZTGB_XML",
                "ACK^O01",
                CheckMessageModels.CheckStatusBody.class,
                null
        ));
        definitions.put("ORU^R01^JCBGHC_XML", new MessageDefinition<>(
                "ORU^R01^JCBGHC_XML",
                "ACK^O01",
                CheckMessageModels.CheckReportBody.class,
                null
        ));
        definitions.put("OML_O21_OML_O21", new MessageDefinition<>(
                "OML_O21_OML_O21",
                "ACK_OML_O21_OML_O21",
                LabMessageModels.LabApplyBody.class,
                null
        ));
        definitions.put("OML^O21^JYZTGB_XML", new MessageDefinition<>(
                "OML^O21^JYZTGB_XML",
                "ACK^O01",
                LabMessageModels.LabStatusBody.class,
                null
        ));
        definitions.put("OUL^R21^JYBGHC_XML", new MessageDefinition<>(
                "OUL^R21^JYBGHC_XML",
                "ACK^O01",
                LabMessageModels.LabReportBody.class,
                null
        ));
        definitions.put("OMP^O09^OMP_O09", new MessageDefinition<>(
                "OMP^O09^OMP_O09",
                "ACK_OMP^O09^OMP_O09",
                OrderMessageModels.OmpO09Body.class,
                null
        ));
        return definitions;
    }

    private String validatePatientMessage(
            CommonXmlModels.RequestEnvelope<PatientMessageModels.AdtA01MzBody> request
    ) {
        PatientMessageModels.EventInfo eventInfo = request.getMessageBody().getEventInfo();
        if (eventInfo == null || SingleDiseaseMessageSupport.isBlank(eventInfo.getEventTypeCode())) {
            return "EventInfo.EventTypeCode不能为空";
        }
        if (!"REG".equals(eventInfo.getEventTypeCode())) {
            return "当前接口仅处理挂号成功事件REG";
        }
        return null;
    }

    private <T> CommonXmlModels.RequestEnvelope<T> readRequest(String requestXml, Class<T> bodyType)
            throws JsonProcessingException {
        JavaType requestType = xmlMapper.getTypeFactory()
                .constructParametricType(CommonXmlModels.RequestEnvelope.class, bodyType);
        return xmlMapper.readValue(requestXml, requestType);
    }

    private CommonXmlModels.MessageHeader tryExtractMessageHeader(String requestXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            Document document = factory.newDocumentBuilder().parse(
                    new InputSource(new StringReader(requestXml))
            );
            NodeList headers = document.getElementsByTagName("MessageHeader");
            if (headers.getLength() == 0) {
                return null;
            }

            Element headerElement = (Element) headers.item(0);
            CommonXmlModels.MessageHeader header = new CommonXmlModels.MessageHeader();
            header.setSender(readChildText(headerElement, "Sender"));
            header.setReceiver(readChildText(headerElement, "Receiver"));
            header.setSendTime(readChildText(headerElement, "SendTime"));
            header.setEventType(readChildText(headerElement, "EventType"));
            header.setMsgId(readChildText(headerElement, "MsgId"));
            return header;
        } catch (Exception ex) {
            return null;
        }
    }

    private String readChildText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        String value = nodes.item(0).getTextContent();
        return value == null ? null : value.trim();
    }

    private String serializeAck(CommonXmlModels.ResponseEnvelope<?> response) {
        try {
            return xmlMapper.writeValueAsString(response);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("ACK响应序列化失败", ex);
        }
    }

    @FunctionalInterface
    private interface AdditionalValidator<T> {
        String validate(CommonXmlModels.RequestEnvelope<T> request);
    }

    private record MessageDefinition<T>(
            String requestEventType,
            String ackEventType,
            Class<T> bodyType,
            AdditionalValidator<T> additionalValidator
    ) {
    }
}
