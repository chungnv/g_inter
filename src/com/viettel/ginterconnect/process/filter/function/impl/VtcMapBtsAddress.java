/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.aerospike.client.query.Filter;
import com.viettel.ginterconnect.cached.bean.VtcBtsAddress;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.util.GIClient;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class VtcMapBtsAddress implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (object.get(fieldParams) == null) {
            object.set("BTS_CODE", "NULL");
            object.set("CENTER_CODE", "");
            object.set("PROVINCE", "");
            return object;
        }
        String cell = object.get(fieldParams).toString();
        VtcBtsAddress vtcBts = (VtcBtsAddress) GIClient.getInstance().getBtsAddress("VTC_BTS_ADDRESS", Filter.equal("SUBSTR_LAC_CI", cell));
        if (vtcBts != null) {
            object.set("BTS_CODE", vtcBts.getBtsCode());
            object.set("CENTER_CODE", vtcBts.getCenterCode());
            object.set("PROVINCE", vtcBts.getProvinceCode());
        } else {
            object.set("BTS_CODE", "NULL");
            object.set("CENTER_CODE", "");
            object.set("PROVINCE", "");
        }
        return object;
    }

}
