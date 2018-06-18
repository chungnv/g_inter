package com.viettel.ginterconnect.worker.util.convert.ggsn;

public class AsnLocationInfor extends BasicFormat
{
  public String decode(byte[] btValue, int iOffset, int iLength)
    throws Exception
  {
    if ((iLength != 7) && (iLength != 8))
    {
      OctetString formatStr = (OctetString)AsnFormatFactory.getAsnFormat("OctetString");
      return formatStr.decode(btValue, iOffset, iLength);
    }

    if (iLength == 8)
    {
      StringBuffer value = new StringBuffer();

      value.append(hexValueToChar((byte)(btValue[iOffset] & 0xF)));
      value.append(hexValueToChar((byte)((btValue[iOffset] & 0xF0) >> 4)));

      value.append(hexValueToChar((byte)(btValue[(iOffset + 1)] & 0xF)));
      value.append(hexValueToChar((byte)((btValue[(iOffset + 1)] & 0xF0) >> 4)));
      value.append(hexValueToChar((byte)(btValue[(iOffset + 2)] & 0xF)));

      value.append(hexValueToChar((byte)(btValue[(iOffset + 3)] & 0xF)));
      value.append(hexValueToChar((byte)((btValue[(iOffset + 3)] & 0xF0) >> 4)));
      byte lastNetworkChar = (byte)((btValue[(iOffset + 2)] & 0xF0) >> 4);

      if (lastNetworkChar != 15) {
        value.append(hexValueToChar(lastNetworkChar));
      }

      for (int i = iOffset + 4; i < iOffset + 8; i++)
      {
        value.append(hexValueToChar((byte)(btValue[i] & 0xF)));

        value.append(hexValueToChar((byte)((btValue[i] & 0xF0) >> 4)));
      }

      String str = value.toString();
      
      if (str != null && str.length() == 15) {
          str = str.substring(2);
      }
      
      return str;
    }

    StringBuffer value = new StringBuffer();

    value.append(hexValueToChar((byte)(btValue[iOffset] & 0xF)));
    value.append(hexValueToChar((byte)((btValue[iOffset] & 0xF0) >> 4)));
    value.append(hexValueToChar((byte)(btValue[(iOffset + 1)] & 0xF)));

    value.append(hexValueToChar((byte)(btValue[(iOffset + 2)] & 0xF)));
    value.append(hexValueToChar((byte)((btValue[(iOffset + 2)] & 0xF0) >> 4)));
    byte lastNetworkChar = (byte)((btValue[(iOffset + 1)] & 0xF0) >> 4);

    if (lastNetworkChar != 15) {
      value.append(hexValueToChar(lastNetworkChar));
    }

    for (int i = iOffset + 3; i < iOffset + 7; i++)
    {
      value.append(hexValueToChar((byte)(btValue[i] & 0xF)));

      value.append(hexValueToChar((byte)((btValue[i] & 0xF0) >> 4)));
    }

    return value.toString();
  }

  private char hexValueToChar(byte h)
  {
    if (h < 10)
    {
      return (char)(byte)(48 + h);
    }

    return (char)(byte)(65 + h - 10);
  }

  public byte[] encode(String src)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}