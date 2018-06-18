/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author Le Thanh Cong
 */
public class SiemenGMSCCdrRecordHeader extends SiemenGMSCPackager implements CdrRecordHeader
{

    private int length;

    public SiemenGMSCCdrRecordHeader()
    {
        super();
    }

    public SiemenGMSCCdrRecordHeader(BasePackager header)
    {
        super(header);
    }

    public int getStartOfPackageData()
    {
        return getLength();
    }

    public int getRecordLength()
    {
        return length;
    }

    public void setRecordLength(int length)
    {
        this.length = length;
    }
}
