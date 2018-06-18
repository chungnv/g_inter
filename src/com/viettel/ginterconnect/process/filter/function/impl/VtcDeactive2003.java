/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.util.GIUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author viettq
 */
public class VtcDeactive2003 implements IFunction {

	private GIUtils utils;

	private CdrObject coalesceCdr(CdrObject object, String columnName) {

		Object o = utils.coalesce(object.get(columnName));
		if (o != null) {
			object.set(columnName, o);
		}
		else {
			object.set(columnName, 0);
		}
		return object;
	}

	@Override
	public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
		if ((object.containsProperty("ACCOUNT_TYPE1") && object.get("ACCOUNT_TYPE1") != null)) {
			String acountType = object.get("ACCOUNT_TYPE1").toString();
			if ("2003".equals(acountType)) {
				coalesceCdr(object, "CLEANED_AMOUNT1");
				return Double.valueOf(object.get("CLEANED_AMOUNT1").toString());
			}
		}
		return 0;
	}

}
