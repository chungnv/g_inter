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
 * @author ubuntu
 */
@Table(name = "switchboard_function_config")
public class MapConvert {
    
    @Column(name = "ID", primaryKey = true)
    private Long id;
    @Column(name = "SWITCH_TYPE")
    private String switchType;
    @Column(name = "COUNTRY")
    private String country;
    @Column(name = "CONVERT_CLASS")
    private String convertClassName;
    @Column(name = "CONVERT_CONFIG")
    private String convertConfigPath;
    @Column(name = "TIME_ZONE")
    private Double timeZone;
    @Column(name = "PRE_FUNCTION")
    private String preFilterFunction;
    @Column(name = "POST_FUNCTION")
    private String postFilterFunction;
    @Column(name = "STATUS")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSwitchType() {
        return switchType;
    }

    public void setSwitchType(String switchType) {
        this.switchType = switchType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getConvertClassName() {
        return convertClassName;
    }

    public void setConvertClassName(String convertClassName) {
        this.convertClassName = convertClassName;
    }

    public String getConvertConfigPath() {
        return convertConfigPath;
    }

    public void setConvertConfigPath(String convertConfigPath) {
        this.convertConfigPath = convertConfigPath;
    }

    public String getPreFilterFunction() {
        return preFilterFunction;
    }

    public void setPreFilterFunction(String preFilterFunction) {
        this.preFilterFunction = preFilterFunction;
    }

    public String getPostFilterFunction() {
        return postFilterFunction;
    }

    public void setPostFilterFunction(String postFilterFunction) {
        this.postFilterFunction = postFilterFunction;
    }

    public Double getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Double timeZone) {
        this.timeZone = timeZone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

