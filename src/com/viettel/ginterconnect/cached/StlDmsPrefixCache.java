/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.cached;

import com.viettel.ginterconnect.cached.bean.CommonDmsPrefix;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class StlDmsPrefixCache extends ThreadCached {

    private String url;
    private String username;
    private String password;
    private String driver = "oracle.jdbc.OracleDriver";

    public StlDmsPrefixCache(String threadName, String description) {
        super(threadName, description);
    }

    @Override
    protected void reloadCache(Map cache) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "SELECT * FROM V_DM_PREFIX_NUMBER";
        ResultSet rs = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            cache.clear();
            while (rs.next()) {
                Long prefixId = rs.getLong("PREFIX_ID");
                String prefix = rs.getString("PREFIX_CODE");
                Long length = rs.getLong("LENGTH");
                Long fixLength = rs.getLong("FIX_LENGTH");
                String prefixPriority = rs.getString("PRIORITY");
                String addPrefix = rs.getString("ADD_PREFIX");
                Long serviceId = rs.getLong("SERVICE_ID");
                Long areaId = rs.getLong("AREA_ID");
                Long partnerId = rs.getLong("PARTNER_ID");
                Long numberFunction = rs.getLong("NUMBER_FUNCTION");
                Long timeOffPeakId = rs.getLong("TIME_OFF_PEAK_ID");
                String serviceType = rs.getString("SERVICE_TYPE");
                String partType = rs.getString("PART_TYPE");
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
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
            System.exit(1);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        }

    }

    @Override
    protected void parseParams(String params) throws Exception {
        try {
            JSONObject object = new JSONObject(params);
//            driver = object.getString("driver");
            url = object.getString("url");
            username = object.getString("username");
            password = object.getString("password");
            try {
                this.timeSchedule = object.getString("scheduler");
                this.timeSleep = object.getString("sleep") == null ? 0 : Long.valueOf(object.getString("sleep"));
            } catch (JSONException je) {
            }
        } catch (JSONException ex) {
            System.out.println("Error: " + ex.getMessage());
            throw ex;
        }
    }

}
