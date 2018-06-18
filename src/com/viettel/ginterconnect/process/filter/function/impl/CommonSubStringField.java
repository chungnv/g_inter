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
public class CommonSubStringField implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (object.get(fieldParams) == null) {
            return null;
        }
        String fieldValue = object.getString(fieldParams);
        if (params != null) {
            String arrParams[] = params.split(";");
            for (String substringParam : arrParams) {
                if (fieldValue.startsWith(substringParam)) {
                    return fieldValue.substring(substringParam.length());
                }
            }
        }
        return fieldValue;
    }

}
