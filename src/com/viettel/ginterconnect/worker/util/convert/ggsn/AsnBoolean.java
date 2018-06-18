package com.viettel.ginterconnect.worker.util.convert.ggsn;

public class AsnBoolean extends BasicFormat
{
  public String decode(byte[] btValue, int iOffset, int iLength)
    throws Exception
  {
    for (int i = 0; i < iLength; i++)
    {
      if (btValue[(iOffset + i)] != 0)
        return "TRUE";
    }
    return "FALSE";
  }

  public byte[] encode(String src)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}