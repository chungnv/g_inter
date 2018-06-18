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
 * @author viettq
 */
public class VtcCalledRec implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        String called = "";
        if (object.containsProperty("CALLED_PARTY_NUMBER") && object.get("CALLED_PARTY_NUMBER") != null) {
            called = object.get("CALLED_PARTY_NUMBER").toString();
        }
        String calling = "";
        if (object.containsProperty("CALLING_PARTY_NUMBER") && object.get("CALLING_PARTY_NUMBER") != null) {
            calling = object.get("CALLING_PARTY_NUMBER").toString();
        }
        if ((object.containsProperty("SERVICE_FLOW") && object.get("SERVICE_FLOW") != null)
                && (object.containsProperty("ACCOUNT_TYPE_1") && object.get("ACCOUNT_TYPE_1") != null)) {
            if ("2".equals(object.get("SERVICE_FLOW"))
                    && "2000".equals(object.get("ACCOUNT_TYPE_1"))) {
                if (calling.length() > 4) {
                    return calling.substring(3);
                }
            }
        } else if ((object.containsProperty("CALL_TYPE") && object.get("CALL_TYPE") != null)
                && (object.containsProperty("CALLED_PARTY_NUMBER") && object.get("CALLED_PARTY_NUMBER") != null)) {
            if ("3".equals(object.get("CALL_TYPE"))) {
                return object.get("CALLED_PARTY_NUMBER");
            }
        } else if ((object.containsProperty("CALL_TYPE") && object.get("CALL_TYPE") != null)
                && (object.containsProperty("CALLED_PARTY_NUMBER") && object.get("CALLED_PARTY_NUMBER") != null)) {
            if (!("3".equals(object.get("CALL_TYPE"))) && !called.startsWith("855")) {
                return object.get("CALLED_PARTY_NUMBER");
            } else if (called.length() > 4) {
                return called.substring(3);
            }
        }
        return null;
    }
}
