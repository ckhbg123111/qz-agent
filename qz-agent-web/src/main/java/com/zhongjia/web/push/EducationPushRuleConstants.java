package com.zhongjia.web.push;

public final class EducationPushRuleConstants {

    private EducationPushRuleConstants() {
    }

    public static final String EVENT_TYPE_DIAGNOSIS = "DIAGNOSIS_EVENT";
    public static final String EVENT_TYPE_PRESCRIPTION = "PRESCRIPTION_EVENT";

    public static final String TRIGGER_TYPE_IMMEDIATE = "IMMEDIATE";
    public static final String TRIGGER_TYPE_DELAYED = "DELAYED";

    public static final String FIELD_DIAGNOSIS_CODE_SYSTEM = "diagnosisCodeSystem";
    public static final String FIELD_DIAGNOSIS_CODE = "diagnosisCode";
    public static final String FIELD_MEDICINE_NAME = "medicineName";

    public static final String MATCH_EQUALS_IGNORE_CASE = "EQUALS_IGNORE_CASE";
    public static final String MATCH_CONTAINS_IGNORE_CASE = "CONTAINS_IGNORE_CASE";

    public static final String DELAY_UNIT_HOURS = "HOURS";
    public static final String DELAY_UNIT_DAYS = "DAYS";
    public static final String DELAY_UNIT_MONTHS = "MONTHS";
}
