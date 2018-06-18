/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author duclv
 */
public class NCdrCauseForForwarding extends BaseCdrField {

    public NCdrCauseForForwarding(int length, String description) {
        super(length, description);
    }

    public NCdrCauseForForwarding() {
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
      //  System.out.println("vstrReturn"+ vstrReturn);
        return vstrReturn;
    }

    public String fmBCDByte(byte pbyte) {
        String sReturn = "";
        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte) (pbyte & 0xf);

        if (h < 10) {
            sReturn += Character.forDigit(h, 10);
        } else {

            return null;
        }

        if (l < 10) {
          //  System.out.println("Ho Ho" + sReturn + Character.forDigit(l, 10));
            return sReturn + Character.forDigit(l, 10);
        } else if (l >= 10) {
           
            return sReturn + Integer.toHexString(l).toUpperCase().toString();
        } else {
            // System.out.println("he he" + Integer.toHexString(l).toUpperCase().toString() + Character.forDigit(l, 10));
          
            return sReturn;// Integer.toHexString(l).toUpperCase().toString() + Character.forDigit(l, 10);

        }
    }
}
