package com.zhongjia.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "lab.query")
public class LabQueryProperties {

    private boolean enabled = false;

    private String taskName = "outpatient-lab-query";

    private String wsdlUrl;

    private String endpointUrl;

    private String namespace = "http://tempuri.org/";

    private String serviceName;

    private String portName;

    private String methodName;

    private String soapAction;

    private String messageParamName = "message";

    private String sender = "ZJ_AGENT";

    private String receiver = "LIS";

    private String messageQueryName = "Z06^查询检验申请信息";

    private String queryTag = "Z0601";

    private String patientType = "1";

    private String visitId = "";

    private String orgIdExec = "";

    private String isFilter = "0";

    private String patientIds = "";

    private long fixedDelayMs = 600000L;

    private int initialLookbackMinutes = 10;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public String getMessageParamName() {
        return messageParamName;
    }

    public void setMessageParamName(String messageParamName) {
        this.messageParamName = messageParamName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageQueryName() {
        return messageQueryName;
    }

    public void setMessageQueryName(String messageQueryName) {
        this.messageQueryName = messageQueryName;
    }

    public String getQueryTag() {
        return queryTag;
    }

    public void setQueryTag(String queryTag) {
        this.queryTag = queryTag;
    }

    public String getPatientType() {
        return patientType;
    }

    public void setPatientType(String patientType) {
        this.patientType = patientType;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getOrgIdExec() {
        return orgIdExec;
    }

    public void setOrgIdExec(String orgIdExec) {
        this.orgIdExec = orgIdExec;
    }

    public String getIsFilter() {
        return isFilter;
    }

    public void setIsFilter(String isFilter) {
        this.isFilter = isFilter;
    }

    public String getPatientIds() {
        return patientIds;
    }

    public void setPatientIds(String patientIds) {
        this.patientIds = patientIds;
    }

    public long getFixedDelayMs() {
        return fixedDelayMs;
    }

    public void setFixedDelayMs(long fixedDelayMs) {
        this.fixedDelayMs = fixedDelayMs;
    }

    public int getInitialLookbackMinutes() {
        return initialLookbackMinutes;
    }

    public void setInitialLookbackMinutes(int initialLookbackMinutes) {
        this.initialLookbackMinutes = initialLookbackMinutes;
    }

    public List<String> getPatientIdList() {
        return Arrays.stream(patientIds.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
    }
}
