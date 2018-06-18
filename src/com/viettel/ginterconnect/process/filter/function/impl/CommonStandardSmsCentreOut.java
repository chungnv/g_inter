/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.cached.CommonSmsCentreCache;
import com.viettel.ginterconnect.cached.ThreadCached;
import com.viettel.ginterconnect.cached.bean.SmsCentre;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.exception.StandardException;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.tool.hbm2x.StringUtils;

/**
 *
 * @author ubuntu
 */
public class CommonStandardSmsCentreOut implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        try {
            CommonSmsCentreCache smsCentreCache = (CommonSmsCentreCache) ThreadCached.initCache("CommonSmsCentreCache", fieldParams, params);
            Map<String, HashMap<String, SmsCentre>> map = smsCentreCache.getCached();
            String smsCentreOut = object.getString("SMS_CENTRE_OUT");
            if (StringUtils.isEmpty(smsCentreOut)) {
                object.set("SMS_OUT_IS_NATIONAL", 0L);
                object.set("SMS_OUT_PARTNER_ID", object.get("OWNER_ID"));
            } else {
                SmsCentre smsCentre = null;
                HashMap<String, SmsCentre> mapSmsCentre;
                if (!smsCentreOut.contains(".")) {
                    mapSmsCentre = (HashMap<String, SmsCentre>) map.get("SMS");
                } else {
                    mapSmsCentre = (HashMap<String, SmsCentre>) map.get("IP");
                }
                for (int i = smsCentreOut.length(); i > 0; i--) {
                    String prefixNum = smsCentreOut.substring(0, i);
                    if (mapSmsCentre.containsKey(prefixNum)) {
                        smsCentre = (SmsCentre) mapSmsCentre.get(prefixNum);
                        break;
                    }
                }
                if (smsCentre != null) {
                    object.set("SMS_OUT_ID", smsCentre.getSmsCentreId());
                    object.set("SMS_OUT_IS_NATIONAL", smsCentre.getIsNational());
                    object.set("SMS_OUT_PARTNER_ID", smsCentre.getPartnerId());
                    object.set("SMS_OUT_SERVICE_ID", smsCentre.getServiceId());
                } else {
                    object.set("STANDARD_FAIL", "1");
                    throw new StandardException("New Sms centre", StandardException.PRIORITY_NORMAL);
                }
            }
            return object;
        } catch (Exception ex) {
            throw ex;
        }
    }
}
