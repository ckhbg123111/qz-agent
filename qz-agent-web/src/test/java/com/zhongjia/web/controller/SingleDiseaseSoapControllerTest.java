package com.zhongjia.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SingleDiseaseSoapControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SingleDiseaseMessageDispatcher dispatcher = new SingleDiseaseMessageDispatcher();
        SingleDiseaseSoapController controller = new SingleDiseaseSoapController(dispatcher);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void shouldReturnSuccessAckForSupportedMessage() throws Exception {
        String requestXml = """
                <Request>
                  <MessageHeader>
                    <Sender>HIS</Sender>
                    <Receiver>ZJ_AGENT</Receiver>
                    <SendTime>20260401120000</SendTime>
                    <EventType>ADT_A01_MZ</EventType>
                    <MsgId>SOAP-001</MsgId>
                  </MessageHeader>
                  <MessageBody>
                    <EventInfo>
                      <EventTypeCode>REG</EventTypeCode>
                    </EventInfo>
                  </MessageBody>
                </Request>
                """;

        mockMvc.perform(post(SingleDiseaseSoapController.SERVICE_PATH)
                        .contentType(MediaType.TEXT_XML)
                        .content(buildSoapRequest(requestXml)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("receiveMessageResponse")))
                .andExpect(content().string(containsString("ACK_ADT_A01_MZ")))
                .andExpect(content().string(containsString("&lt;Code&gt;CA&lt;/Code&gt;")));
    }

    @Test
    void shouldReturnErrorAckForUnsupportedEventType() throws Exception {
        String requestXml = """
                <Request>
                  <MessageHeader>
                    <Sender>HIS</Sender>
                    <Receiver>ZJ_AGENT</Receiver>
                    <SendTime>20260401120000</SendTime>
                    <EventType>UNKNOWN_EVENT</EventType>
                    <MsgId>SOAP-002</MsgId>
                  </MessageHeader>
                  <MessageBody>
                    <Dummy>1</Dummy>
                  </MessageBody>
                </Request>
                """;

        mockMvc.perform(post(SingleDiseaseSoapController.SERVICE_PATH)
                        .contentType(MediaType.TEXT_XML)
                        .content(buildSoapRequest(requestXml)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;Code&gt;CE&lt;/Code&gt;")))
                .andExpect(content().string(containsString("暂不支持的EventType: UNKNOWN_EVENT")));
    }

    private String buildSoapRequest(String messageXml) {
        return """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sdhp="http://aiqikang.com/sdhp/ws">
                  <soapenv:Header/>
                  <soapenv:Body>
                    <sdhp:receiveMessage>
                      <sdhp:messageXml>%s</sdhp:messageXml>
                    </sdhp:receiveMessage>
                  </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(escapeXml(messageXml));
    }

    private String escapeXml(String xml) {
        return xml.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
