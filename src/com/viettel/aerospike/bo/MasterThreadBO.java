/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.bo;

import com.aerospike.client.Record;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class MasterThreadBO {
    
    private int PK = 0;
    private int MASTER_ID = 0;
    private String PROCESS_CODE = "";
    private String DESCRIPTION = "";
    private String PROCESS_CLASS = "";
    private boolean WHEN_ERROR = false;
    private boolean isActive = true;
    private Long SWITCH_ID = 0L;
    private int RUN_STATUS = 1;
    private String LAST_RUN_TIME;
    private List<String> lstStep;
    private String IP = "";
    private String RUN_DIR = "";
    private String step1;
    private String step2;
    private String step3;
    private String step4;
    private String step5;
    private String step6;
    private String step7;
    private String step8;
    private String step9;
    private String step10;
    private Long sleepTime;
    private Record record;
    private String scheduler;
    
    public int getPrimaryKey() {
        return PK;
    }

    public void setPrimaryKey(int PK) {
        this.PK = PK;
    }
    

    public int getMasterId() {
        return MASTER_ID;
    }

    public void setMasterId(int MASTER_ID) {
        this.MASTER_ID = MASTER_ID;
    }

    public String getMasterProcessCode() {
        return PROCESS_CODE;
    }

    public void setMasterProcessCode(String PROCESS_CODE) {
        this.PROCESS_CODE = PROCESS_CODE;
    }

    public String getDescrIPtion() {
        return DESCRIPTION;
    }

    public void setDescrIPtion(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getProcessClass() {
        return PROCESS_CLASS;
    }

    public void setProcessClass(String PROCESS_CLASS) {
        this.PROCESS_CLASS = PROCESS_CLASS;
    }

    public boolean isStopWhenError() {
        return WHEN_ERROR;
    }

    public void setStopWhenError(boolean WHEN_ERROR) {
        this.WHEN_ERROR = WHEN_ERROR;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public int getRunStatus() {
        return RUN_STATUS;
    }

    public void setRunStatus(int RUN_STATUS) {
        this.RUN_STATUS = RUN_STATUS;
    }

    public List<String> getLstStep() {
        return lstStep;
    }

    public void setLstStep(List<String> lstStep) {
        this.lstStep = lstStep;
    }

    public String getIp() {
        return IP;
    }

    public void setIp(String IP) {
        this.IP = IP;
    }

    public String getRunDir() {
        return RUN_DIR;
    }

    public void setRunDir(String RUN_DIR) {
        this.RUN_DIR = RUN_DIR;
    }

    public String getStep1() {
        return step1;
    }

    public void setStep1(String step1) {
        this.step1 = step1;
    }

    public String getStep2() {
        return step2;
    }

    public void setStep2(String step2) {
        this.step2 = step2;
    }

    public String getStep3() {
        return step3;
    }

    public void setStep3(String step3) {
        this.step3 = step3;
    }

    public String getStep4() {
        return step4;
    }

    public void setStep4(String step4) {
        this.step4 = step4;
    }

    public String getStep5() {
        return step5;
    }

    public void setStep5(String step5) {
        this.step5 = step5;
    }

    public String getStep6() {
        return step6;
    }

    public void setStep6(String step6) {
        this.step6 = step6;
    }

    public String getStep7() {
        return step7;
    }

    public void setStep7(String step7) {
        this.step7 = step7;
    }

    public String getStep8() {
        return step8;
    }

    public void setStep8(String step8) {
        this.step8 = step8;
    }

    public String getStep9() {
        return step9;
    }

    public void setStep9(String step9) {
        this.step9 = step9;
    }

    public String getStep10() {
        return step10;
    }

    public void setStep10(String step10) {
        this.step10 = step10;
    }
    
    public String getFlow() {
        String flow = StringUtils.join(lstStep, "|");
        return flow.toString();
    }

    public String getLastRunTime() {
        return LAST_RUN_TIME;
    }

    public void setLastRunTime(String LAST_RUN_TIME) {
        this.LAST_RUN_TIME = LAST_RUN_TIME;
    }

    public Long getSWITCH_ID() {
        return SWITCH_ID;
    }

    public void setSWITCH_ID(Long SWITCH_ID) {
        this.SWITCH_ID = SWITCH_ID;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public Long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

}
