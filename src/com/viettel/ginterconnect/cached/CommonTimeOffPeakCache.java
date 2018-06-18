/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.cached;

import com.viettel.ginterconnect.cached.bean.CommonTimeOffPeak;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ubuntu
 */
public class CommonTimeOffPeakCache extends ThreadCached {

    private String url;
    private String username;
    private String password;
    private String driver = "oracle.jdbc.OracleDriver";

    public CommonTimeOffPeakCache(String threadName, String description) {
        super(threadName, description);
    }

    @Override
    protected void reloadCache(Map cache) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "SELECT * FROM V_CM_TIME_OFF_PEAK where MARKET_ID = " + marketId;
        ResultSet rs = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            cache.clear();
            while (rs.next()) {
                Long timeOffPeakId = rs.getLong("TIME_OFF_PEAK_ID");
                String startHour = rs.getString("START_HOUR");
                String endHour = rs.getString("END_HOUR");
                String itemType = rs.getString("ITEM_TYPE");
                CommonTimeOffPeak timeOffPeak = new CommonTimeOffPeak(timeOffPeakId, startHour, endHour, itemType);
                List<CommonTimeOffPeak> lstTimeOffPeak;
                if (cache.containsKey(timeOffPeakId)) {
                    lstTimeOffPeak = (List<CommonTimeOffPeak>) cache.get(timeOffPeakId);
                } else {
                    lstTimeOffPeak = new ArrayList<>();
                    cache.put(timeOffPeakId, lstTimeOffPeak);
                }
                lstTimeOffPeak.add(timeOffPeak);
            }
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
            } catch (JSONException je) {
            }
            try {
                this.timeSleep = object.getString("sleep") == null ? 0 : Long.valueOf(object.getString("sleep"));
            } catch (JSONException je) {
            }
        } catch (JSONException ex) {
            System.out.println("Error: " + ex.getMessage());
            throw ex;
        }
    }

}
