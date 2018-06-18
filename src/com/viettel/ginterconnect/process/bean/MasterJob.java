/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;
import java.util.Date;

/**
 *
 * @author ubuntu
 */
@Table(name = "jobs")
public class MasterJob {
    
    @Column(name = "ID", primaryKey = true)
    private String ID;
    @Column(name = "CREATE_PROCESS")
    private String CREATE_PROCESS = "";
    @Column(name = "JOB_STATUS")
    private String JOB_STATUS;
    @Column(name = "JOB_FIELD")
    private String JOB_FIELD;
    @Column(name = "JOB_PARAM")
    private String JOB_PARAM;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCREATE_PROCESS() {
        return CREATE_PROCESS;
    }

    public void setCREATE_PROCESS(String CREATE_PROCESS) {
        this.CREATE_PROCESS = CREATE_PROCESS;
    }

    public String getJOB_STATUS() {
        return JOB_STATUS;
    }

    public void setJOB_STATUS(String JOB_STATUS) {
        this.JOB_STATUS = JOB_STATUS;
    }

    public String getJOB_FIELD() {
        return JOB_FIELD;
    }

    public void setJOB_FIELD(String JOB_FIELD) {
        this.JOB_FIELD = JOB_FIELD;
    }

    public String getJOB_PARAM() {
        return JOB_PARAM;
    }

    public void setJOB_PARAM(String JOB_PARAM) {
        this.JOB_PARAM = JOB_PARAM;
    }
}

