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
public class VtcOrgChargeMon implements IFunction {

    final String VALUE = "2004";
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
//        coalesceCdr(object, "CHARGE_FROM_PREPAID");
        Double totalCharge = GIUtils.coalesceDouble(object, 0.0, "CHARGE_FROM_PREPAID");
        if (VALUE.equals(object.get("ACCOUNT_TYPE_1"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_1");
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_1");
        }
        if (VALUE.equals(object.get("ACCOUNT_TYPE_2"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_2");
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_2");
        }
        if (VALUE.equals(object.get("F43_ACCOUNTTYPE3"))) {
//            coalesceCdr(object, "F44_CHARGEAMOUNT3");
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "F44_CHARGEAMOUNT3");
        }
        if (VALUE.equals(object.get("F46_ACCOUNTTYPE4"))) {
//            coalesceCdr(object, "F47_CHARGEAMOUNT4");
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "F47_CHARGEAMOUNT4");
        }
        if (VALUE.equals(object.get("F49_ACCOUNTTYPE5"))) {
//            coalesceCdr(object, "F50_CHARGEAMOUNT5");
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "F50_CHARGEAMOUNT5");
        }
        if (VALUE.equals(object.get("F52_ACCOUNTTYPE6"))) {
//            coalesceCdr(object, "F53_CHARGEAMOUNT6");
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "F53_CHARGEAMOUNT6");
        }
        if (VALUE.equals(object.get("F55_ACCOUNTTYPE7"))) {
//            coalesceCdr(object, "F56_CHARGEAMOUNT7");
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "F56_CHARGEAMOUNT7");
        }
        if (VALUE.equals(object.get("F58_ACCOUNTTYPE8"))) {
//            coalesceCdr(object, "F59_CHARGEAMOUNT8");
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "F59_CHARGEAMOUNT8");
        }
        if (VALUE.equals(object.get("F61_ACCOUNTTYPE9"))) {
//            coalesceCdr(object, "F62_CHARGEAMOUNT9");
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "F62_CHARGEAMOUNT9");
        }
        if (VALUE.equals(object.get("F64_ACCOUNTTYPE10"))) {
//            coalesceCdr(object, "F65_CHARGEAMOUNT10");
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "F65_CHARGEAMOUNT10");
        }
//        return Double.valueOf(object.get("CHARGE_FROM_PREPAID").toString())
//                + Double.valueOf(object.get("CHARGE_AMOUNT_1").toString())
//                + Double.valueOf(object.get("CHARGE_AMOUNT_1").toString()) + Double.valueOf(object.get("F53_CHARGEAMOUNT6").toString())
//                + Double.valueOf(object.get("F44_CHARGEAMOUNT3").toString()) + Double.valueOf(object.get("F56_CHARGEAMOUNT7").toString())
//                + Double.valueOf(object.get("F47_CHARGEAMOUNT4").toString()) + Double.valueOf(object.get("F59_CHARGEAMOUNT8").toString())
//                + Double.valueOf(object.get("F50_CHARGEAMOUNT5").toString()) + Double.valueOf(object.get("F62_CHARGEAMOUNT9").toString())
//                + Double.valueOf(object.get("F65_CHARGEAMOUNT10").toString());
        return totalCharge;
    }
}
