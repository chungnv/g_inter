package com.viettel.ginterconnect.worker.util.convert.ggsn;

import codec.asn1.ASN1Integer;

public class Enumerated extends BasicFormat
{
  public String decode(byte[] btValue, int iOffset, int iLength)
    throws Exception
  {
    byte[] dst = new byte[iLength];
    System.arraycopy(btValue, iOffset, dst, 0, iLength);
    ASN1Integer decode = new ASN1Integer(dst);
    return decode.getValue().toString();
  }

  public byte[] encode(String src)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}