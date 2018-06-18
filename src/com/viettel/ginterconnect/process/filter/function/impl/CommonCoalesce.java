/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.util.GIUtils;
import com.viettel.ginterconnect.process.exception.StandardException;
import org.apache.log4j.Logger;

/**
 *
 * @author viettq
 */
public class CommonCoalesce implements IFunction {

	private GIUtils utils;

	private CdrObject coalesceCdr(CdrObject object, String columnName) {
		Object o = utils.coalesce(object.get(columnName));
		if (o != null)
			object.set(columnName, o);
		else
			object.set(columnName, 0);
		return object;
	}
	@Override
	public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
		if (params == null || params.isEmpty()) {
			throw new StandardException("ERROR! Param function is null");
		}
		String[] param = params.split(",");
		String rowName1 = param[0];
		coalesceCdr(object, rowName1);
		Integer value = Integer.valueOf(object.get(rowName1).toString());
		for (int i = 1; i < param.length; i = i+2) {
			String operator = param[i].trim();
			String rowName2 = param[i + 1].trim();
			coalesceCdr(object, rowName2);
			if ("+".equals(operator))
				value += Integer.valueOf(object.get(rowName2).toString());
			if ("-".equals(operator))
				value -= Integer.valueOf(object.get(rowName2).toString());
		}
		return value;
	}
}
