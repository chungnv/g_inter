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
public class VTBStandardCommon implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        String calling = object.getString("CALLING_NOT_STANDARDIZE");
        String called = object.getString("CALLED_NOT_STANDARDIZE");
        if (called.startsWith("00")) {
            called = called.substring(2);
        }
        if (called.startsWith("0")) {
            called = called.substring(1);
        }
        if (called.startsWith("257")) {
            called = called.substring(3);
        }
        if (calling.startsWith("00")) {
            calling = calling.substring(2);
        }
        if (calling.startsWith("0")) {
            calling = calling.substring(1);
        }
        if (calling.startsWith("257")) {
            calling = calling.substring(3);
        }
        object.set("CALLING", calling);
        object.set("CALLED", called);
        return object;
    }

}
