/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.convert.switchboard;


import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.worker.convert.ConvertOutput;
import com.viettel.ginterconnect.worker.util.convert.BaseFunction;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.BaseCdrField;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.BasePackager;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


/**
 *
 * @author chungdq
 */
public class HuaweiPSTNCdrBinaryConverter extends PSTNCdrBinaryConverter {
    
    private final String PARAM_PAKAGE_FUNC_VALUE = "com.viettel.ginterconnect.worker.util.convert";
    private final String PARAM_CALLER_MAP_CODE = "map_code";
    private final String PARAM_MAP_CODE = "code";
    private final String PARAM_FUNCTION_VALUE = "function";
    private final String PARAM_FIELDS_VALUE = "fields";
    private final String PARAM_MAP_VALUE = "map";
    protected Map<String,Map<String,String>> map; //caller DNSet
    protected Map<String, String> getRecord = new HashMap<String, String>();
    protected int recordTypePos = 1; // default value
    
    @Override
    protected void prepareConvert() {
        super.prepareConvert();
        //get caller_DNSet
        try{
            String mapCode = paramFile.getString(PARAM_CALLER_MAP_CODE);
            String strs[] = mapCode.trim().split(",");
            if (strs.length > 0)
            {
                
                map = new HashMap<String, Map<String,String>>();
                for (int i = 0; i < strs.length; i++)
                {
                    //get map item
                    String mapItems = paramFile.getString(PARAM_MAP_CODE + "_" + strs[i].trim());
                    String items[] = mapItems.split(",");
                    Map<String, String> itemMap = new HashMap<String, String>();
                    for (int j = 0; j < items.length; j++)
                    {
                        String key = items[j].trim();
                        String value = paramFile.getString(PARAM_MAP_CODE + "_" + strs[i].trim() + "_" + items[j]).trim();
                        itemMap.put(key, value);
                    }
                    map.put(strs[i].trim(), itemMap);
                }
            }
            String loadRStr = paramFile.getString("GetRecordType");
            if (loadRStr != null && loadRStr.length() > 0) {
                String[] recs = loadRStr.trim().split(",");
                if (recs.length > 0) {
                    for (int i = 0; i < recs.length; i++) {
                        getRecord.put(recs[i], recs[i]);
                    }
                }
            }
            loadRStr = paramFile.getString("FieldRecordType");
            try{
                recordTypePos = Integer.parseInt(loadRStr.trim());
            }
            catch(Exception e)
            {
                recordTypePos = 1;
            }
        }
        catch(Exception ex)
        {
            if (logger != null)
            {
                logger.debug("Have error when read Caller DNSet Config: " + ex.getMessage());
            }
        }
    }

    
    @Override
    protected String getColumn(String columnName)
    {
        columnName = refineColumnName(columnName);
        // Packager identifier; fiel no
        if(columnName == null){
            return "";
        }
        String[] arrStr = columnName.split(PARAM_VALUE_SEPARATE, -1);
        BasePackager packager = null;
        //set fix Value

        if (arrStr[0].equals(PARAM_NULL_VALUE))
        {
            return "";
        }
        else if (arrStr.length == 1)
        {
            return arrStr[0];
        }
        else if (arrStr[0].equals(PARAM_RECORD_HEADER))
        {
            packager = (BasePackager) recordHeader;
        }
        else if (arrStr[0].equals(PARAM_FUNCTION_VALUE))
        {
            packager = (BasePackager) recordHeader;
            if (arrStr.length >= 2){
                String funS = arrStr[1];
                //get parameter
                //get function name
                StringTokenizer tokenStrs = new StringTokenizer(funS.trim(), "(,)", true);
                if (tokenStrs.countTokens() >= 3)
                {
                    try {
                        List<String> tokens = new ArrayList<String>();
                        while (tokenStrs.hasMoreTokens()) {
                            String token = tokenStrs.nextToken();
                            if (token.trim().length() > 0) {
                                tokens.add(token.trim());
                            }
                        }
                        String funName = (String) tokens.get(0);
                        
                        //arg pos = 3,5,7
                        //count argument
                        int countArgs = 0;
                        Object[] args;
                        if (tokens.size() > 3) {
                            countArgs = (int) (tokens.size() - 2) / 2;
                        }
                        args = new Object[countArgs];
                        for (int i = 0; i < countArgs; i++) {
                            String field = tokens.get(2 + i * 2);
                            String[] fields = field.split("#");
                            if (fields.length >= 2) {
                                if (fields[0].equals(PARAM_FIELDS_VALUE)) {
                                    args[i] = packager.getFields()[Integer.parseInt(fields[1])];
                                }
                                else if (fields[0].equals(PARAM_MAP_VALUE))
                                {
                                    if (map.containsKey(fields[1])){
                                        args[i] = map.get(fields[1]);
                                    }else{
                                        args[i] = "";
                                    }
                                }
                                else{
                                    args[i] = "";
                                }
                            } else {
                                args[i] = "";
                            }
                        }
                        //get value
                        Object value =  BaseFunction.getValue(PARAM_PAKAGE_FUNC_VALUE, funName, args);
                        if (value instanceof Date) {
                            return dateFormat.format((Date) value);
                        } else {
                            return value.toString();
                        }

                    } catch (Exception ex) {
                        if (logger != null)
                            logger.debug("Error when get column: " + ex.getMessage());
                        return "";
                    }
                } else {
                    return "";
                }
            } else {
                return "";
            }
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
            //Long columnCount = Long.valueOf(paramFile.getString(PARAM_COLUMN_COUNT));
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
                    //if ( (BasePackager) recordHeader)
                    if (recordHeader instanceof BasePackager)
                    {
                        if ( getRecord.containsKey( ((BasePackager) recordHeader).getFields()[recordTypePos] + "") ){
                            record.clear();
                            boolean error = false;
                            int outputSize = Integer.parseInt(paramFile.getString("output_size"));
                            //for (int clIndex = 0; clIndex < columnCount; clIndex++) {
                            for (int clIndex = 0; clIndex < outputSize; clIndex++) {
                                // Read column
                                String columnName = paramFile.getString("OUT_" +(clIndex + 1));
                                //String value = getColumn(String.format(PARAM_COLUMN_FORMAT, clIndex + 1));
                                String value = getColumn("column_" + columnName);
                                if (value == null) {
                                    addErrorRecord();
                                    // Go to next record
                                    error = true;
                                    break;
                                } else {
                                    record.add(value);
                                }
                            }

                            // Write to correct output file
                            if (!error) {
                                if (fos == null) {
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
                        else
                        {
                            addErrorRecord();
                        }
                    }
                    else
                    {
                        addErrorRecord();
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

    @Override
    protected boolean goNextRecord() {
        boolean result = true;
        cdrError = false;
        curRecordPos += recordLength;
        if (curRecordPos > fiLength - recordLength) {
            result = false;
        } else {
            result = goCurRecord();
            // Prepare for cur record (read record length, ticket type, ...)
            if (result) {
                prepareForCurRecord();
            }
        }
        return result;
    }

}
