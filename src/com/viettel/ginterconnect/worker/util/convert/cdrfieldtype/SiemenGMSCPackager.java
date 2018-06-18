/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 * Siemen packager
 *      Format: Identifier,Length(may have or not),Field1,Field2,...
 * @author Le Thanh Cong
 */
public class SiemenGMSCPackager extends BasePackager
{

    private static final int IDENTIFIER_POSITION = 0;
    private static final int LENGTH_POSITION = 1;

    public enum Type
    {

        FIXED_LENGTH, // Format: Identifier,Field1,Field2,...
        VARIABLE_LENGTH, // Format: Identifier,Length,Field1,Field2,...
        DIGITS              // Format: Identifier,LAC Length (may have or not),Digits Length,Field1,Field2,...
    }

    public SiemenGMSCPackager()
    {
        super();
    }

    public SiemenGMSCPackager(BasePackager packager)
    {
        fields = new BaseCdrField[packager.getFields().length];
        System.arraycopy(packager.getFields(), 0, fields, 0, packager.getFields().length);
    }

    public int getIdentifier()
    {
        return ((Long) fields[IDENTIFIER_POSITION].getValue()).intValue();
    }

    /**
     * @param	b	message image
     * @param   offset - starting offset within the binary image
     * @return  consumed bytes
     * @exception Exception
     */
    @Override
    public int unpack(byte[] b, int offset) throws Exception
    {
        int consumed = 0;
        BaseCdrField field = null;
        int lastIndex = fields.length - 1;
        for (int i = 0; i < fields.length; i++)
        {
            field = fields[i];
            // Length of digits is value of previous field
            if (field instanceof CdrDigits && i > 0 && field.getLength() < 0)
            {
                field.setLength(((Long) fields[i - 1].getValue()).intValue());
            }
            // Length of phone number is value of previous field
            else if (field instanceof CdrPhoneNumber && i > 1 && field.getLength() < 0)
            {
                field.setLength(((Long) fields[i - 1].getValue()).intValue());
                ((CdrPhoneNumber) field).setLacLength(((Long) fields[i - 2].getValue()).intValue());
            }
            // Note last field may not configure length
            // We need calculate length of it
            else if (i == lastIndex && field.getLength() < 0)
            {
                int length = ((Long) fields[LENGTH_POSITION].getValue()).intValue();
                field.setLength(length - consumed / BITS_PER_BYTE);
            }
            field.unpack(b, offset + consumed / BITS_PER_BYTE);
            consumed += field.getLengthByBits();
            // Refine call date time
            if (i > 0 && fields[i - 1] instanceof CdrDateTime)
            {
                ((CdrDateTime) fields[i - 1]).refineTime(((Long) field.getValue()).intValue());
            }
        }
        return consumed;
    }
}
