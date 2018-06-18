/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.vtz;

/**
 *
 * @author Le Thanh Cong
 */
public class VTZCdrBitString extends BaseCdrField
{

    /**
     * Default constructor
     */
    public VTZCdrBitString()
    {
        super();
    }

    public VTZCdrBitString(int length, String description)
    {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception
    {
        StringBuffer value = new StringBuffer();
        int bit = b[offset]& 0xff;
        int iValue = 0;
        for (int iIndex = offset + 1; iIndex < offset + getLength(); iIndex++)
        {
            iValue <<= 8;
            iValue |= b[iIndex] & 0xff;
        }
        
        iValue >>= bit;
        return new Long(iValue);
    }
}
