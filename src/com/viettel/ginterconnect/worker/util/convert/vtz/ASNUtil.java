package com.viettel.ginterconnect.worker.util.convert.vtz;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// Referenced classes of package com.fss.viettelcrm.asn1:
import java.util.GregorianCalendar;
import java.util.List;
//            ASNData
public class ASNUtil {

    public static DecimalFormat FORMAT_00 = new DecimalFormat("00");
    public static DecimalFormat FORMAT_SHARP = new DecimalFormat("#");
    public static SimpleDateFormat fmtDateTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public ASNUtil() {
    }

    public static String fmBCDByte(byte pbyte) {
        String vstrReturn = "";
        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
        char ch2 = Character.forDigit(pbyte & 0xf, 16);
        vstrReturn = vstrReturn + ch2;
        vstrReturn = vstrReturn + ch1;
        return vstrReturn;
    }
    
    public static String formatEricSuppCode(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        StringBuilder value = new StringBuilder();
        int i = iOffset + 1;
        byte h = (byte) ((btValue[i] & 0xf0) >>> 4);
        if (h < 10) {
            h = (byte) (48 + h);
        } else {
            h = (byte) ((65 + h) - 10);
        }
        byte l = (byte) (btValue[i] & 0xf);
        if (l < 10) {
            l = (byte) (48 + l);
        } else {
            l = (byte) ((65 + l) - 10);
        }

        if (h != 70) {
            value.append((char) h);
        }
        if (l != 70) {
            value.append((char) l);
        }
//        }
        if (value != null) {
            String s = value.toString();
            if ("03".equals(s)) {
                i++;
                StringBuilder toi = new StringBuilder();
                h = (byte) ((btValue[i] & 0xf0) >>> 4);
                if (h < 10) {
                    h = (byte) (48 + h);
                } else {
                    h = (byte) ((65 + h) - 10);
                }
                l = (byte) (btValue[i] & 0xf);
                if (l < 10) {
                    l = (byte) (48 + l);
                } else {
                    l = (byte) ((65 + l) - 10);
                }

                if (h != 70) {
                    toi.append((char) h);
                }
                if (l != 70) {
                    toi.append((char) l);
                }

//                if (toi != null) {
                String x = toi.toString();
                if ("03".equals(x)) {
                    return "21";
                } else if ("04".equals(x)) {
                    return "2A";
                } else if ("05".equals(x)) {
                    return "29";
                } else if ("32".equals(x)) {
                    return "2B";
                }
//                }
            }
            return null;
        } else {
            return null;
        }
    }
    
    public static String getTextByte(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        StringBuffer value = new StringBuffer();
        for (int i = iOffset; i < iLastOffset; i++) {
            byte h = (byte) ((btValue[i] & 0xf0) >>> 4);
            if (h < 10) {
                h = (byte) (48 + h);
            } else {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) (btValue[i] & 0xf);
            if (l < 10) {
                l = (byte) (48 + l);
            } else {
                l = (byte) ((65 + l) - 10);
            }
            if (h != 70) {
                value.append((char) h);
            }
            if (l != 70) {
                value.append((char) l);
            }
        }

        return value.toString();
    }
    
    public static String formatHWSuppCode(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        List<String> lstValidSuppCode = new ArrayList<String>();
        lstValidSuppCode.add("20");
        lstValidSuppCode.add("21");
        lstValidSuppCode.add("28");
        lstValidSuppCode.add("29");
        lstValidSuppCode.add("2A");
        lstValidSuppCode.add("2B");
        StringBuffer value = new StringBuffer();
        int i = iOffset + 4;
//        for (int i = iOffset; i < iLastOffset; i++)
//        {
        byte h = (byte) ((btValue[i] & 0xf0) >>> 4);
        if (h < 10) {
            h = (byte) (48 + h);
        } else {
            h = (byte) ((65 + h) - 10);
        }
        byte l = (byte) (btValue[i] & 0xf);
        if (l < 10) {
            l = (byte) (48 + l);
        } else {
            l = (byte) ((65 + l) - 10);
        }

        if (h != 70) {
            value.append((char) h);
        }
        if (l != 70) {
            value.append((char) l);
        }
//        }
        if (lstValidSuppCode.contains(value.toString())) {
            return value.toString();
        } else {
            return null;
        }
    }

    public static String formatInteger(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        long iValue = 0;
        for (int iIndex = iOffset; iIndex < iLastOffset; iIndex++) {
            iValue <<= 8;
            iValue |= btValue[iIndex] & 0xff;
        }

        return String.valueOf(iValue);
    }

