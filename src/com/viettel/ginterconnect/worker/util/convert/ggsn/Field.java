package com.viettel.ginterconnect.worker.util.convert.ggsn;

import java.util.Map;

public class Field
{
  public static final int FIELDTYPE_VALUE = 0;
  public static final int FIELDTYPE_SET = 1;
  public static final int FIELDTYPE_SEQ = 2;
  public static final int FIELDTYPE_CHOICE = 3;
  private String fieldName;
  private int fieldType;
  private String format;
  private String fieldHEXCode;
  private Field parentField;
  private Map<String, Field> mapChilds;

  public String getFieldHEXCode()
  {
    return this.fieldHEXCode;
  }

  public void setFieldHEXCode(String fieldHEXCode)
  {
    this.fieldHEXCode = fieldHEXCode;
  }

  public String getFieldName()
  {
    return this.fieldName;
  }

  public void setFieldName(String fieldName)
  {
    this.fieldName = fieldName;
  }

  public int getFieldType()
  {
    return this.fieldType;
  }

  public void setFieldType(int fieldType)
  {
    this.fieldType = fieldType;
  }

  public String getFormat()
  {
    return this.format;
  }

  public void setFormat(String format)
  {
    this.format = format;
  }

  public Map<String, Field> getMapChilds()
  {
    return this.mapChilds;
  }

  public void setMapChilds(Map<String, Field> mapChilds)
  {
    this.mapChilds = mapChilds;
  }

  public Field getParentField()
  {
    return this.parentField;
  }

  public void setParentField(Field parentField)
  {
    this.parentField = parentField;
  }
}