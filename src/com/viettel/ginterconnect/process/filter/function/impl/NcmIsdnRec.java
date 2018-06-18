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
 * @author ubuntu
 */
public class NcmIsdnRec implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
//        CASE
//              WHEN ISDN LIKE '509%'
//              THEN
//                  SUBSTR (ISDN, 4)
//              WHEN ISDN LIKE '00%'
//              THEN
//                  SUBSTR (ISDN, 3)
//              WHEN ISDN LIKE '0%'
//              THEN
//                  SUBSTR (ISDN, 2)
//              ELSE
//                  ISDN
//        END
        String isdn = object.get("ISDN").toString();
        if (isdn.startsWith("509")) {
            return isdn.substring(3);
        }
        if (isdn.startsWith("00")) {
            return isdn.substring(2);
        }
        if (isdn.startsWith("0")) {
            return isdn.substring(1);
        }
        return isdn;
    }
    
}
