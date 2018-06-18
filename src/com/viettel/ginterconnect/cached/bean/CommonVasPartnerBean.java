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
public class CommonVasPartnerBean {

    String cdrAccount;
    String partnerCode;
//    String prefixNum;
    String serviceCode;
    String servicePrefix;
    String partnerName;
    String serviceName;
//    String serviceType;
    String priceName;
    Long priceValue;
    String priceDescription;

//    public StlVasPartnerBean(String cdrAccount, String partnerCode, String prefixNum, String serviceCode, String partnerName, String serviceName, String serviceType) {
//        this.cdrAccount = cdrAccount;
//        this.partnerName = partnerName;
//        this.prefixNum = prefixNum;
//        this.serviceCode = serviceCode;
//        this.partnerCode = partnerCode;
//        this.serviceName = serviceName;
//        this.serviceType = serviceType;
//    }
    
    public CommonVasPartnerBean(String cdrAccount, String partnerCode, String serviceCode, String servicePrefix, String priceName, Long priceValue, String priceDescription, String serviceName, String partnerName) {
        this.cdrAccount = cdrAccount;
        this.partnerCode = partnerCode;
        this.serviceCode = serviceCode;
        this.priceName = priceName;
        this.priceValue = priceValue;
        this.servicePrefix = servicePrefix;
        this.priceDescription = priceDescription;
        this.serviceName = serviceName;
        this.partnerName = partnerName;
    }

    public String getCdrAccount() {
        return cdrAccount;
    }

    public void setCdrAccount(String cdrAccount) {
        this.cdrAccount = cdrAccount;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

//    public String getPrefixNum() {
//        return prefixNum;
//    }
//
//    public void setPrefixNum(String prefixNum) {
//        this.prefixNum = prefixNum;
//    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

//    public String getPartnerName() {
//        return partnerName;
//    }
//
//    public void setPartnerName(String partnerName) {
//        this.partnerName = partnerName;
//    }
//
//    public String getServiceName() {
//        return serviceName;
//    }
//
//    public void setServiceName(String serviceName) {
//        this.serviceName = serviceName;
//    }

    public String getPriceName() {
        return priceName;
    }

    public void setPriceName(String priceName) {
        this.priceName = priceName;
    }

    public Long getPriceValue() {
        return priceValue;
    }

    public void setPriceValue(Long priceValue) {
        this.priceValue = priceValue;
    }

    public String getPriceDescription() {
        return priceDescription;
    }

    public void setPriceDescription(String priceDescription) {
        this.priceDescription = priceDescription;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

}
