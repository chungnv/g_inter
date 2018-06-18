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
public class CommonTrunk {

    private String name;
    private Long switchId;
    private Long partnerId;
    private Long isNational;
    private Long trunkId;
    private String connectSwitchType;
    private String partType;

    public CommonTrunk(String name, Long switchId, Long partnerId, Long isNational, Long trunkId, String connectSwitchType, String partType) {
        this.name = name;
        this.switchId = switchId;
        this.partnerId = partnerId;
        this.isNational = isNational;
        this.trunkId = trunkId;
        this.connectSwitchType = connectSwitchType;
        this.partType = partType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Long getIsNational() {
        return isNational;
    }

    public void setIsNational(Long isNational) {
        this.isNational = isNational;
    }

    public Long getTrunkId() {
        return trunkId;
    }

    public void setTrunkId(Long trunkId) {
        this.trunkId = trunkId;
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

}
