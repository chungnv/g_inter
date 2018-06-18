package com.viettel.ginterconnect.cached;

import com.aerospike.client.query.Filter;
import com.viettel.ginterconnect.cached.bean.MvtProduct;
import com.viettel.ginterconnect.util.GIClient;

import java.util.List;
import java.util.Map;

/**
 * Created by hoangsinh on 18/08/2017.
 */
public class MvtProductCache extends ThreadCached {

    public MvtProductCache(String threadName, String description) {
        super(threadName, description);
    }

    @Override
    protected void reloadCache(Map cache) {
//        Map<String, MvtProduct> cache = new HashMap<>();
        cache.clear();
        List<MvtProduct> lst = GIClient.getInstance().getRecordByFilter(MvtProduct.class, Filter.equal("STATUS", "1"));
        for (MvtProduct bean : lst) {
            cache.put(bean.getProductId(), bean);
        }
//        return cache;
    }

    @Override
    protected void parseParams(String params) {
        return;
    }
}
