/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.cached.CommonDmsPrefixCache;
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
public class CommonStandardDmsPrefixCalling implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        Long isCallingNational = object.getLong("TRUNK_IN_IS_NATIONAL", "SMS_IN_IS_NATIONAL");
        if (isCallingNational == 1) {
            return object;
        }
        try {
            CommonDmsPrefixCache stlDmsPrefixCache = (CommonDmsPrefixCache) ThreadCached.initCache("CommonDmsPrefixCache", fieldParams, params);

            Map<String, Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>>> map = stlDmsPrefixCache.getCached();
            //validate
            int recordType = Integer.valueOf(object.get("RECORD_TYPE").toString());
            Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>> normalPrefix = map.get("NORMAL");
            String calling = object.getString("CALLING");
            CommonDmsPrefix callingPrefix = StlStandardPrefixNumber.getDomesticPrefixNumber(calling, recordType, normalPrefix);
            if (callingPrefix != null) {
                object.set("CALLING_PREFIX_NUMBER_ID", callingPrefix.getPrefixId());
                object.set("CALLING_PREFIX_NUMBER", callingPrefix.getPrefix());
                object.set("CALLING_PREFIX_SERVICE_TYPE", callingPrefix.getServiceType());
                object.set("CALLING_PREFIX_AREA_ID", callingPrefix.getAreaId());
                object.set("CALLING_PREFIX_PARTNER_ID", callingPrefix.getPartnerId());
                object.set("CALLING_PART_TYPE", callingPrefix.getPartType());
            }
            return object;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
