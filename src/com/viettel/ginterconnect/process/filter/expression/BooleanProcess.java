/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.filter.expression;

import org.apache.log4j.Logger;

/**
 *
 * @version 1.0
 * @since May 28, 2011
 */
public class BooleanProcess {

    public static boolean process(Object left, Object right, String operator, boolean constant, Logger logger) {
        if (left == null || right == null || operator == null) {
            //logger.warn("null value in sub ex");
            return false;
        }
        Boolean bRight;
        if (constant) {
            bRight = new Boolean(right.toString());
        } else {
            if (!(right instanceof Boolean)) {
                return false;
            }
            bRight = (Boolean) right;
        }
        if (("=".equals(operator)) && ((Boolean) left) && ((Boolean) bRight)) {
            return true;
        }
        if (("=".equals(operator)) && (!(Boolean) left) && (!(Boolean) bRight)) {
            return true;
        }
        if ((!"=".equals(operator)) && (!(Boolean) left) && ((Boolean) bRight)) {
            return true;
        }
        if ((!"=".equals(operator)) && ((Boolean) left) && (!(Boolean) bRight)) {
            return true;
        }
        return false;
    }
}

