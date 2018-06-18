/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author ubuntu
 */
public class NCdrBCDbytes extends BaseCdrField {

    public NCdrBCDbytes(int length, String description) {
        super(length, description);
    }

    public NCdrBCDbytes() {
        super();
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
//        String temp;
//        String vstrReturn = "";
//        for (int i = 0; i < super.length; i++) {
//            temp = fmHexByte(b[offset + i]);
//            if (temp != null)
//                vstrReturn += temp;
//            else
//                return vstrReturn;
//        }
//        return Integer.parseInt(vstrReturn);
        String temp;
        String vstrReturn = "";
        for (int i = 0; i < super.length; i++) {
            temp = fmBCDByte(b[offset + i]);
            if (temp != null)
                vstrReturn += temp;
            else
                return vstrReturn;
        }
        return vstrReturn;
    }

//    public String fmHexByte(byte pbyte) {
////        String vstrReturn = "";
////        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
////        char ch2 = Character.forDigit(pbyte & 0xf, 16);
////        vstrReturn = vstrReturn + ch1;
////        vstrReturn = vstrReturn + ch2;
////        return vstrReturn;
//        String sReturn = "";
//        byte h = (byte) ((pbyte & 0xf0) >> 4);
//        byte l = (byte)(pbyte & 0xf);
//
//        if (l < 10)
//            sReturn += Character.forDigit(l, 10);
//        else
//            return null;
//
//        if (h < 10)
//            return sReturn + Character.forDigit(h, 10);
//        else
//            return sReturn;
//    }
    public String fmBCDByte(byte pbyte) {
//        String vstrReturn = "";
//        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
//        char ch2 = Character.forDigit(pbyte & 0xf, 16);
//        vstrReturn = vstrReturn + ch1;
//        vstrReturn = vstrReturn + ch2;
//        return vstrReturn;
        String sReturn = "";
        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte) (pbyte & 0xf);
        
        if (l < 10) {
            sReturn += Character.forDigit(l, 10);
        } else {
            return null;
        }

        if (h < 10) {
            return sReturn + Character.forDigit(h, 10);
        } else {
            return sReturn;
        }
    }

}
