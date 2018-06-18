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
public class VcrOrgChargeVoice implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //VcrOrgChargeVoice
//        (NVL(TK1_CHARGE,0) 
//        + NVL(TK17_CHARGE,0) 
//        + NVL(TK37_CHARGE,0) 
//        + NVL(TK45_CHARGE,0))/1000
        return (GIUtils.coalesceDouble(object.get("TK1_CHARGE"), 0)
                + GIUtils.coalesceDouble(object.get("TK17_CHARGE"), 0)
                + GIUtils.coalesceDouble(object.get("TK37_CHARGE"), 0)
                + GIUtils.coalesceDouble(object.get("TK45_CHARGE"), 0)) / 1000;
    }

}
