/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.cached.StlDmsPrefixCache;
import com.viettel.ginterconnect.cached.ThreadCached;
import com.viettel.ginterconnect.cached.bean.CommonDmsPrefix;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.worker.util.standard.common.StlStandardPrefixNumber;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class STLStandardDmsPrefixCalled implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        Long isCalledNational = object.getLong("TRUNK_OUT_IS_NATIONAL");
        if (isCalledNational == 1) {
            return object;
        }
        try {
            StlDmsPrefixCache stlDmsPrefixCache = (StlDmsPrefixCache)ThreadCached.initCache("StlDmsPrefixCache", params, "");

            Map<String, Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>>> map = stlDmsPrefixCache.getCached();
            //validate
            int recordType = Integer.valueOf(object.get("RECORD_TYPE").toString());
            Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>> freePrefix = map.get("FREE");
            Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>> vasPrefix = map.get("VAS");
            String called = object.getString(fieldParams);
            CommonDmsPrefix calledPrefix = StlStandardPrefixNumber.getDomesticPrefixNumber(called, recordType, freePrefix);
            if (calledPrefix != null) {
                object.set("MIDDLE_SERVICE", calledPrefix);
                object.set("MIDDLE_SERVICE_ID", calledPrefix.getPrefixId().toString());
            } else {
                calledPrefix = StlStandardPrefixNumber.getDomesticPrefixNumber(object.get(fieldParams).toString(), recordType, vasPrefix);
                if (calledPrefix != null) {
                    object.set("MIDDLE_SERVICE", calledPrefix);
                    object.set("MIDDLE_SERVICE_ID", calledPrefix.getPrefixId().toString());
                } else {
                    Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>> connectPrefix = map.get("CONNECT");
                    calledPrefix = StlStandardPrefixNumber.getDomesticPrefixNumber(object.get(fieldParams).toString(), recordType, connectPrefix);
                    if (calledPrefix != null) {
                        object.set("PREFIX_SERVICE", calledPrefix);
                        object.set("CALLED_AREA_ID", calledPrefix.getAreaId());
                    } else {
                        Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>> bdtPrefix = map.get("BDT");
                        calledPrefix = StlStandardPrefixNumber.getDomesticPrefixNumber(object.get(fieldParams).toString(), recordType, bdtPrefix);
                        if (calledPrefix != null) {
                            object.set("MIDDLE_SERVICE", calledPrefix);
                            object.set("MIDDLE_SERVICE_ID", calledPrefix.getPrefixId().toString());
                        } else {
                            Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>> normalPrefix = map.get("NORMAL");
                            calledPrefix = StlStandardPrefixNumber.getDomesticPrefixNumber(object.get(fieldParams).toString(), recordType, normalPrefix);
                            if (calledPrefix != null) {
                                object.set("CALLED_PREFIX_NUMBER", calledPrefix.getPrefix());
                                object.set("CALLED_PREFIX_ID", calledPrefix.getPrefixId());
                                object.set("CALLED_PREFIX_PARTNER_ID", calledPrefix.getPartnerId());
                                object.set("CALLED_PREFIX_SERVICE_TYPE", calledPrefix.getServiceType());
                                object.set("TIME_OFF_PEAK_ID", calledPrefix.getTimeOffPeakId());
                            }
                        }
                    }
                }
            }
            if (object.containsProperty("PREFIX_SERVICE")) {
                if (object.get("CALLING_AREA_ID") != null && object.get("CALLED_AREA_ID") != null
                        && object.get("CALLING_AREA_ID") == object.get("CALLED_AREA_ID")) {
                    object.set("MIDDLE_SERVICE", null);
                }
                object.set("SERVICE_ID", ((CommonDmsPrefix) object.get("PREFIX_SERVICE")).getServiceId());
            }
            if (object.containsProperty("MIDDLE_SERVICE")) {
                CommonDmsPrefix prefix = ((CommonDmsPrefix) object.get("MIDDLE_SERVICE"));
                object.set("CALLED_PREFIX_SERVICE_ID", prefix.getServiceId());
                object.set("CALLED_PREFIX_NUMBER", prefix.getPrefix());
                object.set("CALLED_PREFIX_ID", prefix.getPrefixId());
                object.set("CALLED_PREFIX_PARTNER_ID", prefix.getPartnerId());
                object.set("CALLED_PREFIX_SERVICE_TYPE", prefix.getServiceType());
                object.set("TIME_OFF_PEAK_ID", prefix.getTimeOffPeakId());
            }

            return object;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
