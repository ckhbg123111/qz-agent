package com.zhongjia.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lab_query_cursor")
public class LabQueryCursor {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskName;

    private LocalDateTime lastQueryStartTime;

    private LocalDateTime lastQueryEndTime;

    private String lastStatus;

    private String lastErrorMessage;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
