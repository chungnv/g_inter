/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;


/**
 *
 * @author chungdq
 */
public class CdrLowerLongDuration extends BaseCdrField
{

    /**
     * Default constructor
     */
    public CdrLowerLongDuration()
    {
        super();
    }

    public CdrLowerLongDuration(int length, String description)
    {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception
    {
        if (super.length < 4)
            return new Long(0L);
        else{
            String temp;
            String vstrReturn = "";
            for (int i = 3; i >= 0; i--) {
                
                temp = fmHEXByte(b[offset + i]);
                vstrReturn += temp;
            }
            //int a = Integer.parseInt(vstrReturn, 16);
            long a = Long.parseLong(vstrReturn, 16);
            long s = a / 100;
            return s;
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
