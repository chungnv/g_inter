/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author ubuntu
 */
public class ZteNGNCdrPhoneNumber extends BaseCdrField {
    
    public ZteNGNCdrPhoneNumber() {
        super();
    }
    
    public ZteNGNCdrPhoneNumber(int length, String description) {
        super(length, description);
    }
    
    public String fmBCDByte(byte pbyte) {
        String sReturn = "";
        char hexChars[] = {'0','1','2','3','4','5','6','7','8','9','0'};
        int charsLen = hexChars.length;
        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte) (pbyte & 0xf);

        sReturn += ((l > 0) && (l < charsLen)) ? hexChars[l] : "";
        sReturn += ((h > 0) && (h < charsLen)) ? hexChars[h] : "";
        return sReturn;
    }
    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception 
    {
        String vstrReturn = "";
        for (int i = 0; i < super.length; i++) {
            vstrReturn = vstrReturn + fmBCDByte(b[offset + i]);
        }
        return vstrReturn;
    }
}
