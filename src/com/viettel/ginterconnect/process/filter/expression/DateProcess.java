/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.filter.expression;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @version 1.0
 * @since Jul 29, 2011
 */
public class DateProcess {

    public static boolean process(Object left, Object right, String operator, boolean constant, Logger logger) {
        //date phai theo dang yyyy-MM-dd hh:mm:ss
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        if (left == null || right == null || operator == null) {
            return false;
        }
        Date rightDate = new Date();
        Date leftDate = new Date();
        boolean rightIsNotDate = false;
        if (constant) {
            try {
                rightDate = sdf.parse(right.toString().trim());
                leftDate = (Date) left;
            } catch (Exception ex) {
//                ex.printStackTrace();
//                return false;
                System.out.println("Parse right ex failed. Value: " + right);
                rightIsNotDate = true;
            }
        } else {
            try {
                rightDate = (Date) right;
                leftDate = (Date) left;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        if (rightIsNotDate && ("=".equals(operator))) {
            return false;
        }
        if (rightIsNotDate) {
            return true;
        }
        if (("=".equals(operator)) && (leftDate.equals(rightDate))) {
            return true;
        } else if ((">".equals(operator)) && (leftDate.after(rightDate))) {
            return true;
        } else if (("<".equals(operator)) && (leftDate.before(rightDate))) {
            return true;
        } else if ((!"=".equals(operator)) && (!">".equals(operator)) && (!"<".equals(operator)) && (!leftDate.equals(rightDate))) {
            return true;
        }

        return false;
    }
}

