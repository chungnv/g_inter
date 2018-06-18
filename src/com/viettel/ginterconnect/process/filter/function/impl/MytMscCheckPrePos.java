/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.exception.FilterException;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class MytMscCheckPrePos implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (fieldParams == null) {
            throw new FilterException("MytMscCheckPrePos miss fieldParam", FilterException._FAIL);
        }
        if (object.get(fieldParams) == null) {
            return "PRE";
        }
        ArrayList<String> posArr = new ArrayList<String>(); //"6", "3", "00000030"
        posArr.add("6");
        posArr.add("3");
        posArr.add("00000030");
        String fieldContent = object.get(fieldParams).toString();
        if (posArr.contains(fieldContent)) {
            return "POS";
        }
        return "PRE";
    }
}
