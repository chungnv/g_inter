/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author ubuntu
 */
public class CdrCIC extends BaseCdrField {

    public boolean littleEndian = true;
    
    public CdrCIC() {
        super();
    }

    public CdrCIC(int length, String description, boolean littleEndian) {
        super(length, description);
        this.littleEndian = littleEndian;
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isLittleEndian() {
        return littleEndian;
    }

    public void setLittleEndian(boolean littleEndian) {
        this.littleEndian = littleEndian;
    }
}
