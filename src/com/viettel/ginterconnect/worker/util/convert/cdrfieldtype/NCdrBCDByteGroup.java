/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author chungdq
 * @version 1.0
 */
public class NCdrBCDByteGroup extends BaseCdrField {

    /**
     * @param description desciption
     * @param length length of
     * .*/
    public NCdrBCDByteGroup(int length, String description) {
        super(length, description);
    }
    /**
     * default constructor
     * .*/
    public NCdrBCDByteGroup() {
        super();
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        String temp;
        String vstrReturn = "";
        for (int i = super.length - 1; i >= 0; i--) {
            temp = fmBCDByte(b[offset + i]);
            if (temp != null) {
                vstrReturn += temp;
            } else {
                return vstrReturn;
            }
        }
        return Integer.parseInt(vstrReturn);
    }

    /**
     * @param pbyte bytes needs to be converted
     * @return string after convert
     * .*/
    public String fmBCDByte(byte pbyte) {
        String sReturn = "";
        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte)(pbyte & 0xf);
        if (h < 10) {
            sReturn += Character.forDigit(h, 10);
        } else {
            return null;
        }
        if (l < 10)
            return sReturn + Character.forDigit(l, 10);
        else
            return sReturn;
    }
}
