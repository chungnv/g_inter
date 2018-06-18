/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert.vtc;

/**
 *
 * @author chungdq
 */
public class WriteItemField extends WriteItem
{
    public WriteItemField()
    {
        super(WriteItem.FIELD_TYPE_FIELD);
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
