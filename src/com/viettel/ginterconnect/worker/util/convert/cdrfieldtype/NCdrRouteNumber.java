/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author duclv
 */
public class NCdrRouteNumber extends BaseCdrField
{

    /**
     * Default constructor
     */
    public NCdrRouteNumber()
    {
        super();
    }

    public NCdrRouteNumber(int length, String description)
    {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception
    {
        String temp;
        String strReturn = "";
        
        for (int i = super.length - 1; i >= 0; i--) {
            temp = fmHEXByte(b[offset + i]);
            if (temp != null) {
                strReturn += temp;
            } else {
                return strReturn;
            }
        }
        // Kiem tra neu toan la FFF..FF thi` tra lai xau rong
        char[] allFs = new char[length * 2];
        for(int i=0; i<allFs.length; i++) allFs[i] = 'f';
        if(new String(allFs).equals(strReturn))
            return "";
        // Loai bo "0" o dau
        while(strReturn.startsWith("0")) {
            strReturn = strReturn.substring(1);
        }
        return strReturn;
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
