/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;


/**
 *
 * @author duclv
 * Ghi lai so dang HEX
 */
public class NCdrHEXNumber extends BaseCdrField
{

    /**
     * Default constructor
     */
    public NCdrHEXNumber()
    {
        super();
    }

    public NCdrHEXNumber(int length, String description)
    {
        super(length, description);
    }
       
    public String fmHEXByte(byte pbyte) {
        String sReturn = "";
        char hexChars[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte) (pbyte & 0xf);
        
        sReturn += hexChars[l];
        sReturn += hexChars[h];
        return sReturn;
    }
    
    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
     
        String strReturn = "";
        String temp;
        for (int i = 0; i < super.length; i++) {
            temp = fmHEXByte(b[offset + i]);
            if (temp != null)
                strReturn += temp;
            else
                return strReturn;
        }
        // Kiem tra neu toan la FFF..FF thi` tra lai xau rong
        char[] allFs = new char[length * 2];
        for(int i=0; i<allFs.length; i++) allFs[i] = 'F';
        if(new String(allFs).equals(strReturn))
            return "";
        return strReturn;
             
    }
}
