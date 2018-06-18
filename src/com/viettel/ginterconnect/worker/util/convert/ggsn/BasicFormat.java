package com.viettel.ginterconnect.worker.util.convert.ggsn;

public abstract class BasicFormat
{
  public abstract String decode(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws Exception;

  public abstract byte[] encode(String paramString);
}