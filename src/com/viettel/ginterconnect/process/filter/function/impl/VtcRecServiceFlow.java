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
public class VtcRecServiceFlow implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //SERVICE_FLOW = 2 AND ACCOUNT_TYPE_1 = 2000
        if (object.get("SERVICE_FLOW") != null && object.get("ACCOUNT_TYPE_1") != null) {
            if ("2".equals(object.get("SERVICE_FLOW").toString())
                    && "2000".equals(object.get("ACCOUNT_TYPE_1").toString())) {
                return true;
            }
        }
        return false;
    }
    
}
