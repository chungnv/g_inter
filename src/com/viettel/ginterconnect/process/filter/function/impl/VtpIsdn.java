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
public class VtpIsdn implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //case when ( ISDN like '9%' or ISDN like '1%') and length(ISDN)<11 then ISDN else substr(ISDN,3) end
        String isdn = object.get("ISDN").toString();
        if ((isdn.startsWith("9") || isdn.startsWith("1")) && isdn.length() < 11) {
            return isdn;
        }
        return isdn.substring(2);
    }
    
}
