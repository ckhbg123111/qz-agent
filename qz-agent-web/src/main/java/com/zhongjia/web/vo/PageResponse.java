package com.zhongjia.web.vo;

import java.util.List;
import lombok.Data;

@Data
public class PageResponse<T> {

    private long current;
    private long size;
    private long total;
    private List<T> records;
}
