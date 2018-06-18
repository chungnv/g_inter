/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.convert.switchboard;


import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.worker.convert.BaseConverter;
import com.viettel.ginterconnect.worker.convert.ConvertOutput;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.BaseCdrField;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.BasePackager;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.CdrRecordHeader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class use to convert binary CDR file
 * @author Pham Van Kien
 */
public abstract class CdrBinaryConverter extends BaseConverter
{

    protected static final String PARAM_KEY_CONECTER = "_";
    protected static final String PARAM_NULL_VALUE = "null";
    protected static final String PARAM_CONST_VALUE = "const";
    protected static final String PARAM_RECORD_HEADER = "record_header";
    protected static final String PARAM_COLUMN_COUNT = "column_count";
    protected static final String PARAM_COLUMN_FORMAT = "column%d";
    protected static final String PARAM_COLUMN_SEPARATE = "separate";
    protected static final String PARAM_ADDITION_SEPARATE = "#";
    protected static final String PARAM_VALUE_SEPARATE = ";";
    protected Long curRecordPos = null;
    protected RandomAccessFile fis = null;
    protected Long fiLength = null;
    protected Long recordLength = null;
    protected byte[] recordData = null;
    protected BasePackager[] packagerList = null;
    protected CdrRecordHeader recordHeader = null;

    protected boolean goNextRecord()
    {
        boolean result = true;
        cdrError = false;
//        if (curRecordPos > 2138724) {
//            System.out.println("stop here");
//        }
        curRecordPos += recordLength;
        if (curRecordPos > fiLength)
        {
            result = false;
        }
        else
        {
            result = goCurRecord();
            // Prepare for cur record (read record length, ticket type, ...)
            if (result)
            {
                prepareForCurRecord();
            }
        }
        return result;
    }

    protected boolean goCurRecord()
    {
        boolean result = true;
        try
        {
            fis.seek(curRecordPos.longValue());
        }
        catch (IOException ex)
        {
            result = false;
            logger.debug(ex.toString());
        }
        return result;
    }

    protected abstract void prepareForCurRecord();

    protected String refineColumnName(String columnName)
    {
        String temp = columnName;
        columnName = paramFile.getString(columnName);
        BasePackager packager = (BasePackager) recordHeader;
        if (columnName == null) {
            return null;
        }
        if (columnName.indexOf("fields") >= 0)
        {
            String[] arr = columnName.split(PARAM_ADDITION_SEPARATE, -1);
            columnName = paramFile.getString(temp + PARAM_KEY_CONECTER + ((Long) packager.getFields()[Integer.parseInt(arr[1])].getValue()).toString());            
        }
        return columnName;
    }

    protected String getColumn(String columnName)
    {
        columnName = refineColumnName(columnName);
        // Packager identifier; fiel no
        if(columnName == null){
            return "";
        }
        String[] arrStr = columnName.split(PARAM_VALUE_SEPARATE, -1);
        BasePackager packager = null;
        if (arrStr[0].equals(PARAM_NULL_VALUE))
        {
            return "";
        }
        else if(arrStr[0].equals(PARAM_CONST_VALUE) && arrStr.length >=2)
        {
            return arrStr[1];
        }
        else if (arrStr[0].equals(PARAM_RECORD_HEADER))
        {
            packager = (BasePackager) recordHeader;
        }
        else
        {
            packager = packagerList[Integer.parseInt(arrStr[0])];
        }

        int index = Integer.parseInt(arrStr[1]);
        if (packager == null)
        {
            // Check nullable
            if (arrStr.length > 2 && arrStr[2].equals("true"))
            {
                return "";
            }
            else if (arrStr.length > 3)
            {
                packager = packagerList[Integer.parseInt(arrStr[2])];
                index = Integer.parseInt(arrStr[3]);

                if (packager == null)
                {
                    return null;
                }
            }
            else
            {
                return null;
            }
        }
        if (index > packager.getFields().length)
        {
            return null;
        }
        else
        {
            BaseCdrField field = (packager.getFields()[index]);
            Object value = field.getValue();
            if (value instanceof Date)
            {
                return dateFormat.format((Date) value);
            }
            else
            {
                return field.toString();
            }
        }
    }

    protected byte[] getCurBinaryRecord()
    {
        byte[] result = new byte[recordLength.intValue()];
        try
        {
            fis.seek(curRecordPos.longValue());
            fis.read(result);
        }
        catch (IOException ex)
        {
            logger.debug(ex.toString());
        }
        return result;
    }

    /**
     * Prepare parameters for convert
     */
    protected abstract void prepareConvert() throws Exception;

    @Override
    public ConvertLog convertDetail() throws Exception
    {
        try
        {
            File fi = new File(inputFileName);
            fis = new RandomAccessFile(fi, "r");
            fiLength = fi.length();
            prepareConvert();
            List record = new ArrayList();
            // Dua ra Header
            String strHeader = paramFile.getString("Header_String");
            if(strHeader != null){
                String[] writeArr = strHeader.split(",", -1);
                if(fos == null){
                    fos = new ConvertOutput(outputFileName, paramFile.getString("separate"));
                    //add by chungdq for add fileID
                    fos.setFileIdPosition(fileIdPosition);
                    fos.setFileId(super.getFileId());
                }
                fos.addRecord(writeArr);
                fos.flush();
            }
            
            // Read column count of text file
            Long columnCount = Long.valueOf(paramFile.getString(PARAM_COLUMN_COUNT));
            boolean notEOF = true;
            cdrError = false;
            prepareForCurRecord();
            while (notEOF)
            {
                // In goNextRecord() function, maybe verify error record
                if (cdrError)
                {
                    addErrorRecord();
                }
                else
                {
                    record.clear();
                    boolean error = false;
                    for (int clIndex = 0; clIndex < columnCount; clIndex++)
                    {
                        // Read column
                        String value = getColumn(String.format(PARAM_COLUMN_FORMAT, clIndex + 1));
                        if (value == null)
                        {
                            addErrorRecord();

                            // Go to next record
                            error = true;
                            break;
                        }
                        else
                        {
                            record.add(value);
                        }
                    }
                    // Write to correct output file
                    if (!error)
                    {
                        if (fos == null)
                        {
                            fos = new ConvertOutput(outputFileName, paramFile.getString(PARAM_COLUMN_SEPARATE));

                            //add by chungdq for add fileID
                            fos.setFileIdPosition(fileIdPosition);
                            fos.setFileId(super.getFileId());
                        }
                        fos.addRecord(record);
                        totalConverted++;
                        fos.flush();
                    }
                }
                notEOF = goNextRecord();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
        finally
        {
            fis.close();
        }
        return new ConvertLog();
    }

    /**
     * Add error record to error file
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    protected void addErrorRecord() throws FileNotFoundException, IOException
    {
        // Error record, write binary record to error file
        if (fos_error == null)
        {
            fos_error = new FileOutputStream(errorFileName);
        }

        fos_error.write(getCurBinaryRecord());
        fos_error.flush();
    }
}
