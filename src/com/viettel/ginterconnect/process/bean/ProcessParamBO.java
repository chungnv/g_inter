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
@Table(name = "process_param")
public class ProcessParamBO {
    
    @Column(name = "ID", primaryKey = true)
    private Long id;
    @Column(name = "MASTER_ID")
    private Long masterId;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "PARAM_NAME")
    private String paramName;
    @Column(name = "PARAM_TYPE")
    private String paramType;
    @Column(name = "PARAM_FORMAT")
    private String paramFormat;
    @Column(name = "PARAM_VALUE")
    private String paramValue;
    @Column(name = "STATUS")
    private Long status;

    public String getType() {
        return type;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamFormat() {
        return paramFormat;
    }

    public void setParamFormat(String paramFormat) {
        this.paramFormat = paramFormat;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public Long getMasterId() {
        return masterId;
    }

    public void setMasterId(Long masterId) {
        this.masterId = masterId;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

}
