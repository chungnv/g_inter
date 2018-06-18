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
public class LongProcess {

    public static boolean process(Object left, Object right, String operator, boolean constant, Logger logger) {
        Long lLeft;
        Long lRight;
        try {
            lLeft = Long.parseLong(left.toString().trim());
            lRight = Long.parseLong(right.toString().trim());
        } catch (Exception ex) {
            return false;
        }
        if (operator == null || operator.trim().length() == 0) {
            return false;
        }
        if (("=".equals(operator)) && (lLeft.longValue() == lRight.longValue())) {
            return true;
        } else if ((">".equals(operator)) && (lLeft > lRight)) {
            return true;
        } else if (("<".equals(operator)) && (lLeft < lRight)) {
            return true;
        } else if ((!"=".equals(operator)) && (!">".equals(operator)) && (!"<".equals(operator)) && (lLeft.longValue() != lRight.longValue())) {
            return true;
        }
        return false;
    }
}

