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
public class VtbOrgCharge implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //sum(COALESCE(CAST(TK1_CHARGE as double),0))/100
        double c = GIUtils.coalesceDouble(object.get("TK1_CHARGE"), 0) / 100;;
        return GIUtils.coalesceDouble(object.get("TK1_CHARGE"), 0) / 100;
    }
    
}
