/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Le Van Duc
 */
public class AlcatelCdrDateTime extends BaseCdrField
{
    /**
     * Default constructor
     */
    public AlcatelCdrDateTime()
    {
        super();
    }

    public AlcatelCdrDateTime(int length, String description)
    {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception
    {
        long second = getBcdValue(b, 4, offset);
//        System.out.println("second"+ second);
        
        long ms = getBcdValue(b, 1, offset + 4);
        
//        System.out.println("ms" + ms);
        
        
        long timestamp = second * 1000 + ms * 10;
        
        
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(timestamp);
       
      //  cl.add(Calendar.HOUR, -7);
  //      cl.add(Calendar.YEAR, 20);

        return cl.getTime();
    }

    /*
     * Tra lai gia tri theo kieu BCD
     */
    public long getBcdValue(byte[] b, int length, int offset)
    {
        String temp;
        String vstrReturn = "";
        for (int i = length - 1; i >= 0; i--) {
            temp = fmHEXByte(b[offset + i]);
            if (temp != null)
                vstrReturn += temp;
            else
                return 0L;
        }
        return Long.parseLong(vstrReturn, 16);
    }
    
    // Lay ve gia tri Hex
    public String fmHEXByte(byte pbyte) {
        String vstrReturn = "";
        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
        char ch2 = Character.forDigit(pbyte & 0xf, 16);
        vstrReturn = vstrReturn + ch1;
        vstrReturn = vstrReturn + ch2;
        return vstrReturn;
    }
}
