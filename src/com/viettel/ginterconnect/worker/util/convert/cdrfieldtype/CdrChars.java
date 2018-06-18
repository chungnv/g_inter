/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author Le Thanh Cong
 */
public class CdrChars extends BaseCdrField
{

    /**
     * Default constructor
     */
    public CdrChars()
    {
        super();
    }

    public CdrChars(int length, String description)
    {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception
    {
        StringBuffer value = new StringBuffer();
        for (int i = offset; i < offset + getLength(); i++)
        {
            value.append((char) b[i]);
        }

        return value.toString().trim();
    }
}
