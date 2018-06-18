/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.exception.FilterException;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class CommonExactFromDateField implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (params == null || fieldParams == null) {
            throw new FilterException("CommonExactFromDateField param null", FilterException._CONFIG_FAIL);
        }
        if (object.get(fieldParams) == null) {
            return null;
        }
        return (new SimpleDateFormat(params).format((Date) object.get(fieldParams)));
    }

}
