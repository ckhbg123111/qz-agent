package com.zhongjia.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("wechat_push_task")
public class WechatPushTask {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskType;

    private String patientId;

    private String tag;

    private String sourceNo;

    private String idempotentKey;

    private LocalDateTime baseTime;

    private LocalDateTime triggerTime;

    private LocalDateTime nextRetryTime;

    private Integer retryCount;

    private Integer maxRetryCount;

    private String status;

    private String enqueueStatus;

    private String requestJson;

    private String lastErrorMessage;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
