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
public class SmsCentre {

    private Long smsCentreId;
    private String smsPrefix;
    private Long partnerId;
    private Long isNational;
    private Long serviceId;
    private Long marketId;

    public SmsCentre() {
    }

    public SmsCentre(Long smsCentreId, String smsPrefix, Long partnerId) {
        this.smsCentreId = smsCentreId;
        this.smsPrefix = smsPrefix;
        this.partnerId = partnerId;
    }

    public SmsCentre(Long smsCentreId, String smsPrefix, Long partnerId, Long isNational, Long serviceId, Long marketId) {
        this.smsCentreId = smsCentreId;
        this.smsPrefix = smsPrefix;
        this.partnerId = partnerId;
        this.isNational = isNational;
        this.marketId = marketId;
        this.serviceId = serviceId;
    }

    public Long getSmsCentreId() {
        return smsCentreId;
    }

    public void setSmsCentreId(Long smsCentreId) {
        this.smsCentreId = smsCentreId;
    }

    public String getSmsPrefix() {
        return smsPrefix;
    }

    public void setSmsPrefix(String smsPrefix) {
        this.smsPrefix = smsPrefix;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public Long getIsNational() {
        return isNational;
    }

    public void setIsNational(Long isNational) {
        this.isNational = isNational;
    }

    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
