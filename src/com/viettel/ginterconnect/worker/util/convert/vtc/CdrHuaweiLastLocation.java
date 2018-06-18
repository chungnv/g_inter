/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.ginterconnect.worker.util.convert.vtc;

public class CdrHuaweiLastLocation extends BaseCdrField {

    public CdrHuaweiLastLocation(int length, String description) {
        super(length, description);
    }

    public CdrHuaweiLastLocation() {
        super();
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        int iLastOffset = offset + length;
        StringBuffer strValue = new StringBuffer();
        if (length < 22) {
            return null;
        } 
        if (length % 22 != 0) {
            return null;
        }
        int iFirstOffset = length - 22 + 7;
        for (int i = offset + iFirstOffset; i < iLastOffset - 11; i++) {
            byte h = (byte) ((b[i] & 0xf0) >>> 4);
            if (h < 10) {
                h = (byte)(48 + h);
            } else {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) ((b[i] & 0xf));
            if (l < 10) {
                l = (byte)(48 + l);
            } else {
                l = (byte)((65 + l) - 10);
            }
            //if (l != 70) {
                strValue.append((char)l);
            //}
            //if (h != 70) {
                strValue.append((char)h);
            //}
        }
        return strValue;
    }
}

