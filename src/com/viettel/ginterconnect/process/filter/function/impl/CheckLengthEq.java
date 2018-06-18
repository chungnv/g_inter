package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.process.exception.StandardException;
import org.apache.log4j.Logger;

/**
 * Created by hoangsinh on 15/08/2017.
 */
public class CheckLengthEq implements IFunction {
    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
            if (fieldParams == null || fieldParams.isEmpty()) {
                throw new StandardException("ERROR! Field param function is null");
            }
        if (params == null || !params.matches("\\d+")) {
            throw new StandardException("ERROR! Param function is null or invalid number");
        }
        if (!object.containsProperty(fieldParams)) {
            return false;
        }
        Object fieldValue = object.get(fieldParams);
        if (fieldValue == null) {
            return false;
        }
        return fieldValue.toString().length() == Integer.valueOf(params);
    }
}
