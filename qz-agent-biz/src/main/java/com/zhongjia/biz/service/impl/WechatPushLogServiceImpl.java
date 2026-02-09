package com.zhongjia.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongjia.biz.entity.WechatPushLog;
import com.zhongjia.biz.mapper.WechatPushLogMapper;
import com.zhongjia.biz.service.WechatPushLogService;
import org.springframework.stereotype.Service;

@Service
public class WechatPushLogServiceImpl extends ServiceImpl<WechatPushLogMapper, WechatPushLog>
        implements WechatPushLogService {
}
