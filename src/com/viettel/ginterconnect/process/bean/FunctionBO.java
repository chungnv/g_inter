/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;

/**
 *
 * @since May 9, 2011
 * @version 1.0
 */
@Table(name = "md_function")
public class FunctionBO {
    @Column(name = "FUNC_ID",primaryKey = true)
    private Long functionId;
    @Column(name = "FUNC_NAME")
    private String functionName;
    @Column(name = "PARAMS")
    private String params;
    @Column(name = "FIELD_PARAMS")
    private String fieldParams;
    @Column(name = "STATUS")
    private String status;
    private String script;
    private String input;
    @Column(name = "IS_SCRIPT")
    private Long isScript;
    private String importPackages;
    @Column(name = "DESCRIPTION")
    private String description;

    public Long getIsScript() {
        return isScript;
    }

    public void setIsScript(Long isScript) {
        this.isScript = isScript;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getFieldParams() {
        return fieldParams;
    }

    public void setFieldParams(String fieldParams) {
        this.fieldParams = fieldParams;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getImportPackages() {
        return importPackages;
    }

    public void setImportPackages(String importPackages) {
        this.importPackages = importPackages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

