/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author Le Thanh Cong
 */
public class CdrBytes extends BaseCdrField {

    /**
     * Default constructor
     */
    public CdrBytes() {
        super();
    }

    public CdrBytes(int length, String description) {
        super(length, description);
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        byte[] value = new byte[getLength()];
        try {
            value = new byte[getLength()];
            System.arraycopy(b, offset, value, 0, getLength());
        } catch (Exception ex) {
            System.out.println("Length: " + getLength());
            System.out.println("Offset: " + offset);
            ex.printStackTrace();
        }
        return value;
    }
}
