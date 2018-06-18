/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.convert.switchboard;

import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.worker.convert.BaseConverter;
import com.viettel.ginterconnect.worker.convert.ConvertOutput;
import com.viettel.ginterconnect.util.DateTimeUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author chungdq
 */
public class SMPPConverter extends BaseConverter {

    private BufferedReader buffer;
    private int columnSize;

    private String separate;
    private String column[];
    private static final String PARAM_DATE_FORMAT = "date_format";
    private Map<Integer, Integer> indexMap;
    private Map<Integer, String> typeMap;
    private Map<Integer, Integer> lengthMap;
    private Map<Integer, String> fixValueMap;

    @Override
    public ConvertLog convertDetail() throws Exception {

        ConvertOutput textFile = null;

        try {
            loadConvertParam();
            textFile = new ConvertOutput(outputFileName,separate);
            //add by chungdq for add fileID
            textFile.setFileIdPosition(fileIdPosition);
            textFile.setFileId(super.getFileId());

            loadToBuffer(inputFileName);
            String line = buffer.readLine();
            while (line != null)
            {
                processLine(line);
                totalConverted++;
                textFile.addRecord(column);
                // next line
                line = buffer.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            textFile.close();
        }
        finally{
            buffer.close();
            textFile.close();
        }
        return new ConvertLog();
    }

    protected void processLine(String line)
    {
        column = new String[columnSize];
        String temp[] = line.split(",");
        for (Integer i = 0; i < columnSize && i < temp.length; i++)
        {
            if (typeMap.containsKey(i))
            {
                if (typeMap.get(i).equals("DateTime"))
                {
                    column[i.intValue()] = dateFormat.format(getDateTimeValue(temp[indexMap.get(i).intValue()].trim()));
                }else
                {
                    column[i.intValue()] = temp[indexMap.get(i).intValue()].trim();
                }
            }else{
                if (fixValueMap.containsKey(i))
                {
                    column[i.intValue()] = fixValueMap.get(i).trim();
                }
            }
        }
    }

    public void loadConvertParam() throws Exception {
        //get property
        separate = paramFile.getString("separate").trim();
        dateFormat = new SimpleDateFormat(paramFile.getString(PARAM_DATE_FORMAT));
        columnSize = Integer.parseInt(paramFile.getString("column_count"));

        indexMap = new HashMap<Integer, Integer>();
        typeMap = new HashMap<Integer, String>();
        lengthMap = new HashMap<Integer, Integer>();
        fixValueMap = new HashMap<Integer, String>();
        //split tag
        for (Integer i = 1; i <= columnSize; i++)
        {
            String str = paramFile.getString("Column" + i);
            String arrS[] = str.split("#");
            if (arrS.length == 3)
            {
                indexMap.put(i - 1, Integer.parseInt(arrS[0]));
                lengthMap.put(i - 1, Integer.parseInt(arrS[1]));
                typeMap.put(i - 1, arrS[2].trim());
            }else{
                fixValueMap.put(i - 1, str.trim());
            }
        }
    }


    private Date getDateTimeValue(String str)
    {
        return DateTimeUtils.convertStringToTime(str.substring(0, 19), "yyyy/MM/dd HH:mm:ss");
    }

    public void loadToBuffer(String strFilePath) throws Exception
     {
        //Exception exception;
        buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(new FileInputStream(new File(strFilePath))));
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            if (buffer != null)
                  buffer.close();
            throw new Exception("Error when load file to buffer: " + ex.getMessage());
        }
    }
}
