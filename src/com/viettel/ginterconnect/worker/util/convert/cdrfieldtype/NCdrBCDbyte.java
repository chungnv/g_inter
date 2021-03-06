/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author ubuntu
 */
public class NCdrBCDbyte extends BaseCdrField {

    public NCdrBCDbyte(int length, String description) {
        super(length, description);
    }

    public NCdrBCDbyte() {
        super();
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        String vstrReturn = "";
        try {
            char ch1 = Character.forDigit(b[offset] >> 4 & 0xf, 10);
            char ch2 = Character.forDigit(b[offset] & 0xf, 10);

            vstrReturn = vstrReturn + ch1;
            vstrReturn = vstrReturn + ch2;
            vstrReturn = vstrReturn.replaceAll("f", "");
            vstrReturn = vstrReturn.replaceAll(" ", "");
            vstrReturn = vstrReturn.replaceAll("F", "");
            vstrReturn = vstrReturn.replaceAll("\u0000", "");
            vstrReturn = vstrReturn.replaceAll(" ", "");
            if (vstrReturn.length() < 1){
                vstrReturn = "0";
            }
            return Integer.parseInt(vstrReturn);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public String fmHexByte(byte pbyte) {
        String vstrReturn = "";
        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
        char ch2 = Character.forDigit(pbyte & 0xf, 16);
        vstrReturn = vstrReturn + ch1;
        vstrReturn = vstrReturn + ch2;
        return vstrReturn;
    }
}
