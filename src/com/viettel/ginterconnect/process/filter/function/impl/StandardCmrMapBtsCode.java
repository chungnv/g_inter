package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.cached.BtsCodeCache;
import com.viettel.ginterconnect.cached.ThreadCached;
import com.viettel.ginterconnect.cached.bean.BtsCode;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;

import java.util.Map;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;

/**
 * Created by on 18/08/2017.
 */
public class StandardCmrMapBtsCode implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        if (object != null && object.containsProperty("CELL_A") && object.get("CELL_A") != null) {
            try {
                BtsCodeCache btsCache = (BtsCodeCache)ThreadCached.initCache("BtsCodeCache", null, "");

                Map<String, BtsCode> cache = btsCache.getCached();
                if (cache != null) {
                    if (cache.get(object.get("CELL_A").toString()) != null) {
                        BtsCode btsCode = cache.get(object.get("CELL_A").toString());
                        object.set("BTS_CODE", btsCode.getBtsCode());
                        object.set("CENTER_CODE", btsCode.getCenterCode());
                        object.set("PROVINCE", btsCode.getProvinceCode());
                        object.set("BTS_TYPE", btsCode.getBtsType());
                        object.set("CONFIG", btsCode.getConfig());
                        object.set("POPULATION", btsCode.getPopulation());
                        object.set("LATTITUDE", btsCode.getLaTitute());
                        object.set("LONGTITUTE", btsCode.getLongTitute());
                    }
                }
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                throw ex;
            }
        }
        return object;
    }
}
