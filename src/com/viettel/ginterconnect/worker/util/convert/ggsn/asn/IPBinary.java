package com.viettel.ginterconnect.worker.util.convert.ggsn.asn;

import com.viettel.ginterconnect.worker.util.convert.ggsn.BasicFormat;
import java.text.DecimalFormat;

public class IPBinary extends BasicFormat
{
  public static DecimalFormat FORMAT_SHARP = new DecimalFormat("#");

  public String decode(byte[] btValue, int iOffset, int iLength)
    throws Exception
  {
    int iLastOffset = iOffset + iLength;
    if ((btValue.length < iLastOffset) || (iLength < 1))
    {
      return "";
    }
    StringBuffer strReturn = new StringBuffer();
    for (int i = iOffset; i < iLastOffset; i++)
    {
      if (i != iOffset) {
        strReturn.append('.');
      }
      strReturn.append("" + (btValue[i] & 0xFF));
    }

    return strReturn.toString();
  }

  public byte[] encode(String src)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}