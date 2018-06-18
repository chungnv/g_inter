/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author duclv
 */
public class NCdrTon extends BaseCdrField {

    public NCdrTon(int length, String description) {
        super(length, description);
    }

    public NCdrTon() {
        super();
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        String temp;
        String vstrReturn = "";
        for (int i = super.length-1; i >= 0 ; i--) {
            temp = fmBCDByte(b[offset + i]);
            if (temp != null)
                vstrReturn += temp;
            else
                return vstrReturn;
        }
        int ton = Integer.parseInt(vstrReturn);
        switch (ton) {
            case 4 :
                return "0";
            case 5:
                return "1";
            case 6:
                return "2";
            default:
                return "0";
        }
    }

    public String fmBCDByte(byte pbyte) {
        String sReturn = "";
        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte)(pbyte & 0xf);

        if (h < 10)
            sReturn += Character.forDigit(h, 10);
        else
            return null;

        if (l < 10)
            return sReturn + Character.forDigit(l, 10);
        else
            return sReturn;
    }
}
