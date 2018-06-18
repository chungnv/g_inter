/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.exception.FilterException;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class CommonExtractDateFromString implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        String[] arrParam = params.split(",");
        if (object.get(fieldParams) == null) {
            return null;
        }
        try {
            Date checkedDate = (new SimpleDateFormat(arrParam[2])).
                    parse(object.get(fieldParams).toString().substring(Integer.valueOf(arrParam[0]),
                                    Integer.valueOf(arrParam[1])));
            return (new SimpleDateFormat(arrParam[3])).format(checkedDate);
        } catch (ParseException ex) {
            throw new FilterException("CommonExtractDateFromString parse exception", FilterException._CONFIG_FAIL);
        }
    }

    public static void main(String[] args) {
        String[] arrParam = "3,11,yyyyMMdd,yyMM".split(",");
        try {
            Date checkedDate = (new SimpleDateFormat(arrParam[2])).
                    parse("ABC20170910".substring(Integer.valueOf(arrParam[0]),
                                    Integer.valueOf(arrParam[1])));
            System.out.println((new SimpleDateFormat(arrParam[3])).format(checkedDate));
        } catch (ParseException ex) {
            throw new FilterException("CommonExtractDateFromString parse exception", FilterException._CONFIG_FAIL);
        }
    }
}
