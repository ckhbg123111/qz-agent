package com.zhongjia.web.vo.singleDisease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

public final class OrderMessageModels {

    private OrderMessageModels() {
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OmpO09Body {

        @JacksonXmlProperty(localName = "PatientInfo")
        private OrderPatientInfo patientInfo;

        @JacksonXmlProperty(localName = "PatientVisitInfo")
        private OrderPatientVisitInfo patientVisitInfo;

        @JacksonXmlProperty(localName = "OrderInfoList")
        private OrderInfoList orderInfoList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OrderInfoList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "OrderInfo")
        private List<OrderInfo> orderInfos;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OrderInfo {

        @JacksonXmlProperty(localName = "OrderItemInfo")
        private OrderItemInfo orderItemInfo;

        @JacksonXmlProperty(localName = "OrderTimingInfo")
        private OrderTimingInfo orderTimingInfo;

        @JacksonXmlProperty(localName = "OrderItemDetail")
        private OrderItemDetail orderItemDetail;

        @JacksonXmlProperty(localName = "Comments")
        private OrderComment comments;

        @JacksonXmlProperty(localName = "OrderRouteInfo")
        private OrderRouteInfo orderRouteInfo;

        @JacksonXmlProperty(localName = "AssignedDrugInfo")
        private AssignedDrugInfo assignedDrugInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OrderItemInfo {

        @JacksonXmlProperty(localName = "OrderControll")
        private String orderControll;

        @JacksonXmlProperty(localName = "BillOrderId")
        private String billOrderId;

        @JacksonXmlProperty(localName = "ExecOrderId")
        private String execOrderId;

        @JacksonXmlProperty(localName = "OrderId")
        private String orderId;

        @JacksonXmlProperty(localName = "OrderStatus")
        private String orderStatus;

        @JacksonXmlProperty(localName = "OrgPlanBegDate")
        private String orgPlanBegDate;

        @JacksonXmlProperty(localName = "EmpIdBegDoct")
        private String empIdBegDoct;

        @JacksonXmlProperty(localName = "EmpNameBegDoct")
        private String empNameBegDoct;

        @JacksonXmlProperty(localName = "OrgIdInput")
        private String orgIdInput;

        @JacksonXmlProperty(localName = "OrgNameInput")
        private String orgNameInput;

        @JacksonXmlProperty(localName = "OrderTypeCode")
        private String orderTypeCode;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OrderTimingInfo {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "FreqCode")
        private String freqCode;

        @JacksonXmlProperty(localName = "FreqName")
        private String freqName;

        @JacksonXmlProperty(localName = "FreqFrequency")
        private String freqFrequency;

        @JacksonXmlProperty(localName = "FreqLimit")
        private String freqLimit;

        @JacksonXmlProperty(localName = "FreqLimitUnit")
        private String freqLimitUnit;

        @JacksonXmlProperty(localName = "FreqDuration")
        private String freqDuration;

        @JacksonXmlProperty(localName = "BegExecDate")
        private String begExecDate;

        @JacksonXmlProperty(localName = "StopExecDate")
        private String stopExecDate;

        @JacksonXmlProperty(localName = "LongTempCode")
        private String longTempCode;

        @JacksonXmlProperty(localName = "ExecDateDesc")
        private String execDateDesc;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OrderItemDetail {

        @JacksonXmlProperty(localName = "ItemId")
        private String itemId;

        @JacksonXmlProperty(localName = "ItemName")
        private String itemName;

        @JacksonXmlProperty(localName = "ItemSpec")
        private String itemSpec;

        @JacksonXmlProperty(localName = "PerMedQty")
        private String perMedQty;

        @JacksonXmlProperty(localName = "PerMedUnit")
        private String perMedUnit;

        @JacksonXmlProperty(localName = "PerMedUnitName")
        private String perMedUnitName;

        @JacksonXmlProperty(localName = "DosageForm")
        private String dosageForm;

        @JacksonXmlProperty(localName = "BillDesc")
        private String billDesc;

        @JacksonXmlProperty(localName = "isRelatedPrescription")
        private String isRelatedPrescription;

        @JacksonXmlProperty(localName = "DrippingSpeed")
        private String drippingSpeed;

        @JacksonXmlProperty(localName = "IsSelfMed")
        private String isSelfMed;

        @JacksonXmlProperty(localName = "IsSelfMedName")
        private String isSelfMedName;

        @JacksonXmlProperty(localName = "Total")
        private String total;

        @JacksonXmlProperty(localName = "TotalUnit")
        private String totalUnit;

        @JacksonXmlProperty(localName = "AntiLevel")
        private String antiLevel;

        @JacksonXmlProperty(localName = "AntiLevelName")
        private String antiLevelName;

        @JacksonXmlProperty(localName = "PurposesName")
        private String purposesName;

        @JacksonXmlProperty(localName = "OrgIdProvide")
        private String orgIdProvide;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OrderComment {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "Comment")
        private String comment;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OrderRouteInfo {

        @JacksonXmlProperty(localName = "UsageCode")
        private String usageCode;

        @JacksonXmlProperty(localName = "UsageName")
        private String usageName;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class AssignedDrugInfo {

        @JacksonXmlProperty(localName = "AssignedDrugCode")
        private String assignedDrugCode;

        @JacksonXmlProperty(localName = "AssignedDrugLevel")
        private String assignedDrugLevel;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OrderPatientInfo {

        @JacksonXmlProperty(localName = "PatientId")
        private String patientId;

        @JacksonXmlProperty(localName = "IDNumber")
        private String idNumber;

        @JacksonXmlProperty(localName = "IDType")
        private String idType;

        @JacksonXmlProperty(localName = "PatientName")
        private String patientName;

        @JacksonXmlProperty(localName = "PatientNameSpell")
        private String patientNameSpell;

        @JacksonXmlProperty(localName = "BirthDate")
        private String birthDate;

        @JacksonXmlProperty(localName = "PatientGender")
        private String patientGender;

        @JacksonXmlProperty(localName = "Address")
        private String address;

        @JacksonXmlProperty(localName = "Telphone")
        private String telphone;

        @JacksonXmlProperty(localName = "MaritalStatus")
        private String maritalStatus;

        @JacksonXmlProperty(localName = "AgeMeter")
        private String ageMeter;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OrderPatientVisitInfo {

        @JacksonXmlProperty(localName = "PatientType")
        private String patientType;

        @JacksonXmlProperty(localName = "DeptCode")
        private String deptCode;

        @JacksonXmlProperty(localName = "CurrentWard")
        private String currentWard;

        @JacksonXmlProperty(localName = "BedNo")
        private String bedNo;

        @JacksonXmlProperty(localName = "AttendingDoctorId")
        private String attendingDoctorId;

        @JacksonXmlProperty(localName = "AttendingDoctorName")
        private String attendingDoctorName;

        @JacksonXmlProperty(localName = "VisitID")
        private String visitID;

        @JacksonXmlProperty(localName = "HospitalAreaCode")
        private String hospitalAreaCode;

        @JacksonXmlProperty(localName = "AdmissionDate")
        private String admissionDate;

        @JacksonXmlProperty(localName = "BigDept")
        private String bigDept;

        @JacksonXmlProperty(localName = "BigDeptName")
        private String bigDeptName;
    }
}
