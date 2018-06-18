package com.viettel.ginterconnect.worker.util.convert.ggsn;

public class AsnIntegerRSN extends BasicFormat {

    public String decode(byte[] btValue, int iOffset, int iLength)
            throws Exception {
        int iLastOffset = iOffset + iLength;
        if ((btValue.length < iLastOffset) || (iLength < 1)) {
            return "";
        }
        long iValue = 0;
        for (int iIndex = iOffset; iIndex < iLastOffset; iIndex++) {
            iValue <<= 8;
            iValue |= btValue[iIndex] & 0xFF;
        }

        return String.valueOf(iValue);
    }

    public byte[] encode(String src) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
