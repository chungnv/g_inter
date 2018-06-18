package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

public class ZteNGNCdrBCDByteGroup extends BaseCdrField {

    public ZteNGNCdrBCDByteGroup(int length, String description) {
        super(length, description);
    }

    public ZteNGNCdrBCDByteGroup() {
    }

    protected Object unpackDetail(byte[] b, int offset)
            throws Exception {
        String vstrReturn = "";
        for (int i = this.length - 1; i > 0; i--) {
            String temp = fmBCDByte(b[(offset + i)]);
            if (temp != null) {
                vstrReturn = vstrReturn + temp;
            } else {
                return vstrReturn;
            }
        }
        return Integer.valueOf(Integer.parseInt(vstrReturn));
    }

    public String fmBCDByte(byte pbyte) {
        String sReturn = "";
        byte h = (byte) ((pbyte & 0xF0) >> 4);
        byte l = (byte) (pbyte & 0xF);
        if (h < 10) {
            sReturn = sReturn + Character.forDigit(h, 10);
        } else {
            return null;
        }
        if (l < 10) {
            sReturn = sReturn + Character.forDigit(l, 10);
        } else {
            sReturn = sReturn + "";
        }
        return sReturn;
    }
}