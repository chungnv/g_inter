/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

public class NCdrCauseForTerminate extends BaseCdrField {

    /**
     * Default constructor
     */
    public NCdrCauseForTerminate() {
        super();
    }

    public NCdrCauseForTerminate(int length, String description) {
        super(length, description);
    }

    public String fmHEXByte(byte pbyte) {
        String sReturn = "";
        char hexChars[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte) (pbyte & 0xf);

        sReturn += hexChars[l];
        sReturn += hexChars[h];
        return sReturn;
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {

        String strReturn = "";
        String temp;
        for (int i = 0; i < super.length; i++) {
            temp = fmHEXByte(b[offset + i]);
            if (temp != null) {
                strReturn += temp;
            } else {
                return strReturn;
            }
        }
        // Kiem tra neu toan la FFF..FF thi` tra lai xau rong
        char[] allFs = new char[length * 2];
        for (int i = 0; i < allFs.length; i++) {
            allFs[i] = 'F';
        }
        if (new String(allFs).equals(strReturn)) {
            return "";
        } else {
            StringBuffer dest = new StringBuffer();
            for (int i = strReturn.length() - 1; i >= 0; i--) {
                dest.append(strReturn.charAt(i));
            }
            if (dest != null && !"".equals(dest.toString())) {
                return dest;
            }
            return strReturn;
        }

    }
}

