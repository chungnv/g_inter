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
@Table(name = "convert_file_log")
public class ConvertLog {
    
    @Column(name = "FILE_ID", primaryKey = true)
    private Long fileId;
    @Column(name = "CONVERT_TIME")
    private String convertTimestamp;
    @Column(name = "PROCESS_CODE")
    private String processCode;
    @Column(name = "CONVERT_TOTAL")
    private Long convertTotal;
    @Column(name = "SWITCH_ID")
    private Long switchId;
    @Column(name = "ORG_FILE_NAME")
    private String orgFileName;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getConvertTimestamp() {
        return convertTimestamp;
    }

    public void setConvertTimestamp(String convertTimestamp) {
        this.convertTimestamp = convertTimestamp;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public Long getConvertTotal() {
        return convertTotal;
    }

    public void setConvertTotal(Long convertTotal) {
        this.convertTotal = convertTotal;
    }

    public Long getSwitchId() {
        return switchId;
    }

    public void setSwitchId(Long switchId) {
        this.switchId = switchId;
    }

    public String getOrgFileName() {
        return orgFileName;
    }

    public void setOrgFileName(String orgFileName) {
        this.orgFileName = orgFileName;
    }
}

