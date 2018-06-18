/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author chungdq
 * 
 * to get location of Huawei MSC and GMSC = LAC + LAI
 */
public class CdrHuaweiLocation extends BaseCdrField {

    public CdrHuaweiLocation(int length, String description) {
        super(length, description);
    }

    public CdrHuaweiLocation() {
        super();
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        int iLastOffset = offset + super.getLength();
        StringBuffer value = new StringBuffer();
        for (int i = offset; i < iLastOffset; i++) {
            byte h = (byte) ((b[i] & 0xf0) >>> 4);
            if (h < 10) {
                h = (byte) (48 + h);
            } else {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) (b[i] & 0xf);
            if (l < 10) {
                l = (byte) (48 + l);
            } else {
                l = (byte) ((65 + l) - 10);
            }
            value.append((char) l);
            value.append((char) h);
//            value.append((char) h);
//            value.append((char) l);
        }
        String vReturn = value.toString();
        if (vReturn.length() != 16) {
            return "";
        } else {
            return vReturn.substring(4, 8) + vReturn.substring(12, 16);
        }
    }
}
