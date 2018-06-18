/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.cached.bean;

/**
 *
 * @author ubuntu
 */
public class CommonDmsPrefix {

    private Long prefixId;
    private String prefix;
    private Long length;
    private Long fixLength;
    private String priority;
    private String addPrefix;
    private Long serviceId;
    private Long areaId;
    private String serviceType;
    private Long numberFunction = 3L;
    private Long partnerId;
    private Long timeOffPeakId;
    private String partType;

    public CommonDmsPrefix(Long id, String prefix, Long length, Long fixLength, String priority, String addPrefix, Long serviceId, Long areaId, String serviceType, Long timeOffPeakId, String partType) {
        this.prefixId = id;
        this.prefix = prefix;
        this.length = length;
        this.fixLength = fixLength;
        this.priority = priority;
        this.addPrefix = addPrefix;
        this.serviceId = serviceId;
        this.areaId = areaId;
        this.serviceType = serviceType;
        this.timeOffPeakId = timeOffPeakId;
        this.partType = partType;
    }

    public Long getPrefixId() {
        return prefixId;
    }

    public void setPrefixId(Long prefixId) {
        this.prefixId = prefixId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getFixLength() {
        return fixLength;
    }

    public void setFixLength(Long fixLength) {
        this.fixLength = fixLength;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAddPrefix() {
        return addPrefix;
    }

    public void setAddPrefix(String addPrefix) {
        this.addPrefix = addPrefix;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Long getNumberFunction() {
        return numberFunction;
    }

    public void setNumberFunction(Long numberFunction) {
        this.numberFunction = numberFunction;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public Long getTimeOffPeakId() {
        return timeOffPeakId;
    }

    public void setTimeOffPeakId(Long timeOffPeakId) {
        this.timeOffPeakId = timeOffPeakId;
    }

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }

}
