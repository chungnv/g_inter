/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.bo;

/**
 *
 * @author xuanhong95
 */
public class CdrThreadBO {

    private int PK = 0;
    private int CDR_ID = 0;
    private String CALLING = "";
    private String CALLED = "";
    private String START_TIME = "";
    private String DURATION = "";
    private String TRUNK_IN = "";
    private String TRUNK_OUT = "";
    private String SMS_IN = "";
    private String SMS_OUT = "";

    public int getPrimaryKey() {
        return PK;
    }

    public void setPrimaryKey(int PK) {
        this.PK = PK;
    }

    public int getCdrID() {
        return CDR_ID;
    }

    public void setCdrID(int CDR_ID) {
        this.CDR_ID = CDR_ID;
    }

    public String getCalling() {
        return CALLING;
    }

    public void setCalling(String CALLING) {
        this.CALLING = CALLING;
    }

    public String getCalled() {
        return CALLED;
    }

    public void setCalled(String CALLED) {
        this.CALLED = CALLED;
    }

    public String getStartTime() {
        return START_TIME;
    }

    public void setStartTime(String START_TIME) {
        this.START_TIME = START_TIME;
    }

    public String getDuration() {
        return DURATION;
    }

    public void setDuration(String DURATION) {
        this.DURATION = DURATION;
    }

    public String getTrunkIn() {
        return TRUNK_IN;
    }

    public void setTrunkIn(String TRUNK_IN) {
        this.TRUNK_IN = TRUNK_IN;
    }

    public String getTrunkOut() {
        return TRUNK_OUT;
    }

    public void setTrunkOut(String TRUNK_OUT) {
        this.TRUNK_OUT = TRUNK_OUT;
    }

    public String getSmsIn() {
        return SMS_IN;
    }

    public void setSmsIn(String SMS_IN) {
        this.SMS_IN = SMS_IN;
    }

    public String getSmsOut() {
        return SMS_OUT;
    }

    public void setSmsOut(String SMS_OUT) {
        this.SMS_OUT = SMS_OUT;
    }
}
