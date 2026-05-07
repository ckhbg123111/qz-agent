package com.zhongjia.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("qz_education_push_rule_condition")
public class QzEducationPushRuleCondition {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ruleId;

    private String fieldName;

    private String matchType;

    private String matchValue;

    private Integer enabled;

    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
