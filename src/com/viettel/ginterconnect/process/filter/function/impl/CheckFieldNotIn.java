package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.process.exception.StandardException;
import org.apache.log4j.Logger;

/**
 * Created by hoangsinh on 15/08/2017.
 */
public class CheckFieldNotIn implements IFunction {
    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (fieldParams == null || fieldParams.isEmpty()) {
            throw new StandardException("ERROR! Field param function is null");
        }
        if (params == null || params.isEmpty()) {
            throw new StandardException("ERROR! Param function is null");
        }
        Object fieldValue = object.get(fieldParams);
        String[] stringNotIn = params.split(";");
        for (String par : stringNotIn) {
            if (par == null && fieldValue == null) {
                return false;
            }

            if (par != null && fieldValue != null && par.equals(fieldValue.toString())) {
                return false;
            }
        }
        return true;
    }
}
