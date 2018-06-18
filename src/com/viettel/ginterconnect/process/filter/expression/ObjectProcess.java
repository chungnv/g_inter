/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.filter.expression;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * 
 * @version 1.0
 * @since Jul 28, 2011
 */
public class ObjectProcess {

    /**
     * xu li cac nghiep vu tuong ung trong bieu thuc sub_expression
     * viec chon ham xu li thong qua gia tri tra ve cua ve trai
     * la kieu gi: Long hay String hay Boolean hay Double..
     *
     * @param left     ket qua ve trai
     * @param right    ket qua ve phai
     * @param operator toan tu
     * @param constant ve phai co phai constant khong
     * @param logger   de ghi log
     * @return true neu bieu thuc tra ve dung va false nguoc lai
     */
    public boolean process(Object left, Object right, String operator, boolean constant, Logger logger) {

        if (left instanceof Long) {
            return LongProcess.process(left, right, operator, constant, logger);
        } else if (left instanceof String) {
            return StringProcess.process(left, right, operator, constant, logger);
        } else if (left instanceof Boolean) {
            return BooleanProcess.process(left, right, operator, constant, logger);
        } else if (left instanceof Double) {
            return DoubleProcess.process(left, right, operator, constant, logger);
        } else if (left instanceof Date) {
            return DateProcess.process(left, right, operator, constant, logger);
        } else if (left == null && right == null && operator.equals("=")) {
            return true;
        }
        return false;
    }
}

