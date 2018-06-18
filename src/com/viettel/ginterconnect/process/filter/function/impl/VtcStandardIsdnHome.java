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
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class VtcStandardIsdnHome implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (object.get("ISDN") == null) {
            return null;
        }
        String isdn = object.get("ISDN").toString();
        VtcHome home = GIClient.getInstance().getListVtcHome(Filter.equal("ISDN", isdn));
        if (home == null) {
            object.set("HOME_BTS_CODE", "");
            object.set("HOME_PARTITION", "");
            object.set("HOME_CENTER_CODE", "");
            object.set("HOME_PROVINCE_CODE", "NULL");
            return object;
        }
//        VtcHome home = lstHome.get(0);
        //kiem tra ngay trong CDR co thoa man
        try {
//            if (object.get("START_TIME") != null) {
//                System.out.println("WTF");
//            }
            long cdrDate = object.get("CHARGING_TIME") == null ? -1 : ((Date) object.get("CHARGING_TIME")).getTime();
            long partitionDate = (new SimpleDateFormat("yyyyMMdd")).parse(home.getPartition()).getTime();
            if (cdrDate == -1 || cdrDate > partitionDate) {
                object.set("HOME_BTS_CODE", home.getBtsCode());
                object.set("HOME_PARTITION", home.getPartition());
                object.set("HOME_CENTER_CODE", home.getCenterCode());
                object.set("HOME_PROVINCE_CODE", home.getProvinceCode());
                return object;
            } else {
                object.set("HOME_BTS_CODE", "");
                object.set("HOME_PARTITION", "");
                object.set("HOME_CENTER_CODE", "");
                object.set("HOME_PROVINCE_CODE", "NULL");
                return object;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return object;
    }

}