    public static String formatChargeDuration(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength != 3) {
            return "";
        }
        int iValue = 0;
        int hours = 0, mins = 0, secs = 0;

        hours |= btValue[iOffset] & 0xff;
        mins |= btValue[iOffset + 1] & 0xff;
        secs |= btValue[iOffset + 2] & 0xff;

        iValue = hours * 60 * 60 + mins * 60 + secs;

        return String.valueOf(iValue);
    }

    public static String formatBoolean(byte btValue) {
        if (btValue == -1) {
            return "TRUE";
        } else {
            return "FALSE";
        }
    }

    public static String formatHEX(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        StringBuilder value = new StringBuilder();
        for (int i = iOffset; i < iLastOffset; i++) {
            byte h = (byte) ((btValue[i] & 0xf0) >>> 4);
            if (h < 10) {
                h = (byte) (48 + h);
            } else {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) (btValue[i] & 0xf);
            if (l < 10) {
                l = (byte) (48 + l);
            } else {
                l = (byte) ((65 + l) - 10);
            }
            value.append((char) h);
            value.append((char) l);
        }

        return value.toString();
    }

    public static String formatBCD(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        StringBuffer value = new StringBuffer();
        for (int i = iOffset; i < iLastOffset; i++) {
            byte h = (byte) ((btValue[i] & 0xf0) >>> 4);
            if (h < 10) {
                h = (byte) (48 + h);
            } else {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) (btValue[i] & 0xf);
            if (l < 10) {
                l = (byte) (48 + l);
            } else {
                l = (byte) ((65 + l) - 10);
            }
            value.append((char) h);
            value.append((char) l);
        }

        return value.toString();
    }

    public static String formatSequence(String dateFormat, byte btValue[], int iOffset, int iLength, int seqType, String pattern) {
        if (seqType == 16) {
            int countServices = iLength / seqType;
            String strSeq = "";
            for (int i = 0; i < countServices; i++) {
                String serType = getStrByte(btValue[iOffset + i * 16 + 4]);
                String serTime = formatHuaWeiTimeStamp(dateFormat, btValue, iOffset + i * 16 + 7, 9, 0);
                strSeq += serType + " " + serTime + pattern;
            }
            if (strSeq.endsWith(pattern)) {
                return strSeq.substring(0, strSeq.length() - 1);
            } else {
                return strSeq;
            }
        } else {
            return null;
        }
    }
    
    public static String formatByteToString(byte btValue[], int iOffset, int iLength) {
        String strReturn = "";
        for (int i = 0; i < iLength; i++) {
            strReturn += getStrByte(btValue[iOffset + i]);
        }
        return strReturn;
    }

    public static String formatTBCD(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        StringBuffer value = new StringBuffer();
        for (int i = iOffset; i < iLastOffset; i++) {
            byte h = (byte) ((btValue[i] & 0xf0) >>> 4);
            if (h < 10) {
                h = (byte) (48 + h);
            } else {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) (btValue[i] & 0xf);
            if (l < 10) {
                l = (byte) (48 + l);
            } else {
                l = (byte) ((65 + l) - 10);
            }
            if (l != 70) {
                value.append((char) l);
            }
            if (h != 70) {
                value.append((char) h);
            }
        }

        return value.toString();
    }

    public static String formatAddressString(byte btValue[], int iOffset, int iLength) {
        return formatTBCD(btValue, iOffset + 1, iLength - 1);
    }

    public static String formatDate(byte btValue[], int iOffset, int iLength) {
        if (btValue.length < iOffset + iLength || iLength < 4) {
            return "";
        } else {
            return getBCDString(btValue[iOffset + 3]) + "/" + getBCDString(btValue[iOffset + 2]) + "/" + getBCDString(btValue[iOffset + 0]) + getBCDString(btValue[iOffset + 1]);
        }
    }

    public static String formatErissonDate(byte btValue[], int iOffset, int iLength) {

        if (btValue.length < iOffset + iLength || iLength < 4) {
            return "";
        } else {
            String result = getBCDString(btValue[iOffset + 3]) + "/" + getBCDString(btValue[iOffset + 2]) + "/" + getBCDString(btValue[iOffset + 0]) + "" + getBCDString(btValue[iOffset + 1]);
            return result;
        }
    }

    public static String formatTime(byte btValue[], int iOffset, int iLength) {
        if (btValue.length < iOffset + iLength || iLength < 3) {
            return "";
        } else {
            return getBCDString(btValue[iOffset + 0]) + ":" + getBCDString(btValue[iOffset + 1]) + ":" + getBCDString(btValue[iOffset + 2]);
        }
    }

    public static Long getBCDTime(byte btValue[], int iOffset, int iLength) {
        return (long) ((btValue[iOffset] * 60 * 60 + btValue[iOffset + 1] * 60 + btValue[iOffset + 2]) * 1000);
    }

    public static Long getBCDDate(byte btValue[], int iOffset, int iLength) {
        Calendar calendar = new GregorianCalendar((int) (btValue[iOffset]) * 100 + btValue[iOffset + 1], (int) (btValue[iOffset + 2] - 1), (int) (btValue[iOffset + 3]), 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    public static String formatTimeStamp(byte btValue[], int iOffset, int iLength, int iUtcOffset) {
        if (btValue.length < iOffset + iLength || iLength < 9) {
            return "";
        }
        Date dtReturn = null;
        long lOffset = 0L;
        if (btValue[iOffset + 2] == 43 || btValue[iOffset + 2] == 45) {
            dtReturn = new Date(getBCDValue(btValue[iOffset + 8]), getBCDValue(btValue[iOffset + 7]) - 1, getBCDValue(btValue[iOffset + 6]), getBCDValue(btValue[iOffset + 5]), getBCDValue(btValue[iOffset + 4]), getBCDValue(btValue[iOffset + 3]));
            lOffset = getBCDValue(btValue[iOffset + 1]) * 3600;
            lOffset += getBCDValue(btValue[iOffset]) * 60;
            lOffset *= 1000L;
            if (btValue[iOffset + 2] == 45) {
                lOffset = -lOffset;
            }
            dtReturn.setTime((dtReturn.getTime() - lOffset) + (long) iUtcOffset);
            return fmtDateTime.format(dtReturn);
        }
        dtReturn = new Date(getBCDValue(btValue[iOffset]), getBCDValue(btValue[iOffset + 1]) - 1, getBCDValue(btValue[iOffset + 2]), getBCDValue(btValue[iOffset + 3]), getBCDValue(btValue[iOffset + 4]), getBCDValue(btValue[iOffset + 5]));
        lOffset = getBCDValue(btValue[iOffset + 7]) * 3600;
        lOffset += getBCDValue(btValue[iOffset + 8]) * 60;
        lOffset *= 1000L;
        if (btValue[iOffset + 6] == 45) {
            lOffset = -lOffset;
        }
        dtReturn.setTime((dtReturn.getTime() - lOffset) + (long) iUtcOffset);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dtReturn);
        cal.add(Calendar.HOUR, 7);

        return fmtDateTime.format(cal.getTime());
    }

    public static String formatHuaWeiTimeStamp(String format, byte btValue[], int iOffset, int iLength, int iUtcOffset) {
        if (btValue.length < iOffset + iLength || iLength < 9) {
            return "";
        }
        Date dtReturn = null;
        long lOffset = 0L;
        int year = getBCDValue(btValue[iOffset]);
        if (year < 70) {
            year += 100;
        }
        dtReturn = new Date(year, getBCDValue(btValue[iOffset + 1]) - 1, getBCDValue(btValue[iOffset + 2]), getBCDValue(btValue[iOffset + 3]), getBCDValue(btValue[iOffset + 4]), getBCDValue(btValue[iOffset + 5]));
        lOffset = getBCDValue(btValue[iOffset + 7]) * 3600;
        lOffset += getBCDValue(btValue[iOffset + 8]) * 60;
        lOffset *= 1000L;
        if (btValue[iOffset + 6] == 45) {
            lOffset = -lOffset;
        }
        dtReturn.setTime((dtReturn.getTime() - lOffset) + (long) iUtcOffset);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dtReturn);
        cal.add(Calendar.HOUR, 7);

        if (format != null) {
            fmtDateTime = new SimpleDateFormat(format);
        }

        return fmtDateTime.format(cal.getTime());
    }

    public static String formatIPAddress(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        StringBuilder strReturn = new StringBuilder();
        for (int i = iOffset; i < iLastOffset; i++) {
            strReturn.append('.');
            strReturn.append(FORMAT_SHARP.format(btValue[i] & 0xff));
        }

        return strReturn.substring(1, strReturn.length());
    }

    public static String formatDate1(byte btValue[], int iOffset, int iLength) {
        if (btValue.length < iOffset + iLength || iLength < 4) {
            return "";
        } else {
            return ASNUtil.FORMAT_00.format(btValue[iOffset + 3]) + "/" + ASNUtil.FORMAT_00.format(btValue[iOffset + 2]) + "/" + ASNUtil.FORMAT_00.format(btValue[iOffset]) + ASNUtil.FORMAT_00.format(btValue[iOffset + 1]);
        }
    }

    public static String formatTime1(byte btValue[], int iOffset, int iLength) {
        if (btValue.length < iOffset + iLength || iLength < 3) {
            return "";
        } else {
            return ASNUtil.FORMAT_00.format(btValue[iOffset]) + ":" + ASNUtil.FORMAT_00.format(btValue[iOffset + 1]) + ":" + ASNUtil.FORMAT_00.format(btValue[iOffset + 2]);
        }
    }

