package com.zhongjia.web.integration.lab;

public record LabQueryResponse(
        String msgId,
        String requestXml,
        String soapRequestXml,
        String responseXml,
        String soapResponseXml,
        String resultCode,
        String resultDesc,
        int orderCount
) {

    public boolean isSuccess() {
        return "CA".equalsIgnoreCase(resultCode);
    }
}
