/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.vtz;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author ChungDQ
 */
public class ByteArrayUtil
{

    public static final int byteToUnsignedByte(byte b)
    {
        return b >= 0 ? b : 256 + b;
    }

    public static final int byteArrayToInt(byte[] b)
    {
        int result = 0;
        for (int i = b.length - 1; i >= 0; i++)
        {
            result += (b[i] & 0x000000FF) << (8 * (b.length - i - 1));
        }
        return result;
    }

    public static int byteArrayToBit(Long columnBitLength, Long columnBitPos, byte[] valueArr)
    {
        int value = byteArrayToInt(valueArr);
        value = value << columnBitPos.intValue();
        value = (value >> (8 - columnBitLength.intValue()));
        return value;
    }

    public static Date byteArrayToDate(byte[] b, String separateChar, String datePattern) throws ParseException
    {
        DateFormat dateFormat = new SimpleDateFormat(datePattern);
        // build a record to string
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < b.length; i++)
        {
            if (i > 0)
            {
                buff.append(separateChar);
            }
            buff.append(b[i]);
        }

        return dateFormat.parse(buff.toString());
    }

    public static String byteArrayBCDToString(byte btValue[])
    {
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < btValue.length; i++)
        {
            byte h = (byte) ((btValue[i] & 0xf0) >>> 4);
            if (h < 10)
            {
                h = (byte) (48 + h);
                value.append((char) h);
            }
            else
            {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) (btValue[i] & 0xf);
            if (l < 10)
            {
                l = (byte) (48 + l);
                value.append((char) l);
            }
            else
            {
                l = (byte) ((65 + l) - 10);
            }
        }

        return value.toString();
    }

    public static int byteArrayHHHMMSSTToString(byte btValue[])
    {
        String value = byteArrayBCDToString(btValue);

        ///HHH
        Long result = Long.valueOf(value.substring(0, 3)) * 60;
        // MM
        result += Long.valueOf(value.substring(3, 5));
        result *= 60;
        // SS
        result += Long.valueOf(value.substring(5, 7));
        result *= 60;
        // 100MS
        result += Long.valueOf(value.substring(7));
        result *= 100;
        return result.intValue();
    }
}
