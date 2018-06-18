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
public class VtbIsdnRec implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //case when ISDN  like '8123%' or ISDN  like '8888%' or length(ISDN) < 10 
        //then ISDN  else substr(ISDN , 4) end 
        String isdn = object.get("ISDN").toString();
        if (isdn.startsWith("8123") || isdn.startsWith("8888") || isdn.length() < 10) {
            return isdn;
        }
        return isdn.substring(3);
    }
}
