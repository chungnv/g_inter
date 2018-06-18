package com.viettel.ginterconnect.cached;

import com.viettel.ginterconnect.cached.bean.BtsCode;
import com.viettel.ginterconnect.util.GIClient;
import java.util.List;
import java.util.Map;

/**
 * Created by hoangsinh on 18/08/2017.
 */
public class BtsCodeCache extends ThreadCached {

    public BtsCodeCache(String threadName, String description) {
        super(threadName, description);
    }

    @Override
    protected void reloadCache(Map cache) {
//        Map<String, BtsCode> cache = new HashMap<>();
        List<BtsCode> lst = GIClient.getInstance().getRecordByFilter(BtsCode.class, null);
        for (BtsCode bean : lst) {
            cache.put(bean.getLacCi(), bean);
        }
    }

    @Override
    protected void parseParams(String params) {
        return;
    }
}