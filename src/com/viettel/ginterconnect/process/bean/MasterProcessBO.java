/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;

/**
 *
 * @author
 */
@Table(name = "master_process")
public class MasterProcessBO {

    @Column(name = "MASTER_ID", primaryKey = true)
    private String masterId;
    @Column(name = "CURRENT_SEQ")
    private String currentSeq;
    @Column(name = "CURRENT_TIME")
    private String currentTime;

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public String getCurrentSeq() {
        return currentSeq;
    }

    public void setCurrentSeq(String currentSeq) {
        this.currentSeq = currentSeq;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
    
    

}
