/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.worker.convert.switchboard;

import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.worker.convert.BaseConverter;
import com.viettel.ginterconnect.worker.convert.ConvertOutput;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.BaseCdrField;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.TLVLengthType;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author chungdq
 * @version 1.0
 */
public class NokiaMSCConverter extends BaseConverter {

    /** file cdr is stored in mBuffer .*/
    private byte[] mBuffer;
    /** min index.*/
    private int mintIndex = -1;
    /** date format, is configurated in the configuration file.*/
    private static final String PARAM_DATE_FORMAT = "date_format";
    /** max length of file is 8176 B.*/
    private static final int MAX_LENGTH = 8176;
    private Map<String, Integer> mapConfig = new Hashtable<String, Integer>();
    private int i = 0;

    @Override
    public ConvertLog convertDetail() throws Exception {

        ConvertLog convertLog = new ConvertLog();
        try {
            //Load file CDR
            dateFormat = new SimpleDateFormat(paramFile.getString(PARAM_DATE_FORMAT));

            String filePath = inputFileName;
            loadToBuffer(filePath);
            //read  length of header bloc
            int headerLength = Integer.parseInt(paramFile.getString("max_header_size"));
            int fileLength = mBuffer.length;

            String convertFields = paramFile.getString("Record_Fields");
            String arrFields[] = convertFields.split(";");
            for (int i = 0; i < arrFields.length; i++) {
                mapConfig.put(arrFields[i], i);
            }
            //read for type param
            int recordHeaderPart = Integer.parseInt(paramFile.getString("record_header_part_count"));
            String recordHeaderStr = paramFile.getString("record_header_part1");
            String[] fields = null;
            while (mintIndex < fileLength) {
                int maxleng = mintIndex + MAX_LENGTH;
                if (fileLength < MAX_LENGTH) {
                    maxleng = fileLength;
                }
                mintIndex += headerLength;
                processForBloc(recordHeaderPart, recordHeaderStr, fields, mintIndex, maxleng);
                mintIndex = maxleng;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (fos != null) {
                fos.close();
            }
            throw e;
        }
        return convertLog;
    }

    /**
     * Process for header of record
     * @return
     * @throws Exception exception
     * @param recordHearderPart record heard part
     * @param recordHeaderStr Record Header Str
     * @param mintIndex min Index
     * @return TLVLengthType of h
     */
    public TLVLengthType processForRecordHeader(int recordHearderPart,
            String recordHeaderStr, int mintIndex) throws Exception {
        TLVLengthType obj = new TLVLengthType();

        try {
            StringTokenizer st = new StringTokenizer(recordHeaderStr, ";");
            int length = Integer.parseInt(st.nextToken());
            if (length >= 0 && mintIndex >= 0) {
                byte[] tempHeader = new byte[length];
                if (mBuffer.length > 0 && tempHeader.length > 0) {
                    if (mintIndex == mBuffer.length - 1) {
                        System.arraycopy(mBuffer, mintIndex - 2, tempHeader, 0, length);
                    } else if (mintIndex == mBuffer.length - 2) {
                        System.arraycopy(mBuffer, mintIndex - 1, tempHeader, 0, length);
                    } else {
                        System.arraycopy(mBuffer, mintIndex, tempHeader, 0, length);
                    }
                    st.nextToken();
                    int recordHeaderLength = Integer.parseInt(paramFile.getString("record_header_lenth"));
                    int recordHeaderRecordType = Integer.parseInt(paramFile.getString("record_header_recordType"));

                    StringTokenizer fileSt = new StringTokenizer(st.nextToken(), ",");
                    for (int i = 0; i < recordHearderPart; i++) {
                        if (i == recordHeaderLength) {
                            BaseCdrField cd = BaseCdrField.valueOf("", fileSt.nextToken(), -1);
                            Object re1 = cd.unpack(tempHeader, 0);
                            obj.setLength((Long) re1);
//                obj.setLength(Long.parseLong(re1.toString()));
                        } else if (i == recordHeaderRecordType) {
                            BaseCdrField cd = BaseCdrField.valueOf("", fileSt.nextToken(), -1);
                            Object re1 = cd.unpack(tempHeader, 2);
                            obj.setType((Integer) re1);
                        //obj.setType(Integer.parseInt(re1.toString()));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

        return obj;
    }

    /**
     * @param strFilePath load file
     * @exception Exception while load file exception
     * .*/
    @SuppressWarnings("empty-statement")
    public void loadToBuffer(String strFilePath)
            throws Exception {
        RandomAccessFile fileCDR;
        fileCDR = null;
        try {
            fileCDR = new RandomAccessFile(strFilePath, "r");
            int iLength = (int) fileCDR.length();

            mBuffer = new byte[iLength];
            int intBytesRead = fileCDR.read(mBuffer);
            if (intBytesRead != iLength) {
                throw new Exception("Error when reading file");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new Exception("Error when load file to buffer: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Error when load file to buffer: " + e.getMessage());
        } finally {
            mintIndex = 0;
        }
        try {
            fileCDR.close();
        } catch (Exception e) {
            System.out.println("");
        }

    }

    private void processForBloc(int recordHeaderPart, String recordHeaderStr,
            String[] fields, int mintIndex, int maxLength) throws Exception {
        try {
            do {
                TLVLengthType headerBlock = processForRecordHeader(recordHeaderPart, recordHeaderStr, mintIndex);

                String recordStr = "";
                String outputFields = "";
                String strRecord = paramFile.getString("Record_Type_" + headerBlock.getType());
                if (strRecord == null || strRecord.length() < 1) {
                    recordStr = "";
                } else {
                    String arrRecord[] = paramFile.getString("Record_Type_" + headerBlock.getType()).split(",");
                    if (arrRecord != null && arrRecord.length > 1) {
                        int outputSize = Integer.parseInt(paramFile.getString("output_size"));
                        for (int i = 0; i < outputSize; i++) {
                            String outputField = paramFile.getString("OUT_" + (i + 1));
                            if (outputField != null && mapConfig.containsKey(outputField)) {
                                outputFields += (arrRecord[mapConfig.get(outputField).intValue()] + ",");
                            } else {
                                outputFields += "@,";
                            }
                        }
                    }
                    recordStr = outputFields;
                }
                //String recordStr = paramFile.getString("Record_Type_" + headerBlock.getType());
                //recordStr = outputFields;

                List record = new ArrayList();

                if (mintIndex + headerBlock.getLength() <= maxLength) {

                    byte[] source = new byte[(int) headerBlock.getLength()];

                    System.arraycopy(mBuffer, mintIndex, source, 0, (int) headerBlock.getLength());

                    //1#4#cdrNumber
                    if (recordStr != null && !recordStr.trim().equals("")) {

                        fields = recordStr.split(",");
                        //repalce all field is @ to ""
                        if (fields != null) {
                            for (int i = 0; i < fields.length; i++) {
                                if (fields[i] != null && fields[i].equals("@")) {
                                    fields[i] = "";
                                }
                            }
                        }

//                    System.out.println(recordStr);
//                    System.out.println(fields.length);
                        for (String string : fields) {

                            if (string != null && string.trim().length() > 0) {
                                StringTokenizer fst = new StringTokenizer(string, "#");
                                if (fst.countTokens() > 2) {
                                    //position
                                    int post = Integer.parseInt(fst.nextToken());
                                    //length
                                    int length = Integer.parseInt(fst.nextToken());

                                    //Pattern
                                    String pattern = fst.nextToken();

                                    //unpack
                                    BaseCdrField cd = BaseCdrField.valueOf("", pattern, length);

                                    Object ob;
                                    try {
                                        ob = cd.unpack(source, post);
                                        if (ob != null) {
                                            if (ob.toString().equals("6513879101")) {
                                                System.out.println("comehere");
                                            }
                                        }
                                    } catch (Exception ex) {
                                        ob = null;
                                    }
                                    if (ob instanceof Date) {
                                        record.add(dateFormat.format((Date) ob));
                                    } else if (ob == null) {
                                        record.add(new String(""));
                                    } else {
                                        record.add(ob);
                                    }
                                } else {
                                    record.add(fst.nextToken());
                                }
                            } else {
                                record.add("");
                            }

                        }

                        if (fos == null) {
                            fos = new ConvertOutput(outputFileName, paramFile.getString("separate"));
                            fos.setFileIdPosition(fileIdPosition);
                            fos.setFileId(super.getFileId());
                        }
                        totalConverted++;

                        //check drop to add
                        String causeCode = String.valueOf(record.get(24));
                        //comment 07/01/17 for write all records
//                        if (mapDropCauseCode.containsKey(causeCode)) {
//                            fos.addRecord(record);
//                        }
                        fos.addRecord(record);
                        fos.flush();
                    }
                }
                //next block
                if ((int) headerBlock.getLength() == 0) {
                    break;
                } else {
                    mintIndex += (int) headerBlock.getLength();
                }
            } while (mintIndex < maxLength);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public static final Map<String, String> mapDropCauseCode;//nokia
    static{
        mapDropCauseCode = new HashMap<>();
        mapDropCauseCode.put("00000811", "1");
        mapDropCauseCode.put("00000A04", "1");
        mapDropCauseCode.put("00000B1B", "1");
        mapDropCauseCode.put("00000B13", "1");
        mapDropCauseCode.put("00000B1A", "1");
        mapDropCauseCode.put("00000812", "1");
    };
}
