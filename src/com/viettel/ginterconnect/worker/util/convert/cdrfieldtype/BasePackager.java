/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author Dau Quoc Chung
 */
public class BasePackager
{

    protected BaseCdrField[] fields = new BaseCdrField[]{};
    protected static final int BITS_PER_BYTE = 8;
    protected static final String PARAM_FIELD_SEPARATE = ",";
    protected static final String PARAM_VALUE_SEPARATE = ";";
    protected static final int INVALID_LENGTH = -1;

    public BasePackager()
    {
    }

    public BasePackager(BasePackager packager)
    {
        fields = new BaseCdrField[packager.getFields().length];
        System.arraycopy(packager.getFields(), 0, fields, 0, packager.getFields().length);
    }

    public BaseCdrField[] getFields()
    {
        return fields;
    }

    public void setFields(BaseCdrField[] fields)
    {
        this.fields = fields;
    }

    /**
     * @param	b	message image
     * @param   offset - starting offset within the binary image
     * @return  consumed bytes
     * @exception Exception
     */
    public int unpack(byte[] b, int offset) throws Exception
    {
        int consumed = 0;
        BaseCdrField field = null;
        for (int i = 0; i < fields.length; i++)
        {
            field = fields[i];
            field.unpack(b, offset + consumed / BITS_PER_BYTE);
            consumed += field.getLengthByBits();
        }
        return consumed;
    }

    public int getLengthByBits()
    {
        int length = 0;
        for (int i = 0; i < fields.length; i++)
        {
            length += fields[i].getLengthByBits();
        }
        return length;
    }

    public int getLength()
    {
        return getLengthByBits() / BITS_PER_BYTE;
    }

    /**
     * Build a packager from parameter
     * @param packager Field1,Field2,...
     *      Ex: CdrNumeric#1,CdrNumeric#2,CdrBits#1#7,CdrBits#7#0,CdrBytes#2,CdrBits#4#4,CdrBits#4#0
     * @return
     */
    public static BasePackager valueOf(String packager) throws Exception
    {
        return valueOf(packager, null);
    }

    /**
     * Build a packager from parameter
     * @param packager Lnegth;Decription;Field1,Field2,...
     *      Ex: 11;Date/Time/Duration;CdrNumeric#1,CdrDateTime#7,CdrNumeric#3
     *      Ex: variable;Partner Directory Number;CdrNumeric#1,CdrNumeric#1,CdrDigits
     *      Ex: fixed;Received Charge Determination Information;CdrNumeric#1,CdrNumeric#1,CdrBytes
     * @return
     */
    public static BasePackager parsePackager(String packager) throws Exception
    {
        // Read parameters of package in siemen CDR
        String[] arrValue = packager.split(PARAM_VALUE_SEPARATE, -1);
        return valueOf(arrValue[2], arrValue[1]);
    }

    /**
     * Build a packager from parameter
     * @param packager Field1,Field2,...
     *      Ex: CdrNumeric#1,CdrNumeric#2,CdrBits#1#7,CdrBits#7#0,CdrBytes#2,CdrBits#4#4,CdrBits#4#0
     * @return
     */
    public static BasePackager valueOf(String packager, String description) throws Exception
    {
        BasePackager result = new BasePackager();
        // Read parameters of package in config file
        String[] fieldList = packager.split(PARAM_FIELD_SEPARATE, -1);
        if (fieldList.length > 0)
        {
            BaseCdrField[] fields = new BaseCdrField[fieldList.length];
            // Build fields array base on record data and fields description
            for (int iPackager = 0; iPackager < fieldList.length; iPackager++)
            {
                // build field
                fields[iPackager] = BaseCdrField.valueOf(description, fieldList[iPackager], INVALID_LENGTH);
            }
            result.setFields(fields);
        }

        return result;
    }

    /**
     * Concat two packager
     * @param pack1
     * @param pack2
     * @return
     * @throws java.lang.Exception
     */
    public static BasePackager concat(BasePackager pack1, BasePackager pack2) throws Exception
    {
        BasePackager result = new BasePackager();
        BaseCdrField[] fields = new BaseCdrField[pack1.getFields().length + pack2.getFields().length];
        System.arraycopy(pack1.getFields(), 0, fields, 0, pack1.getFields().length);
        System.arraycopy(pack2.getFields(), 0, fields, pack1.getFields().length, pack2.getFields().length);
        result.setFields(fields);

        return result;
    }

    /**
     * Concat two packager
     * @param pack1
     * @param pack2
     * @return
     * @throws java.lang.Exception
     */
    public void extendFields(BasePackager extend) throws Exception
    {
        BaseCdrField[] newFields = new BaseCdrField[fields.length + extend.getFields().length];
        System.arraycopy(fields, 0, newFields, 0, fields.length);
        System.arraycopy(extend.getFields(), 0, newFields, fields.length, extend.getFields().length);
        fields = newFields;
    }
}
