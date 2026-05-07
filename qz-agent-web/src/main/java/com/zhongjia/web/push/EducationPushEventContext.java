package com.zhongjia.web.push;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        return Collections.emptyList();
    }

    private List<String> singleValue(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        return Collections.singletonList(value);
    }
}
