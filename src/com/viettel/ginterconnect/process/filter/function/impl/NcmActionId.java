/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class NcmActionId implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //action_id in('0','3','4')
        List<String> lstCheckActionId = new ArrayList<String>();
        lstCheckActionId.add("0");
        lstCheckActionId.add("3");
        lstCheckActionId.add("4");
        String actionId = object.get("ACTION_ID") == null ? null : object.get("ACTION_ID").toString();
        if (actionId != null && lstCheckActionId.contains(actionId)) {
            return true;
        }
        return false;
    }
    
}
