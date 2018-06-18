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
public class CommonJoinString implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (fieldParams == null) {
            return null;
        }
        String returnString = "";
        String joinStr = params == null ? "" : params.trim();
        String fields[] = fieldParams.split(",");
        for (String s : fields) {
            returnString += object.get(s) == null ? "" : object.get(s).toString() + joinStr;
        }
        if (returnString.length() > 1) {
            returnString = returnString.substring(0, returnString.length() - joinStr.length());
        }
        return returnString;
    }
    
}
