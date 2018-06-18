/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author chungdq
 * @version 1.0
 */
public class CdrDateTime extends BaseCdrField {
    /** .*/
    private int flagF7 = 0; //0 - begin date time; 1 - end date time
    /** .*/
    private int flagF0 = 1; //0 - year + 1900; 1 - year + 2000
    /** .*/
    private int flagF8 = 0; //0 - time secure; 1 - time insecure

    /**
     * Default constructor
     */
    public CdrDateTime() {
        super();
    }

    /**
     * constructor
     * @param length length of datetime
     * @param description description
     * .*/
    public CdrDateTime(int length, String description) {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        //check null
        boolean isNull = true;
        for (int i = 0; i < 6; i++){
            if ((b[offset + i] & 0xff) != 0xff) {
                isNull = false;
                break;
            }
        }
        if (isNull) {
            return "";
        }
        Calendar cal = Calendar.getInstance();
        if (getLength() > 6) {
            flagF8 = (b[offset + 6] & 0x80) > 0 ? 1 : 0;
            flagF7 = (b[offset + 6] & 0x70) > 0 ? 1 : 0;
            flagF0 = (b[offset + 6] & 0x01) > 0 ? 1 : 0;
        }
        int year = b[offset++];
        if (flagF0 == 0) {
            year += 1900;
        }
        else {
            year += 2000;
        }
        cal.set(year, b[offset++] - 1, b[offset++], b[offset++], b[offset++], b[offset++]);

        return cal.getTime();
    }

    public void refineTime(int duration)
    {
        if (flagF7 == 1) {
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) value);
            cal.add(Calendar.SECOND, -duration);
            value = cal.getTime();
        }
    }
}
