package com.zhongjia.web.push;

public final class EducationPushRuleConstants {

    private EducationPushRuleConstants() {
    }

    public static final String EVENT_TYPE_DIAGNOSIS = "DIAGNOSIS_EVENT";
    public static final String EVENT_TYPE_PRESCRIPTION = "PRESCRIPTION_EVENT";
    public static final String EVENT_TYPE_SURGERY_CONFIRMATION = "SURGERY_CONFIRMATION_EVENT";
    public static final String EVENT_TYPE_SURGERY_COMPLETION = "SURGERY_COMPLETION_EVENT";

    public static final String TRIGGER_TYPE_IMMEDIATE = "IMMEDIATE";
    public static final String TRIGGER_TYPE_DELAYED = "DELAYED";

    public static final String FIELD_DIAGNOSIS_CODE_SYSTEM = "diagnosisCodeSystem";
    public static final String FIELD_DIAGNOSIS_CODE = "diagnosisCode";
    public static final String FIELD_MEDICINE_NAME = "medicineName";
    public static final String FIELD_PREOPERATIVE_DIAGNOSIS_CODE = "preoperativeDiagnosisCode";
    public static final String FIELD_PLANNED_OPERATION_CODE = "plannedOperationCode";
    public static final String FIELD_PERFORMED_OPERATION_CODE = "performedOperationCode";
    public static final String FIELD_PLANNED_START_TIME = "plannedStartTime";
    public static final String FIELD_ACTUAL_END_TIME = "actualEndTime";

    public static final String MATCH_EQUALS_IGNORE_CASE = "EQUALS_IGNORE_CASE";
    public static final String MATCH_CONTAINS_IGNORE_CASE = "CONTAINS_IGNORE_CASE";

    public static final String DELAY_UNIT_HOURS = "HOURS";
    public static final String DELAY_UNIT_DAYS = "DAYS";
    public static final String DELAY_UNIT_MONTHS = "MONTHS";

    public static final String TRIGGER_TIME_STRATEGY_IMMEDIATE = "IMMEDIATE";
    public static final String TRIGGER_TIME_STRATEGY_RELATIVE_OFFSET = "RELATIVE_OFFSET";
    public static final String TRIGGER_TIME_STRATEGY_ANCHOR_TIME = "ANCHOR_TIME";

    public static final String LATE_POLICY_SKIP = "SKIP";
    public static final String LATE_POLICY_IMMEDIATE = "IMMEDIATE";
}
