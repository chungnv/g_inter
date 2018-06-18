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
public class JobsBO {

    private long PK = 0;
    private long ID = 0;
    private String CREATE_PROCESS = "";
    private int JOB_STATUS = 0;
    private String JOB_TYPE = "";
    private String FLOW = "";
    private String SWITCH_TYPE = "";
    private String COUNTRY = "";
    private String JOB_PARAM = "";
    private String JOB_FIELD = "";
    private String CREATE_DATE = "";
    private String PROCESS_DATE = "";
    private String WORKER_ID = "";
    private String IP = "";
    private String processCode;
    
    public long getPrimaryKey() {
        return PK;
    }

    public void setPrimaryKey(long PK) {
        this.PK = PK;
    }
    
    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }
    
    public String getCreateProcess() {
        return CREATE_PROCESS;
    }

    public void setCreateProcess(String CREATE_PROCESS) {
        this.CREATE_PROCESS = CREATE_PROCESS;
    }
    
    public int getJobsStatus() {
        return JOB_STATUS;
    }

    public void setJobsStatus(int JOB_STATUS) {
        this.JOB_STATUS = JOB_STATUS;
    }
    
    public String getJobType() {
        return JOB_TYPE;
    }

    public void setJobType(String JOB_TYPE) {
        this.JOB_TYPE = JOB_TYPE;
    }
    
    public String getFlow() {
        return FLOW;
    }

    public void setFlow(String FLOW) {
        this.FLOW = FLOW;
    }

    public String getSwitchType() {
        return SWITCH_TYPE;
    }

    public void setSwitchType(String SWITCH_TYPE) {
        this.SWITCH_TYPE = SWITCH_TYPE;
    }

    public String getCountry() {
        return COUNTRY;
    }

    public void setCountry(String COUNTRY) {
        this.COUNTRY = COUNTRY;
    }

    public String getJobParam() {
        return JOB_PARAM;
    }

    public void setJobParam(String JOB_PARAM) {
        this.JOB_PARAM = JOB_PARAM;
    }

    public String getCreateDate() {
        return CREATE_DATE;
    }

    public void setCreateDate(String CREATE_DATE) {
        this.CREATE_DATE = CREATE_DATE;
    }

    public String getProcessDate() {
        return PROCESS_DATE;
    }

    public void setProcessDate(String PROCESS_DATE) {
        this.PROCESS_DATE = PROCESS_DATE;
    }

    public String getWorkerID() {
        return WORKER_ID;
    }

    public void setWorkerID(String WORKER_ID) {
        this.WORKER_ID = WORKER_ID;
    }

    public String getIp() {
        return IP;
    }

    public void setIp(String IP) {
        this.IP = IP;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }
    
    public String getJobField() {
        return this.JOB_FIELD;
    }
    
    public void setJobField(String jobField) {
        this.JOB_FIELD = jobField;
    }
    
    public String toString(String spliter) {
        StringBuilder builder = new StringBuilder();
        builder.append(PK).append(spliter);
        builder.append(CREATE_PROCESS).append(spliter);
        builder.append(JOB_STATUS).append(spliter);
        builder.append(JOB_TYPE).append(spliter);
        builder.append(FLOW).append(spliter);
        builder.append(SWITCH_TYPE).append(spliter);
        builder.append(COUNTRY).append(spliter);
        builder.append(JOB_PARAM).append(spliter);
        builder.append(JOB_FIELD).append(spliter);
        builder.append(CREATE_DATE).append(spliter);
        builder.append(PROCESS_DATE).append(spliter);
        builder.append(WORKER_ID).append(spliter);
        builder.append(IP);
        return builder.toString();
    }
}