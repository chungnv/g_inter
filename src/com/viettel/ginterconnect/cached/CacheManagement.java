/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.cached;

import com.aerospike.client.query.Filter;
import com.viettel.ginterconnect.cached.bean.CacheManagerBean;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIClient;
import com.viettel.ginterconnect.util.ThreadUtil;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.collections4.CollectionUtils;

/**
 *
 * @author ubuntu
 */
public class CacheManagement extends Thread {

    private ConcurrentHashMap<String, ThreadCached> cacheManager = new ConcurrentHashMap<>();

    private static CacheManagement instance = null;

    SimpleDateFormat sdf = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);

    public void addToManager(String name, ThreadCached cached) {
        cacheManager.put(name, cached);
    }

    public static synchronized CacheManagement getInstance() {
        if (instance == null) {
            instance = new CacheManagement();
            instance.start();
        }
        return instance;
    }

    @Override
    public void run() {
        while (true) {
            if (cacheManager != null && !cacheManager.isEmpty()) {
                //lay danh sach need reload
                List<CacheManagerBean> lstBean = GIClient.getInstance().getRecordByFilter(CacheManagerBean.class,
                        Filter.equal("STATUS", "1"));
                if (!CollectionUtils.isEmpty(lstBean)) {
                    for (CacheManagerBean bean : lstBean) {
                        if (cacheManager.containsKey(bean.getName())) {
                            ThreadCached cached = cacheManager.get(bean.getName());
                            if (cached != null && cached.isReloadByWS()) {
                                String svTime = bean.getUpdateTime();
                                String threadTime = cached.getLastReloadTime();
                                if (svTime != null && threadTime != null) {
                                    try {
                                        Date svDate = sdf.parse(svTime);
                                        Date threadDate = sdf.parse(threadTime);
                                        if (svDate.after(threadDate)) {
                                            cached.setNeedReload(true);
                                        }
                                    } catch (ParseException pe) {
                                        pe.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ThreadUtil.delay(5000L);
        }
    }

}
