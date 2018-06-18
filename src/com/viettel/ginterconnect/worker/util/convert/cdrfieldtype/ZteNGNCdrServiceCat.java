package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

public class ZteNGNCdrServiceCat extends BaseCdrField {

    public ZteNGNCdrServiceCat() {
    }

    public ZteNGNCdrServiceCat(int length, String description) {
        super(length, description);
    }

    public String fmBCDByte(byte pbyte) {
        String sReturn = "";
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int charsLen = hexChars.length;
        byte h = (byte) ((pbyte & 0xF0) >> 4);
        byte l = (byte) (pbyte & 0xF);

        sReturn += (h < charsLen) ? Character.valueOf(hexChars[h]) : "";
        sReturn += (l < charsLen) ? Character.valueOf(hexChars[l]) : "";
        return sReturn;
    }

    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        String vstrReturn = "";
        for (int i = 0; i < this.length; i++) {
            vstrReturn = vstrReturn + fmBCDByte(b[(offset + i)]).toString();
        }
        return vstrReturn;
    }
}