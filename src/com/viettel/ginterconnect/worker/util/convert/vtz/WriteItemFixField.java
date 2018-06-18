/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert.vtz;

/**
 *
 * @author chungdq
 */
public class WriteItemFixField extends WriteItem
{
    public WriteItemFixField()
    {
        super(WriteItem.FIELD_TYPE_FIX);
    }
    private int fieldId = 0;
    public void setFieldId(int id)
    {
        this.fieldId = id;
    }

    public int getFieldId() {
        return fieldId;
    }
    
}
