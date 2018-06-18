/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.ginterconnect.process.filter;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.bean.Result;

public class CdrSaveItem {

    private String fileName;
    private String tableName;
    private CdrObject cdrObject;
    private String threadName;
    private Result result;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public CdrObject getCdrObject() {
        return cdrObject;
    }

    public void setCdrObject(CdrObject cdrObject) {
        this.cdrObject = cdrObject;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public CdrSaveItem(String threadName, CdrObject object, Result result) {
        this.threadName = threadName;
        this.cdrObject = object;
        this.result = result;
    }
    
}

