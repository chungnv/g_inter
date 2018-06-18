package com.viettel.ginterconnect.worker.util.convert.ggsn;

public class MsIsdnString extends BasicFormat {
    
    public String decode(byte[] btValue, int iOffset, int iLength)
            throws Exception {
        TBCDString formatStr = (TBCDString) AsnFormatFactory.getAsnFormat("TBCDString");
        String fullNumber = formatStr.decode(btValue, iOffset, iLength);
        
        if ((fullNumber != null) && (fullNumber.length() > 3) && fullNumber.startsWith("19")) {
            return fullNumber.substring(2);
        }
        
        return fullNumber;
    }
    
    public byte[] encode(String src) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
