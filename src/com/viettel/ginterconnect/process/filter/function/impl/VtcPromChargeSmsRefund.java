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
public class VtcPromChargeSmsRefund implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        //(-1)*(
        //  case when ACCOUNT_TYPE_1 not in('2004','2000') then COALESCE(CAST(CHARGE_AMOUNT_1 as double),0.0) else 0.0 end 
        //+ case when ACCOUNT_TYPE_2 not in('2004','2000')  then COALESCE(CAST(CHARGE_AMOUNT_2 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_3 not in('2004','2000')  then COALESCE(CAST(CHARGE_AMOUNT_3 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_4 not in('2004','2000')  then COALESCE(CAST(CHARGE_AMOUNT_4 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_5 not in('2004','2000')  then COALESCE(CAST(CHARGE_AMOUNT_5 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_6 not in('2004','2000')  then COALESCE(CAST(CHARGE_AMOUNT_6 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_7 not in('2004','2000')  then COALESCE(CAST(CHARGE_AMOUNT_7 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_8 not in('2004','2000')  then COALESCE(CAST(CHARGE_AMOUNT_8 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_9 not in('2004','2000')  then COALESCE(CAST(CHARGE_AMOUNT_9 as double),0.0) else 0.0 end  
        //+ case when ACCOUNT_TYPE_10 not in('2004','2000')  then COALESCE(CAST(CHARGE_AMOUNT_10 as double),0.0) else 0.0 end)
        double totalCharge = 0.0;
        if (!"2004".equals(object.getString("ACCOUNT_TYPE_1"))
                && !"2000".equals(object.getString("ACCOUNT_TYPE_1")) && object.getString("ACCOUNT_TYPE_1").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_1");
        }
        if (!"2004".equals(object.getString("ACCOUNT_TYPE_2"))
                && !"2000".equals(object.getString("ACCOUNT_TYPE_2")) && object.getString("ACCOUNT_TYPE_2").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_2");
        }
        if (!"2004".equals(object.getString("ACCOUNT_TYPE_3"))
                && !"2000".equals(object.getString("ACCOUNT_TYPE_3")) && object.getString("ACCOUNT_TYPE_3").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_3");
        }
        if (!"2004".equals(object.getString("ACCOUNT_TYPE_4"))
                && !"2000".equals(object.getString("ACCOUNT_TYPE_4")) && object.getString("ACCOUNT_TYPE_4").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_4");
        }
        if (!"2004".equals(object.getString("ACCOUNT_TYPE_5"))
                && !"2000".equals(object.getString("ACCOUNT_TYPE_5")) && object.getString("ACCOUNT_TYPE_5").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_5");
        }
        if (!"2004".equals(object.getString("ACCOUNT_TYPE_6"))
                && !"2000".equals(object.getString("ACCOUNT_TYPE_6")) && object.getString("ACCOUNT_TYPE_6").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_6");
        }
        if (!"2004".equals(object.getString("ACCOUNT_TYPE_7"))
                && !"2000".equals(object.getString("ACCOUNT_TYPE_7")) && object.getString("ACCOUNT_TYPE_7").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_7");
        }
        if (!"2004".equals(object.getString("ACCOUNT_TYPE_8"))
                && !"2000".equals(object.getString("ACCOUNT_TYPE_8")) && object.getString("ACCOUNT_TYPE_8").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_8");
        }
        if (!"2004".equals(object.getString("ACCOUNT_TYPE_9"))
                && !"2000".equals(object.getString("ACCOUNT_TYPE_9")) && object.getString("ACCOUNT_TYPE_9").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_9");
        }
        if (!"2004".equals(object.getString("ACCOUNT_TYPE_10"))
                && !"2000".equals(object.getString("ACCOUNT_TYPE_10")) && object.getString("ACCOUNT_TYPE_10").startsWith("2")) {
            totalCharge += GIUtils.coalesceDouble(object, 0.0, "CHARGE_AMOUNT_10");
        }
        return (totalCharge * -1);
    }

}
