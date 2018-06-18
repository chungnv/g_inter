/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

import java.util.Calendar;
import java.util.Date;


/**
 *
 * @author chungdq
 */
public class HCdrTimeDuration extends BaseCdrField
{

    /**
     * Default constructor
     */
    public HCdrTimeDuration()
    {
        super();
    }

    public HCdrTimeDuration(int length, String description)
    {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception
    {
        char bcds[] = new char[8];

        if (super.length < 4)
            return 0;

        for (int i = 0; i < super.length && i < 4; i++)
        {
            byte temp = b[offset + i];
            bcds[i * 2] = Character.forDigit(temp >> 4 & 0xf, 10);
            bcds[i * 2 + 1] = Character.forDigit(temp & 0xf, 10);
        }
        
        // get hh
        String hhh = "" + bcds[0] + bcds[1] + bcds[2];
        String mm = "" + bcds[3] + bcds[4];
        String ss = "" + bcds[5] + bcds[6];
        String t = "" + bcds[7];
        Integer duration = 0;
        try{
            duration += Integer.parseInt(hhh) * 60 * 60;
            duration += Integer.parseInt(mm) * 60;
            duration += Integer.parseInt(ss);
            //round if 5 =< t < 9
            if (Integer.parseInt(t) >= 5)
                duration++;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally{
            return duration;
        }
    }
}
