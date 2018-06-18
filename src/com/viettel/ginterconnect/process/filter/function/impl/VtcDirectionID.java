package com.viettel.ginterconnect.process.filter.function.impl;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author viettq
 */
public class VtcDirectionID implements IFunction {

	final String INTERNAL = "1";
	final String DOMESTIC = "2";
	final String INTERNATIONAL = "3";
	final String VAS = "4";
	final String ROAMING = "5";

	@Override
	public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
		List<Integer> subCalls1 = Arrays.asList(5005, 6255, 7021, 7022, 7149);
		List<Integer> subCalls2 = Arrays.asList(5006, 6256, 5010);
		List<Integer> subCalls3 = Arrays.asList(5009, 7105);
		List<Integer> subCalls4 = Arrays.asList(62, 63, 64, 7023);
		List<Integer> subCalls5 = Arrays.asList(5007, 5008, 7145);

		if (object.containsProperty("EVENT_TYPE") && object.get("EVENT_TYPE") != null) {
			String eventType = object.get("EVENT_TYPE").toString();
			if (subCalls1.contains(Integer.valueOf(eventType)))
				return INTERNAL;
			if (subCalls2.contains(Integer.valueOf(eventType)))
				return DOMESTIC;
			if (subCalls3.contains(Integer.valueOf(eventType)))
				return INTERNATIONAL;
			if (subCalls4.contains(Integer.valueOf(eventType)))
				return VAS;
			if (subCalls5.contains(Integer.valueOf(eventType)))
				return ROAMING;
		}
		return -1;
	}
}
