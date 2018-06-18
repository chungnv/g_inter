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
public class VtcPromChargeRec implements IFunction {

    final String VALUE = "2004";

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
//        coalesceCdr(object, "CHARGE_OF_FUND_ACCOUNTS");
//        coalesceCdr(object, "CHARGE_FROM_PREPAID");
        Double totalCharge = GIUtils.coalesceDouble(object, 0.0, "CHARGE_OF_FUND_ACCOUNTS");
        totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_FROM_PREPAID");
        if (VALUE.equals(object.get("ACCOUNT_TYPE_1"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_1");
            totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_1");
        }
        if (VALUE.equals(object.get("ACCOUNT_TYPE_2"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_2");
            totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_2");
        }
        if (VALUE.equals(object.get("ACCOUNT_TYPE_3"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_3");
            totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_3");
        }
        if (VALUE.equals(object.get("ACCOUNT_TYPE_4"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_4");
            totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_4");
        }
        if (VALUE.equals(object.get("ACCOUNT_TYPE_5"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_5");
            totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_5");
        }
        if (VALUE.equals(object.get("ACCOUNT_TYPE_6"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_6");
            totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_6");
        }
        if (VALUE.equals(object.get("ACCOUNT_TYPE_7"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_7");
            totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_7");
        }
        if (VALUE.equals(object.get("ACCOUNT_TYPE_8"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_8");
            totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_8");
        }
        if (VALUE.equals(object.get("ACCOUNT_TYPE_9"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_9");
            totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_9");
        }
        if (VALUE.equals(object.get("ACCOUNT_TYPE_10"))) {
//            coalesceCdr(object, "CHARGE_AMOUNT_10");
            totalCharge -= GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_10");
        }
        return totalCharge;
    }
}
