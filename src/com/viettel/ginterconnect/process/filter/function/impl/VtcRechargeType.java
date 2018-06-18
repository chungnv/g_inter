/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class VtcRechargeType implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //case when recharge_type_id in('1','2','3','4','5')  then 'Card' when recharge_type_id='6' then 'Anypay' else 'Other' end
        if (object.get("recharge_type_id") == null) {
            return null;
        }
        if ("12345".contains(object.get("recharge_type_id").toString())) {
            return "Card";
        }
        if ("6".equals(object.get("recharge_type_id").toString())) {
            return "Anypay";
        }
        return "Other";
    }
    
}
