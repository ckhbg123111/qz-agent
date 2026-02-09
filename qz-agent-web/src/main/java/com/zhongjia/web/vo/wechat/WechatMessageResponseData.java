package com.zhongjia.web.vo.wechat;

import lombok.Data;

@Data
public class WechatMessageResponseData {

    private String replyTitle;
    private String replyDescription;
    private String replyImageUrl;
    private String jumpLink;
}
