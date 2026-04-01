package com.zhongjia.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongjia.biz.entity.LabQueryRecord;
import com.zhongjia.biz.mapper.LabQueryRecordMapper;
import com.zhongjia.biz.service.LabQueryRecordService;
import org.springframework.stereotype.Service;

@Service
public class LabQueryRecordServiceImpl extends ServiceImpl<LabQueryRecordMapper, LabQueryRecord>
        implements LabQueryRecordService {
}
