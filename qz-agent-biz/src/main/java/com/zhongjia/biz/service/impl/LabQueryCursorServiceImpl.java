package com.zhongjia.biz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhongjia.biz.entity.LabQueryCursor;
import com.zhongjia.biz.mapper.LabQueryCursorMapper;
import com.zhongjia.biz.service.LabQueryCursorService;
import org.springframework.stereotype.Service;

@Service
public class LabQueryCursorServiceImpl extends ServiceImpl<LabQueryCursorMapper, LabQueryCursor>
        implements LabQueryCursorService {
}
