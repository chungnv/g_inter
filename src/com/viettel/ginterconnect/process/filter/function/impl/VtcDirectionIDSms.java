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
public class VtcDirectionIDSms implements IFunction {

    final String INTERNAL = "1";
    final String DOMESTIC = "2";
    final String INTERNATIONAL = "3";
    final String VAS = "4";
    final String ROAMING = "5";
    final String UNKNOW = "-1";

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (object.get("ROAM_STATE") != null) {
            if ("3".equals(object.get("ROAM_STATE").toString())) {
                return ROAMING;
            }
        }
        if (object.get("PRODUCT_ID") != null) {
            if ("17".equals(object.get("PRODUCT_ID").toString())) {
                return VAS;
            }
        }
        if (object.get("ONNET_INDICATOR") != null) {
            if ("0".equals(object.get("ONNET_INDICATOR").toString())) {
                return INTERNAL;
            }
            if ("1".equals(object.get("ONNET_INDICATOR").toString())) {
                return DOMESTIC;
            }
            if ("2".equals(object.get("ONNET_INDICATOR").toString())) {
                return INTERNATIONAL;
            }
        }
        return UNKNOW;
    }

}
