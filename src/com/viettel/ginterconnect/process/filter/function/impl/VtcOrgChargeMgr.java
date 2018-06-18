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
 * @author viettq
 */
public class VtcOrgChargeMgr implements IFunction {

    private GIUtils utils;

    private CdrObject coalesceCdr(CdrObject object, String columnName) {
        Object o = utils.coalesce(object.get(columnName));
        if (o != null) {
            object.set(columnName, o);
        } else {
            object.set(columnName, 0);
        }
        return object;
    }

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        coalesceCdr(object, "CHARGE_AMOUNT_1");
        return Double.valueOf(object.get("CHARGE_AMOUNT_1").toString()) / 20;
    }
}
