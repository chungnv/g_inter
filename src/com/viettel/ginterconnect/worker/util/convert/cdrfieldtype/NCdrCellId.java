/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author Le Van Duc
 */
public class NCdrCellId extends BaseCdrField
{
    /**
     * Default constructor
     */
    public NCdrCellId()
    {
        super();
    }

    public NCdrCellId(int length, String description)
    {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception
    {
        String temp;
        String vstrReturn = "";
        // LAC
        for (int i = 1; i >= 0; i--) {
            temp = fmHEXByte(b[offset + i]);
            if (temp != null)
                vstrReturn += temp;
        }
        // CI
        for (int i = length-1; i >= 2; i--) {
            temp = fmHEXByte(b[offset + i]);
            if (temp != null)
                vstrReturn += temp;
        }
        return "45204" + vstrReturn;
    }

    // Lay ve gia tri Hex
    public String fmHEXByte(byte pbyte) {
        String sReturn = "";
        char hexChars[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E', 'F'};
        byte h = (byte) ((pbyte & 0xf0) >> 4);
        byte l = (byte) (pbyte & 0xf);
        
        sReturn += hexChars[l];
        sReturn += hexChars[h];
        return sReturn;
    }
}
