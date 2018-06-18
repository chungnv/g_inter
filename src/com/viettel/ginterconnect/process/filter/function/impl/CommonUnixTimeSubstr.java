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
public class CommonUnixTimeSubstr implements IFunction {

	@Override
	public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
		if (fieldParams == null || fieldParams.isEmpty()) {
			throw new StandardException("ERROR! Field param function is null");
		}
		if (params == null || params.isEmpty()) {
			throw new StandardException("ERROR! Param function is null");
		}
		String[] fieldParam = fieldParams.split(",");
		String rowName = fieldParam[0];
		String typeDate = fieldParam[1];
		String typeConvert = fieldParam[2];

		String[] param = params.split(",");
		int start = Integer.valueOf(param[0]);
		int end = -1;
		if (param.length > 1) {
			end = Integer.valueOf(param[1]);
		}

		if (object.containsProperty(rowName) && object.get(rowName) != null) {
			SimpleDateFormat formatter = new SimpleDateFormat(typeConvert);
			try {
				if (end > -1) {
					return formatter.format((Date) object.get(rowName)).toString().substring(start -1, (start + end)-1);
				}
				else {
					return formatter.format((Date) object.get(rowName)).toString().substring(start -1);
				}
			}
			catch (Exception ex) {
				Exceptions.printStackTrace(ex);
			}
		}
		return null;
	}
}
