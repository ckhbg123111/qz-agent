package com.zhongjia.web.integration.lab;

import com.zhongjia.web.config.LabQueryProperties;
import com.zhongjia.web.exception.BizException;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPBodyElement;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

@Component
public class LabQueryClient {

    private static final Logger log = LoggerFactory.getLogger(LabQueryClient.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final LabQueryProperties properties;

    public LabQueryClient(LabQueryProperties properties) {
        this.properties = properties;
    }

    public LabQueryResponse queryApplications(LabQueryRequest request) {
        validateConfig();
        validateRequest(request);

        String requestXml = buildBusinessRequestXml(request);
        try {
            QName serviceQName = new QName(properties.getNamespace(), properties.getServiceName());
            QName portQName = new QName(properties.getNamespace(), properties.getPortName());
            URL wsdlUrl = new URL(properties.getWsdlUrl());
            Service service = Service.create(wsdlUrl, serviceQName);
            Dispatch<SOAPMessage> dispatch = service.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);
            dispatch.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
            dispatch.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, resolveSoapAction());
            dispatch.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, resolveEndpoint());

            SOAPMessage soapRequest = buildSoapRequest(requestXml);
            SOAPMessage soapResponse = dispatch.invoke(soapRequest);
            return parseSoapResponse(request.msgId(), requestXml, soapMessageToString(soapRequest), soapResponse);
        } catch (Exception ex) {
            log.error("门诊检验查询接口调用失败: patientId={}, endpoint={}", request.patientId(), resolveEndpoint(), ex);
            throw new BizException(502, "门诊检验查询接口调用失败");
        }
    }

    void validateConfig() {
        if (!StringUtils.hasText(properties.getWsdlUrl())
                || !StringUtils.hasText(properties.getNamespace())
                || !StringUtils.hasText(properties.getServiceName())
                || !StringUtils.hasText(properties.getPortName())
                || !StringUtils.hasText(properties.getMethodName())
                || !StringUtils.hasText(properties.getMessageParamName())) {
            throw new BizException(500, "门诊检验查询接口配置不完整");
        }
    }

    void validateRequest(LabQueryRequest request) {
        if (request == null) {
            throw new BizException(400, "检验查询请求不能为空");
        }
        if (!StringUtils.hasText(request.patientId())) {
            throw new BizException(400, "门诊检验查询必须提供PatientId");
        }
        if (!StringUtils.hasText(request.orgIdExec())) {
            throw new BizException(400, "门诊检验查询必须提供OrgIdExec");
        }
        if (request.startDateTime() == null) {
            throw new BizException(400, "门诊检验查询必须提供开始时间");
        }
    }

    String buildBusinessRequestXml(LabQueryRequest request) {
        String endDateTime = request.endDateTime() == null ? "" : formatDateTime(request.endDateTime());
        return "<Request>"
                + "<MessageHeader>"
                + element("Sender", request.sender())
                + element("Receiver", request.receiver())
                + element("SendTime", formatDateTime(LocalDateTime.now()))
                + element("EventType", "QBP^Q11^CXJYSQ_XML")
                + element("MsgId", request.msgId())
                + "</MessageHeader>"
                + "<MessageBody>"
                + element("MessageQueryName", request.messageQueryName())
                + element("QueryTag", request.queryTag())
                + element("PatientType", request.patientType())
                + element("PatientId", request.patientId())
                + element("VisitId", request.visitId())
                + element("OrgIdExec", request.orgIdExec())
                + element("StartDateTime", formatDateTime(request.startDateTime()))
                + element("EndDateTime", endDateTime)
                + element("IsFilter", request.isFilter())
                + "</MessageBody>"
                + "</Request>";
    }

    SOAPMessage buildSoapRequest(String requestXml) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        String namespace = properties.getNamespace();
        String prefix = "lab";
        envelope.addNamespaceDeclaration(prefix, namespace);
        SOAPBody body = soapMessage.getSOAPBody();
        QName methodQName = new QName(namespace, properties.getMethodName(), prefix);
        SOAPBodyElement methodElement = body.addBodyElement(methodQName);
        methodElement.addChildElement(new QName(namespace, properties.getMessageParamName(), prefix))
                .addTextNode(requestXml);
        soapMessage.saveChanges();
        return soapMessage;
    }

    LabQueryResponse parseSoapResponse(
            String msgId,
            String requestXml,
            String soapRequestXml,
            SOAPMessage soapResponse
    ) throws Exception {
        String soapResponseXml = soapMessageToString(soapResponse);
        SOAPBody soapBody = soapResponse.getSOAPBody();
        if (soapBody == null) {
            throw new BizException(502, "门诊检验查询接口返回空SOAP Body");
        }
        if (soapBody.hasFault()) {
            throw new BizException(502, "门诊检验查询接口返回SOAP Fault: " + soapBody.getFault().getFaultString());
        }

        String responseXml = extractBusinessResponseXml(soapBody);
        ParsedResult parsedResult = parseBusinessResponse(responseXml);
        return new LabQueryResponse(
                msgId,
                requestXml,
                soapRequestXml,
                responseXml,
                soapResponseXml,
                parsedResult.resultCode(),
                parsedResult.resultDesc(),
                parsedResult.orderCount()
        );
    }

    private String extractBusinessResponseXml(SOAPBody soapBody) throws Exception {
        SOAPElement responseElement = firstChildElement(soapBody.getChildElements());
        if (responseElement == null) {
            throw new BizException(502, "门诊检验查询接口返回空响应");
        }

        Iterator<?> childIterator = responseElement.getChildElements();
        while (childIterator.hasNext()) {
            Object child = childIterator.next();
            if (!(child instanceof SOAPElement soapElement)) {
                continue;
            }

            String text = normalizeText(soapElement.getValue());
            if (!text.isBlank()) {
                return text;
            }
            text = normalizeText(soapElement.getTextContent());
            if (text.startsWith("<Response")) {
                return text;
            }

            Node firstChild = soapElement.getFirstChild();
            if (firstChild instanceof Element element && "Response".equals(element.getTagName())) {
                return nodeToString(element);
            }
        }

        String bodyText = normalizeText(responseElement.getTextContent());
        if (!bodyText.isBlank()) {
            return bodyText;
        }
        throw new BizException(502, "门诊检验查询接口未返回业务XML");
    }

    private ParsedResult parseBusinessResponse(String responseXml) throws Exception {
        Document document = parseXml(responseXml);
        String resultCode = textContent(document, "Code");
        String resultDesc = textContent(document, "Desc");
        int orderCount = document.getElementsByTagName("OrderInfo").getLength();
        return new ParsedResult(defaultText(resultCode), defaultText(resultDesc), orderCount);
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

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    private String textContent(Document document, String tagName) {
        NodeList nodes = document.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return "";
        }
        return defaultText(nodes.item(0).getTextContent());
    }

    private String nodeToString(Node node) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }

    private String soapMessageToString(SOAPMessage soapMessage) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        soapMessage.writeTo(outputStream);
        return outputStream.toString(java.nio.charset.StandardCharsets.UTF_8);
    }

    private String resolveEndpoint() {
        if (StringUtils.hasText(properties.getEndpointUrl())) {
            return properties.getEndpointUrl();
        }
        String wsdl = properties.getWsdlUrl();
        int index = wsdl.indexOf("?");
        return index > 0 ? wsdl.substring(0, index) : wsdl;
    }

    private String resolveSoapAction() {
        if (StringUtils.hasText(properties.getSoapAction())) {
            return properties.getSoapAction();
        }
        return properties.getMethodName();
    }

    private String element(String name, String value) {
        return "<" + name + ">" + escapeXml(defaultText(value)) + "</" + name + ">";
    }

    private String escapeXml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String formatDateTime(LocalDateTime time) {
        return DATE_TIME_FORMATTER.format(time);
    }

    private String defaultText(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim();
    }

    private record ParsedResult(String resultCode, String resultDesc, int orderCount) {
    }
}
