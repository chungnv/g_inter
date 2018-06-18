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
public class VtcBalancePromotion implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //COALESCE(CAST(a.SUBACCOUNT_2001 as Double),0) + COALESCE(CAST(a.SUBACCOUNT_2100 as Double),0)+ 
        //COALESCE(CAST(a.SUBACCOUNT_2113 as Double),0)+ COALESCE(CAST(a.SUBACCOUNT_2002 as Double),0) 
        //+ COALESCE(CAST(a.SUBACCOUNT_2500 as Double),0)+ COALESCE(CAST(a.SUBACCOUNT_2400 as Double),0)+ 
        //COALESCE(CAST(a.SUBACCOUNT_2501 as Double),0) + COALESCE(CAST(a.SUBACCOUNT_2502 as Double),0)+
        //COALESCE(CAST(a.SUBACCOUNT_2003 as Double),0) + COALESCE(CAST(a.SUBACCOUNT_2004 as Double),0) + 
        //COALESCE(CAST(a.SUBACCOUNT_2505 as Double),0)
        Double balancePromotion = GIUtils.coalesceDouble(object, 0.0, "SUBACCOUNT_2001",
                                    "SUBACCOUNT_2100", "SUBACCOUNT_2113", "SUBACCOUNT_2002",
                                    "SUBACCOUNT_2500", "SUBACCOUNT_2400", "SUBACCOUNT_2501",
                                    "SUBACCOUNT_2502", "SUBACCOUNT_2003", "SUBACCOUNT_2004",
                                    "SUBACCOUNT_2505");
        return balancePromotion;
    }
    
}
