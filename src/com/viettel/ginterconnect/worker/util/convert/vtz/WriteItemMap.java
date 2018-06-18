/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert.vtz;
import java.util.Map;

/**
 *
 * @author chungdq
 */
public class WriteItemMap extends WriteItem{

    private Map<String, String> map;
    private int fieldId;
    
    public void setMap(Map<String, String> map)
    {
        this.map = map;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public int getFieldId() {
        return fieldId;
    }

    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }
    
    public WriteItemMap()
    {
        super(WriteItem.FIELD_TYPE_MAP);
    }
    
    @Override
    public void caculate(Object ... args) throws Exception
    {
        value = null;
        if (args.length >= 1)
        {
            value = map.get(args[0].toString());
        }
    }
}
