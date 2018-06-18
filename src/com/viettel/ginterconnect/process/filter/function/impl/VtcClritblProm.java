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
public class VtcClritblProm implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //(case when Account_Type1 not  in('2004','2000') and  cast(Account_Type1 as int)<4000 then cast(Cleaned_Amount1 as double) else 0.0 end)
        //+(case when Account_Type2 not  in('2004','2000') and  cast(Account_Type2 as int)<4000 then cast(Cleaned_Amount2 as double) else 0.0 end)
        //+(case when Account_Type3 not  in('2004','2000') and  cast(Account_Type3 as int)<4000 then cast(Cleaned_Amount3 as double) else 0.0 end)
        //+(case when Account_Type4 not  in('2004','2000') and  cast(Account_Type4 as int)<4000 then cast(Cleaned_Amount4 as double) else 0.0 end)
        Double val = 0.0;
        if (object.get("Account_Type1") != null && !"2000".equals(object.get("Account_Type1").toString()) 
                && !"2004".equals(object.get("Account_Type1").toString()) && (Integer.valueOf(object.get("Account_Type1").toString()) < 4000)) {
            val += GIUtils.coalesceDouble(object.get("Cleaned_Amount1"), 0);
        }
        if (object.get("Account_Type2") != null && !"2000".equals(object.get("Account_Type2").toString()) 
                && !"2004".equals(object.get("Account_Type2").toString()) && (Integer.valueOf(object.get("Account_Type2").toString()) < 4000)) {
            val += GIUtils.coalesceDouble(object.get("Cleaned_Amount2"), 0);
        }
        if (object.get("Account_Type3") != null && !"2000".equals(object.get("Account_Type3").toString()) 
                && !"2004".equals(object.get("Account_Type3").toString()) && (Integer.valueOf(object.get("Account_Type3").toString()) < 4000)) {
            val += GIUtils.coalesceDouble(object.get("Cleaned_Amount3"), 0);
        }
        if (object.get("Account_Type4") != null && !"2000".equals(object.get("Account_Type4").toString()) 
                && !"2004".equals(object.get("Account_Type4").toString()) && (Integer.valueOf(object.get("Account_Type4").toString()) < 4000)) {
            val += GIUtils.coalesceDouble(object.get("Cleaned_Amount4"), 0);
        }
        return val;
    }
    
}
