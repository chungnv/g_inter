/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class CommonToString implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //
        if (object.get(fieldParams) == null || !(object.get(fieldParams) instanceof Date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(params);
        return sdf.format((Date) object.get(fieldParams));
    }

}
