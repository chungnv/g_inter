/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;
import java.io.Serializable;

@Table(name = "md_standardize")
public class StandardizeField implements Serializable {
    @Column(name = "STAND_ID", primaryKey = true)
    private Long standardizeId;
    @Column(name = "RESULT_ID")
    private Long resultId;
    @Column(name = "STAND_FIELD")
    private String standardizeField;
    @Column(name = "FUNCTION_ID")
    private Long functionId;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "PRIORITY")
    private Long priority;
    @Column(name = "SWITCH_TYPE")
    private String switchType;
    @Column(name = "COUNTRY")
    private String country;

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getStandardizeField() {
        return standardizeField;
    }

    public void setStandardizeField(String standardizeField) {
        this.standardizeField = standardizeField;
    }

    public Long getStandardizeId() {
        return standardizeId;
    }

    public void setStandardizeId(Long standardizeId) {
        this.standardizeId = standardizeId;
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
}

