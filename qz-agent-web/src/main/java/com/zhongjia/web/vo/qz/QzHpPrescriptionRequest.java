package com.zhongjia.web.vo.qz;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(name = "QzHpPrescriptionRequest", description = "处方推送请求")
public class QzHpPrescriptionRequest {

    @Schema(description = "患者ID")
    private String patientId;

    @Schema(description = "患者名称")
    private String patientName;

    @Schema(description = "患者性别（男/女/未知）")
    private String gender;

    @Schema(description = "患者年龄")
    private Integer age;

    @Schema(description = "就诊号/病案号")
    private String visitNo;

    @Schema(description = "主诊断编码，优先传 ICD-10；糖尿病示例 E14.90")
    private String diagnosisCode;

    @Schema(description = "诊断编码体系，默认 ICD-10")
    private String diagnosisCodeSystem;

    @Schema(description = "主诊断名称，如糖尿病、幽门螺杆菌感染")
    private String diagnosis;

    @Schema(description = "处方日期（ISO-8601 或 yyyy-MM-dd）")
    private String prescriptionDate;

    @Schema(description = "治疗方法")
    private String therapy;

    @Valid
    @Schema(description = "药品信息列表，优先按药品编码识别；糖尿病场景示例可传胰岛素、利拉鲁肽、司美格鲁肽、替尔泊肽对应院内药品字典编码")
    private List<QzHpMedicineItem> medicineItem;

    @Schema(description = "药品信息列表")
    private List<String> medicines;

    @Schema(description = "医院")
    private String hospital;

    @Schema(description = "科室")
    private String department;

    @Schema(description = "处方医师")
    private String doctor;

    @Schema(description = "审核药师")
    private String pharmacist;

    public void setMedicineItem(List<QzHpMedicineItem> medicineItem) {
        this.medicineItem = medicineItem;
    }

    @JsonSetter("medicines")
    public void setMedicines(JsonNode medicinesNode) {
        if (medicinesNode == null || medicinesNode.isNull()) {
            this.medicines = null;
            return;
        }
        if (containsMedicineObject(medicinesNode)) {
            this.medicineItem = parseMedicineItems(medicinesNode);
            this.medicines = null;
            return;
        }
        this.medicines = parseMedicineNames(medicinesNode);
    }

    private boolean containsMedicineObject(JsonNode medicinesNode) {
        if (medicinesNode.isObject()) {
            return true;
        }
        if (!medicinesNode.isArray()) {
            return false;
        }
        for (JsonNode itemNode : medicinesNode) {
            if (itemNode != null && itemNode.isObject()) {
                return true;
            }
        }
        return false;
    }

    private List<QzHpMedicineItem> parseMedicineItems(JsonNode medicinesNode) {
        List<QzHpMedicineItem> result = new ArrayList<>();
        if (medicinesNode.isObject()) {
            addMedicineItem(result, medicinesNode);
            return result;
        }
        if (!medicinesNode.isArray()) {
            return result;
        }
        for (JsonNode itemNode : medicinesNode) {
            addMedicineItem(result, itemNode);
        }
        return result;
    }

    private void addMedicineItem(List<QzHpMedicineItem> result, JsonNode itemNode) {
        if (itemNode == null || !itemNode.isObject()) {
            return;
        }
        QzHpMedicineItem item = new QzHpMedicineItem();
        item.setMedicineCode(readText(itemNode, "medicineCode"));
        item.setMedicineName(readText(itemNode, "medicineName"));
        item.setMedicineCodeSystem(readText(itemNode, "medicineCodeSystem"));
        item.setSpecification(readText(itemNode, "specification"));
        item.setDosage(readText(itemNode, "dosage"));
        item.setDosageUnit(readText(itemNode, "dosageUnit"));
        item.setFrequency(readText(itemNode, "frequency"));
        item.setRoute(readText(itemNode, "route"));
        result.add(item);
    }

    private List<String> parseMedicineNames(JsonNode medicinesNode) {
        List<String> result = new ArrayList<>();
        if (medicinesNode.isTextual()) {
            result.add(medicinesNode.asText());
            return result;
        }
        if (!medicinesNode.isArray()) {
            return result;
        }
        for (JsonNode itemNode : medicinesNode) {
            if (itemNode != null && !itemNode.isNull()) {
                result.add(itemNode.asText());
            }
        }
        return result;
    }

    private String readText(JsonNode itemNode, String fieldName) {
        JsonNode fieldNode = itemNode.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }
        return fieldNode.asText();
    }
}
