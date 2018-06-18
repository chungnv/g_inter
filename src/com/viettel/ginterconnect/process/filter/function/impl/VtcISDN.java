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
 * @author viettq
 */
public class VtcISDN implements IFunction {

	@Override
	public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
		if (object.containsProperty("CALLING_NBR") && object.get("CALLING_NBR") != null) {
			String calling = object.get("CALLING_NBR").toString();
			if (calling.length() < 10)
				return object.get("CALLING_NBR");
			else
				return calling.substring(3);
		}
		return null;
	}

}
