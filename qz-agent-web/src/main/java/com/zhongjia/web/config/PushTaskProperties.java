package com.zhongjia.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "push.task")
public class PushTaskProperties {

    private String zsetKey = "wechat:push:task:delayed";

    private int batchSize = 100;

    private int maxRetryCount = 5;

    private int baseRetryDelayMinutes = 10;

    public String getZsetKey() {
        return zsetKey;
    }

    public void setZsetKey(String zsetKey) {
        this.zsetKey = zsetKey;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public int getBaseRetryDelayMinutes() {
        return baseRetryDelayMinutes;
    }

    public void setBaseRetryDelayMinutes(int baseRetryDelayMinutes) {
        this.baseRetryDelayMinutes = baseRetryDelayMinutes;
    }
}
