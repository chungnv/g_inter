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
public class VtcServiceFlowRec implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if ((object.containsProperty("SERVICE_FLOW") && object.get("SERVICE_FLOW") != null)
                && (object.containsProperty("ACCOUNT_TYPE_1") && object.get("ACCOUNT_TYPE_1") != null)) {
            if ("2".equals(object.get("SERVICE_FLOW").toString())
                    && "2000".equals(object.get("ACCOUNT_TYPE_1"))) {
                return true;
            }
        }
        return false;
    }

}
