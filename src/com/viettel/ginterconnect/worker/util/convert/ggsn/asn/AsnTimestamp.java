package com.viettel.ginterconnect.worker.util.convert.ggsn.asn;

import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.worker.util.convert.ggsn.BasicFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AsnTimestamp extends BasicFormat {

    private SimpleDateFormat fmtDateTime = null;

    public String decode(byte[] btValue, int iOffset, int iLength)
            throws Exception {
        if (fmtDateTime == null) {
            if (Constants.GGSN_DEFAULT_DATE_FORMAT == null || Constants.GGSN_DEFAULT_DATE_FORMAT.length() < 1) {
                fmtDateTime = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
            } else {
                fmtDateTime = new SimpleDateFormat(Constants.GGSN_DEFAULT_DATE_FORMAT);
            }
        }
        if ((btValue.length < iOffset + iLength) || (iLength < 9)) {
            return "";
        }
        Date dtReturn = null;
        long lOffset = 0L;

        dtReturn = new Date(getBCDValue(btValue[iOffset]) < 30 ? getBCDValue(btValue[iOffset]) + 100 : getBCDValue(btValue[iOffset]), getBCDValue(btValue[(iOffset + 1)]) - 1, getBCDValue(btValue[(iOffset + 2)]), getBCDValue(btValue[(iOffset + 3)]), getBCDValue(btValue[(iOffset + 4)]), getBCDValue(btValue[(iOffset + 5)]));

        if (btValue[(iOffset + 6)] == 45) {
            lOffset = -lOffset;
        } else if (btValue[(iOffset + 6)] != 43) {
            lOffset = 0L;
        }

        dtReturn.setTime(dtReturn.getTime() - lOffset);
        try {
            return fmtDateTime.format(dtReturn);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("###############");
            System.out.println("dtReturn: " + dtReturn + ". Offset: " + iOffset + ". Length: " + iLength);
            System.out.println("###############");
            throw ex;
        }
    }

    public byte[] encode(String src) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private int getBCDValue(byte btValue) {
        int iReturn = ((btValue & 0xF0) >>> 4) * 10;
        iReturn += (btValue & 0xF);
        return iReturn;
    }
}
