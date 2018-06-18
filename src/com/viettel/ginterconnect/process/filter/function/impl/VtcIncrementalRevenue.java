/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class VtcIncrementalRevenue implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        HashMap<Object, Map<Object, Object>> mapRevenue = (HashMap<Object, Map<Object, Object>>) filterCache;
        Map<Object, Object> mapRevDay = null;
//        if (object.containsProperty("BTS_CODE") && object.get("BTS_CODE") != null
        if (object.containsProperty("BTS_CODE")
                && object.containsProperty("ISDN") && object.get("ISDN") != null) {
            String btsCode = getString(object, "BTS_CODE");
//            if ("".equals(btsCode) || btsCode.isEmpty()) {
//                return null;
//            }

            String isdn = object.get("ISDN").toString();
            String currentRev = object.get("ORG_CHARGE") == null ? "0" : object.get("ORG_CHARGE").toString();
            String revenueDate = (new SimpleDateFormat("yyyyMMdd")).format((Date) object.get("CHARGING_TIME"));
            String lastRev = currentRev;
            if (isNullKey(isdn, mapRevenue)) {
                mapRevDay = new HashMap<>();
                mapRevDay.put("ISDN", isdn);
                mapRevDay.put("REVENUE_DATE", revenueDate);
                mapRevDay.put("REVENUE", currentRev);
                mapRevDay.put("BTS_CODE", btsCode);
                mapRevDay.put("CENTER_CODE", getString(object, "CENTER_CODE"));
                mapRevDay.put("PROVINCE_CODE", getString(object, "PROVINCE_CODE"));
                mapRevenue.put(isdn, mapRevDay);
            } else {
                mapRevDay = mapRevenue.get(isdn);
                String beforeRev = (String) mapRevDay.get("REVENUE");
                mapRevDay.put("ISDN", isdn);
                mapRevDay.put("REVENUE_DATE", revenueDate);
                lastRev = String.valueOf(Double.valueOf(currentRev) + Double.valueOf(beforeRev));
                mapRevDay.put("REVENUE", lastRev);
                mapRevDay.put("BTS_CODE", btsCode);
                mapRevDay.put("CENTER_CODE", getString(object, "CENTER_CODE"));
                mapRevDay.put("PROVINCE_CODE", getString(object, "PROVINCE_CODE"));
//                mapRevenue.put(isdn, mapRevDay);
            }
            return lastRev;
        }
        return null;
    }

    private String getString(CdrObject object, String field) {
        return object.get(field) == null ? "" : object.get(field).toString();
    }

    private boolean isNullKey(String key, HashMap<Object, Map<Object, Object>> map) {
        Object flag = map.get(key);
        if (flag != null) {
            return false;
        } else {
            return true;
        }
    }
}
