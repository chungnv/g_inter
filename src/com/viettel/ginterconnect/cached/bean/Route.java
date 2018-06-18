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
public class Route {

    private Long routeId;
    private String routeName;
    private String directionName;
    private Long switchId;
    private Long partnerId;
    private String connectSwitchType;
    private String partType;
    private Long connectAreaId;
    private Long serviceId;
    private String areaPrefixNumber;
    private Long prefixNumberId;
    private String prefixNumber;
    private Long lengthNumber;
    private String partAbbreviate;
    private Long isNational;
    private Long isVas;

    public Route() {
    }

    public Route(Long routeId, String routeName, Long partnerId) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.partnerId = partnerId;
    }

    public Route(Long routeId, String routeName, Long switchId, Long partnerId, String connectSwitchType, String partType, Long connectAreaId, String prefixNumber, Long lengthNumber, String partAbbreviate, Long isNational, Long isVas) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.switchId = switchId;
        this.partnerId = partnerId;
        this.connectSwitchType = connectSwitchType;
        this.partType = partType;
        this.connectAreaId = connectAreaId;
        this.prefixNumber = prefixNumber;
        this.lengthNumber = lengthNumber;
        this.partAbbreviate = partAbbreviate;
        this.isNational = isNational;
        this.isVas = isVas;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Long getSwitchId() {
        return switchId;
    }

    public void setSwitchId(Long switchId) {
        this.switchId = switchId;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public String getConnectSwitchType() {
        return connectSwitchType;
    }

    public void setConnectSwitchType(String connectSwitchType) {
        this.connectSwitchType = connectSwitchType;
    }

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }

    public Long getConnectAreaId() {
        return connectAreaId;
    }

    public void setConnectAreaId(Long connectAreaId) {
        this.connectAreaId = connectAreaId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getAreaPrefixNumber() {
        return areaPrefixNumber;
    }

    public void setAreaPrefixNumber(String areaPrefixNumber) {
        this.areaPrefixNumber = areaPrefixNumber;
    }

    public Long getPrefixNumberId() {
        return prefixNumberId;
    }

    public void setPrefixNumberId(Long prefixNumberId) {
        this.prefixNumberId = prefixNumberId;
    }

    public String getPrefixNumber() {
        return prefixNumber;
    }

    public void setPrefixNumber(String prefixNumber) {
        this.prefixNumber = prefixNumber;
    }

    public Long getLengthNumber() {
        return lengthNumber;
    }

    public void setLengthNumber(Long lengthNumber) {
        this.lengthNumber = lengthNumber;
    }

    public String getPartAbbreviate() {
        return partAbbreviate;
    }

    public void setPartAbbreviate(String partAbbreviate) {
        this.partAbbreviate = partAbbreviate;
    }

    public Long getIsNational() {
        return isNational;
    }

    public void setIsNational(Long isNational) {
        this.isNational = isNational;
    }

    public Long getIsVas() {
        return isVas;
    }

    public void setIsVas(Long isVas) {
        this.isVas = isVas;
    }

    public String getDirectionName() {
        return directionName;
    }

    public void setDirectionName(String directionName) {
        this.directionName = directionName;
    }
}
