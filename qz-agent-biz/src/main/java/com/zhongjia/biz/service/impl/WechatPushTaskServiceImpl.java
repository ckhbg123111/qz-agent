package com.zhongjia.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongjia.biz.entity.WechatPushTask;
import com.zhongjia.biz.mapper.WechatPushTaskMapper;
import com.zhongjia.biz.service.WechatPushTaskService;
import org.springframework.stereotype.Service;

@Service
public class WechatPushTaskServiceImpl extends ServiceImpl<WechatPushTaskMapper, WechatPushTask>
        implements WechatPushTaskService {
}
