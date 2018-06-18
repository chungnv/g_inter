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
public class DoubleProcess {

    public static boolean process(Object left, Object right, String operator, boolean constant, Logger logger) {
        Double lLeft;
        Double lRight;
        try {
            lLeft = Double.parseDouble(left.toString().trim());
            lRight = Double.parseDouble(right.toString().trim());
        } catch (Exception ex) {
            return false;
        }
        if (operator == null || operator.trim().length() == 0) {
            return false;
        }
        if (("=".equals(operator)) && (lLeft == lRight)) {
            return true;
        } else if ((">".equals(operator)) && (lLeft > lRight)) {
            return true;
        } else if (("<".equals(operator)) && (lLeft < lRight)) {
            return true;
        } else if ((!"=".equals(operator)) && (!">".equals(operator)) && (!"<".equals(operator)) && (lLeft != lRight)) {
            return true;
        }
        return false;
    }
}

