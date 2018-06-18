/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.cached.StlVASCache;
import com.viettel.ginterconnect.cached.ThreadCached;
import com.viettel.ginterconnect.cached.bean.StlVasAccount;
import com.viettel.ginterconnect.cached.bean.CommonVasPartnerBean;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.exception.StandardException;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class STLVasPartnerStandard implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        try {
            StlVASCache stlPartnerCache = (StlVASCache) ThreadCached.initCache("StlVASCache", params, "");
            String called = object.getString("CALLED_NUMBER");
            String cdrAccount = object.getString("USERNAME");
            Long priceValue = object.getLong("CHARGE");
            HashMap<String, StlVasAccount> mapAccount = (HashMap<String, StlVasAccount>) stlPartnerCache.getCached();
            StlVasAccount vasAccount = mapAccount.get(cdrAccount);
            if (vasAccount != null) {
                HashMap<String, HashMap<Long, CommonVasPartnerBean>> vasPartner = vasAccount.getMapPrefix();
                object.set("PARTNER_CODE", vasAccount.getPartnerCode());
                if (vasPartner != null) {
                    for (int i = called.length(); i > 0; i--) {
                        String prefix = called.substring(0, i);
                        if (vasPartner.containsKey(prefix)) {
                            HashMap<Long, CommonVasPartnerBean> mapPriceVal = vasPartner.get(prefix);
                            if (mapPriceVal.containsKey(priceValue)) {
                                CommonVasPartnerBean bean = mapPriceVal.get(priceValue);
                                object.set("PRICE_NAME", bean.getPriceName());
                                object.set("SERVICE_CODE", bean.getServiceCode());
                                object.set("SERVICE_NAME", bean.getServiceName());
                                object.set("SERVICE_PREFIX", bean.getServicePrefix());
                                object.set("PARTNER_NAME", bean.getPartnerName());
                                break;
                            }
                        }
                    }
                }
            } else {
                object.set("STANDARD_FAIL", "1");
                throw new StandardException("New Partner", StandardException.PRIORITY_NORMAL);
            }
            return object;
        } catch (Exception ex) {
            throw ex;
        }
    }

}
