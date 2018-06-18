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
public class VtcClritbl2003 implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        // (case when Account_Type1 in('2003') then cast(Cleaned_Amount1 as double) else 0.0 end)
        //+(case when Account_Type2 in('2003') then cast(Cleaned_Amount2 as double) else 0.0 end)
        //+(case when Account_Type3 in('2003') then cast(Cleaned_Amount3 as double) else 0.0 end)
        //+(case when Account_Type4 in('2003') then cast(Cleaned_Amount4 as double) else 0.0 end)

        Double val = 0.0;
        if (object.get("Account_Type1") != null && "2003".equals(object.get("Account_Type1").toString())) {
            val += GIUtils.coalesceDouble(object.get("Cleaned_Amount1"), 0);
        }
        if (object.get("Account_Type2") != null && "2003".equals(object.get("Account_Type2").toString())) {
            val += GIUtils.coalesceDouble(object.get("Cleaned_Amount2"), 0);
        }
        if (object.get("Account_Type3") != null && "2003".equals(object.get("Account_Type3").toString())) {
            val += GIUtils.coalesceDouble(object.get("Cleaned_Amount3"), 0);
        }
        if (object.get("Account_Type4") != null && "2003".equals(object.get("Account_Type4").toString())) {
            val += GIUtils.coalesceDouble(object.get("Cleaned_Amount4"), 0);
        }
        return val;
    }
    
}
