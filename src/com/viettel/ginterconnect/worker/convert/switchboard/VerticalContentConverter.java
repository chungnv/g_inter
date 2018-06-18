/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.worker.convert.switchboard;

import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.worker.convert.BaseConverter;
import com.viettel.ginterconnect.worker.convert.ConvertOutput;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class VerticalContentConverter extends BaseConverter {

    private HashMap<String, String> mapField = null;
    private ArrayList<String> writeField = new ArrayList<String>();
    private String startCdr = "{";
    private String endCdr = "}";
    private String cdrSplit = "=";
    private String cdrDateFormat = "yyyyMMddHHmmss";
    private String dateFormat = "yyyy/MM/dd HH:mm:ss";
    private BufferedReader buffer;

    public void loadProperties() throws Exception {

        mapField = new HashMap<String, String>();

        if (paramFile.getString("cdr_start") != null && paramFile.getString("cdr_start").length() > 0) {
            startCdr = paramFile.getString("cdr_start");
        }
        if (paramFile.getString("cdr_date_format") != null && paramFile.getString("cdr_date_format").length() > 0) {
            cdrDateFormat = paramFile.getString("cdr_date_format");
        }
        if (paramFile.getString("cdr_end") != null && paramFile.getString("cdr_end").length() > 0) {
            endCdr = paramFile.getString("cdr_end");
        }
        if (paramFile.getString("cdr_field_split") != null && paramFile.getString("cdr_field_split").length() > 0) {
            cdrSplit = paramFile.getString("cdr_field_split");
        }
        if (paramFile.getString("separate") != null && paramFile.getString("separate").length() > 0) {
            separate = paramFile.getString("separate");
        }
        if (paramFile.getString("date_format") != null && paramFile.getString("date_format").length() > 0) {
            dateFormat = paramFile.getString("date_format");
        }

        int outputSize = Integer.parseInt(paramFile.getString("output_size"));
        ArrayList<String> output = new ArrayList<String>();
        for (int i = 0; i < outputSize; i++) {
            if (paramFile.getString("OUT_" + (i + 1)) == null || paramFile.getString("OUT_" + (i + 1)).length() < 1) {
                System.out.println("OUT_" + (i + 1) + "  is not configured.");
                throw new Exception();
            }
//            if ("FILE_ID".equals(paramFile.getString("OUT_" + (i + 1)).trim())) {
//                fileIdPosition = i + 1;
//            }
            output.add(paramFile.getString("OUT_" + (i + 1)).trim());
        }

//        if (!output.contains("FILE_ID")) {
//            fileIdPosition = outputSize;
//        }
        String[] arrHeader = paramFile.getString("record_header").split(",");
        String[] arrHeaderValue = paramFile.getString("record_header_value").split(",");

        if (arrHeader.length != arrHeaderValue.length) {
            System.out.println("Header and Header Value is not compatible");
            throw new Exception("Header and Header Value is not compatible");
        }

        HashMap<String, Integer> mapHeader = new HashMap<String, Integer>();
        for (int i = 0; i < arrHeader.length; i++) {
            mapHeader.put(arrHeader[i], i);
        }

        for (int i = 0; i < output.size(); i++) {
            if (mapHeader.containsKey(output.get(i))) {
                mapField.put(arrHeader[mapHeader.get(output.get(i))], arrHeaderValue[mapHeader.get(output.get(i))]);
                writeField.add(arrHeader[mapHeader.get(output.get(i))]);
            } else {
                writeField.add(output.get(i));
            }
        }
//        for (int i = 0; i < arrHeader.length; i++) {
//            if (output.contains(arrHeader[i].trim())) {
//                mapField.put(arrHeader[i], arrHeaderValue[i]);
//                writeField.add(arrHeader[i]);
//            }
//        }
    }

    @Override
    public ConvertLog convertDetail() throws Exception {
        
        if (mapField == null) {
            loadProperties();
        }
        
//        TextOutputFile textFile = new TextOutputFile(outputFileName, separate);
//        //add by chungdq for add fileID
//        textFile.setFileIdPosition(fileIdPosition);
//        textFile.setFileId(super.getFileId());
        loadToBuffer(inputFileName);
        String line;
        List record = new ArrayList();
        HashMap<String, String> mapCdr = new HashMap<String, String>();
        while ((line = buffer.readLine()) != null) {
            if (startCdr.equals(line)) {
                record = new ArrayList();
                mapCdr = new HashMap<String, String>();
            } else if (endCdr.equals(line)) {
                for (int i = 0; i < writeField.size(); i++) {
                    if (!mapField.containsKey(writeField.get(i))) {
                        record.add("");
                    } else {
                        String[] fieldKey = mapField.get(writeField.get(i)).split(":");
                        if (fieldKey.length == 1) {
                            if (mapCdr.containsKey(fieldKey[0])) {
                                record.add(mapCdr.get(fieldKey[0]));
                            } else {
                                record.add("");
                            }
                        } else if (fieldKey.length == 2) {
                            if ("Date".equals(fieldKey[1])) {
                                if (mapCdr.containsKey(fieldKey[0])) {
                                    SimpleDateFormat sdf = new SimpleDateFormat(cdrDateFormat);
                                    Date dateCdr = sdf.parse(mapCdr.get(fieldKey[0]));
                                    sdf = new SimpleDateFormat(dateFormat);
                                    record.add(sdf.format(dateCdr));
                                } else {
                                    record.add("");
                                }
                            }
                        } else {
                            System.out.println("Wrong Config: " + mapField.get(writeField.get(i)));
                        }
                    }
                }
                if (this.fos == null) {
                    this.fos = new ConvertOutput(this.outputFileName, this.paramFile.getString("separate"));

                    this.fos.setFileIdPosition(this.fileIdPosition);
                    this.fos.setFileId(super.getFileId());
                }

//                if (fileIdPosition < record.size()) {
//                    try {
//                        record = insertToList(fileIdPosition - 1, super.getFileId().toString(), record);
//                    } catch (Exception ex) {
//                        System.out.println("x");
//                    }
//                }

                this.fos.addRecord(record);
                this.totalConverted += 1;
                this.fos.flush();

            } else {
                String[] arr = line.split(cdrSplit);
                if (!line.contains(cdrSplit)) {
                    System.out.println("Line Error: " + line);
                } else if (arr.length != 2) {
                    //System.out.println("Line Error: " + line);
                    mapCdr.put(arr[0].trim(), "");
                } else {
                    if (arr[1] != null) {
//                        String tmp = arr[1].trim();
//                        while (tmp.endsWith(",")) {
//                            tmp = tmp.substring(0, tmp.length() - 1);
//                        }
                        mapCdr.put(arr[0].trim(), arr[1].trim());
//                        mapCdr.put(arr[0].trim(), tmp);
                    } else {
                        mapCdr.put(arr[0].trim(), "");
                    }
                }
            }
        }
        return new ConvertLog();
//        this.fos.close();
//        this.fos = null;

    }

    public List insertToList(int pos, String value, List<String> record) {
        List<String> temp = new ArrayList<String>();
        for (int i = 0; i < pos; i++) {
            temp.add(record.get(i));
        }
        temp.add(value);
        for (int i = pos; i < record.size(); i++) {
            temp.add(record.get(i));
        }
        return temp;
    }

    public void loadToBuffer(String strFilePath) throws Exception {
        //Exception exception;
        buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(new FileInputStream(new File(strFilePath))));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            if (buffer != null) {
                buffer.close();
            }
            throw new Exception("Error when load file to buffer: " + ex.getMessage());
        }
    }
}
