/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.vtz;


/**
 *
 * @author Chungdq
 */
public class CdrNumeric extends BaseCdrField
{

    private boolean littleEndian = true;

    /**
     * Default constructor
     */
    public CdrNumeric()
    {
        super();
    }

    public CdrNumeric(int length, String description, boolean littleEndian)
    {
        super(length, description);
        this.littleEndian = littleEndian;
    }

    public CdrNumeric(int length, String description)
    {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception
    {
        int iLastOffset = offset + getLength();
        if (b.length < iLastOffset || getLength() < 1)
        {
            return null;
        }
        int iValue = 0;
        if (isLittleEndian())
        {
            for (int iIndex = iLastOffset - 1; iIndex >= offset; iIndex--)
            {
                iValue <<= 8;
                iValue |= b[iIndex] & 0xff;
            }
        }
        else
        {
            for (int iIndex = offset; iIndex < iLastOffset; iIndex++)
            {
                iValue <<= 8;
                iValue |= b[iIndex] & 0xff;
            }
        }

        return new Long(iValue);
    }

    public boolean isLittleEndian()
    {
        return littleEndian;
    }

    public void setLittleEndian(boolean littleEndian)
    {
        this.littleEndian = littleEndian;
    }
}
