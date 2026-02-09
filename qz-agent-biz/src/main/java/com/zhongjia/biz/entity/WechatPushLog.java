package com.zhongjia.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("wechat_push_log")
public class WechatPushLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String bizcode;

    private String patientId;

    private String tag;

    private String message;

    private Integer wechatApiCode;

    private String wechatApiMessage;

    private String jumpLink;

    private String pushStatus;

    private String errorMessage;

    private LocalDateTime createTime;
}
