package com.zhongjia.web.integration.wechat;

import com.zhongjia.web.config.WechatPushProperties;
import com.zhongjia.web.exception.BizException;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPBodyElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Dispatch;
import jakarta.xml.ws.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.xml.namespace.QName;
import java.net.URL;

@Component
public class WechatPushClient {

    private static final Logger log = LoggerFactory.getLogger(WechatPushClient.class);

    private final WechatPushProperties properties;

    public WechatPushClient(WechatPushProperties properties) {
        this.properties = properties;
    }

    public void pushMessage(String bizcode, String patientId, String messageXml) {
        if (!StringUtils.hasText(properties.getWsdlUrl())
                || !StringUtils.hasText(properties.getNamespace())
                || !StringUtils.hasText(properties.getServiceName())
                || !StringUtils.hasText(properties.getPortName())
                || !StringUtils.hasText(properties.getMethodName())) {
            throw new BizException(500, "微信推送接口配置不完整");
        }

        try {
            QName serviceQName = new QName(properties.getNamespace(), properties.getServiceName());
            QName portQName = new QName(properties.getNamespace(), properties.getPortName());
            URL wsdlUrl = new URL(properties.getWsdlUrl());
            Service service = Service.create(wsdlUrl, serviceQName);
            Dispatch<SOAPMessage> dispatch = service.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);
            dispatch.getRequestContext().put(BindingProvider.SOAPACTION_USE_PROPERTY, Boolean.TRUE);
            dispatch.getRequestContext().put(BindingProvider.SOAPACTION_URI_PROPERTY, resolveSoapAction());
            dispatch.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, resolveEndpoint());
            dispatch.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, null);
            dispatch.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, null);

            SOAPMessage request = buildRequest(bizcode, patientId, messageXml);
            dispatch.invoke(request);
        } catch (Exception e) {
            log.error("微信推送接口调用失败: bizcode={}, patientId={}, endpoint={}", bizcode, patientId, resolveEndpoint(), e);
            throw new BizException(502, "微信推送接口调用失败");
        }
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

    private SOAPMessage buildRequest(String bizcode, String patientId, String messageXml) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        String namespace = properties.getNamespace();
        String prefix = "dhc";
        envelope.addNamespaceDeclaration(prefix, namespace);
        SOAPBody body = soapMessage.getSOAPBody();
        QName methodQName = new QName(namespace, properties.getMethodName(), prefix);
        SOAPBodyElement methodElement = body.addBodyElement(methodQName);
        methodElement.addChildElement(new QName(namespace, "bizcode", prefix)).addTextNode(bizcode);
        methodElement.addChildElement(new QName(namespace, "message", prefix)).addTextNode(messageXml);
        methodElement.addChildElement(new QName(namespace, "patientId", prefix)).addTextNode(patientId);
        soapMessage.saveChanges();
        return soapMessage;
    }
}
