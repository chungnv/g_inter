/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.filter.expression;

import org.apache.log4j.Logger;

/**
 *
 * @version 1.0
 * @since Jul 28, 2011
 */
public class StringProcess {

    public static boolean process(Object left, Object right, String operator, boolean constant, Logger logger) {
        if (operator == null) {
            return false;
        }
        if (("=".equals(operator)) && ((left == null && right == null) || ((left != null && right != null) && (left.toString().equals(right.toString()))))) {
            return true;
        } else if ((">".equals(operator)) 
                && (((left != null && !"".equals(left)) && right == null)
                    || (left != null && right != null && left.toString().compareTo(right.toString()) > 0))) {
            return true;
        } else if (("<".equals(operator)) 
                && ((left == null && (right != null && !"".equals(right)))
                    || (left != null && right != null && left.toString().compareTo(right.toString()) < 0))) {
            return true;
        } else if ((!"=".equals(operator)) && (!">".equals(operator)) && (!"<".equals(operator)) 
                && ((right == null && (left != null && !"".equals(left.toString().trim())))
                    || (left == null && (right != null && !"".equals(right.toString().trim())))
                    || (left != null && right != null && !left.toString().equals(right.toString())))) {
            return true;
        }
        return false;
    }
}

