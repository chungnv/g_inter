/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.cached.StlTrunkCache;
import com.viettel.ginterconnect.cached.ThreadCached;
import com.viettel.ginterconnect.cached.bean.CommonTrunk;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.exception.StandardException;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class STLStandardTrunkOut implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        try {
            StlTrunkCache stlTrunk = (StlTrunkCache) ThreadCached.initCache("StlTrunkCache", params, "");
            Map<String, CommonTrunk> map = stlTrunk.getCached();
            //validate
            String trunkOut = object.getString(fieldParams);
//            if (StringUtils.isEmpty(trunkOut)) {
//                object.set("TRUNK_OUT_IS_NATIONAL", 0L);
//                return object;
//            }
            CommonTrunk trunk = (CommonTrunk) map.get(trunkOut);
            if (trunk != null) {
                object.set("TRUNK_OUT_ID", trunk.getTrunkId());
                object.set("TRUNK_OUT_IS_NATIONAL", trunk.getIsNational());
                object.set("TRUNK_OUT_PARTNER_ID", trunk.getPartnerId());
                object.set("SWITCH_CONNECT_TYPE_OUT", trunk.getConnectSwitchType());
            } else {
                object.set("STANDARD_FAIL", "1");
                throw new StandardException("New Route", StandardException.PRIORITY_NORMAL);
            }
            return object;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
