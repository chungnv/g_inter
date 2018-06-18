/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class CommonInList implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        String field = object.getString(fieldParams);
        if (StringUtils.isEmpty(field)) {
            return false;
        }
        List<String> lstParams = Arrays.asList(params.split(","));
        if (lstParams.contains(field)) {
            return true;
        }
        return false;
    }
    
}
