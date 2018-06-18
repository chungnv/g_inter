/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.filter.function;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.bean.FunctionBO;
import com.viettel.ginterconnect.process.exception.FilterException;
import com.viettel.ginterconnect.process.filter.GICache;
import com.viettel.ginterconnect.util.Constants;
import org.apache.log4j.Logger;

public class ExecuteFunction {

    private FunctionBO function;
    private String fieldName;
    private CdrObject object;
    private GICache filterCache;
    private Logger logger;

    /**
     * override
     *
     * @return Object sau khi chon ham xu li tra ve ket qua la 1 object
     * @throws java.lang.Exception
     */
    public Object execute() throws Exception {
        //ValidateFunction validateFunction = new ValidateFunction(function.getScript());
        String fName = function.getFunctionName();
        try {
            Class instance = Class.forName(Constants.FILTER_FUNCTION_IMPL_PCKG + fName);
            if (instance.newInstance() instanceof IFunction) {
                IFunction func = (IFunction) instance.newInstance();
                Object obj = func.execute(object, function.getInput(), function.getParams(), function.getFieldParams(), filterCache, logger);
                return obj;
            }
        } catch (FilterException fe) {
            throw fe;
        }
        return null;
    }

    public Object execute(Object cacheObject) throws Exception {
        //ValidateFunction validateFunction = new ValidateFunction(function.getScript());
        String fName = function.getFunctionName();
        try {
            Class instance = Class.forName(Constants.FILTER_FUNCTION_IMPL_PCKG + fName);
            if (instance.newInstance() instanceof IFunction) {
                IFunction func = (IFunction) instance.newInstance();
                Object obj = func.execute(object, function.getInput(), function.getParams(), function.getFieldParams(), cacheObject, logger);
                return obj;
            }
        } catch (Exception fe) {
            throw fe;
        }
        return null;
    }

    public FunctionBO getFunction() {
        return this.function;
    }

    public void setFunction(FunctionBO function) {
        this.function = function;
    }

    public CdrObject getObject() {
        return object;
    }

    public void setObject(CdrObject object) {
        this.object = object;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public GICache getFilterCache() {
        return filterCache;
    }

    public void setFilterCache(GICache filterCache) {
        this.filterCache = filterCache;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
