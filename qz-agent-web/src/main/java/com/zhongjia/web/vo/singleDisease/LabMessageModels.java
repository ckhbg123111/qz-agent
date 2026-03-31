package com.zhongjia.web.vo.singleDisease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

public final class LabMessageModels {

    private LabMessageModels() {
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabApplyBody {

        @JacksonXmlProperty(localName = "PatientInfo")
        private LabPatientInfo patientInfo;

        @JacksonXmlProperty(localName = "PatientVisitInfo")
        private LabPatientVisitInfo patientVisitInfo;

        @JacksonXmlProperty(localName = "OrderInfoList")
        private LabApplyOrderInfoList orderInfoList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabApplyOrderInfoList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "OrderInfo")
        private List<LabApplyOrderInfo> orderInfoList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabApplyOrderInfo {

        @JacksonXmlProperty(localName = "OrderItemInfo")
        private LabApplyOrderItemInfo orderItemInfo;

        @JacksonXmlProperty(localName = "OrderItemDetail")
        private LabApplyOrderItemDetail orderItemDetail;

        @JacksonXmlProperty(localName = "DiagnosisList")
        private LabDiagnosisList diagnosisList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabApplyOrderItemInfo {

        @JacksonXmlProperty(localName = "OrderControll")
        private String orderControll;

        @JacksonXmlProperty(localName = "BillOrderId")
        private String billOrderId;

        @JacksonXmlProperty(localName = "OrderId")
        private String orderId;

        @JacksonXmlProperty(localName = "ExecOrderId")
        private String execOrderId;

        @JacksonXmlProperty(localName = "CheckStatus")
        private String checkStatus;

        @JacksonXmlProperty(localName = "ApplyTime")
        private String applyTime;

        @JacksonXmlProperty(localName = "ApplyDocNo")
        private String applyDocNo;

        @JacksonXmlProperty(localName = "ApplyDocName")
        private String applyDocName;

        @JacksonXmlProperty(localName = "ApplyDeptCode")
        private String applyDeptCode;

        @JacksonXmlProperty(localName = "ApplyDeptName")
        private String applyDeptName;

        @JacksonXmlProperty(localName = "OrderType")
        private String orderType;

        @JacksonXmlProperty(localName = "ExecDeptCode")
        private String execDeptCode;

        @JacksonXmlProperty(localName = "ExecDeptName")
        private String execDeptName;

        @JacksonXmlProperty(localName = "AmountSuggest")
        private String amountSuggest;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabApplyOrderItemDetail {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "ItemId")
        private String itemId;

        @JacksonXmlProperty(localName = "ItemName")
        private String itemName;

        @JacksonXmlProperty(localName = "IsUrgent")
        private String isUrgent;

        @JacksonXmlProperty(localName = "ClinicalInformation")
        private String clinicalInformation;

        @JacksonXmlProperty(localName = "SampleType")
        private String sampleType;

        @JacksonXmlProperty(localName = "SampleName")
        private String sampleName;

        @JacksonXmlProperty(localName = "ReasonForStudy")
        private String reasonForStudy;

        @JacksonXmlProperty(localName = "ParentItemId")
        private String parentItemId;

        @JacksonXmlProperty(localName = "ParentItemName")
        private String parentItemName;

        @JacksonXmlProperty(localName = "CuvetteBarCode")
        private String cuvetteBarCode;

        @JacksonXmlProperty(localName = "CuvetteColour")
        private String cuvetteColour;

        @JacksonXmlProperty(localName = "NumberTestTube")
        private String numberTestTube;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabDiagnosisList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Diagnosis")
        private List<LabDiagnosis> diagnosisList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabDiagnosis {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "IcdCode")
        private String icdCode;

        @JacksonXmlProperty(localName = "IcdName")
        private String icdName;

        @JacksonXmlProperty(localName = "DiagType")
        private String diagType;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabStatusBody {

        @JacksonXmlProperty(localName = "PatientInfo")
        private LabPatientInfo patientInfo;

        @JacksonXmlProperty(localName = "PatientVisitInfo")
        private LabPatientVisitInfo patientVisitInfo;

        @JacksonXmlProperty(localName = "OrderInfoList")
        private LabStatusOrderInfoList orderInfoList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabStatusOrderInfoList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "OrderInfo")
        private List<LabStatusOrderInfo> orderInfoList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabStatusOrderInfo {

        @JacksonXmlProperty(localName = "OrderItemInfo")
        private LabStatusOrderItemInfo orderItemInfo;

        @JacksonXmlProperty(localName = "OrderItemDetail")
        private LabStatusOrderItemDetail orderItemDetail;

        @JacksonXmlProperty(localName = "SpecimenInfo")
        private SpecimenInfo specimenInfo;

        @JacksonXmlProperty(localName = "CuvetteInfo")
        private CuvetteInfo cuvetteInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabStatusOrderItemInfo {

        @JacksonXmlProperty(localName = "OrderControll")
        private String orderControll;

        @JacksonXmlProperty(localName = "BillOrderId")
        private String billOrderId;

        @JacksonXmlProperty(localName = "ExecOrderId")
        private String execOrderId;

        @JacksonXmlProperty(localName = "ParentBillOrderId")
        private String parentBillOrderId;

        @JacksonXmlProperty(localName = "OperatorTime")
        private String operatorTime;

        @JacksonXmlProperty(localName = "Operator")
        private String operator;

        @JacksonXmlProperty(localName = "OperatorName")
        private String operatorName;

        @JacksonXmlProperty(localName = "OrderType")
        private String orderType;

        @JacksonXmlProperty(localName = "OrgIdExec")
        private String orgIdExec;

        @JacksonXmlProperty(localName = "OrgNameExec")
        private String orgNameExec;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabStatusOrderItemDetail {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "ItemId")
        private String itemId;

        @JacksonXmlProperty(localName = "ItemName")
        private String itemName;

        @JacksonXmlProperty(localName = "ResultStatus")
        private String resultStatus;

        @JacksonXmlProperty(localName = "ResultOpition")
        private String resultOpition;

        @JacksonXmlProperty(localName = "ParentItemCode")
        private String parentItemCode;

        @JacksonXmlProperty(localName = "ParentItemName")
        private String parentItemName;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SpecimenInfo {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "SampleType")
        private String sampleType;

        @JacksonXmlProperty(localName = "SampleName")
        private String sampleName;

        @JacksonXmlProperty(localName = "AcquisitionMode")
        private String acquisitionMode;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CuvetteInfo {

        @JacksonXmlProperty(localName = "CuvetteBarCode")
        private String cuvetteBarCode;

        @JacksonXmlProperty(localName = "CuvetteColour")
        private String cuvetteColour;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabReportBody {

        @JacksonXmlProperty(localName = "PatientInfo")
        private LabPatientInfo patientInfo;

        @JacksonXmlProperty(localName = "PatientVisitInfo")
        private LabPatientVisitInfo patientVisitInfo;

        @JacksonXmlProperty(localName = "OrderInfoList")
        private LabReportOrderInfoList orderInfoList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabReportOrderInfoList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "OrderInfo")
        private List<LabReportOrderInfo> orderInfoList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabReportOrderInfo {

        @JacksonXmlProperty(localName = "OrderItemInfo")
        private LabReportOrderItemInfo orderItemInfo;

        @JacksonXmlProperty(localName = "OrderItemDetail")
        private LabReportOrderItemDetail orderItemDetail;

        @JacksonXmlProperty(localName = "ObservationExtendInfo")
        private ObservationExtendInfo observationExtendInfo;

        @JacksonXmlProperty(localName = "ObservationDrugInfoList")
        private ObservationDrugInfoList observationDrugInfoList;

        @JacksonXmlProperty(localName = "ObservationResultDetailList")
        private ObservationResultDetailList observationResultDetailList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabReportOrderItemInfo {

        @JacksonXmlProperty(localName = "OrderControll")
        private String orderControll;

        @JacksonXmlProperty(localName = "CriticalValuesMarkCode")
        private String criticalValuesMarkCode;

        @JacksonXmlProperty(localName = "CriticalValuesMarkName")
        private String criticalValuesMarkName;

        @JacksonXmlProperty(localName = "OrderType")
        private String orderType;

        @JacksonXmlProperty(localName = "ExecDeptCode")
        private String execDeptCode;

        @JacksonXmlProperty(localName = "ExecDeptName")
        private String execDeptName;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabReportOrderItemDetail {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "BillOrderId")
        private String billOrderId;

        @JacksonXmlProperty(localName = "OrderId")
        private String orderId;

        @JacksonXmlProperty(localName = "ReportId")
        private String reportId;

        @JacksonXmlProperty(localName = "ItemId")
        private String itemId;

        @JacksonXmlProperty(localName = "ItemName")
        private String itemName;

        @JacksonXmlProperty(localName = "ObservationDateTime")
        private String observationDateTime;

        @JacksonXmlProperty(localName = "SpecimenReceivedDateTime")
        private String specimenReceivedDateTime;

        @JacksonXmlProperty(localName = "SampleType")
        private String sampleType;

        @JacksonXmlProperty(localName = "SampleName")
        private String sampleName;

        @JacksonXmlProperty(localName = "ReportDate")
        private String reportDate;

        @JacksonXmlProperty(localName = "ResultStatus")
        private String resultStatus;

        @JacksonXmlProperty(localName = "ParentResult")
        private String parentResult;

        @JacksonXmlProperty(localName = "ParentLinkLev")
        private String parentLinkLev;

        @JacksonXmlProperty(localName = "EmpIdReportCode")
        private String empIdReportCode;

        @JacksonXmlProperty(localName = "EmpIdReport")
        private String empIdReport;

        @JacksonXmlProperty(localName = "EmpIdCheckCode")
        private String empIdCheckCode;

        @JacksonXmlProperty(localName = "EmpIdCheck")
        private String empIdCheck;

        @JacksonXmlProperty(localName = "ReportName")
        private String reportName;

        @JacksonXmlProperty(localName = "ParentItemCode")
        private String parentItemCode;

        @JacksonXmlProperty(localName = "ParentItemName")
        private String parentItemName;

        @JacksonXmlProperty(localName = "Note")
        private String note;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ObservationExtendInfo {

        @JacksonXmlProperty(localName = "PY")
        private String py;

        @JacksonXmlProperty(localName = "PJ")
        private String pj;

        @JacksonXmlProperty(localName = "PYJG")
        private String pyjg;

        @JacksonXmlProperty(localName = "NYBX")
        private String nybx;

        @JacksonXmlProperty(localName = "JL")
        private String jl;

        @JacksonXmlProperty(localName = "BYSC")
        private String bysc;

        @JacksonXmlProperty(localName = "XJMC")
        private String xjmc;

        @JacksonXmlProperty(localName = "PYFF")
        private String pyff;

        @JacksonXmlProperty(localName = "JGBZ")
        private String jgbz;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ObservationDrugInfoList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "ObservationDrugInfo")
        private List<ObservationDrugInfo> observationDrugInfos;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ObservationDrugInfo {

        @JacksonXmlProperty(localName = "AntibioticId")
        private String antibioticId;

        @JacksonXmlProperty(localName = "AntibioticName")
        private String antibioticName;

        @JacksonXmlProperty(localName = "SmearMethod")
        private String smearMethod;

        @JacksonXmlProperty(localName = "ItemFoldPoint")
        private String itemFoldPoint;

        @JacksonXmlProperty(localName = "ItemResult")
        private String itemResult;

        @JacksonXmlProperty(localName = "ItemSensitivity")
        private String itemSensitivity;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ObservationResultDetailList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "ObservationResultDetail")
        private List<ObservationResultDetail> observationResultDetails;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ObservationResultDetail {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "ValueType")
        private String valueType;

        @JacksonXmlProperty(localName = "ItemId")
        private String itemId;

        @JacksonXmlProperty(localName = "ItemName")
        private String itemName;

        @JacksonXmlProperty(localName = "ItemGroupCode")
        private String itemGroupCode;

        @JacksonXmlProperty(localName = "ItemGroupName")
        private String itemGroupName;

        @JacksonXmlProperty(localName = "ItemResult")
        private String itemResult;

        @JacksonXmlProperty(localName = "ItemUnit")
        private String itemUnit;

        @JacksonXmlProperty(localName = "ItemUnitName")
        private String itemUnitName;

        @JacksonXmlProperty(localName = "ReferScope")
        private String referScope;

        @JacksonXmlProperty(localName = "AbnormalFlag")
        private String abnormalFlag;

        @JacksonXmlProperty(localName = "CheckStatus")
        private String checkStatus;

        @JacksonXmlProperty(localName = "ObservationMethod")
        private String observationMethod;

        @JacksonXmlProperty(localName = "CalibrationValue")
        private String calibrationValue;

        @JacksonXmlProperty(localName = "Instruments")
        private String instruments;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabPatientInfo {

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

        @JacksonXmlProperty(localName = "Heigh")
        private String heigh;

        @JacksonXmlProperty(localName = "Weigh")
        private String weigh;

        @JacksonXmlProperty(localName = "LastMenstruation")
        private String lastMenstruation;

        @JacksonXmlProperty(localName = "MaritalStatus")
        private String maritalStatus;

        @JacksonXmlProperty(localName = "AgeMeter")
        private String ageMeter;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class LabPatientVisitInfo {

        @JacksonXmlProperty(localName = "PatientType")
        private String patientType;

        @JacksonXmlProperty(localName = "DeptCode")
        private String deptCode;

        @JacksonXmlProperty(localName = "CurrentWard")
        private String currentWard;

        @JacksonXmlProperty(localName = "BedNo")
        private String bedNo;

        @JacksonXmlProperty(localName = "CurrentNursingUnit")
        private String currentNursingUnit;

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
