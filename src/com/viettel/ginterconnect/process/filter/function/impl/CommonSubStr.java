/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.exception.FilterException;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.process.exception.StandardException;
import org.apache.log4j.Logger;

/**
 *
 * @author viettq
 */
public class CommonSubStr implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (params == null || params.isEmpty()) {
            throw new StandardException("ERROR! Param function is null");
        }
        String[] param = params.split(",");
        String rowName = param[0];
        int start = Integer.valueOf(param[1]);
        int end = -1;
        if (param.length > 2) {
            end = Integer.valueOf(param[2]);
        }
        if (object.containsProperty(rowName) && object.get(rowName) != null) {
            String getRow = object.getString(rowName);
            if (end < 0) {
                if (start < 0) {
                    try {
                        return getRow.substring(getRow.length() - (-start));
                    } catch (Exception ex) {
                        throw new StandardException("SubString isdn fail" + getRow, StandardException.PRIORITY_REJECT_CDR);
                    }
                } else {
                    return getRow.substring(start - 1);
                }
            } else {
                return getRow.substring(start - 1, end - 1);
            }
        }
        return null;
    }
//	
//	 public static void main(String[] args) {
//        SubStr func = new SubStr();
//        CdrObject object = new CdrObject();
//        object.set("DATE", "2017-08-09 23:24:25");
//        System.out.println(func.execute(object, null, "DATE,-1", null, null));
//    }
}
