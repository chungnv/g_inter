/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.cached;

import com.viettel.ginterconnect.cached.bean.SmsCentre;
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
public class CommonSmsCentreCache extends ThreadCached {

    private String url;
    private String username;
    private String password;
    private String driver = "oracle.jdbc.OracleDriver";

    public CommonSmsCentreCache(String threadName, String description) {
        super(threadName, description);
    }

    @Override
    protected void reloadCache(Map cache) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "SELECT * FROM V_CM_SMS_CONNECTION where MARKET_ID = " + marketId;
        ResultSet rs = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            cache.clear();
            while (rs.next()) {
                Long smsCentreId = rs.getLong("CONNECTION_ADDRESS_ID");
                String smsPrefix = rs.getString("ADDRESS");
                Long partnerId = rs.getLong("PARTNER_ID");
                Long isNational = rs.getLong("IS_NATIONAL");
                Long smsMarketId = rs.getLong("MARKET_ID");
                Long serviceId = rs.getLong("SERVICE_ID");
                SmsCentre smsCentre = new SmsCentre(smsCentreId, smsPrefix, partnerId, isNational, serviceId, smsMarketId);
                HashMap<String, SmsCentre> mapSmsCentre;
                if (smsPrefix.contains(".")) {
                    if (cache.containsKey("IP")) {
                        mapSmsCentre = (HashMap<String, SmsCentre>) cache.get("IP");
                    } else {
                        mapSmsCentre = new HashMap<>();
                        cache.put("IP", mapSmsCentre);
                    }
                } else {
                    if (cache.containsKey("SMS")) {
                        mapSmsCentre = (HashMap<String, SmsCentre>) cache.get("SMS");
                    } else {
                        mapSmsCentre = new HashMap<>();
                        cache.put("SMS", mapSmsCentre);
                    }
                }
                mapSmsCentre.put(smsPrefix, smsCentre);
            }
//            return cache;
        } catch (ClassNotFoundException | SQLException ex) {
            logger.error(ex.getMessage(), ex);
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
                logger.error(ex2.getMessage(), ex2);
            }
        }

    }

    @Override
    protected void parseParams(String params) throws Exception {
        try {
            JSONObject object = new JSONObject(params);
            url = object.getString("url");
            username = object.getString("username");
            password = object.getString("password");
//            this.timeSchedule = object.getString("scheduler");
//            this.timeSleep = object.getString("sleep") == null ? 0 : Long.valueOf(object.getString("sleep"));
            try {
                this.timeSchedule = object.getString("scheduler");
                this.timeSleep = object.getString("sleep") == null ? 0 : Long.valueOf(object.getString("sleep"));
            } catch (JSONException je) {
            }
        } catch (JSONException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

}
