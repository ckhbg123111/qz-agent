package com.zhongjia.web.push;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class EducationPushEventContext {

    private String eventType;

    private String patientId;

    private String patientName;

    private String gender;

    private Integer age;

    private String diagnosis;

    private String diagnosisCodeSystem;

    private String diagnosisCode;

    private String prescription;

    private String examTime;

    private String sourceNo;

    private List<String> medicineNames = new ArrayList<>();

    private List<String> preoperativeDiagnosisCodes = new ArrayList<>();

    private List<String> plannedOperationCodes = new ArrayList<>();

    private List<String> performedOperationCodes = new ArrayList<>();

    private Map<String, LocalDateTime> businessTimes = new HashMap<>();

    public List<String> getValues(String fieldName) {
        if (EducationPushRuleConstants.FIELD_DIAGNOSIS_CODE_SYSTEM.equals(fieldName)) {
            return singleValue(diagnosisCodeSystem);
        }
        if (EducationPushRuleConstants.FIELD_DIAGNOSIS_CODE.equals(fieldName)) {
            return singleValue(diagnosisCode);
        }
        if (EducationPushRuleConstants.FIELD_MEDICINE_NAME.equals(fieldName)) {
            return medicineNames == null ? Collections.emptyList() : medicineNames;
        }
        if (EducationPushRuleConstants.FIELD_PREOPERATIVE_DIAGNOSIS_CODE.equals(fieldName)) {
            return safeValues(preoperativeDiagnosisCodes);
        }
        if (EducationPushRuleConstants.FIELD_PLANNED_OPERATION_CODE.equals(fieldName)) {
            return safeValues(plannedOperationCodes);
        }
        if (EducationPushRuleConstants.FIELD_PERFORMED_OPERATION_CODE.equals(fieldName)) {
            return safeValues(performedOperationCodes);
        }
        return Collections.emptyList();
    }

    public void putBusinessTime(String fieldName, LocalDateTime time) {
        if (fieldName == null || fieldName.isBlank() || time == null) {
            return;
        }
        if (businessTimes == null) {
            businessTimes = new HashMap<>();
        }
        businessTimes.put(fieldName, time);
    }

    public LocalDateTime getBusinessTime(String fieldName) {
        if (fieldName == null || fieldName.isBlank() || businessTimes == null) {
            return null;
        }
        return businessTimes.get(fieldName);
    }

    private List<String> singleValue(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(value);
    }

    private List<String> safeValues(List<String> values) {
        return values == null ? Collections.emptyList() : values;
    }
}
