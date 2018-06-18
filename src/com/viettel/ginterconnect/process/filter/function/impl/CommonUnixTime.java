/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.process.exception.StandardException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author viettq
 */
public class CommonUnixTime implements IFunction {

	@Override
	public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
		if (fieldParams == null || fieldParams.isEmpty()) {
			throw new StandardException("ERROR! Field param function is null");
		}
		String[] param = fieldParams.split(",");
		String rowName = param[0];
		String typeDate = param[1];
		String typeConvert = param[2];

		if (object.containsProperty(rowName) && object.get(rowName) != null) {
			Date date = (Date) object.get(rowName);
			SimpleDateFormat formatter = new SimpleDateFormat(typeConvert);
			try {
				String s = formatter.format((Date)object.get(rowName));
				return s;
			}
			catch (Exception ex) {
				Exceptions.printStackTrace(ex);
			}
		}
		return null;
	}


	public static void main(String[] args) {
		CommonUnixTime func = new CommonUnixTime();
		CdrObject object = new CdrObject();
		object.set("DATE", new Date());
		System.out.println(func.execute(object, null, null, "DATE,yyyy-MM-dd HH:mm:ss,yyyyMMdd", null, null));
	}
}
