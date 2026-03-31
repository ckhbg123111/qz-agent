package com.zhongjia.web.vo.singleDisease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

public final class CheckMessageModels {

    private CheckMessageModels() {
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckApplyBody {

        @JacksonXmlProperty(localName = "Event")
        private String event;

        @JacksonXmlProperty(localName = "ReservationInfo")
        private CheckReservationInfo reservationInfo;

        @JacksonXmlProperty(localName = "PatientInfo")
        private CheckPatientInfo patientInfo;

        @JacksonXmlProperty(localName = "ApplyLists")
        private CheckApplyLists applyLists;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckApplyLists {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "ApplyInfo")
        private List<CheckApplyInfo> applyInfoList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckApplyInfo {

        @JacksonXmlProperty(localName = "ApplyNo")
        private String applyNo;

        @JacksonXmlProperty(localName = "ApplyType")
        private String applyType;

        @JacksonXmlProperty(localName = "IsUrgent")
        private String isUrgent;

        @JacksonXmlProperty(localName = "IsBed")
        private String isBed;

        @JacksonXmlProperty(localName = "FunPartGroupId")
        private String funPartGroupId;

        @JacksonXmlProperty(localName = "FunPartGroupName")
        private String funPartGroupName;

        @JacksonXmlProperty(localName = "FunPartGroupCode")
        private String funPartGroupCode;

        @JacksonXmlProperty(localName = "FunPartId")
        private String funPartId;

        @JacksonXmlProperty(localName = "FunPartName")
        private String funPartName;

        @JacksonXmlProperty(localName = "FunPartCode")
        private String funPartCode;

        @JacksonXmlProperty(localName = "Remark")
        private String remark;

        @JacksonXmlProperty(localName = "SpecialRequest")
        private String specialRequest;

        @JacksonXmlProperty(localName = "ClinicStatus")
        private String clinicStatus;

        @JacksonXmlProperty(localName = "ClinicDiagnose")
        private String clinicDiagnose;

        @JacksonXmlProperty(localName = "ClinicDiagnoseName")
        private String clinicDiagnoseName;

        @JacksonXmlProperty(localName = "DiagnosticType")
        private String diagnosticType;

        @JacksonXmlProperty(localName = "Optional")
        private String optional;

        @JacksonXmlProperty(localName = "OptionalName")
        private String optionalName;

        @JacksonXmlProperty(localName = "OrgNameBeg")
        private String orgNameBeg;

        @JacksonXmlProperty(localName = "OrgIdBeg")
        private String orgIdBeg;

        @JacksonXmlProperty(localName = "OrgIdExec")
        private String orgIdExec;

        @JacksonXmlProperty(localName = "OrgNameExec")
        private String orgNameExec;

        @JacksonXmlProperty(localName = "EmpIdBegDoct")
        private String empIdBegDoct;

        @JacksonXmlProperty(localName = "EmpNameBegDoct")
        private String empNameBegDoct;

        @JacksonXmlProperty(localName = "OrderId")
        private String orderId;

        @JacksonXmlProperty(localName = "ApplyDate")
        private String applyDate;

        @JacksonXmlProperty(localName = "DeviceNum")
        private String deviceNum;

        @JacksonXmlProperty(localName = "AmountSuggest")
        private String amountSuggest;

        @JacksonXmlProperty(localName = "Diagnoses")
        private CheckDiagnoses diagnoses;

        @JacksonXmlProperty(localName = "ApplyItemList")
        private CheckApplyItemList applyItemList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckDiagnoses {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Diagnose")
        private List<CheckDiagnose> diagnoseList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckDiagnose {

        @JacksonXmlProperty(localName = "DiagnoseIcd")
        private String diagnoseIcd;

        @JacksonXmlProperty(localName = "DiagnoseName")
        private String diagnoseName;

        @JacksonXmlProperty(localName = "DiagnoseType")
        private String diagnoseType;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckApplyItemList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "ApplyItem")
        private List<CheckApplyItem> applyItemList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckApplyItem {

        @JacksonXmlProperty(localName = "ClinicalItemCode")
        private String clinicalItemCode;

        @JacksonXmlProperty(localName = "ItemName")
        private String itemName;

        @JacksonXmlProperty(localName = "ApplyDetailId")
        private String applyDetailId;

        @JacksonXmlProperty(localName = "ItemApplyType")
        private String itemApplyType;

        @JacksonXmlProperty(localName = "CheckItemClass")
        private String checkItemClass;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckReservationPushBody {

        @JacksonXmlProperty(localName = "Reservation")
        private Reservation reservation;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Reservation {

        @JacksonXmlProperty(localName = "ReservationNo")
        private String reservationNo;

        @JacksonXmlProperty(localName = "Rcptno")
        private String rcptno;

        @JacksonXmlProperty(localName = "Event")
        private String event;

        @JacksonXmlProperty(localName = "ReservationInfo")
        private ReservationInfo reservationInfo;

        @JacksonXmlProperty(localName = "PatientInfo")
        private ReservationPatientInfo patientInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ReservationInfo {

        @JacksonXmlProperty(localName = "ApplyNo")
        private String applyNo;

        @JacksonXmlProperty(localName = "TimeConsuming")
        private String timeConsuming;

        @JacksonXmlProperty(localName = "TimeConsumingUnit")
        private String timeConsumingUnit;

        @JacksonXmlProperty(localName = "ReservationDate")
        private String reservationDate;

        @JacksonXmlProperty(localName = "ReservationShift")
        private String reservationShift;

        @JacksonXmlProperty(localName = "ReservationShiftName")
        private String reservationShiftName;

        @JacksonXmlProperty(localName = "OperatorCode")
        private String operatorCode;

        @JacksonXmlProperty(localName = "OperatorName")
        private String operatorName;

        @JacksonXmlProperty(localName = "ExecOrgCode")
        private String execOrgCode;

        @JacksonXmlProperty(localName = "ExecOrgName")
        private String execOrgName;

        @JacksonXmlProperty(localName = "ResourcesCode")
        private String resourcesCode;

        @JacksonXmlProperty(localName = "Location")
        private String location;

        @JacksonXmlProperty(localName = "CurrentNo")
        private String currentNo;

        @JacksonXmlProperty(localName = "Memo")
        private String memo;

        @JacksonXmlProperty(localName = "AppItemList")
        private ReservationItemList appItemList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ReservationPatientInfo {

        @JacksonXmlProperty(localName = "PatientID")
        private String patientID;

        @JacksonXmlProperty(localName = "VisitID")
        private String visitID;

        @JacksonXmlProperty(localName = "PatientType")
        private String patientType;

        @JacksonXmlProperty(localName = "HospitalAreaCode")
        private String hospitalAreaCode;

        @JacksonXmlProperty(localName = "PatientName")
        private String patientName;

        @JacksonXmlProperty(localName = "BirthDate")
        private String birthDate;

        @JacksonXmlProperty(localName = "PatientGender")
        private String patientGender;

        @JacksonXmlProperty(localName = "PatientGenderName")
        private String patientGenderName;

        @JacksonXmlProperty(localName = "DeptCode")
        private String deptCode;

        @JacksonXmlProperty(localName = "BedNo")
        private String bedNo;

        @JacksonXmlProperty(localName = "AdmissionDate")
        private String admissionDate;

        @JacksonXmlProperty(localName = "Ward")
        private String ward;

        @JacksonXmlProperty(localName = "AgeMeter")
        private String ageMeter;

        @JacksonXmlProperty(localName = "BigDept")
        private String bigDept;

        @JacksonXmlProperty(localName = "BigDeptName")
        private String bigDeptName;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ReservationItemList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "ApplyItem")
        private List<ReservationItem> applyItemList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ReservationItem {

        @JacksonXmlProperty(localName = "ClinicalItemCode")
        private String clinicalItemCode;

        @JacksonXmlProperty(localName = "ItemName")
        private String itemName;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckReservationQueryBody {

        @JacksonXmlProperty(localName = "MessageQueryName")
        private String messageQueryName;

        @JacksonXmlProperty(localName = "QueryTag")
        private String queryTag;

        @JacksonXmlProperty(localName = "PatientType")
        private String patientType;

        @JacksonXmlProperty(localName = "PatientId")
        private String patientId;

        @JacksonXmlProperty(localName = "VisitId")
        private String visitId;

        @JacksonXmlProperty(localName = "OrgIdExec")
        private String orgIdExec;

        @JacksonXmlProperty(localName = "StartDateTime")
        private String startDateTime;

        @JacksonXmlProperty(localName = "EndDateTime")
        private String endDateTime;

        @JacksonXmlProperty(localName = "ApplyId")
        private String applyId;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckReservationQueryResponseBody {

        @JacksonXmlProperty(localName = "Result")
        private CommonXmlModels.Result result;

        @JacksonXmlProperty(localName = "ReservationList")
        private ReservationList reservationList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ReservationList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "Reservation")
        private List<ReservationQueryResult> reservationList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ReservationQueryResult {

        @JacksonXmlProperty(localName = "ReservationInfo")
        private ReservationInfo reservationInfo;

        @JacksonXmlProperty(localName = "PatientInfo")
        private QueryPatientInfo patientInfo;

        @JacksonXmlProperty(localName = "PatientVisitInfo")
        private QueryPatientVisitInfo patientVisitInfo;

        @JacksonXmlProperty(localName = "CheckApplyItem")
        private QueryCheckApplyItem checkApplyItem;

        @JacksonXmlProperty(localName = "CheckAppItemList")
        private QueryCheckAppItemList checkAppItemList;

        @JacksonXmlProperty(localName = "CheckApplyDetail")
        private QueryCheckApplyDetail checkApplyDetail;

        @JacksonXmlProperty(localName = "Comments")
        private QueryComments comments;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class QueryPatientInfo {

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

        @JacksonXmlProperty(localName = "Address")
        private String address;

        @JacksonXmlProperty(localName = "Telphone")
        private String telphone;

        @JacksonXmlProperty(localName = "MaritalStatus")
        private String maritalStatus;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class QueryPatientVisitInfo {

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
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class QueryCheckApplyItem {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "SegmentActionCode")
        private String segmentActionCode;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class QueryCheckAppItemList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "CheckAppItem")
        private List<QueryCheckAppItem> checkAppItems;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class QueryCheckAppItem {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "ItemCode")
        private String itemCode;

        @JacksonXmlProperty(localName = "ItemName")
        private String itemName;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class QueryCheckApplyDetail {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "ResourcesCode")
        private String resourcesCode;

        @JacksonXmlProperty(localName = "Location")
        private String location;

        @JacksonXmlProperty(localName = "ReservationGroupCode")
        private String reservationGroupCode;

        @JacksonXmlProperty(localName = "ReservationGroupName")
        private String reservationGroupName;

        @JacksonXmlProperty(localName = "ReservationGroupNo")
        private String reservationGroupNo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class QueryComments {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "Comment")
        private String comment;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckStatusBody {

        @JacksonXmlProperty(localName = "PatientInfo")
        private QueryPatientInfo patientInfo;

        @JacksonXmlProperty(localName = "PatientVisitInfo")
        private QueryPatientVisitInfo patientVisitInfo;

        @JacksonXmlProperty(localName = "OrderInfoList")
        private CheckStatusOrderInfoList orderInfoList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckStatusOrderInfoList {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "OrderInfo")
        private List<CheckStatusOrderInfo> orderInfoList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckStatusOrderInfo {

        @JacksonXmlProperty(localName = "OrderItemInfo")
        private CheckStatusOrderItemInfo orderItemInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckStatusOrderItemInfo {

        @JacksonXmlProperty(localName = "OrderControll")
        private String orderControll;

        @JacksonXmlProperty(localName = "BillOrderId")
        private String billOrderId;

        @JacksonXmlProperty(localName = "OrderId")
        private String orderId;

        @JacksonXmlProperty(localName = "ExecOrderId")
        private String execOrderId;

        @JacksonXmlProperty(localName = "OrderStatus")
        private String orderStatus;

        @JacksonXmlProperty(localName = "OperatorTime")
        private String operatorTime;

        @JacksonXmlProperty(localName = "ImageNumber")
        private String imageNumber;

        @JacksonXmlProperty(localName = "DeviceType")
        private String deviceType;

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

        @JacksonXmlProperty(localName = "ImageNo")
        private String imageNo;

        @JacksonXmlProperty(localName = "RisId")
        private String risId;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckReportBody {

        @JacksonXmlProperty(localName = "PatientInfo")
        private QueryPatientInfo patientInfo;

        @JacksonXmlProperty(localName = "PatientVisitInfo")
        private QueryPatientVisitInfo patientVisitInfo;

        @JacksonXmlProperty(localName = "OrderInfo")
        private CheckReportOrderInfo orderInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckReportOrderInfo {

        @JacksonXmlProperty(localName = "OrderItemInfo")
        private CheckReportOrderItemInfo orderItemInfo;

        @JacksonXmlProperty(localName = "ObservationResultInfo")
        private ObservationResultInfo observationResultInfo;

        @JacksonXmlProperty(localName = "ObservationResultDetailList")
        private ObservationResultDetailList observationResultDetailList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckReportOrderItemInfo {

        @JacksonXmlProperty(localName = "OrderControll")
        private String orderControll;

        @JacksonXmlProperty(localName = "BillOrderId")
        private String billOrderId;

        @JacksonXmlProperty(localName = "ExecOrderId")
        private String execOrderId;

        @JacksonXmlProperty(localName = "CriticalValuesMarkCode")
        private String criticalValuesMarkCode;

        @JacksonXmlProperty(localName = "CriticalValuesMark")
        private String criticalValuesMark;

        @JacksonXmlProperty(localName = "ImageNumber")
        private String imageNumber;

        @JacksonXmlProperty(localName = "DeviceType")
        private String deviceType;

        @JacksonXmlProperty(localName = "OrderType")
        private String orderType;

        @JacksonXmlProperty(localName = "OrgIdExec")
        private String orgIdExec;

        @JacksonXmlProperty(localName = "OrgNameExec")
        private String orgNameExec;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ObservationResultInfo {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "ReportId")
        private String reportId;

        @JacksonXmlProperty(localName = "ItemCode")
        private String itemCode;

        @JacksonXmlProperty(localName = "ItemName")
        private String itemName;

        @JacksonXmlProperty(localName = "ReportDate")
        private String reportDate;

        @JacksonXmlProperty(localName = "DiagnosisType")
        private String diagnosisType;

        @JacksonXmlProperty(localName = "ReportStatus")
        private String reportStatus;

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

        @JacksonXmlProperty(localName = "TestItemCode")
        private String testItemCode;

        @JacksonXmlProperty(localName = "TestItemName")
        private String testItemName;

        @JacksonXmlProperty(localName = "CheckFinding")
        private String checkFinding;

        @JacksonXmlProperty(localName = "CheckResult")
        private String checkResult;

        @JacksonXmlProperty(localName = "AbnormalFlag")
        private String abnormalFlag;

        @JacksonXmlProperty(localName = "ReportStatus")
        private String reportStatus;

        @JacksonXmlProperty(localName = "CheckMethod")
        private String checkMethod;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckReservationInfo {

        @JacksonXmlProperty(localName = "RegisterDate")
        private String registerDate;

        @JacksonXmlProperty(localName = "ReservationDate")
        private String reservationDate;

        @JacksonXmlProperty(localName = "ReservationShift")
        private String reservationShift;

        @JacksonXmlProperty(localName = "ResourcesCode")
        private String resourcesCode;

        @JacksonXmlProperty(localName = "ResourcesName")
        private String resourcesName;

        @JacksonXmlProperty(localName = "CurrentNo")
        private String currentNo;

        @JacksonXmlProperty(localName = "ReservationStartDate")
        private String reservationStartDate;

        @JacksonXmlProperty(localName = "ReservationEndDate")
        private String reservationEndDate;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class CheckPatientInfo {

        @JacksonXmlProperty(localName = "PatientID")
        private String patientID;

        @JacksonXmlProperty(localName = "VisitID")
        private String visitID;

        @JacksonXmlProperty(localName = "PatientType")
        private String patientType;

        @JacksonXmlProperty(localName = "HospitalAreaCode")
        private String hospitalAreaCode;

        @JacksonXmlProperty(localName = "PatientName")
        private String patientName;

        @JacksonXmlProperty(localName = "BirthDate")
        private String birthDate;

        @JacksonXmlProperty(localName = "PatientGender")
        private String patientGender;

        @JacksonXmlProperty(localName = "PatientGenderName")
        private String patientGenderName;

        @JacksonXmlProperty(localName = "DeptCode")
        private String deptCode;

        @JacksonXmlProperty(localName = "BedNo")
        private String bedNo;

        @JacksonXmlProperty(localName = "AdmissionDate")
        private String admissionDate;

        @JacksonXmlProperty(localName = "Ward")
        private String ward;

        @JacksonXmlProperty(localName = "NurseUnit")
        private String nurseUnit;

        @JacksonXmlProperty(localName = "Address")
        private String address;

        @JacksonXmlProperty(localName = "Telephone")
        private String telephone;

        @JacksonXmlProperty(localName = "IdNumber")
        private String idNumber;

        @JacksonXmlProperty(localName = "AgeMeter")
        private String ageMeter;

        @JacksonXmlProperty(localName = "BigDept")
        private String bigDept;

        @JacksonXmlProperty(localName = "BigDeptName")
        private String bigDeptName;
    }
}
