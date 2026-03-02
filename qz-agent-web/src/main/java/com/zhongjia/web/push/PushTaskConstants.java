package com.zhongjia.web.push;

public final class PushTaskConstants {

    private PushTaskConstants() {
    }

    public static final String TAG_LAB_APPOINTMENT = "UUID_EXAMPLE_1";
    public static final String TAG_REPORT = "UUID_EXAMPLE_3";
    public static final String TAG_PRESCRIPTION = "UUID_EXAMPLE_2";
    public static final String TAG_FOLLOW_UP = "UUID_EXAMPLE_10";

    public static final String TASK_TYPE_REPORT_WARNING = "REPORT_WARNING";
    public static final String TASK_TYPE_FOLLOW_UP_REMINDER = "FOLLOW_UP_REMINDER";

    public static final String TASK_STATUS_PENDING = "PENDING";
    public static final String TASK_STATUS_SENDING = "SENDING";
    public static final String TASK_STATUS_SUCCESS = "SUCCESS";
    public static final String TASK_STATUS_FAILED = "FAILED";
    public static final String TASK_STATUS_DEAD = "DEAD";

    public static final String ENQUEUE_STATUS_ENQUEUED = "ENQUEUED";
    public static final String ENQUEUE_STATUS_WAITING = "WAITING_ENQUEUE";
    public static final String ENQUEUE_STATUS_DEAD = "DEAD";
}
