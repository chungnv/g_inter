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
public class VtcTotalChargeSmsRefund implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        //(-1)*(
        //  case when ACCOUNT_TYPE_1 like '2%' then COALESCE(CAST(CHARGE_AMOUNT_1 as double),0.0) else 0.0 end 
        //+ case when ACCOUNT_TYPE_2 like '2%'  then COALESCE(CAST(CHARGE_AMOUNT_2 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_3 like '2%'  then COALESCE(CAST(CHARGE_AMOUNT_3 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_4 like '2%'  then COALESCE(CAST(CHARGE_AMOUNT_4 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_5 like '2%'  then COALESCE(CAST(CHARGE_AMOUNT_5 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_6 like '2%'  then COALESCE(CAST(CHARGE_AMOUNT_6 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_7 like '2%'  then COALESCE(CAST(CHARGE_AMOUNT_7 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_8 like '2%'  then COALESCE(CAST(CHARGE_AMOUNT_8 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_9 like '2%'  then COALESCE(CAST(CHARGE_AMOUNT_9 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_10 like '2%'  then COALESCE(CAST(CHARGE_AMOUNT_10 as double),0.0) else 0.0 end)
        double totalCharge = 0.0;
        if (object.getString("ACCOUNT_TYPE_1").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_1");
        }
        if (object.getString("ACCOUNT_TYPE_2").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_2");
        }
        if (object.getString("ACCOUNT_TYPE_3").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_3");
        }
        if (object.getString("ACCOUNT_TYPE_4").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_4");
        }
        if (object.getString("ACCOUNT_TYPE_5").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_5");
        }
        if (object.getString("ACCOUNT_TYPE_6").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_6");
        }
        if (object.getString("ACCOUNT_TYPE_7").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_7");
        }
        if (object.getString("ACCOUNT_TYPE_8").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_8");
        }
        if (object.getString("ACCOUNT_TYPE_9").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_9");
        }
        if (object.getString("ACCOUNT_TYPE_10").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_10");
        }
        return (totalCharge * -1);
    }

}
