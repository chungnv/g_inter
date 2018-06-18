/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.cached;

import com.viettel.ginterconnect.cached.bean.CommonDmsPrefix;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ubuntu
 */
public class CommonDmsPrefixCacheTest extends ThreadCached {

    private String url;
    private String username;
    private String password;
    private String driver = "oracle.jdbc.OracleDriver";

    public CommonDmsPrefixCacheTest(String threadName, String description) {
        super(threadName, description);
    }

    @Override
    protected void reloadCache(Map cache) {
//        Connection con = null;
//        PreparedStatement ps = null;
//        String sql = "SELECT * FROM V_CM_DM_PREFIX_NUMBER where MARKET_ID = " + marketId;
//        ResultSet rs = null;
        try {
//            Class.forName(driver);
//            con = DriverManager.getConnection(url, username, password);
//            ps = con.prepareStatement(sql);
//            rs = ps.executeQuery();
//            cache.clear();
//            while (rs.next()) {
            
            CommonDmsPrefix calledPrefixTest1 = new CommonDmsPrefix(559L, "942", 11L, 
                0L, "HIGH", "0", 46L, 
                0L, "NORMAL", 270L, null);
            calledPrefixTest1.setPartnerId(1110L);
            calledPrefixTest1.setNumberFunction(2L);
            
            CommonDmsPrefix calledPrefixTest2 = new CommonDmsPrefix(1131L, "942", 11L, 
                0L, "HIGH", "0", 65L, 
                null, "NORMAL", null, null);
            calledPrefixTest2.setPartnerId(1110L);
            calledPrefixTest2.setNumberFunction(1L);
            
            List<CommonDmsPrefix> lstCm = new ArrayList<>();
            lstCm.add(calledPrefixTest1);
            lstCm.add(calledPrefixTest2);
            
            for (CommonDmsPrefix calledPrefixTest : lstCm) {
                Long prefixId = calledPrefixTest.getPrefixId();
                String prefix = calledPrefixTest.getPrefix();
                Long length = calledPrefixTest.getLength();
                Long fixLength = calledPrefixTest.getFixLength();
                String prefixPriority = calledPrefixTest.getPriority();
                String addPrefix = calledPrefixTest.getAddPrefix();
                Long serviceId = calledPrefixTest.getServiceId();
                Long areaId = calledPrefixTest.getAreaId();
                Long partnerId = calledPrefixTest.getPartnerId();
                Long numberFunction = calledPrefixTest.getNumberFunction();
                Long timeOffPeakId = calledPrefixTest.getTimeOffPeakId();
                String serviceType = calledPrefixTest.getServiceType();
                String partType = calledPrefixTest.getPartType();
                CommonDmsPrefix dmsPrefix = new CommonDmsPrefix(prefixId, prefix, length, fixLength, prefixPriority, addPrefix, serviceId, areaId, serviceType, timeOffPeakId, partType);
                dmsPrefix.setPartnerId(partnerId);
                dmsPrefix.setNumberFunction(numberFunction);
                Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>> mapPriorityPrefix = null;
                if (cache.containsKey(serviceType)) {
                    mapPriorityPrefix = (Map<String, Map<Long, Map<String, List<CommonDmsPrefix>>>>) cache.get(serviceType);
                } else {
                    mapPriorityPrefix = new HashMap<>();
                    cache.put(serviceType, mapPriorityPrefix);
                }
                Map<Long, Map<String, List<CommonDmsPrefix>>> mapLengPrefix = null;
                if (mapPriorityPrefix != null && mapPriorityPrefix.containsKey(prefixPriority)) {
                    mapLengPrefix = mapPriorityPrefix.get(prefixPriority);
                } else {
                    mapLengPrefix = new HashMap<>();
                    mapPriorityPrefix.put(prefixPriority, mapLengPrefix);
                }
                if (dmsPrefix.getPrefix() != null) {
                    Map<String, List<CommonDmsPrefix>> mapPrefix = null;
                    if (mapLengPrefix != null && mapLengPrefix.containsKey(Long.valueOf(prefix.length()))) {
                        mapPrefix = mapLengPrefix.get(Long.valueOf(prefix.length()));
                    } else {
                        mapPrefix = new HashMap<>();
                        mapLengPrefix.put(Long.valueOf(prefix.length()), mapPrefix);
                    }
                    List<CommonDmsPrefix> lstPrefix = null;
                    if (mapPrefix.containsKey(dmsPrefix.getPrefix())) {
                        lstPrefix = mapPrefix.get(dmsPrefix.getPrefix());
                    } else {
                        lstPrefix = new ArrayList<>();
                        mapPrefix.put(prefix, lstPrefix);
                    }
                    lstPrefix.add(dmsPrefix);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            System.exit(1);
        } finally {
//            try {
//                if (rs != null) {
//                    rs.close();
//                }
//                if (ps != null) {
//                    ps.close();
//                }
//                if (con != null) {
//                    con.close();
//                }
//            } catch (SQLException ex2) {
//                logger.error(ex2.getMessage(), ex2);
//            }
        }
    }

    @Override
    protected void parseParams(String params) throws Exception {
        try {
            JSONObject object = new JSONObject(params);
            url = object.getString("url");
            username = object.getString("username");
            password = object.getString("password");
            try {
                this.timeSchedule = object.getString("scheduler");
            } catch (JSONException je) {
            }
            try {
                this.timeSleep = object.getString("sleep") == null ? 0 : Long.valueOf(object.getString("sleep"));
            } catch (JSONException je) {
            }
//            try {
//                this.timeSchedule = object.getString("scheduler");
//                this.timeSleep = object.getString("sleep") == null ? 0 : Long.valueOf(object.getString("sleep"));
//            } catch (JSONException je) {
//            }
        } catch (JSONException ex) {
            System.out.println("Error: " + ex.getMessage());
            throw ex;
        }
    }

}
