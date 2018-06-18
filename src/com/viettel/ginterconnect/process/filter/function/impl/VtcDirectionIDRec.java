/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.util.GIUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class VtcDirectionIDRec implements IFunction {

    private GIUtils utils;
    final String INTERNAL = "1";
    final String DOMESTIC = "2";
    final String INTERNATIONAL = "3";
    final String VAS = "4";
    final String ROAMING = "5";

    private CdrObject coalesceCdr(CdrObject object, String columnName) {
        Object o = utils.coalesce(object.get(columnName));
        if (o != null) {
            object.set(columnName, o);
        } else {
            object.set(columnName, -1);
        }
        return object;
    }

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (object.containsProperty("ROAM_STATE") && object.get("ROAM_STATE") != null) {
            if ("3".equals(object.get("ROAM_STATE").toString())) {
                return ROAMING;
            }
        }
        if (object.containsProperty("CALL_TYPE") && object.get("CALL_TYPE") != null) {
            if ("3".equals(object.get("CALL_TYPE").toString())) {
                return INTERNATIONAL;
            }
            if ("0".equals(object.get("CALL_TYPE").toString())) {
                return INTERNAL;
            }
        }
        if ((object.containsProperty("CALL_TYPE") && object.get("CALL_TYPE") != null)
                && (object.containsProperty("CALLED_PARTY_NUMBER") && object.get("CALLED_PARTY_NUMBER") != null)) {
            if (("1".equals(object.get("CALL_TYPE").toString()) && "17".equals(object.get("PRODUCT_ID").toString()))
                    || (object.get("CALLED_PARTY_NUMBER").toString().startsWith("85517800"))
                    || (object.get("CALLED_PARTY_NUMBER").toString().startsWith("85517550"))) {
                return VAS;
            }
        }
        if (object.containsProperty("CALL_TYPE") && object.get("CALL_TYPE") != null
                && object.containsProperty("CALLED_ROAM_NETWORK_CODE") && object.get("CALLED_ROAM_NETWORK_CODE") != null) {

            if ("1".equals(object.get("CALL_TYPE").toString())
                    && "1".equals(object.get("CALLED_ROAM_NETWORK_CODE").toString())) {
                return INTERNAL;
            }
            if ("1".equals(object.get("CALL_TYPE").toString())
                    && !"1".equals(object.get("CALLED_ROAM_NETWORK_CODE").toString())) {
                return DOMESTIC;
            }
            if ("2".equals(object.get("CALL_TYPE").toString())
                    && "1".equals(object.get("CALLED_ROAM_NETWORK_CODE").toString())) {
                return INTERNAL;
            }
        }
        coalesceCdr(object, "CALL_TYPE");
        return object.get("CALL_TYPE");
    }

}
