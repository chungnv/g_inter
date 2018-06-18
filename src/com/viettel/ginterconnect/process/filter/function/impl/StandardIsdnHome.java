/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.aerospike.client.query.Filter;
import com.viettel.ginterconnect.cached.bean.VtcHome;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.util.GIClient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class StandardIsdnHome implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (object.get("ISDN") == null) {
            return null;
        }
        String isdn = object.get("ISDN").toString();
        List<VtcHome> lstHome = (List<VtcHome>) GIClient.getInstance().getListHome(params, Filter.equal("ISDN", isdn));
        if (lstHome == null || lstHome.isEmpty()) {
            object.set("BTS_CODE", "");
            object.set("HOME_PARTITION", "");
            object.set("CENTER_CODE", "");
            object.set("PROVINCE_CODE", "TEST_NULL");
            return object;
        }
        VtcHome home = lstHome.get(0);
        //kiem tra ngay trong CDR co thoa man
        try {
            long cdrDate = ((Date) object.get("CHARGING_TIME")).getTime();
            long partitionDate = (new SimpleDateFormat("yyyyMMdd")).parse(home.getPartition()).getTime();
            if (cdrDate > partitionDate) {
                object.set("BTS_CODE", home.getBtsCode());
                object.set("HOME_PARTITION", home.getPartition());
                object.set("CENTER_CODE", home.getCenterCode());
                object.set("PROVINCE_CODE", home.getProvinceCode());
                return object;
            } else {
                object.set("BTS_CODE", "");
                object.set("HOME_PARTITION", "");
                object.set("CENTER_CODE", "");
                object.set("PROVINCE_CODE", "TEST_NULL");
                return object;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return object;
    }

}
