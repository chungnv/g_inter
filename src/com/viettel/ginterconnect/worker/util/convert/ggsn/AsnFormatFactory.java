package com.viettel.ginterconnect.worker.util.convert.ggsn;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class AsnFormatFactory
{
  private static String asnPakage = "com.viettel.ginterconnect.worker.util.convert.ggsn.asn";
  private static Map<String, BasicFormat> mapFormats = new HashMap();

  public static BasicFormat getAsnFormat(String type)
  {
    if ((type == null) || (type.trim().length() == 0)) {
      return null;
    }
    if ((mapFormats.containsKey(type)) && (mapFormats.get(type) != null))
    {
      return (BasicFormat)mapFormats.get(type);
    }

    try
    {
      return (BasicFormat)Class.forName(asnPakage + "." + type).newInstance();
    }
    catch (Exception ex) {
      System.out.println("Type loi " + type);
      ex.printStackTrace();
    }return null;
  }
}