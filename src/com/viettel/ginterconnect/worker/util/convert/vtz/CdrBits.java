/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.vtz;


/**
 *
 * @author Chungdq
 */
public class CdrBits extends BaseCdrField
{
    /** bitPos - gia tri duoc tinh bat dau tu vi tri nay.*/
    private int bitPos = 0;

    @Override
    public int getLengthByBits()
    {
        return getLength();
    }

    /**
     * Default constructor
     */
    public CdrBits()
    {
        super();
    }

    /**
     * Constructor
     * @param length
     * @param description
     */
    public CdrBits(int length, String description)
    {
        super(length, description);
    }

    /**
     * Constructor
     * @param length
     * @param description
     * @param bitPos
     */
    public CdrBits(int length, String description, int bitPos)
    {
        super(length, description);
        this.bitPos = bitPos;
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception
    {
        byte[] bits = new byte[BITS_PER_BYTE];
        for (int i = 0; i < BITS_PER_BYTE; i++)
        {
            bits[i] = (byte) ((b[offset] & (1 << i)) != 0 ? 1 : 0); //bits[i] = 0 neu bit thu i cua b[offset] = 0
        }
        int iLastOffset = bitPos + getLength();
        int iValue = 0;
        for (int iIndex = iLastOffset - 1; iIndex >= bitPos; iIndex--)
        {
            iValue <<= 1;
            iValue |= bits[iIndex] & 0xff;
        }
        return new Long(iValue);
    }

    public int getBitPos()
    {
        return bitPos;
    }

    public void setBitPos(int bitPos)
    {
        this.bitPos = bitPos;
    }
}
