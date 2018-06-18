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
public class EricssonSeizureTime extends BaseFunction{
    
    /**
     * Function only support to calculate seizureTime of Ericsson MSC
     * return String
     * args[0]Date of charge: in milisenconds
     * args[1]Time of charge: unit in miliseconds
     * args[2]int: time of seizure, unit of milisenconds
     * args[3]: date format
     * calculate = args[0] + args[1] - args[2];
     * return String of DateTime seizureTime
     * 
     */

    @Override
    public Object calculate(Object... args) throws Exception {
        String sReturn = null;
        try{
            sReturn = "";
            if (args.length == 4 && args[0] != null && args[1] != null && args[2] != null && args[3] != null)
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat(args[0].toString());
                Date date = new Date();
                date.setTime(Long.parseLong(args[1].toString()) + Long.parseLong(args[2].toString()) - Long.parseLong(args[3].toString()) );
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
