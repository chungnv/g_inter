/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author Chungdq
 */
public class PSTNCdrRecordHeader extends BasePackager implements CdrRecordHeader
{

    private int fixedRecordLength;

    public PSTNCdrRecordHeader()
    {
        super();
    }

    public PSTNCdrRecordHeader(BasePackager header)
    {
        super(header);
    }

    public int getStartOfPackageData()
    {
        return getRecordLength();
    }

    public int getRecordLength()
    {
        return fixedRecordLength;
    }

    public void setFixedRecordLength(int fixedRecordLength)
    {
        this.fixedRecordLength = fixedRecordLength;
    }
}
