package com.viettel.ginterconnect.worker.util.convert.ggsn.asn;

import com.viettel.ginterconnect.worker.util.convert.ggsn.BasicFormat;

public class AsnDefault extends BasicFormat
{
  public String decode(byte[] btValue, int iOffset, int iLength)
    throws Exception
  {
    return "";
  }

  public byte[] encode(String src)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}