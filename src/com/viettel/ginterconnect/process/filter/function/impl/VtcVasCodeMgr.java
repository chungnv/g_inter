/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import org.apache.log4j.Logger;

/**
 *
 * @author viettq
 */
public class VtcVasCodeMgr implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
//        List<String> lst1 = Arrays.asList("3100188", "3100189", "3100191", "3100190", "3100173");
//        List<String> lst2 = Arrays.asList("360016", "360015", "360003", "360002", "360004", "360006", "360007");
//        List<String> lst3 = Arrays.asList("3040001", "3040002", "3040000", "3040004", "3040005", "3040006");
//        List<String> lst4 = Arrays.asList("3100125", "3100124", "3100123", "3100122", "3100121", "3100175");
//        List<String> lst5 = Arrays.asList("3100013", "3100012", "3100011", "3100020", "3100022", "3100021", "3200058", "3200057", "3200059", "3993033", "3489002", "3992032", "3993132", "3100315", "3100316", "3100141", "3100142", "3100133", "3100000", "3100001", "3100002", "3483601", "3100004", "3484001", "3100003", "3100053", "3992735", "3100060", "3100060", "3100062", "3100050", "3100051", "3100052", "3100019", "3100026", "3100024", "3100041", "3100042", "3100043", "3100047", "3100048", "3100049", "3100044", "3100045", "3100046", "3100018", "3100025", "3100023", "3100054", "3100055", "3100056", "3100057", "3100071", "3100058", "3100320", "3100143", "3100065", "3100066", "3100067", "3100068", "3100070", "3100075", "3100069", "3200021", "3200022", "3200023", "3200024", "3200025", "3200027", "3200028", "3200033", "3200034", "3200035", "3200037", "3200041", "3200042", "3200039", "3200040", "3200044", "3200045", "3200071", "3100168", "3100197", "3200075");

        if ((object.containsProperty("PRODUCT_ID") && object.get("PRODUCT_ID") != null)) {
            String productId = object.get("PRODUCT_ID").toString();
            if ((object.containsProperty("ACCOUNT_TYPE1") && object.get("ACCOUNT_TYPE1") != null)) {
                String countType = object.get("ACCOUNT_TYPE1").toString();
                if ((object.containsProperty("CHARGE_AMOUNT1") && object.get("CHARGE_AMOUNT1") != null)) {
                    String charge = object.get("CHARGE_AMOUNT1").toString();
                    if ("4052100".equals(productId) && "2500".equals(countType)
                            && "1000000".equals(charge)) {
                        return "Y10";
                    }
                    if ("4052100".equals(productId) && "2500".equals(countType)
                            && "3000000".equals(charge)) {
                        return "Y20";
                    }
                    if ("4052100".equals(productId) && "2500".equals(countType)
                            && "9000000".equals(charge)) {
                        return "Y50";
                    }
                    if ("4052100".equals(productId) && "2500".equals(countType)
                            && "30000000".equals(charge)) {
                        return "Y100";
                    }
                    if ("4052100".equals(productId) && "2500".equals(countType)
                            && "60000000".equals(charge)) {
                        return "Y200";
                    }
                }
                if (object.containsProperty("FEE_TYPE1") && object.get("FEE_TYPE1") != null) {
                    String fee = object.get("FEE_TYPE1").toString();
                    if ("4052101".equals(productId) && "2000".equals(countType)
                            && "902".equals(fee)) {
                        return "ISHARE";
                    }
                }
            }
            if ("4052106".equals(productId)) {
                return "MODIFY_LANGUAGE";
            }
            if ("4052112".equals(productId)) {
                return "CHANGE MAIN PRODUCT";
            }
            if ("4052104".equals(productId)) {
                return "RELEASE_SUBSCRIBER_FROM_BLACKLIST";
            }
            if ("4052133".equals(productId)) {
                return "QUERY_ACCOUNT_BALANCE";
            }
            if ("4050001".equals(productId)) {
                return "SUBSCRIBE_TO_PRODUCT_FOR_INDIVIDUAL_SUBSCRIBER";
            }
            if ("4050022".equals(productId)) {
                return "CHANGE_SUBSCRIBER_PASSWORD";
            }
            if ("4052146".equals(productId)) {
                return "MANAGE_THE_CALL_SCREENING_SERVICE";
            }
            if ("4050005".equals(productId)) {
                return "CLAIM_MISSING";
            }
            if ("4050022".equals(productId)) {
                return "CHANGE_SUBSCRIBER_PASSWORD";
            }
            if ("4052128".equals(productId)) {
                return "CHANGE_VALIDITY_PERIOD_OF_SUBSCRIBER";
            }
        }
        return "UNKNOWN";
    }

}
//      WHEN PRODUCT_ID='4052146' then 'MANAGE THE CALL SCREENING SERVICE'  
//      WHEN PRODUCT_ID='4050005' then 'CLAIM MISSING'  
//      WHEN PRODUCT_ID='4050022' then 'CHANGE SUBSCRIBER PASSWORD'  
//      WHEN PRODUCT_ID='4052128' then 'CHANGE VALIDITY PERIOD OF SUBSCRIBER' 
//ELSE 'UNKNOWN' 
//END
