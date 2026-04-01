package com.zhongjia.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lab_query_record")
public class LabQueryRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String taskName;

    private String patientId;

    private String queryTag;

    private LocalDateTime queryStartTime;

    private LocalDateTime queryEndTime;

    private String msgId;

    private String requestXml;

    private String responseXml;

    private String resultCode;

    private String resultDesc;

    private Integer orderCount;

    private String status;

    private String errorMessage;

    private LocalDateTime createTime;
}
