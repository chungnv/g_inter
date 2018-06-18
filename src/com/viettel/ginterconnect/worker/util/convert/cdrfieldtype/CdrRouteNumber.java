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
public class CdrRouteNumber extends BaseCdrField
{

    /**
     * Default constructor
     */
    public CdrRouteNumber()
    {
        super();
    }

    public CdrRouteNumber(int length, String description)
    {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        String temp;
        String vstrReturn = "";
        String testStr = "";

        for (int i = super.length - 1; i >= 0; i--) {
            testStr += "FF";
            temp = fmHEXByte(b[offset + i]);
            if (temp != null) {
                vstrReturn += temp;
            } else {
                return vstrReturn;
            }
        }

        if (vstrReturn.toUpperCase().equals(testStr)) {
            return new String("");
        } else {
            return Integer.parseInt(vstrReturn, 16);
        }
    }

    public String fmHEXByte(byte pbyte) {
        String vstrReturn = "";
        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
        char ch2 = Character.forDigit(pbyte & 0xf, 16);
        vstrReturn = vstrReturn + ch1;
        vstrReturn = vstrReturn + ch2;
        return vstrReturn;
    }
    
}
