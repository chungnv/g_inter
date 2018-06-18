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
public class MvtIsdnRec implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
//        case when isdn is not null and length(isdn) > 9 then substr(isdn, 4) else isdn end
        String isdn = object.get("ISDN").toString();
        if (isdn.length() > 9) {
            return isdn.substring(3);
        }
        return isdn;
    }
    
}
