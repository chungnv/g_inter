/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert;

import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.BaseCdrField;
import java.util.Date;


/**
 *
 * @author chungdq
 */
public class PSTNHuaweiStartTime extends BaseFunction{

    /**
     * return Digits String
     * args[0]: End Time Type:CdrDateTime
     * args[1]: Duration : Long
     * return start Time
     * 
     */
    @Override
    public Object calculate(Object... args) throws Exception {
        
        // validate parameter
        if (args.length != 2)
        {
            throw new Exception("Invalid total of parameter");
        }
        
        if (args[0] != null && args[1] != null && args[0] instanceof BaseCdrField && args[1] instanceof BaseCdrField)
        {
            Object value1 = ((BaseCdrField)args[0]).getValue();
            int value2 = Integer.parseInt(args[1].toString());
            if (value1 instanceof Date)
            {
                Date startTime = new Date();
                startTime.setTime( ((Date)value1).getTime() -  value2 * 1000);
                return startTime;
            }
            else
            {
                return value1;
            }
        }else{
            return "";
        }
    }

}
