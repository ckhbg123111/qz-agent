package com.zhongjia.web.vo.wechat;

import lombok.Data;

@Data
public class WechatMessageRequest {

    private String tag;
    private String patientName;
    private String gender;
    private Integer age;
    private String diagnosis;
    private String prescription;
    private String examTime;
    private String patientId;
    private String reminderContent;
}
