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
public class VtcIsdnRegister implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //case when isdn like '855%'  and length(isdn)>8 then substr(isdn,4) else ISDN end
        if (object.get("ISDN") == null) {
            return null;
        }
        String isdn = object.get("ISDN").toString();
//        System.out.println("isdn = " + isdn);
        if (isdn.startsWith("855") && isdn.length() > 8) {
            return isdn.substring(3);
        }
        return isdn;
    }
}
