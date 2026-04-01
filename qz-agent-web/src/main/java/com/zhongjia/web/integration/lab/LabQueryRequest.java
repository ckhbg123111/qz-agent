package com.zhongjia.web.integration.lab;

import java.time.LocalDateTime;

public record LabQueryRequest(
        String sender,
        String receiver,
        String msgId,
        String messageQueryName,
        String queryTag,
        String patientType,
        String patientId,
        String visitId,
        String orgIdExec,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String isFilter
) {
}
