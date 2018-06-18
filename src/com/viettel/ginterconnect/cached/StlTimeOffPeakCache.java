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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ubuntu
 */
public class StlTimeOffPeakCache extends ThreadCached {

    private String url;
    private String username;
    private String password;
    private String driver = "oracle.jdbc.OracleDriver";

    public StlTimeOffPeakCache(String threadName, String description) {
        super(threadName, description);
    }

    @Override
    protected void reloadCache(Map cache) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "SELECT * FROM V_STL_TIME_OFF_PEAK";
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
//            return cache;
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
            this.timeSchedule = object.getString("scheduler");
            this.timeSleep = object.getString("sleep") == null ? 0 : Long.valueOf(object.getString("sleep"));
        } catch (JSONException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

}
