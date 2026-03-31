package com.zhongjia.web.vo.singleDisease;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

public final class PatientMessageModels {

    private PatientMessageModels() {
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class AdtA01MzBody {

        @JacksonXmlProperty(localName = "EventInfo")
        private EventInfo eventInfo;

        @JacksonXmlProperty(localName = "PatientInfo")
        private PatientInfo patientInfo;

        @JacksonXmlProperty(localName = "PatientVisitInfo")
        private PatientVisitInfo patientVisitInfo;

        @JacksonXmlProperty(localName = "ChargeInfo")
        private ChargeInfo chargeInfo;

        @JacksonXmlProperty(localName = "OutpDurationInfo")
        private OutpDurationInfo outpDurationInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class EventInfo {

        @JacksonXmlProperty(localName = "EventTypeCode")
        private String eventTypeCode;

        @JacksonXmlProperty(localName = "RecordedDateTime")
        private String recordedDateTime;

        @JacksonXmlProperty(localName = "Operator")
        private String operator;

        @JacksonXmlProperty(localName = "OperatorName")
        private String operatorName;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class PatientInfo {

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

        @JacksonXmlProperty(localName = "PeopleTypeCode")
        private String peopleTypeCode;

        @JacksonXmlProperty(localName = "PeopleTypeName")
        private String peopleTypeName;

        @JacksonXmlProperty(localName = "Address")
        private String address;

        @JacksonXmlProperty(localName = "ZipCode")
        private String zipCode;

        @JacksonXmlProperty(localName = "AddressType")
        private String addressType;

        @JacksonXmlProperty(localName = "Telphone")
        private String telphone;

        @JacksonXmlProperty(localName = "MaritalStatus")
        private String maritalStatus;

        @JacksonXmlProperty(localName = "Nation")
        private String nation;

        @JacksonXmlProperty(localName = "NativePlace")
        private String nativePlace;

        @JacksonXmlProperty(localName = "Nationality")
        private String nationality;

        @JacksonXmlProperty(localName = "ReserveId")
        private String reserveId;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class PatientVisitInfo {

        @JacksonXmlProperty(localName = "PatientType")
        private String patientType;

        @JacksonXmlProperty(localName = "OutpSpecialId")
        private String outpSpecialId;

        @JacksonXmlProperty(localName = "OutpSpecialName")
        private String outpSpecialName;

        @JacksonXmlProperty(localName = "SepcialCinicDeptCode")
        private String sepcialCinicDeptCode;

        @JacksonXmlProperty(localName = "SepcialCinicDeptName")
        private String sepcialCinicDeptName;

        @JacksonXmlProperty(localName = "RegSortNo")
        private String regSortNo;

        @JacksonXmlProperty(localName = "AttendingDoctorId")
        private String attendingDoctorId;

        @JacksonXmlProperty(localName = "AttendingDoctorName")
        private String attendingDoctorName;

        @JacksonXmlProperty(localName = "OutpTypeCode")
        private String outpTypeCode;

        @JacksonXmlProperty(localName = "RegistryResource")
        private String registryResource;

        @JacksonXmlProperty(localName = "TitleCode")
        private String titleCode;

        @JacksonXmlProperty(localName = "VisitId")
        private String visitId;

        @JacksonXmlProperty(localName = "HospitalAreaCode")
        private String hospitalAreaCode;

        @JacksonXmlProperty(localName = "RegisteringTime")
        private String registeringTime;

        @JacksonXmlProperty(localName = "RegSequence")
        private String regSequence;

        @JacksonXmlProperty(localName = "TriageNo")
        private String triageNo;

        @JacksonXmlProperty(localName = "RegistryFlag")
        private String registryFlag;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class ChargeInfo {

        @JacksonXmlProperty(localName = "SerialNumber")
        private String serialNumber;

        @JacksonXmlProperty(localName = "ChargeTypeCode")
        private String chargeTypeCode;

        @JacksonXmlProperty(localName = "ChargeTypeName")
        private String chargeTypeName;

        @JacksonXmlProperty(localName = "InsuranceCompanyID")
        private String insuranceCompanyID;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class OutpDurationInfo {

        @JacksonXmlProperty(localName = "OutpDate")
        private String outpDate;

        @JacksonXmlProperty(localName = "OutpDurationCode")
        private String outpDurationCode;

        @JacksonXmlProperty(localName = "OutpDurationName")
        private String outpDurationName;

        @JacksonXmlProperty(localName = "OutPatientService")
        private String outPatientService;

        @JacksonXmlProperty(localName = "SpecificTime")
        private String specificTime;
    }
}
