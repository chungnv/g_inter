/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;


/**
 *
 * @author chungdq
 * Phone Number
 */
public class NCdrHEXbytes extends BaseCdrField
{

    private int lacLength;
    /**
     * Default constructor
     */
    public NCdrHEXbytes()
    {
        super();
    }

    public NCdrHEXbytes(int length, String description)
    {
        super(length, description);
    }
    
    public String fmHEXByte(byte pbyte) {
        String sReturn = "";
        char converts[] = {'0','1','2','3','4','5','6','7','8','9','A','*','#','B','C'};
        char ch1, ch2;

        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte) (pbyte & 0xf);
        if (l < 15)
        {
            sReturn += converts[l];
            if (h < 15)
            {
                return sReturn + converts[h];
            }else{
                return sReturn;
            }
        }else{
            return null;
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
