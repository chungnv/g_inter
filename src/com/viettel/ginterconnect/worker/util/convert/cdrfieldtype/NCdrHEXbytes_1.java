/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;


/**
 *
 * @author duclv
 * Phone Number
 */
public class NCdrHEXbytes_1 extends BaseCdrField
{

    /**
     * Default constructor
     */
    public NCdrHEXbytes_1()
    {
        super();
    }

    public NCdrHEXbytes_1(int length, String description)
    {
        super(length, description);
    }
       
    public String fmHEXByte(byte pbyte) {
        String sReturn = "";

        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte) (pbyte & 0xf);
        
        if(h == '*' || l == '*')
            return "0";
        else {
            if(l < 10) sReturn += l;
            if(h < 10) sReturn += h;
            return sReturn;
        }
    }
    
    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
     
        String vstrReturn = "";
        String temp;
        for (int i = 0; i < super.length; i++) {
            temp = fmHEXByte(b[offset + i]);
            if (temp != null)
                vstrReturn += temp;
            else
                return vstrReturn;
        }
        return vstrReturn;
             
    }
}
