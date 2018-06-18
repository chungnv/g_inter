/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author Le Thanh Cong
 */
public class CdrDigits extends BaseCdrField
{

    @Override
    public int getLengthByBits()
    {
        return ((int)((getLength() + 1) / 2)) * BITS_PER_BYTE;
    }

    /**
     * Default constructor
     */
    public CdrDigits()
    {
        super();
    }

    public CdrDigits(int length, String description)
    {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception
    {
        int iLastOffset = 2 * offset + getLength();
        if (b.length < iLastOffset / 2 || getLength() < 1)
        {
            return null;
        }
        StringBuffer value = new StringBuffer();
        int i = 2 * offset;
        while (i < iLastOffset)
        {
            byte h = (byte) ((b[(i++) / 2] & 0xf0) >>> 4);
            if (h < 10)
            {
                h = (byte) (48 + h);
                value.append((char) h);
            }
            else
            {
                h = (byte) ((65 + h) - 10);
            }
            if (i < iLastOffset)
            {
                byte l = (byte) (b[(i++) / 2] & 0xf);
                if (l < 10)
                {
                    l = (byte) (48 + l);
                    value.append((char) l);
                }
                else
                {
                    l = (byte) ((65 + l) - 10);
                }
            }
        }

        return value.toString();
    }
}
