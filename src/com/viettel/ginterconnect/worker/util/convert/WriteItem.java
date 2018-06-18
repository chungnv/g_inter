/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert;

/**
 *
 * @author chungdq
 */
public class WriteItem {

    public static final int FIELD_TYPE_CONSTANT = 0;
    public static final int FIELD_TYPE_FIELD = 1;
    public static final int FIELD_TYPE_FIX = 2;
    public static final int FIELD_TYPE_MAP = 3;
    public static final int FIELD_TYPE_FUNCTION = 4;

    private int type;
    
    protected Object value;
    
    public int getType()
    {
        return type;
    }
    
    public void setValue(Object value)
    {
        this.value = value;
    }
    
    public Object getValue()
    {
        return value;
    }
    
    public WriteItem(int type)
    {
        this.type = type;
    }
    
    public void caculate(Object ... args) throws Exception
    {}
}
