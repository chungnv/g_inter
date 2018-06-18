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
public class VtcCdrIncremental implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //REVENUE_DATE, ISDN, <Content CDR>
        HashMap<Object, HashMap<Object, Map<Object, Object>>> mapRevenue = (HashMap<Object, HashMap<Object, Map<Object, Object>>>) filterCache;
        Map<Object, Object> mapRevDay = null;
//        if (object.containsProperty("BTS_CODE") && object.get("BTS_CODE") != null
        if (object.containsProperty("HOME_BTS_CODE")
                && object.containsProperty("ISDN") && object.get("ISDN") != null) {
            String btsCode = getString(object, "HOME_BTS_CODE");
            String isdn = object.get("ISDN").toString();
            String currentRev = (object.get("ORG_CHARGE") == null || "".equals(object.get("ORG_CHARGE").toString())) ? "0" : object.get("ORG_CHARGE").toString();
            String revenueDate = (new SimpleDateFormat("yyyyMMdd")).format((Date) object.get("CHARGING_TIME"));
//            String lastRev = currentRev;
            switch (params) {
                case "CALL":
                    return callIncremental(object, btsCode, mapRevenue, isdn, revenueDate, currentRev);
                case "SMS":
                    return smsIncremental(object, btsCode, mapRevenue, isdn, revenueDate, currentRev);
                case "DATA":
                    return dataIncremental(object, btsCode, mapRevenue, isdn, revenueDate, currentRev);
                case "VAS":
                    return vasIncremental(object, btsCode, mapRevenue, isdn, revenueDate, currentRev);
                default:
                    return normalIncremental(object, btsCode, mapRevenue, isdn, revenueDate, currentRev);
            }
//            return lastRev;
        }
        return null;
    }

    private String callIncremental(CdrObject object, String btsCode, HashMap<Object, HashMap<Object, Map<Object, Object>>> mapRevenue, String isdn, String revenueDate, String currentRev) {
        Map<Object, Object> mapRevDay = null;
        String lastRev = currentRev;
        int numberOfCall = 1;
        Double callRevenue = Double.valueOf(object.get("ORG_CHARGE").toString());
//        String cdrDate = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT).format((Date) object.get("CHARGING_TIME")));
        if (mapRevenue.containsKey(isdn)) {
            if (isNullKey(revenueDate, mapRevenue.get(isdn))) {
                mapRevDay = new HashMap<>();
                setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
                mapRevDay.put("NUMBER_OF_CALL", numberOfCall + "");
                mapRevDay.put("CALL_REVENUE", callRevenue + "");
                mapRevenue.get(isdn).put(revenueDate, mapRevDay);
            } else {
                mapRevDay = mapRevenue.get(isdn).get(revenueDate);
//                String beforeRev = (String) mapRevDay.get("REVENUE") == null ? "0" : mapRevDay.get("REVENUE").toString();
                String beforeRev = (String) ((mapRevDay.get("REVENUE") == null || "".equals(mapRevDay.get("REVENUE").toString()))
                        ? "0" : mapRevDay.get("REVENUE"));
                int totalCallDaily = Integer.valueOf(mapRevDay.get("NUMBER_OF_CALL").toString()) + 1;
                lastRev = String.valueOf(Double.valueOf(currentRev) + Double.valueOf(beforeRev));
                Double callRevDaily = Double.valueOf(mapRevDay.get("CALL_REVENUE").toString()) + callRevenue;
                setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
                mapRevDay.put("NUMBER_OF_CALL", totalCallDaily + "");
                mapRevDay.put("CALL_REVENUE", callRevDaily + "");
            }
        } else {
            HashMap<Object, Map<Object, Object>> mapRevenueDate = new HashMap<>();
            mapRevDay = new HashMap<>();
            setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
            mapRevDay.put("NUMBER_OF_CALL", numberOfCall + "");
            mapRevDay.put("CALL_REVENUE", callRevenue + "");
            mapRevenueDate.put(revenueDate, mapRevDay);
            mapRevenue.put(isdn, mapRevenueDate);
        }
        return lastRev;
    }

    private String smsIncremental(CdrObject object, String btsCode, HashMap<Object, HashMap<Object, Map<Object, Object>>> mapRevenue, String isdn, String revenueDate, String currentRev) {
        Map<Object, Object> mapRevDay = null;
        String lastRev = currentRev;
        int numberOfCall = 1;
        Double callRevenue = Double.valueOf(object.get("ORG_CHARGE").toString());
//        String cdrDate = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT).format((Date) object.get("CHARGING_TIME")));
        if (mapRevenue.containsKey(isdn)) {
            if (isNullKey(revenueDate, mapRevenue.get(isdn))) {
                mapRevDay = new HashMap<>();
                setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
                mapRevDay.put("NUMBER_OF_SMS", numberOfCall + "");
                mapRevDay.put("SMS_REVENUE", callRevenue + "");
                mapRevenue.get(isdn).put(revenueDate, mapRevDay);
            } else {
                mapRevDay = mapRevenue.get(isdn).get(revenueDate);
//                String beforeRev = (String) mapRevDay.get("REVENUE") == null ? "0" : mapRevDay.get("REVENUE").toString();
                String beforeRev = (String) ((mapRevDay.get("REVENUE") == null || "".equals(mapRevDay.get("REVENUE").toString()))
                        ? "0" : mapRevDay.get("REVENUE"));
                int totalCallDaily = Integer.valueOf(mapRevDay.get("NUMBER_OF_SMS").toString()) + 1;
                lastRev = String.valueOf(Double.valueOf(currentRev) + Double.valueOf(beforeRev));
                Double callRevDaily = Double.valueOf(mapRevDay.get("SMS_REVENUE").toString()) + callRevenue;
                setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
                mapRevDay.put("NUMBER_OF_SMS", totalCallDaily + "");
                mapRevDay.put("SMS_REVENUE", callRevDaily + "");
            }
        } else {
            HashMap<Object, Map<Object, Object>> mapRevenueDate = new HashMap<>();
            mapRevDay = new HashMap<>();
            setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
            mapRevDay.put("NUMBER_OF_SMS", numberOfCall + "");
            mapRevDay.put("SMS_REVENUE", callRevenue + "");
            mapRevenueDate.put(revenueDate, mapRevDay);
            mapRevenue.put(isdn, mapRevenueDate);
        }
        return lastRev;
    }

    private String dataIncremental(CdrObject object, String btsCode, HashMap<Object, HashMap<Object, Map<Object, Object>>> mapRevenue, String isdn, String revenueDate, String currentRev) {
        Map<Object, Object> mapRevDay = null;
        String lastRev = currentRev;
        int numberOfCall = 1;
        Double callRevenue = Double.valueOf(object.get("ORG_CHARGE").toString());
//        String cdrDate = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT).format((Date) object.get("CHARGING_TIME")));
        if (mapRevenue.containsKey(isdn)) {
            if (isNullKey(revenueDate, mapRevenue.get(isdn))) {
                mapRevDay = new HashMap<>();
                setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
                mapRevDay.put("NUMBER_OF_DATA", numberOfCall + "");
                mapRevDay.put("DATA_REVENUE", callRevenue + "");
                mapRevenue.get(isdn).put(revenueDate, mapRevDay);
            } else {
                mapRevDay = mapRevenue.get(isdn).get(revenueDate);
                String beforeRev = (String) ((mapRevDay.get("REVENUE") == null || "".equals(mapRevDay.get("REVENUE").toString()))
                        ? "0" : mapRevDay.get("REVENUE"));
                int totalCallDaily = Integer.valueOf(mapRevDay.get("NUMBER_OF_DATA").toString()) + 1;
                lastRev = String.valueOf(Double.valueOf(currentRev) + Double.valueOf(beforeRev));
                Double callRevDaily = Double.valueOf(mapRevDay.get("DATA_REVENUE").toString()) + callRevenue;
                setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
                mapRevDay.put("NUMBER_OF_DATA", totalCallDaily + "");
                mapRevDay.put("DATA_REVENUE", callRevDaily + "");
            }
        } else {
            HashMap<Object, Map<Object, Object>> mapRevenueDate = new HashMap<>();
            mapRevDay = new HashMap<>();
            setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
            mapRevDay.put("NUMBER_OF_DATA", numberOfCall + "");
            mapRevDay.put("DATA_REVENUE", callRevenue + "");
            mapRevenueDate.put(revenueDate, mapRevDay);
            mapRevenue.put(isdn, mapRevenueDate);
        }
        return lastRev;
    }

    private String vasIncremental(CdrObject object, String btsCode, HashMap<Object, HashMap<Object, Map<Object, Object>>> mapRevenue, String isdn, String revenueDate, String currentRev) {
        Map<Object, Object> mapRevDay = null;
        String lastRev = currentRev;
        int numberOfCall = 1;
        Double callRevenue = Double.valueOf(object.get("ORG_CHARGE").toString());
//        String cdrDate = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT).format((Date) object.get("CHARGING_TIME")));
        if (mapRevenue.containsKey(isdn)) {
            if (isNullKey(revenueDate, mapRevenue.get(isdn))) {
                mapRevDay = new HashMap<>();
                setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
                mapRevDay.put("NUMBER_OF_VAS", numberOfCall + "");
                mapRevDay.put("VAS_REVENUE", callRevenue + "");
                mapRevenue.get(isdn).put(revenueDate, mapRevDay);
            } else {
                mapRevDay = mapRevenue.get(isdn).get(revenueDate);
                String beforeRev = (String) ((mapRevDay.get("REVENUE") == null || "".equals(mapRevDay.get("REVENUE").toString()))
                        ? "0" : mapRevDay.get("REVENUE"));
                int totalCallDaily = Integer.valueOf(mapRevDay.get("NUMBER_OF_VAS").toString()) + 1;
                lastRev = String.valueOf(Double.valueOf(currentRev) + Double.valueOf(beforeRev));
                Double callRevDaily = Double.valueOf(mapRevDay.get("VAS_REVENUE").toString()) + callRevenue;
                setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
                mapRevDay.put("NUMBER_OF_VAS", totalCallDaily + "");
                mapRevDay.put("VAS_REVENUE", callRevDaily + "");
            }
        } else {
            HashMap<Object, Map<Object, Object>> mapRevenueDate = new HashMap<>();
            mapRevDay = new HashMap<>();
            setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
            mapRevDay.put("NUMBER_OF_VAS", numberOfCall + "");
            mapRevDay.put("VAS_REVENUE", callRevenue + "");
            mapRevenueDate.put(revenueDate, mapRevDay);
            mapRevenue.put(isdn, mapRevenueDate);
        }
        return lastRev;
    }

    private String normalIncremental(CdrObject object, String btsCode, HashMap<Object, HashMap<Object, Map<Object, Object>>> mapRevenue, String isdn, String revenueDate, String currentRev) {
        Map<Object, Object> mapRevDay = null;
        String lastRev = currentRev;
//        int numberOfCall = 1;
//        Double callRevenue = Double.valueOf(object.get("ORG_CHARGE").toString());
//        String cdrDate = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT).format((Date) object.get("CHARGING_TIME")));
        if (mapRevenue.containsKey(isdn)) {
            if (isNullKey(revenueDate, mapRevenue.get(isdn))) {
                mapRevDay = new HashMap<>();
                setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
//                mapRevDay.put("NUMBER_OF_DATA", numberOfCall + "");
//                mapRevDay.put("DATA_REVENUE", callRevenue + "");
                mapRevenue.get(isdn).put(revenueDate, mapRevDay);
            } else {
                mapRevDay = mapRevenue.get(isdn).get(revenueDate);
//                String beforeRev = (String) mapRevDay.get("REVENUE");
                String beforeRev = (String) ((mapRevDay.get("REVENUE") == null || "".equals(mapRevDay.get("REVENUE").toString()))
                        ? "0" : mapRevDay.get("REVENUE"));
//                int totalCallDaily = Integer.valueOf(mapRevDay.get("NUMBER_OF_DATA").toString()) + 1;
                lastRev = String.valueOf(Double.valueOf(currentRev) + Double.valueOf(beforeRev));
//                Double callRevDaily = Double.valueOf(mapRevDay.get("DATA_REVENUE").toString()) + callRevenue;
                setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
//                mapRevDay.put("NUMBER_OF_DATA", totalCallDaily + "");
//                mapRevDay.put("DATA_REVENUE", callRevDaily + "");
            }
        } else {
            HashMap<Object, Map<Object, Object>> mapRevenueDate = new HashMap<>();
            mapRevDay = new HashMap<>();
            setBasicInfor(mapRevDay, isdn, revenueDate, lastRev, btsCode, object);
//            mapRevDay.put("NUMBER_OF_DATA", numberOfCall + "");
//            mapRevDay.put("DATA_REVENUE", callRevenue + "");
            mapRevenueDate.put(revenueDate, mapRevDay);
            mapRevenue.put(isdn, mapRevenueDate);
        }
        return lastRev;
    }

    private void setBasicInfor(Map<Object, Object> mapRevDay, String isdn, String revenueDate, String currentRev, String btsCode, CdrObject object) {
        mapRevDay.put("ISDN", isdn);
//        mapRevDay.put("REVENUE_DATE", revenueDate);
//        mapRevDay.put("REVENUE", currentRev);
        mapRevDay.put("BTS_CODE", btsCode);
        mapRevDay.put("CENTER_CODE", getString(object, "HOME_CENTER_CODE"));
        mapRevDay.put("PROVINCE_CODE", getString(object, "HOME_PROVINCE_CODE"));
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
