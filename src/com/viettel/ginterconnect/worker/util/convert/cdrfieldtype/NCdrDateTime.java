/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

import java.util.Calendar;

/**
 *
 * @author ubuntu
 */
public class NCdrDateTime extends BaseCdrField {

    /**
     * Default constructor
     */
    public NCdrDateTime() {
        super();
    }

    public NCdrDateTime(int length, String description) {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        Calendar cal = Calendar.getInstance();
        int year = 0;

        String year1 = getBCDString(b[offset + 6]);
        String year2 = getBCDString(b[offset + 5]);
        year = Integer.parseInt(year1 + year2);

        int month = getBCDValue(b[offset + 4]);
        int day = getBCDValue(b[offset + 3]);
        int hour = getBCDValue(b[offset + 2]);
        int minute = getBCDValue(b[offset + 1]);
        int second = getBCDValue(b[offset + 0]);

        cal.set(year, month - 1, day, hour, minute, second);

        return cal.getTime();
    }

    private String getBCDString(byte btValue) {
        byte h = (byte) ((btValue & 0xf0) >>> 4);
        if (h < 10) {
            h = (byte) (48 + h);
        } else {
            h = (byte) ((65 + h) - 10);
        }
        byte l = (byte) (btValue & 0xf);
        if (l < 10) {
            l = (byte) (48 + l);
        } else {
            l = (byte) ((65 + l) - 10);
        }
        return String.valueOf((char) h) + String.valueOf((char) l);
    }

    private int getBCDValue(byte btValue) {
//        int iReturn = ((btValue & 0xf0) >>> 4) * 10;
//        iReturn += btValue & 0xf;
//        return iReturn;
        int iReturn = 0;
        byte h = (byte) ((btValue & 0xf0) >> 4);
        if (h < 10)
            iReturn += h * 10;
        else
            return 0;
        
        byte l = (byte)(btValue & 0xf);
        if (l < 10)
            return iReturn + l;
        else
            return 0;
    }
}
