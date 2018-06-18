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
public class NCdrBCDPhoneNumber extends BaseCdrField {

    /**
     * Default constructor
     */
    public NCdrBCDPhoneNumber() {
        super();
    }

    public NCdrBCDPhoneNumber(int length, String description) {
        super(length, description);
    }

    public String fmBCDByte(byte pbyte) {
        String sReturn = "";
        char hexChars[] = {'0','1','2','3','4','5','6','7','8','9'};
        int charsLen = hexChars.length;
        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte) (pbyte & 0xf);
        
        sReturn += (h < charsLen) ? hexChars[h] : "";
        sReturn += (l < charsLen) ? hexChars[l] : "";
        return sReturn;
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        String vstrReturn = "";
        for (int i = 0; i < super.length; i++) {
            vstrReturn = vstrReturn + fmBCDByte(b[offset + i]);
        }
        return vstrReturn;
    }
}
