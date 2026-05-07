package com.zhongjia.web.push;

public class PushExecutionResult {

    private final boolean skipped;
    private final String jumpLink;

    private PushExecutionResult(boolean skipped, String jumpLink) {
        this.skipped = skipped;
        this.jumpLink = jumpLink;
    }

    public static PushExecutionResult skipped() {
        return new PushExecutionResult(true, "");
    }

    public static PushExecutionResult pushed(String jumpLink) {
        return new PushExecutionResult(false, jumpLink);
    }

    public boolean isSkipped() {
        return skipped;
    }

    public String getJumpLink() {
        return jumpLink;
    }
}
