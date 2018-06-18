/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author chungdq
 */
public class FormatDateAddTime extends BaseFunction{

    /**
     * Function only support format with type %s
     * return String
     * args[0]: pattern
     * args[1]: String of Date in miliseconds
     * args[2]: String of Time in miliseconds
     * return String
     */

    @Override
    public Object calculate(Object... args) throws Exception {
        String sReturn = null;
        try{
            sReturn = "";
            if (args.length == 3 && args[0] != null && args[1] != null && args[2] != null)
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat(args[0].toString());
                Date date = new Date();
                date.setTime(Long.parseLong(args[1].toString()) + Long.parseLong(args[2].toString()));
                sReturn = dateFormat.format(date);
            }
        }
        catch(Exception ex)
        {
            //ex.printStackTrace();
        }
        finally{
            return  sReturn;
        }
    }

}
