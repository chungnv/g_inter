package com.viettel.ginterconnect.worker.util.convert.ggsn;

import java.util.List;

public class ChoiceValue extends BasicValue
{
  public String getValue()
  {
    List lst = getListChilds();
    if ((lst != null) && (lst.size() > 0))
    {
      return ((BasicValue)lst.get(0)).getValue();
    }

    return "";
  }
}