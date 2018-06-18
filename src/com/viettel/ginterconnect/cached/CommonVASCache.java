/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.cached;

import com.viettel.ginterconnect.cached.bean.StlVasAccount;
import com.viettel.ginterconnect.cached.bean.CommonVasPartnerBean;
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
public class CommonVASCache extends ThreadCached {

    private String url;
    private String username;
    private String password;
    private String driver = "oracle.jdbc.OracleDriver";

    public CommonVASCache(String threadName, String description) {
        super(threadName, description);
    }

    @Override
    protected void reloadCache(Map cache) {
        Connection con = null;
        PreparedStatement ps = null;
        String sql = "SELECT * FROM V_CM_VAS_CACHE where MARKET_ID = " + marketId;
        ResultSet rs = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, username, password);
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            cache.clear();
            while (rs.next()) {
                String cdrAccount = rs.getString("CDR_ACCOUNT");
                String partnerCode = rs.getString("PARTNER_CODE");
                String serviceCode = rs.getString("SERVICE_CODE");
                String servicePrefix = rs.getString("SERVICE_PREFIX");
                String serviceName = rs.getString("SERVICE_NAME");
                String partnerName = rs.getString("PARTNER_NAME");
                String priceName = rs.getString("PRICE_NAME");
                String priceDes = rs.getString("PRICE_DESCRIPTION");
//                String priceVal = rs.getLong("PRICE_VALUE") == null ? "0" : rs.getString("PRICE_VALUE");
                Long priceValue = rs.getLong("PRICE_VALUE");
                CommonVasPartnerBean bean = new CommonVasPartnerBean(cdrAccount, partnerCode, serviceCode,
                        servicePrefix, priceName, priceValue, priceDes, serviceName, partnerName);
                StlVasAccount vasAccount;
                HashMap<String, HashMap<Long, CommonVasPartnerBean>> mapService;
                if (cache.containsKey(cdrAccount)) {
                    vasAccount = (StlVasAccount) cache.get(cdrAccount);
                } else {
                    vasAccount = new StlVasAccount(cdrAccount, partnerCode);
                    cache.put(cdrAccount, vasAccount);
                }
                mapService = vasAccount.getMapPrefix();
                HashMap<Long, CommonVasPartnerBean> mapServicePrefixBean;
                if (mapService.containsKey(servicePrefix)) {
                    mapServicePrefixBean = mapService.get(servicePrefix);
                } else {
                    mapServicePrefixBean = new HashMap<>();
                    mapService.put(servicePrefix, mapServicePrefixBean);
                }
                mapServicePrefixBean.put(priceValue, bean);
            }
        } catch (ClassNotFoundException | SQLException ex) {
            threadCachedLogger.error(ex.getMessage(), ex);
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
            try {
                this.timeSchedule = object.getString("scheduler");
            } catch (JSONException je) {
            }
            try {
                this.timeSleep = object.getString("sleep") == null ? 0 : Long.valueOf(object.getString("sleep"));
            } catch (JSONException je) {
            }
            //need to be forced to reload from web
            parseReloadFromWeb(object);
        } catch (JSONException ex) {
            System.out.println("Error: " + ex.getMessage());
            throw ex;
        }
    }

}
