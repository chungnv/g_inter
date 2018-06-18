package com.viettel.ginterconnect.worker.util.convert.ggsn.asn;

import codec.asn1.ASN1Exception;
import codec.asn1.ASN1IA5String;
import com.viettel.ginterconnect.worker.util.convert.ggsn.BasicFormat;

public class IA5String extends BasicFormat {

    public String decode(byte[] btValue, int iOffset, int iLength)
            throws Exception {
        byte[] dst = new byte[iLength];
        System.arraycopy(btValue, iOffset, dst, 0, iLength);
        ASN1IA5String decode = new ASN1IA5String();
        try {
            return decode.convert(dst).trim();
        } catch (ASN1Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public byte[] encode(String src) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}