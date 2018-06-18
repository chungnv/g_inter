package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

import java.util.Calendar;

public class ZteNGNCdrDateTime extends BaseCdrField {

    public ZteNGNCdrDateTime() {
    }

    public ZteNGNCdrDateTime(int length, String description) {
        super(length, description);
    }

    protected Object unpackDetail(byte[] b, int offset)
            throws Exception {
        long second = getTimeInSecond(b, 4, offset);
        long ms = getBcdValue(b, 1, offset + 4);
        long timestamp = second * 1000L + ms * 10L;
        Calendar c = Calendar.getInstance();
        c.set(2000, 0, 1, 0, 0, 0);
        long c2 = c.getTimeInMillis() + timestamp;
        c.setTimeInMillis(c2);

        return c.getTime();
    }

    public long getBcdValue(byte[] b, int length, int offset) {
        String vstrReturn = "";
        for (int i = length - 1; i >= 0; i--) {
            String temp = fmHEXByte(b[(offset + i)]);
            if (temp != null) {
                vstrReturn = vstrReturn + temp;
            } else {
                return 0L;
            }
        }
        return Long.parseLong(vstrReturn, 16);
    }

    public String fmHEXByte(byte pbyte) {
        String vstrReturn = "";
        char ch1 = Character.forDigit(pbyte >> 4 & 0xF, 16);
        char ch2 = Character.forDigit(pbyte & 0xF, 16);
        vstrReturn = vstrReturn + ch1;
        vstrReturn = vstrReturn + ch2;
        return vstrReturn;
    }

    public long getTimeInSecond(byte[] b, int length, int offset) {
        long l = 0L;
        for (int i = offset; i < offset + length; i++) {
            l <<= 8;
            l |= b[i] & 0xFF;
        }
        return l;
    }
}