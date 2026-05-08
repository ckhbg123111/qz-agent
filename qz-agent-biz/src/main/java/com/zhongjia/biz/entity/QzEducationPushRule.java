package com.zhongjia.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("qz_education_push_rule")
public class QzEducationPushRule {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String ruleCode;

    private String ruleName;

    private String eventType;

    private String triggerType;

    private String tag;

    private String previousRuleCode;

    private Integer delayAmount;

    private String delayUnit;

    private String triggerTimeStrategy;

    private String anchorField;

    private Integer anchorDayOffset;

    private String anchorTime;

    private String latePolicy;

    private String windowEndField;

    private Integer enabled;

    private Integer sortOrder;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
