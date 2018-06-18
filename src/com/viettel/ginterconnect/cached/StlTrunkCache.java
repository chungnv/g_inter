/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.cached;

import com.viettel.ginterconnect.cached.bean.CommonTrunk;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ubuntu
 */
public class StlTrunkCache extends ThreadCached {

    private String url;
    private String username;
    private String password;
    private String driver = "oracle.jdbc.OracleDriver";

    public StlTrunkCache(String threadName, String description) {
        super(threadName, description);
    }

    @Override
    protected void reloadCache(Map cache) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "SELECT * FROM V_FULL_TRUNK";
        ResultSet rs = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            cache.clear();
            while (rs.next()) {
                Long trunkId = rs.getLong("ID");
                String name = rs.getString("NAME");
                Long switchId = rs.getLong("SWITCH_ID");
                Long partnerId = rs.getLong("PARTNER_ID");
                Long isNational = rs.getLong("IS_NATIONAL");
                String connectSwitchType = rs.getString("SWITCH_CONNECT_TYPE");
                String partType = rs.getString("PART_TYPE");
                CommonTrunk trunk = new CommonTrunk(name, switchId, partnerId, isNational, trunkId, connectSwitchType, partType);
                cache.put(name, trunk);
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
