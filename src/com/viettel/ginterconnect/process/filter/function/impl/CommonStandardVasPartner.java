/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.cached.CommonVASCache;
import com.viettel.ginterconnect.cached.ThreadCached;
import com.viettel.ginterconnect.cached.bean.StlVasAccount;
import com.viettel.ginterconnect.cached.bean.CommonVasPartnerBean;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.exception.StandardException;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class CommonStandardVasPartner implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        try {
            CommonVASCache stlPartnerCache = (CommonVASCache) ThreadCached.initCache("CommonVASCache", fieldParams, params);
//            String called = object.getString("MSISDN");
            String svcCode = object.getString("SVC_CODE");
            String cdrAccount = object.getString("PRVD_CODE"); //USERNAME
            Long priceValue = object.getLong("CHRG_AMOUNTS"); //CHARGE
            HashMap<String, StlVasAccount> mapAccount = (HashMap<String, StlVasAccount>) stlPartnerCache.getCached();
            StlVasAccount vasAccount = mapAccount.get(cdrAccount);
            if (vasAccount != null) {
                HashMap<String, HashMap<Long, CommonVasPartnerBean>> vasPartner = vasAccount.getMapPrefix();
                object.set("PARTNER_CODE", vasAccount.getPartnerCode());
                if (vasPartner != null) {
                    CommonVasPartnerBean serviceBean = null;
                    for (int i = svcCode.length(); i > 0; i--) {
                        String prefix = svcCode.substring(0, i);
                        if (vasPartner.containsKey(prefix)) {
                            HashMap<Long, CommonVasPartnerBean> mapPriceVal = vasPartner.get(prefix);
                            if (serviceBean == null) {
                                Iterator<Long> itePrice = mapPriceVal.keySet().iterator();
                                if (itePrice.hasNext()) {
                                    serviceBean = mapPriceVal.get(itePrice.next());
                                }
                            }
                            if (mapPriceVal.containsKey(priceValue)) {
                                CommonVasPartnerBean bean = mapPriceVal.get(priceValue);
                                object.set("PRICE_NAME", bean.getPriceName());
                                object.set("SERVICE_CODE", bean.getServiceCode());
                                object.set("SERVICE_NAME", bean.getServiceName());
                                object.set("SERVICE_PREFIX", bean.getServicePrefix());
                                object.set("PARTNER_NAME", bean.getPartnerName());
                                break;
                            }
                            break;
                        }
                    }
                    if (object.get("PARTNER_NAME") == null && serviceBean != null) {
                        object.set("SERVICE_CODE", serviceBean.getServiceCode());
                        object.set("SERVICE_NAME", serviceBean.getServiceName());
                        object.set("PARTNER_NAME", serviceBean.getPartnerName());
                    }
                }
            } else {
                object.set("STANDARD_FAIL", "1");
                throw new StandardException("New Vas Partner", StandardException.PRIORITY_BREAK_FLOW);
//                throw new StandardException("New Vas Partner", StandardException.PRIORITY_NORMAL);
            }
            return object;
        } catch (Exception ex) {
            throw ex;
        }
    }

}
