package com.zhongjia.web.integration.lab;

import com.zhongjia.web.config.LabQueryProperties;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPMessage;
import org.junit.jupiter.api.Test;

import javax.xml.namespace.QName;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LabQueryClientTest {

    @Test
    void shouldBuildBusinessRequestXml() {
        LabQueryClient client = new LabQueryClient(buildProperties());
        LabQueryRequest request = new LabQueryRequest(
                "APP",
                "LIS",
                "MSG-1",
                "Z06^查询检验申请信息",
                "Z0601",
                "1",
                "P1001",
                "",
                "2301",
                LocalDateTime.of(2026, 4, 1, 10, 0, 0),
                LocalDateTime.of(2026, 4, 1, 10, 10, 0),
                "0"
        );

        String xml = client.buildBusinessRequestXml(request);

        assertTrue(xml.contains("<EventType>QBP^Q11^CXJYSQ_XML</EventType>"));
        assertTrue(xml.contains("<PatientId>P1001</PatientId>"));
        assertTrue(xml.contains("<OrgIdExec>2301</OrgIdExec>"));
        assertTrue(xml.contains("<StartDateTime>20260401100000</StartDateTime>"));
        assertTrue(xml.contains("<EndDateTime>20260401101000</EndDateTime>"));
    }

    @Test
    void shouldParseSoapResponseAndCountOrders() throws Exception {
        LabQueryClient client = new LabQueryClient(buildProperties());
        SOAPMessage soapResponse = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL).createMessage();
        SOAPEnvelope envelope = soapResponse.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration("lab", "http://tempuri.org/");
        SOAPBody body = envelope.getBody();
        body.addBodyElement(new QName("http://tempuri.org/", "queryResponse", "lab"))
                .addChildElement(new QName("http://tempuri.org/", "return", "lab"))
                .addTextNode("""
                        <Response>
                          <MessageBody>
                            <Result>
                              <Code>CA</Code>
                              <Desc></Desc>
                            </Result>
                            <OrderInfoList>
                              <OrderInfo></OrderInfo>
                              <OrderInfo></OrderInfo>
                            </OrderInfoList>
                          </MessageBody>
                        </Response>
                        """);
        soapResponse.saveChanges();

        LabQueryResponse response = client.parseSoapResponse("MSG-1", "<Request/>", "<soap/>", soapResponse);

        assertEquals("CA", response.resultCode());
        assertEquals("", response.resultDesc());
        assertEquals(2, response.orderCount());
        assertTrue(response.responseXml().contains("<OrderInfoList>"));
    }

    private LabQueryProperties buildProperties() {
        LabQueryProperties properties = new LabQueryProperties();
        properties.setWsdlUrl("http://example.com/service?wsdl");
        properties.setNamespace("http://tempuri.org/");
        properties.setServiceName("LabQueryService");
        properties.setPortName("LabQueryServiceSoap");
        properties.setMethodName("queryLabOrders");
        properties.setMessageParamName("message");
        return properties;
    }
}
