/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import org.apache.log4j.Logger;
import org.hibernate.tool.hbm2x.StringUtils;

/**
 *
 * @author ubuntu
 */
public class CommonStartWiths implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        String input = object.getString(inputField);
        if (StringUtils.isEmpty(input)) {
            return false;
        }
        if (params == null || "".equals(params.trim())) {
            return true;
        }
        return input.startsWith(params.trim());
    }
    
}
