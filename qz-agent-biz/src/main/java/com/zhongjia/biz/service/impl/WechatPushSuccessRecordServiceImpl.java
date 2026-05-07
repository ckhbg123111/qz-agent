package com.zhongjia.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongjia.biz.entity.WechatPushSuccessRecord;
import com.zhongjia.biz.mapper.WechatPushSuccessRecordMapper;
import com.zhongjia.biz.service.WechatPushSuccessRecordService;
import java.time.LocalDateTime;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class WechatPushSuccessRecordServiceImpl
        extends ServiceImpl<WechatPushSuccessRecordMapper, WechatPushSuccessRecord>
        implements WechatPushSuccessRecordService {

    @Override
    public boolean hasSuccess(String patientId, String tag) {
        String normalizedPatientId = defaultString(patientId).trim();
        String normalizedTag = defaultString(tag).trim();
        if (normalizedPatientId.isEmpty() || normalizedTag.isEmpty()) {
            return false;
        }
        return lambdaQuery()
                .eq(WechatPushSuccessRecord::getPatientId, normalizedPatientId)
                .eq(WechatPushSuccessRecord::getTag, normalizedTag)
                .count() > 0;
    }

    @Override
    public Long recordSuccess(String patientId, String tag, String sourceRuleCode, Long taskId, Long pushLogId) {
        String normalizedPatientId = defaultString(patientId).trim();
        String normalizedTag = defaultString(tag).trim();
        if (normalizedPatientId.isEmpty() || normalizedTag.isEmpty()) {
            return null;
        }

        WechatPushSuccessRecord record = new WechatPushSuccessRecord();
        record.setPatientId(normalizedPatientId);
        record.setTag(normalizedTag);
        record.setSourceRuleCode(defaultString(sourceRuleCode).trim());
        record.setTaskId(taskId);
        record.setPushLogId(pushLogId);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        try {
            save(record);
            return record.getId();
        } catch (DuplicateKeyException ex) {
            WechatPushSuccessRecord existing = lambdaQuery()
                    .eq(WechatPushSuccessRecord::getPatientId, normalizedPatientId)
                    .eq(WechatPushSuccessRecord::getTag, normalizedTag)
                    .one();
            return existing == null ? null : existing.getId();
        }
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
