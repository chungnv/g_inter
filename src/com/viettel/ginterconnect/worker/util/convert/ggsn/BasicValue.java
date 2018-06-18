package com.viettel.ginterconnect.worker.util.convert.ggsn;

import java.util.List;

public abstract class BasicValue
{
  private Field field;  //name,hexcode,type,format,parents,child.
  private String name;
  private int indexValue;
  private int length;
  private BasicValue parentValue;
  private List<BasicValue> listChilds;

  public abstract String getValue();

  public int getLength()
  {
    return this.length;
  }

  public void setLength(int length)
  {
    this.length = length;
  }

  public Field getField()
  {
    return this.field;
  }

  public void setField(Field field)
  {
    this.field = field;
  }

  public List<BasicValue> getListChilds()
  {
    return this.listChilds;
  }

  public void setListChilds(List<BasicValue> listChilds)
  {
    this.listChilds = listChilds;
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public BasicValue getParentValue()
  {
    return this.parentValue;
  }

  public void setParentValue(BasicValue parentValue)
  {
    this.parentValue = parentValue;
  }

  public int getIndexValue()
  {
    return this.indexValue;
  }

  public void setIndexValue(int indexValue)
  {
    this.indexValue = indexValue;
  }
}