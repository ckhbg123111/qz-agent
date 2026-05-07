package com.zhongjia.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("wechat_push_success_record")
public class WechatPushSuccessRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String patientId;

    private String tag;

    private String sourceRuleCode;

    private Long taskId;

    private Long pushLogId;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
