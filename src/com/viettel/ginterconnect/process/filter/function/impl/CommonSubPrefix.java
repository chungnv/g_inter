/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.exception.FilterException;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class CommonSubPrefix implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (fieldParams == null || params == null) {
            throw new FilterException("CommonSubPrefix missing param ", FilterException._FAIL);
        }
        if (object.get(fieldParams.trim()) == null) {
            return null;
        }
        String arrParam[] = params.trim().split(",");
        String strInput = object.getString(fieldParams.trim());
        for (String param : arrParam) {
            if (strInput.startsWith(param.trim())) {
                return strInput.substring(param.trim().length());
            }
        }
        return strInput;
    }
    
}
