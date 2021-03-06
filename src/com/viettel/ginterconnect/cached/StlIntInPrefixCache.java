/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.cached;

import com.viettel.ginterconnect.cached.bean.CommonIntPrefix;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ubuntu
 */
public class StlIntInPrefixCache extends ThreadCached {

    private String url;
    private String username;
    private String password;
    private String driver = "oracle.jdbc.OracleDriver";

    public StlIntInPrefixCache(String threadName, String description) {
        super(threadName, description);
    }

    @Override
    protected void reloadCache(Map cache) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "SELECT * FROM V_STL_INT_IN_PREFIX";
        ResultSet rs = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            cache.clear();
            while (rs.next()) {
                Long id = rs.getLong("ID");
                Long partnerId = rs.getLong("PARTNER_ID");
                String prefix = rs.getString("PREFIX");
                Long lengthPrefix = new Long(prefix.length());
                Long serviceId = rs.getLong("SERVICE_ID");
                Long contractId = rs.getLong("CONTRACT_ID");
                String direction = rs.getString("DIRECTION");
                String service = rs.getString("SERVICE_CODE");
                CommonIntPrefix stlIntPrice = new CommonIntPrefix(id, partnerId, prefix, serviceId, contractId, direction, service);
                //length prefix intprefix
                Map<Long, Map<String, CommonIntPrefix>> mapInterPrefix = null;
                if (cache.containsKey(partnerId)) {
                    mapInterPrefix = (Map<Long, Map<String, CommonIntPrefix>>) cache.get(partnerId);
                } else {
                    mapInterPrefix = new HashMap<>();
                    cache.put(partnerId, mapInterPrefix);
                }
                Map<String, CommonIntPrefix> mapLengPrefix = null;
                if (mapInterPrefix != null && mapInterPrefix.containsKey(lengthPrefix)) {
                    mapLengPrefix = mapInterPrefix.get(lengthPrefix);
                } else {
                    mapLengPrefix = new HashMap<>();
                    mapInterPrefix.put(lengthPrefix, mapLengPrefix);
                }
                mapLengPrefix.put(prefix, stlIntPrice);
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
            } catch (SQLException ex2) {
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
