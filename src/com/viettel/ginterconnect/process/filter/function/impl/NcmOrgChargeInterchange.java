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
public class NcmOrgChargeInterchange implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //COALESCE(CAST(OLD_BALANCE as double),0)-COALESCE(CAST( NEW_BALANCE as double),0)
        return (GIUtils.coalesceDouble(object.get("OLD_BALANCE"), 0) 
                - GIUtils.coalesceDouble(object.get("NEW_BALANCE"), 0));
    }
    
}
