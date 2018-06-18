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
public class MytStandardCommon implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        try {
            String calling = object.getString("CALLING_NOT_STANDARDIZE");
            String called = object.getString("CALLED_NOT_STANDARDIZE");
            if (called.startsWith("95") && called.length() > 8) {
                called = called.substring(2);
            }
            if (called.startsWith("0095")) {
                called = called.substring(4);
            }
            if (called.startsWith("+95")) {
                called = called.substring(3);
            }
            if (called.startsWith("00")) {
                called = called.substring(2);
            }
            if (called.startsWith("0")) {
                called = called.substring(1);
            }
//        if (called.startsWith("95")) {
//            called = called.substring(2);
//        }
            if (calling.startsWith("95") && calling.length() > 8) {
                calling = calling.substring(2);
            }
            if (calling.startsWith("0095")) {
                calling = calling.substring(4);
            }
            if (calling.startsWith("+95")) {
                calling = calling.substring(3);
            }
            if (calling.startsWith("00")) {
                calling = calling.substring(2);
            }
            if (calling.startsWith("0")) {
                calling = calling.substring(1);
            }
//        if (calling.startsWith("95")) {
//            calling = calling.substring(2);
//        }
            object.set("CALLING", calling);
            object.set("CALLED", called);
            return object;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
