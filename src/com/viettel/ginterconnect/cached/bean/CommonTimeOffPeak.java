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
public class CommonTimeOffPeak {

    Long timeOffPeakId;
    String startHour;
    String endHour;
    String itemType;

    public CommonTimeOffPeak(Long timeOffPeakId, String startHour, String endHour, String itemType) {
        this.timeOffPeakId = timeOffPeakId;
        this.startHour = startHour;
        this.endHour = endHour;
        this.itemType = itemType;
    }

    public Long getTimeOffPeakId() {
        return timeOffPeakId;
    }

    public void setTimeOffPeakId(Long timeOffPeakId) {
        this.timeOffPeakId = timeOffPeakId;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

}
