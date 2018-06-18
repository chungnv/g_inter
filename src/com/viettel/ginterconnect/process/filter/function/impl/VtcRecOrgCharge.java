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
public class VtcRecOrgCharge implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //COALESCE(CAST(CHARGE_FROM_PREPAID as double),0) 
//+  case when ACCOUNT_TYPE_1 = '2004' then COALESCE(CAST(CHARGE_AMOUNT_1 as double),0.0) else 0.0 end 
// + case when ACCOUNT_TYPE_2 = '2004' then COALESCE(CAST(CHARGE_AMOUNT_2 as double),0.0) else 0.0 end
// + case when ACCOUNT_TYPE_3 = '2004' then COALESCE(CAST(CHARGE_AMOUNT_3 as double),0.0) else 0.0 end
// + case when ACCOUNT_TYPE_4 = '2004' then COALESCE(CAST(CHARGE_AMOUNT_4 as double),0.0) else 0.0 end
// + case when ACCOUNT_TYPE_5 = '2004' then COALESCE(CAST(CHARGE_AMOUNT_5 as double),0.0) else 0.0 end
// + case when ACCOUNT_TYPE_6 = '2004' then COALESCE(CAST(CHARGE_AMOUNT_6 as double),0.0) else 0.0 end
// + case when ACCOUNT_TYPE_7 = '2004' then COALESCE(CAST(CHARGE_AMOUNT_7 as double),0.0) else 0.0 end
// + case when ACCOUNT_TYPE_8 = '2004' then COALESCE(CAST(CHARGE_AMOUNT_8 as double),0.0) else 0.0 end
// + case when ACCOUNT_TYPE_9 = '2004' then COALESCE(CAST(CHARGE_AMOUNT_9 as double),0.0) else 0.0 end
// + case when ACCOUNT_TYPE_10 = '2004' then COALESCE(CAST(CHARGE_AMOUNT_10 as double),0.0) else 0.0 end
        double orgCharge = 0;
        orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_FROM_PREPAID"), 0);
        if ("2004".equals(object.get("ACCOUNT_TYPE_1").toString())) {
            orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_AMOUNT_1"), 0);
        }
        if ("2004".equals(object.get("ACCOUNT_TYPE_2").toString())) {
            orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_AMOUNT_2"), 0);
        }
        if ("2004".equals(object.get("ACCOUNT_TYPE_3").toString())) {
            orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_AMOUNT_3"), 0);
        }
        if ("2004".equals(object.get("ACCOUNT_TYPE_4").toString())) {
            orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_AMOUNT_4"), 0);
        }
        if ("2004".equals(object.get("ACCOUNT_TYPE_5").toString())) {
            orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_AMOUNT_5"), 0);
        }
        if ("2004".equals(object.get("ACCOUNT_TYPE_6").toString())) {
            orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_AMOUNT_6"), 0);
        }
        if ("2004".equals(object.get("ACCOUNT_TYPE_7").toString())) {
            orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_AMOUNT_7"), 0);
        }
        if ("2004".equals(object.get("ACCOUNT_TYPE_8").toString())) {
            orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_AMOUNT_8"), 0);
        }
        if ("2004".equals(object.get("ACCOUNT_TYPE_9").toString())) {
            orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_AMOUNT_9"), 0);
        }
        if ("2004".equals(object.get("ACCOUNT_TYPE_10").toString())) {
            orgCharge += GIUtils.coalesceDouble(object.get("CHARGE_AMOUNT_10"), 0);
        }
        return orgCharge;
    }

}
