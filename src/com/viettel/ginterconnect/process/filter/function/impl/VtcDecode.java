/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author viettq
 */
public class VtcDecode implements IFunction {
//	decode(sms_id in ( 5185,5334),'POS', 'PRE')
	
	@Override
	public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {	
		List<Integer> lst = Arrays.asList(5185, 5334);
		if (object.containsProperty("SMS_ID") && object.get("SMS_ID") != null) {
			String smsId = object.get("SMS_ID").toString();
			if (lst.contains(smsId))
				return object.get("POS");
		}
		return object.get("PRE");
	}
	
}
