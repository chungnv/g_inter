/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author viettq
 */
public class VtcVasCodeMon implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        List<String> lst1 = Arrays.asList("3100188", "3100189", "3100191", "3100190", "3100173");
        List<String> lst2 = Arrays.asList("360016", "360015", "360003", "360002", "360004", "360006", "360007");
        List<String> lst3 = Arrays.asList("3040001", "3040002", "3040000", "3040004", "3040005", "3040006");
        List<String> lst4 = Arrays.asList("3100125", "3100124", "3100123", "3100122", "3100121", "3100175");
        List<String> lst5 = Arrays.asList("3100013", "3100012", "3100011", "3100020", "3100022", "3100021", "3200058", "3200057", "3200059", "3993033", "3489002", "3992032", "3993132", "3100315", "3100316", "3100141", "3100142", "3100133", "3100000", "3100001", "3100002", "3483601", "3100004", "3484001", "3100003", "3100053", "3992735", "3100060", "3100060", "3100062", "3100050", "3100051", "3100052", "3100019", "3100026", "3100024", "3100041", "3100042", "3100043", "3100047", "3100048", "3100049", "3100044", "3100045", "3100046", "3100018", "3100025", "3100023", "3100054", "3100055", "3100056", "3100057", "3100071", "3100058", "3100320", "3100143", "3100065", "3100066", "3100067", "3100068", "3100070", "3100075", "3100069", "3200021", "3200022", "3200023", "3200024", "3200025", "3200027", "3200028", "3200033", "3200034", "3200035", "3200037", "3200041", "3200042", "3200039", "3200040", "3200044", "3200045", "3200071", "3100168", "3100197", "3200075");

        if (object.containsProperty("PRODUCT_ID") && object.get("PRODUCT_ID") != null) {
            String productId = object.get("PRODUCT_ID").toString();
            if (lst1.contains(productId)) {
                return 168;
            }
            if (lst2.contains(productId)) {
                return "MCA_MONTHLY_FEE";
            }
            if ("360017".equals(productId)) {
                return "MCA_DAILY_FEE";
            }
            if (lst3.contains(productId)) {
                return "CUG";
            }
            if ("3200031".equals(productId)) {
                return "FB_DAILY";
            }
            if ("3200032".equals(productId)) {
                return "FB_MONTH";
            }
            if ("3200064".equals(productId)) {
                return "FB_DAILY_NEW";
            }
            if ("3200065".equals(productId)) {
                return "FB_MONTH_NEW";
            }
            if ("3100204".equals(productId) || "3100202".equals(productId)) {
                return "TG";
            }
            if (lst4.contains(productId)) {
                return "YAK";
            }
            if ("3100120".equals(productId)) {
                return "7PLUS_W2_FEE";
            }
            if ("3100119".equals(productId)) {
                return "7PLUS_W1_FEE";
            }
            if ("3100118".equals(productId)) {
                return "7PLUS_D1_FEE";
            }
            if ("3100116".equals(productId)) {
                return "DEUMTNOUT_CHARGE";
            }
            if ("3100104".equals(productId) || "3100109".equals(productId)) {
                return "178";
            }
            if ("3100153".equals(productId)) {
                return "PRIVATE_NUMBER";
            }
            if ("3484202".equals(productId)) {
                return "COMMIT_MONTHLY_FEE";
            }
            if (lst5.contains(productId)) {
                return "GPRS_MONTHLY_FEE";
            }
            if (Integer.valueOf(productId) >= 3390001 && Integer.valueOf(productId) <= 3390030
                    || Integer.valueOf(productId) >= 3990031 && Integer.valueOf(productId) <= 3390046
                    || Integer.valueOf(productId) == 3420001 || Integer.valueOf(productId) == 3460001) {
                return "FN_MONTHLY_FEE";
            }
            if ("3100159".equals(productId)) {
                return "AUTOEXC_YAK_F4E1";
            }
            if ("3100160".equals(productId)) {
                return "AUTOEXC_M4E2";
            }
            if ("3200068".equals(productId)) {
                return "FREE_VOICE_100";
            }
            if ("3100199".equals(productId)) {
                return "FREE_VOICE_201";
            }
            if ("3100198".equals(productId)) {
                return "FREE_VOICE_202";
            }
            if ("3200069".equals(productId)) {
                return "FREE_VOICE_30";
            }
            if ("3200070".equals(productId)) {
                return "YOUTUBE_MONTHLY";
            }
            if ("3200073".equals(productId)) {
                return "YOUTUBE_WEEKLY";
            }
        }
        return "OTHER";
    }

}
