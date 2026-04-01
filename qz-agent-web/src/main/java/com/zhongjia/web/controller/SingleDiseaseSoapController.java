package com.zhongjia.web.controller;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPBodyElement;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.soap.SOAPMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.namespace.QName;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

@Hidden
@RestController
public class SingleDiseaseSoapController {

    static final String SERVICE_PATH = "/api/b2b/sdhp/ws/health-education-agent-service";
    static final String NAMESPACE = "http://aiqikang.com/sdhp/ws";
    static final String METHOD_NAME = "receiveMessage";

    private static final String PREFIX = "sdhp";
    private static final Set<String> MESSAGE_PARAM_NAMES = Set.of("messageXml", "message", "arg0");
    private static final MediaType SOAP_MEDIA_TYPE = MediaType.parseMediaType("text/xml;charset=UTF-8");

    private final SingleDiseaseMessageDispatcher messageDispatcher;

    public SingleDiseaseSoapController(SingleDiseaseMessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @PostMapping(
            value = SERVICE_PATH,
            consumes = {
                    MediaType.TEXT_XML_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    "application/soap+xml"
            },
            produces = MediaType.TEXT_XML_VALUE
    )
    public ResponseEntity<String> receiveMessage(@RequestBody(required = false) String soapRequestXml) {
        try {
            String messageXml = extractMessageXml(soapRequestXml);
            String ackXml = messageDispatcher.dispatch(messageXml);
            return ResponseEntity.ok()
                    .contentType(SOAP_MEDIA_TYPE)
                    .body(buildSoapResponse(ackXml));
        } catch (SoapClientException ex) {
            return ResponseEntity.badRequest()
                    .contentType(SOAP_MEDIA_TYPE)
                    .body(buildSoapFault("Client", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .contentType(SOAP_MEDIA_TYPE)
                    .body(buildSoapFault("Server", "SOAP消息处理失败"));
        }
    }

    private String extractMessageXml(String soapRequestXml) throws Exception {
        if (SingleDiseaseMessageSupport.isBlank(soapRequestXml)) {
            throw new SoapClientException("SOAP请求不能为空");
        }

        SOAPMessage soapMessage = parseSoapMessage(soapRequestXml);
        SOAPBody soapBody = soapMessage.getSOAPBody();
        if (soapBody == null) {
            throw new SoapClientException("SOAP Body不能为空");
        }

        SOAPElement methodElement = firstChildElement(soapBody.getChildElements());
        if (methodElement == null) {
            throw new SoapClientException("未找到SOAP方法节点");
        }
        if (!METHOD_NAME.equals(methodElement.getLocalName())) {
            throw new SoapClientException("SOAP方法名必须为" + METHOD_NAME);
        }

        SOAPElement messageElement = null;
        Iterator<?> childIterator = methodElement.getChildElements();
        while (childIterator.hasNext()) {
            Object child = childIterator.next();
            if (!(child instanceof SOAPElement soapElement)) {
                continue;
            }
            if (MESSAGE_PARAM_NAMES.contains(soapElement.getLocalName())) {
                messageElement = soapElement;
                break;
            }
            if (messageElement == null) {
                messageElement = soapElement;
            }
        }

        if (messageElement == null) {
            throw new SoapClientException("未找到messageXml参数节点");
        }

        String messageXml = messageElement.getValue();
        if (SingleDiseaseMessageSupport.isBlank(messageXml)) {
            messageXml = messageElement.getTextContent();
        }
        if (SingleDiseaseMessageSupport.isBlank(messageXml)) {
            throw new SoapClientException("messageXml不能为空");
        }
        return messageXml.trim();
    }

    private SOAPMessage parseSoapMessage(String soapRequestXml) throws Exception {
        MimeHeaders headers = new MimeHeaders();
        headers.addHeader("Content-Type", "text/xml; charset=UTF-8");
        return MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage(
                headers,
                new ByteArrayInputStream(soapRequestXml.getBytes(StandardCharsets.UTF_8))
        );
    }

    private SOAPElement firstChildElement(Iterator<?> iterator) {
        while (iterator.hasNext()) {
            Object child = iterator.next();
            if (child instanceof SOAPElement soapElement) {
                return soapElement;
            }
        }
        return null;
    }

    private String buildSoapResponse(String ackXml) throws Exception {
        SOAPMessage soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration(PREFIX, NAMESPACE);
        SOAPBody body = envelope.getBody();

        SOAPBodyElement responseElement = body.addBodyElement(new QName(NAMESPACE, METHOD_NAME + "Response", PREFIX));
        responseElement.addChildElement(new QName(NAMESPACE, "return", PREFIX)).addTextNode(ackXml);

        soapMessage.saveChanges();
        return writeSoapMessage(soapMessage);
    }

    private String buildSoapFault(String faultCode, String faultString) {
        try {
            SOAPMessage soapMessage = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
            SOAPBody body = soapMessage.getSOAPBody();
            SOAPFault fault = body.addFault();
            fault.setFaultCode(new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, faultCode));
            fault.setFaultString(faultString);
            soapMessage.saveChanges();
            return writeSoapMessage(soapMessage);
        } catch (Exception ex) {
            return """
                    <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                      <soap:Body>
                        <soap:Fault>
                          <faultcode>soap:Server</faultcode>
                          <faultstring>SOAP消息处理失败</faultstring>
                        </soap:Fault>
                      </soap:Body>
                    </soap:Envelope>
                    """;
        }
    }

    private String writeSoapMessage(SOAPMessage soapMessage) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        soapMessage.writeTo(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    private static class SoapClientException extends RuntimeException {
        private SoapClientException(String message) {
            super(message);
        }
    }
}
