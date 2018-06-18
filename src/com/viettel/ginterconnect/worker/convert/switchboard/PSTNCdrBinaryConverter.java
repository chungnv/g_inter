/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.convert.switchboard;

/**
 *
 * @author ChungDQ
 */
public class PSTNCdrBinaryConverter extends CdrBinaryConverter
{

    private static final String PARAM_FIXED_RECORD_LENGTH = "fixed_record_length";

    @Override
    protected void prepareConvert()
    {
        recordLength = Long.valueOf(paramFile.getString(PARAM_FIXED_RECORD_LENGTH));
        curRecordPos = 0L;
    }

    @Override
    protected void prepareForCurRecord()
    {
        try
        {
            // Read header of record
            recordData = new byte[recordLength.intValue()];
            fis.read(recordData);
            recordHeader = paramFile.getPSTNRecordHeader(recordData, 0);
        }
        catch (Exception ex)
        {
            cdrError = true;
            ex.printStackTrace();
//            if (logger != null)
//                logger.severe(ex.toString());
        }
    }
}
