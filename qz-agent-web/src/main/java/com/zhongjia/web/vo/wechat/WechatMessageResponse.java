package com.zhongjia.web.vo.wechat;

import lombok.Data;

@Data
public class WechatMessageResponse {

    private int code;
    private String message;
    private WechatMessageResponseData data;
}
