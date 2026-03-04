package com.zhongjia.web.push;

public final class PushTaskConstants {

    private PushTaskConstants() {
    }

    public static final String TAG_LAB_APPOINTMENT = "UUID_EXAMPLE_1";// 检验预约成功推送
    public static final String TAG_REPORT = "UUID_EXAMPLE_9";// 检验出报告两天后推送推送
    public static final String TAG_PRESCRIPTION = "UUID_EXAMPLE_6";// 四联处方开具推送
    public static final String TAG_PRESCRIPTION_2 = "UUID_EXAMPLE_7"; // 二联处方开具推送
    public static final String TAG_FOLLOW_UP = "UUID_EXAMPLE_10";// 处方开具14天后推送

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
