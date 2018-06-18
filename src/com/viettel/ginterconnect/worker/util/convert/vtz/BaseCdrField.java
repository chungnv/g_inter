/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.vtz;

/**
 *
 * @author chungdq
 */
public abstract class BaseCdrField
{

    protected static final int BITS_PER_BYTE = 8;
    protected static final String PARAM_ADDITION_SEPARATE = "#";
    private static final String FIELD_PACKAGE = "com.viettel.mediation.cdrfieldtype.";
    /** length.*/
    protected  int length;
    private String description;
    protected Object value = null;

    public BaseCdrField(int length, String description)
    {
        this.length = length;
        this.description = description;
    }

    public BaseCdrField()
    {
        this.length = -1;
        this.description = null;
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getLengthByBits()
    {
        return length * BITS_PER_BYTE;
    }

    /**
     * @param b - binary image
     * @param offset - starting offset within the binary image
     * @return unpack object
     * @exception Exception
     */
    public Object unpack(byte[] b, int offset)
            throws Exception
    {
        value = unpackDetail(b, offset);
        return value;
    }

    /**
     * @param b - binary image
     * @param offset - starting offset within the binary image
     * @return unpack object
     * @exception Exception
     */
    protected abstract Object unpackDetail(byte[] b, int offset)
            throws Exception;

    /**
     * @param b - binary image
     * @param offset - starting offset within the binary image
     * @return String contain value of unpack object
     * @exception Exception
     */
    public String unpackToString(byte[] b, int offset) throws Exception
    {
        return String.valueOf(unpack(b, offset));
    }

    public Object getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return String.valueOf(value);
    }

    /**
     * Build a Cdr type base on parameter
     * @param description
     * @param packager              Type#Length#Pos
     *      Ex: CdrNumeric#2
     *          CdrBits#7#0
     *          CdrDigits
     * @param defaultFieldLength
     * @return
     * @throws java.lang.Exception
     */
    public static BaseCdrField valueOf(String description, String packager, int defaultFieldLength) throws Exception
    {
        BaseCdrField field = null;

        // In each field description, may be have addition information as Length, Pos, ...
        String[] arrAddition;
        // Read addition information
        // [0] - Field class name
        // [1] - Length
        // [2] - Position (use for CdrBits)
        arrAddition = packager.split(PARAM_ADDITION_SEPARATE, -1);
        // Calculate length of field
        int fieldLength = arrAddition.length > 1 ? Integer.parseInt(arrAddition[1]) : defaultFieldLength;

        // Build field
        Class classField = Class.forName(FIELD_PACKAGE + arrAddition[0]);
        field = (BaseCdrField) classField.newInstance();
        field.setLength(fieldLength);
        field.setDescription(description);
        if (arrAddition[0].equals(CdrBits.class.getSimpleName()))
        {
            ((CdrBits) field).setBitPos(Integer.parseInt(arrAddition[2]));
        }

        return field;
    }
}