//    public static String formatIPAddress(ASNData dat)
//    {
//        if (dat.mpFirstChild != null)
//        {
//            dat = dat.mpFirstChild;
//            if (dat.mpFirstChild.miTagID == 0 || dat.mpFirstChild.miTagID == 1)
//            {
//                return formatIPAddress(dat.mbtData, 0, dat.mbtData.length);
//            }
//            if (dat.mpFirstChild.miTagID == 2 || dat.mpFirstChild.miTagID == 3)
//            {
//                return new String(dat.mbtData);
//            }
//        }
//        return "";
//    }
    public static byte[] parseInteger(String strData) {
        int iValue = Integer.parseInt(strData);
        byte btValue[] = new byte[4];
        int iIndex = 0;
        for (; iValue > 0; iValue >>= 8) {
            btValue[iIndex++] = (byte) (iValue & 0xff);
        }
        if (iIndex == 0) {
            return new byte[1];
        }
        int iCount = iIndex;
        byte btReturn[] = new byte[iCount];
        while (iIndex > 0) {
            btReturn[iCount - iIndex--] = btValue[iIndex];
        }
        return btReturn;
    }

    public static byte[] parseBCD(String strData) {
        return new byte[0];
    }

    public static byte[] parseDate(String strData) {
        return new byte[0];
    }

    public static byte[] parseTime(String strData) {
        return new byte[0];
    }

    public static String getBCDString(byte btValue) {
        byte h = (byte) ((btValue & 0xf0) >>> 4);
        if (h < 10) {
            h = (byte) (48 + h);
        } else {
            h = (byte) ((65 + h) - 10);
        }
        byte l = (byte) (btValue & 0xf);
        if (l < 10) {
            l = (byte) (48 + l);
        } else {
            l = (byte) ((65 + l) - 10);
        }
        return String.valueOf((char) h) + String.valueOf((char) l);
    }

    public static int getBCDValue(byte btValue) {
        int iReturn = ((btValue & 0xf0) >>> 4) * 10;
        iReturn += btValue & 0xf;
        return iReturn;
    }

    public static String getStrByte(byte b) {
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        byte l = (byte) (b & 0x0f);
        byte h = (byte) (b & 0xf0);
        h >>= 4;
        h = (byte) (h & 0x0f);
        return hex[h] + hex[l];
    }

    public static String getStrByteHLR(byte b) {
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", ""};
        byte l = (byte) (b & 0x0f);
        byte h = (byte) (b & 0xf0);
        h >>= 4;
        h = (byte) (h & 0x0f);
        return hex[h] + hex[l];
    }

    public static String formatBCDHLR(byte btValue[], int iOffset, int iLength) {
        String reStr = "";
        for (int i = 0; i < iLength; i++) {
            reStr += getStrByteHLR(btValue[iOffset + i]);
        }
        return reStr;
    }
    
    public static String formatAlphanumericString(byte btValue[], int iOffset, int iLength,
            String separate) {
        try {
            String hexStr = formatReverseHEX(btValue, iOffset, iLength);

            if (hexStr.startsWith("0D")) {
                return formatHexToAsciiString(hexStr);
            } else {
                return formatTBCD(btValue, iOffset + 1, iLength - 1);
            }
        } catch (Exception ex) {
            return null;
        }

    }
    
    public static String formatReverseHEX(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        StringBuilder value = new StringBuilder();
        for (int i = iOffset; i < iLastOffset; i++) {
            byte h = (byte) ((btValue[i] & 0xf0) >>> 4);
            if (h < 10) {
                h = (byte) (48 + h);
            } else {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) (btValue[i] & 0xf);
            if (l < 10) {
                l = (byte) (48 + l);
            } else {
                l = (byte) ((65 + l) - 10);
            }
            value.append((char) l);
            value.append((char) h);
//            value.append((char) l);
        }

        return value.toString();
    }
    
    public static String formatHexToAsciiString(String hex) {
        hex = hex.substring(2);
        StringBuilder output = new StringBuilder();
//        output.append("A");
        for (int i = 0; i < hex.length(); i += 2) {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }
}