/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.cached.StlIntOutPrefixCache;
import com.viettel.ginterconnect.cached.ThreadCached;
import com.viettel.ginterconnect.cached.bean.CommonIntPrefix;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class STLStandardIntOutPrefix implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        Long isCallingNational = object.getLong("TRUNK_OUT_IS_NATIONAL");
        Long partnerId = object.getLong("TRUNK_OUT_PARTNER_ID");
        if (isCallingNational != 1 || partnerId == null) {
            return object;
        }
        String called = object.get(fieldParams).toString();
        try {
            StlIntOutPrefixCache stlIntOutPrefixCache = (StlIntOutPrefixCache) ThreadCached.initCache("StlIntOutPrefixCache", params, "");
            Map<Long, Map<String, CommonIntPrefix>> map = (Map<Long, Map<String, CommonIntPrefix>>) stlIntOutPrefixCache.getCached().get(partnerId);
            //validate
            if (map != null) {
                for (int i = called.length(); i > 0; i--) {
                    Long length = new Long(i);
                    if (map.containsKey(length)) {
                        String prefix = called.substring(0, i);
                        Map<String, CommonIntPrefix> mapLength = (Map) map.get(length);
                        if (mapLength.containsKey(prefix)) {
                            CommonIntPrefix bingo = (CommonIntPrefix) mapLength.get(prefix);
                            //set result
                            object.set("CALLED_PREFIX_SERVICE_ID", bingo.getServiceId());
                            object.set("CALLED_PREFIX_NUMBER", bingo.getPrefix());
                            object.set("CALLED_PREFIX_NUMBER_ID", bingo.getId());
                            object.set("CALLED_CONTRACT_ID", bingo.getContractId());
                            break;
                        }
                    }
                }
            }
            return object;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
