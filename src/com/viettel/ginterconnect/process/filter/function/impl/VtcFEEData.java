/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.util.GIUtils;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author viettq
 */
public class VtcFEEData implements IFunction{
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
		List<Integer> lst = Arrays.asList(4500, 4560, 4561);
		
		if (object.containsProperty("ACCOUNT_TYPE1") && object.get("ACCOUNT_TYPE1") != null) {
			String acc1 = object.get("CHARGE_AMOUNT1").toString();
			if(lst.contains(Integer.valueOf(acc1)))
				coalesceCdr(object, "CHARGE_AMOUNT1");
		}
		if (object.containsProperty("ACCOUNT_TYPE2") && object.get("ACCOUNT_TYPE2") != null) {
			String acc1 = object.get("CHARGE_AMOUNT2").toString();
			if(lst.contains(Integer.valueOf(acc1)))
				coalesceCdr(object, "CHARGE_AMOUNT2");
		}
		if (object.containsProperty("ACCOUNT_TYPE3") && object.get("ACCOUNT_TYPE3") != null) {
			String acc1 = object.get("CHARGE_AMOUNT3").toString();
			if(lst.contains(Integer.valueOf(acc1)))
				coalesceCdr(object, "CHARGE_AMOUNT3");
		}
		if (object.containsProperty("ACCOUNT_TYPE4") && object.get("ACCOUNT_TYPE4") != null) {
			String acc1 = object.get("CHARGE_AMOUNT4").toString();
			if(lst.contains(Integer.valueOf(acc1)))
				coalesceCdr(object, "CHARGE_AMOUNT4");
		}
		return Double.valueOf(object.getString("CHARGE_AMOUNT_1", "0"))
				+ Double.valueOf(object.getString("CHARGE_AMOUNT_2", "0"))
				+ Double.valueOf(object.getString("CHARGE_AMOUNT_3", "0"))
				+ Double.valueOf(object.getString("CHARGE_AMOUNT_4", "0"));
	}
}
