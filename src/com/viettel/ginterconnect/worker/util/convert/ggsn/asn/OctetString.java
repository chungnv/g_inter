package com.viettel.ginterconnect.worker.util.convert.ggsn.asn;

import com.viettel.ginterconnect.worker.util.convert.ggsn.BasicFormat;

public class OctetString extends BasicFormat {

    @Override
    public String decode(byte[] btValue, int iOffset, int iLength)
            throws Exception {
        int iLastOffset = iOffset + iLength;
        if ((btValue.length < iLastOffset) || (iLength < 1)) {
            return "";
        }
        StringBuilder value = new StringBuilder();
        for (int i = iOffset; i < iLastOffset; i++) {
            byte h = (byte) ((btValue[i] & 0xF0) >> 4);
            if (h < 10) {
                h = (byte) (48 + h);
            } else {
                h = (byte) (65 + h - 10);
            }
            byte l = (byte) (btValue[i] & 0xF);
            if (l < 10) {
                l = (byte) (48 + l);
            } else {
                l = (byte) (65 + l - 10);
            }
            value.append((char) l);
            value.append((char) h);
        }

        return value.toString();
    }

    @Override
    public byte[] encode(String src) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
