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
public class VtdIsdn implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //case when ISDN  like '670%'  and length(ISDN) > 10  then  substr(ISDN , 4)  else ISDN end 
        String isdn = object.get("ISDN").toString();
        if (isdn.startsWith("670") && isdn.length() > 0) {
            return isdn.substring(3);
        }
        return isdn;
    }

}
