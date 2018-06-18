/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.cached.BtsCodeCache;
import com.viettel.ginterconnect.cached.ThreadCached;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author ubuntu
 */
public class MvtMonProductId implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        try {
            BtsCodeCache btsCache = (BtsCodeCache) ThreadCached.initCache("BtsCodeCache", null, "");

            String productId = object.get("PRODUCT_ID") == null ? null : object.get("PRODUCT_ID").toString();
            if (productId == null || !btsCache.getCached().containsKey(productId)) {
                return false;
            }
//        where product_id in (
//        select product_id
//        from vtmoz.r_product_id_mon
//        where minus_money = 'YES')
            return true;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            throw ex;
        }
    }

}
