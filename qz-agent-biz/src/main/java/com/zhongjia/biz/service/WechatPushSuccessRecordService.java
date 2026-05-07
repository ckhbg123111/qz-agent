package com.zhongjia.biz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhongjia.biz.entity.WechatPushSuccessRecord;

public interface WechatPushSuccessRecordService extends IService<WechatPushSuccessRecord> {

    boolean hasSuccess(String patientId, String tag);

    Long recordSuccess(String patientId, String tag, String sourceRuleCode, Long taskId, Long pushLogId);
}
