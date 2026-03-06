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

    private boolean testMode = false;

    private int reportDelayDays = 2;

    private int followUpDelayDays = 14;

    private int triggerHour = 10;

    private int reportDelayMinutesForTest = 2;

    private int followUpDelayMinutesForTest = 4;

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

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public int getReportDelayDays() {
        return reportDelayDays;
    }

    public void setReportDelayDays(int reportDelayDays) {
        this.reportDelayDays = reportDelayDays;
    }

    public int getFollowUpDelayDays() {
        return followUpDelayDays;
    }

    public void setFollowUpDelayDays(int followUpDelayDays) {
        this.followUpDelayDays = followUpDelayDays;
    }

    public int getTriggerHour() {
        return triggerHour;
    }

    public void setTriggerHour(int triggerHour) {
        this.triggerHour = triggerHour;
    }

    public int getReportDelayMinutesForTest() {
        return reportDelayMinutesForTest;
    }

    public void setReportDelayMinutesForTest(int reportDelayMinutesForTest) {
        this.reportDelayMinutesForTest = reportDelayMinutesForTest;
    }

    public int getFollowUpDelayMinutesForTest() {
        return followUpDelayMinutesForTest;
    }

    public void setFollowUpDelayMinutesForTest(int followUpDelayMinutesForTest) {
        this.followUpDelayMinutesForTest = followUpDelayMinutesForTest;
    }
}
