/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class CommonSumField implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (fieldParams == null) {
            return 0;
        }
        String fields[] = fieldParams.split(",");
        Long returnValue = 0L;
        for (String field : fields) {
            returnValue += (object.get(field) == null ? 0L : Long.valueOf(object.get(field).toString()));
        }
        return returnValue;
    }
    
}
