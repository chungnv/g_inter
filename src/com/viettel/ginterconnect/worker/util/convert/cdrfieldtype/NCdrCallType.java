/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author chungdq
 */
public class NCdrCallType extends BaseCdrField {

    private static final int DIRECTION_IN = 0;
    private static final int DIRECTION_OUT = 3;
    private static final int TRANSIT = 1;

    public NCdrCallType(int length, String description) {
        super(length, description);
    }

    public NCdrCallType() {
        super();
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        Integer temp = Integer.parseInt(fmHEXByte(b[offset]), 16);
        switch(temp.intValue())
        {
            case DIRECTION_IN:
                return "I";
            case DIRECTION_OUT:
                return "O";
            case TRANSIT:
                return "T";
            default:
                return "D";
        }
    }

    public String fmHEXByte(byte pbyte) {
        String vstrReturn = "";
        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
        char ch2 = Character.forDigit(pbyte & 0xf, 16);
        vstrReturn = vstrReturn + ch1;
        vstrReturn = vstrReturn + ch2;
        return vstrReturn;
    }
}
