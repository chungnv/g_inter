/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.worker.convert.switchboard;

import codec.asn1.ASN1IA5String;
import codec.asn1.ASN1Integer;
import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.worker.convert.BaseConverter;
import com.viettel.ginterconnect.worker.convert.ConvertOutput;
import com.viettel.ginterconnect.util.ASNUtil;
import com.viettel.ginterconnect.worker.util.ByteArrayUtil;
import com.viettel.ginterconnect.worker.util.convert.WriteItem;
import com.viettel.ginterconnect.worker.util.convert.WriteItemField;
import com.viettel.ginterconnect.worker.util.convert.WriteItemFixField;
import com.viettel.ginterconnect.worker.util.convert.WriteItemFunction;
import com.viettel.ginterconnect.worker.util.convert.WriteItemMap;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.CdrBits;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.CdrChars;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.CdrNumeric;
import com.viettel.ginterconnect.worker.util.convert.ericsson.EricssonRecord;
import com.viettel.ginterconnect.worker.util.convert.cdrfieldtype.TLVLengthType;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.apache.log4j.Logger;

/**
 *
 * @author chungdq
 * @version 1.0
 */
public class EricssonMSCConverter extends BaseConverter {

    /** .*/
    private byte[] mBuffer;
    /** .*/
    private int mintIndex = -1;
    /** .*/
    private int columnSize;
    /** .*/
    private int writeSize;
    /** .*/
    private String separate;
    /** .*/
    private String dateFormat;
    /** .*/
    private String[] columArr;
    /** .*/
    private String[] writeArr;
    /** .*/
    private Map<String, String> getRecord = new HashMap<String, String>();
    /** .*/
    private WriteItem[] writeItems;
    /** .*/
    private int[] writeTypes;
    /** .*/
    private static final Logger LOG = Logger.getLogger(EricssonMSCConverter.class);
    /**bien moi truong de doc tung block.*/
    private String curParentTag; //DEC_HEX_HEX... ex: 160_A0, 166_82
    /** .*/
    private Integer curTag;
    /** .*/
    private Integer beginIndex;
    /** .*/
    private Integer endIndex;
    /** .*/
    private Integer curIndex = -1;
    /** .*/
    private Integer level = 0;
    /** .*/
    private Stack<Object> envStack;

    /** .*/
    private int recordCount = 0;
    /** .*/
    private boolean isNewRecord = false; //check xem co record moi khong
    //map de fix truong hop tong dai day ra loi co 2 gia tri cho 1 truong
    /**Khi dochi lay truong dau tien .*/
    private Map<String, String> mapIsHaveValue = null;
    /** .*/
    private ConvertOutput textFile = null;
    /** .*/
    private Map<Integer, EricssonRecord> recordMap = new HashMap<Integer, EricssonRecord>();
    /** .*/
    private static final int MAX_TAG_LENGTH = 3;
    /** .*/
    private static final long LONG_VALUE_OF_A0 = 160L;

    @Override
    public ConvertLog convertDetail() throws Exception {
        //outputFileName = "C:\\EricssonOutput.txt";
        envStack = new Stack<Object>();
        textFile = new ConvertOutput(outputFileName);
        //add by chungdq for add fileID
        textFile.setFileIdPosition(fileIdPosition);
        textFile.setFileId(super.getFileId());

        //load defaul value
        //paramFile = new ParamFileUtil(FILE_PROPERTIES);
        try {
            //Load file CDR
            String filePath = inputFileName;
            loadToBuffer(filePath);
            loadConvertParam();

            beginIndex = 0;
            endIndex = mBuffer.length - 1;
            curIndex = 0;
            curTag = 0;
            curParentTag = "";

            TLVLengthType value;
            while (curIndex < mBuffer.length - 1) {
                //
//                if ("A7".equals(curParentTag)) {
//                    System.out.println("A7777");
//                }
                //
                if (curIndex < endIndex - 1) {
                    value = TLVBlocProcess();
                    //if read fail pass record
                    if (value == null) {
                        curIndex = endIndex.intValue();
                    } else {
                        String curStr;
                        if (curParentTag.equals("")) {
                            curStr = Integer.toHexString(value.getTagTypeInt()).toUpperCase();
                        } else {
                            curStr = curParentTag + "_" + Integer.toHexString(value.getTagTypeInt()).toUpperCase();
                        }
                        if (paramFile.getString("Record_" + curStr + "_tag") == null) {
                            //check value is have
                            if (level == 1) {
                                //chi process khi ma chua ton tai gia tri da duoc xet
                                if (!mapIsHaveValue.containsKey(curStr)) {
                                    mapIsHaveValue.put(curStr, curStr);
                                    processForValue(value, curParentTag);
                                }
                            } else {
                                // if value tag
                                processForValue(value, curParentTag);
                            }
                            curIndex += ((int) value.getLength() + 1);
                        } else {
//                            System.out.println(curParentTag);
                            pushEnv(); // save environment

                            //constructor mapIsHaveValue
                            if (level == 0) {
                                mapIsHaveValue = null;
                                mapIsHaveValue = new HashMap<String, String>();
                            }

                            level = new Integer(level.intValue() + 1);
                            curTag = value.getTagTypeInt();

                            curParentTag = new String(curStr);

                            curIndex = new Integer(curIndex.intValue() + 1);
                            beginIndex = new Integer(curIndex);
                            endIndex = curIndex + (int) value.getLength() - 1;

                            if (level == 1) {
                                columArr = new String[columnSize];
                                isNewRecord = true;

                                //get RecordType
                                if (!getRecord.containsKey(Integer.toHexString(curTag).toUpperCase())) {
                                    curIndex = endIndex.intValue() + 1;
                                    popEnv();
                                } else {
                                    recordCount++;
                                }
                            }
                        }
                    }
                } else {
                    if (level == 1) {
                        // save record
                        transformColToWrite();
                    }
                    curIndex = endIndex.intValue() + 1;
                    popEnv();
                }
            }

//            System.out.println(ColumArr[7] + " " + ColumArr[8] + " " + ColumArr[9]);
            if (isNewRecord && !isEmptyRecord(columArr)) {
                transformColToWrite();
            }
            LOG.info("Total record: " + totalConverted);
            LOG.info("End for convert file : " + inputFileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("total Record = " + recordCount);
        } finally {
            textFile.close();
        }
        return new ConvertLog();
    }

    private boolean isEmptyRecord(String[] arr) {
        if (arr == null) {
            return true;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && arr[i].trim().length() > 0) {
                return false;
            }
        }
        return true;
    }

    private void transformColToWrite() throws Exception {
        writeArr = new String[writeSize];
        for (int i = 1; i <= writeSize; i++) {

            //map columnArr to writeArr
            if (writeItems[i - 1].getType() == WriteItem.FIELD_TYPE_FIX) {
                writeArr[i - 1] = paramFile.getString("Record_" + Integer.toHexString(curTag).toUpperCase()
                        + "_column" + (((WriteItemFixField) writeItems[i - 1]).getFieldId()));
            } else if (writeItems[i - 1].getType() == WriteItem.FIELD_TYPE_FIELD) {
                writeArr[i - 1] = columArr[((WriteItemField) writeItems[i - 1]).getFieldId() - 1];
            } else if (writeItems[i - 1].getType() == WriteItem.FIELD_TYPE_CONSTANT) {
                writeArr[i - 1] = writeItems[i - 1].getValue().toString();
            } else if (writeItems[i - 1].getType() == WriteItem.FIELD_TYPE_MAP) {
                ((WriteItemMap) writeItems[i - 1]).
                        caculate(columArr[((WriteItemMap) writeItems[i - 1]).getFieldId() - 1]);
                writeArr[i - 1] = ((WriteItemMap) writeItems[i - 1]).getValue().toString();
            } else if (writeItems[i - 1].getType() == WriteItem.FIELD_TYPE_FUNCTION) {
                WriteItemFunction f = (WriteItemFunction) writeItems[i - 1];
                WriteItem[] listArg = f.getColumns();
                Object[] args = new Object[f.getDynamicCount()];
                int dynamicCount = 0;
                int argCount = 0;
                while (dynamicCount < args.length && argCount < listArg.length) {
                    if (listArg[argCount].getType() == WriteItem.FIELD_TYPE_FIELD) {
                        args[dynamicCount] = columArr[((WriteItemField) listArg[argCount]).getFieldId() - 1];
                        if (args[dynamicCount] == null) {
                            //Neu null co the cho nay co the la truong la truong constant.
                            // lay truong theo constant
                            String str = paramFile.getString("Record_" + Integer.toHexString(curTag).toUpperCase()
                                    + "_column" + (((WriteItemField) listArg[argCount]).getFieldId()));
                            if (str != null && str.indexOf(",") < 0) {
                                args[dynamicCount] = str;
                            }
                        }
                        dynamicCount++;
                    } else if (listArg[argCount].getType() == WriteItem.FIELD_TYPE_FIX) {
                        args[dynamicCount] = columArr[((WriteItemFixField) listArg[argCount]).getFieldId() - 1];
                        dynamicCount++;
                    } else if (listArg[argCount].getType() == WriteItem.FIELD_TYPE_MAP) {
                        ((WriteItemMap) listArg[argCount]).
                                caculate(columArr[((WriteItemMap) listArg[argCount]).getFieldId() - 1]);
                        args[dynamicCount] = ((WriteItemMap) listArg[argCount]).getValue().toString();
                        dynamicCount++;
                    }
                    argCount++;
                }
                f.caculate(args);
                if ((f.getValue() != null) && (!"".equals(f.getValue().toString()))) {
                    writeArr[i - 1] = f.getValue().toString();
                } else {
                    writeArr[i - 1] = "";
                }
            } else {
                writeArr[i - 1] = "";
            }

        }

        //load parent env
        totalConverted++;

        //check drop to write
        String ranap = writeArr[28];
        String bssap = writeArr[27];
        String cc = writeArr[24];
        String diag = writeArr[25];

        //comment 07/01/17 for write all records
//        if (mapDropBssap.containsKey(ranap) || mapDropBssap.containsKey(bssap) || mapDropCauseCodeDiag.containsKey(cc + ";" + diag)) {
//            textFile.addRecord(writeArr);
//        }
        textFile.addRecord(writeArr);
        isNewRecord = false;

    }

    /*
     * Xu ly cho tung block
     * voi chi so tag = index
     * Ket qua tra ve kieu object chua TYPE, LENGTH va VALUE
     */
    public TLVLengthType TLVBlocProcess() throws Exception {
        //env
        //curIndex
        //beginIndex
        //endIndex
        //curTag
        //curParentTag

        byte[] temp = new byte[MAX_TAG_LENGTH];
        long tagTypeInt = 0;

        byte tagTitle = mBuffer[curIndex.intValue()];
        TLVLengthType lObj = new TLVLengthType();

        try {
            if (curTag == 0) {
                // record type
                while (ByteArrayUtil.byteToUnsignedByte(tagTitle) != LONG_VALUE_OF_A0) {
                    curIndex++;
                    if (curIndex > mBuffer.length - 1) {
                        return null;
                    }
                    tagTitle = mBuffer[curIndex.intValue()];
                }
                getLength(curIndex + 1);
                //pass from header of record

                curIndex++;
                tagTitle = mBuffer[curIndex.intValue()];
                tagTypeInt = ByteArrayUtil.byteToUnsignedByte(tagTitle);
                lObj.setTagTypeInt((int) tagTypeInt);
                if (recordMap.containsKey((int) tagTypeInt)) {
                    lObj = getLength(curIndex + 1);
                    lObj.setTagTypeExtend(temp);
                    lObj.setTagType(tagTitle);
                    lObj.setTagTypeInt((int) tagTypeInt);
                }
            } else {
                // get child
                if (curParentTag != null && curParentTag.length() > 0) {
                    //get recordType
                    int idx = curParentTag.indexOf("_");
                    int recordType;
                    if (idx >= 0) {
                        recordType = Integer.parseInt(curParentTag.substring(0, idx), 16);
                    } else {
                        recordType = curTag.intValue();
                    }

                    Map tagMap = recordMap.get(recordType).getTagMap();

                    temp[0] = mBuffer[curIndex];
                    temp[1] = mBuffer[curIndex + 1];
                    temp[2] = mBuffer[curIndex + 2];

                    tagTypeInt = ByteArrayUtil.byteToUnsignedByte(temp[0]);
                    if (tagMap.containsKey(curParentTag + "_" + Integer.toHexString((int) tagTypeInt).toUpperCase())) {
                        //doNothing
                    } else {
                        tagTypeInt = Integer.parseInt(ASNUtil.formatInteger(temp, 0, 2));

                        if (tagMap.containsKey(curParentTag + "_"
                                + Integer.toHexString((int) tagTypeInt).toUpperCase())) {
                            curIndex += 1;
                        } else {
                            tagTypeInt = Integer.parseInt(ASNUtil.formatInteger(temp, 0, 3));
                            if (tagMap.containsKey(curParentTag + "_"
                                    + Integer.toHexString((int) tagTypeInt).toUpperCase())) {
                                curIndex += 2;
                            } else {
//                                tagTypeInt = Integer.parseInt(ASNUtil.formatInteger(temp, 0, 2));
                                System.out.println("Encounter new tags! RecordType = " + recordType +" Vi tri: " + curIndex + ". Check bytes:" + ASNUtil.getStrByte(temp[0]) + ASNUtil.getStrByte(temp[1]) + ASNUtil.getStrByte(temp[2]));
                                //if
                                return null;
                            }
                        }
                    }
                    lObj = getLength(curIndex + 1);
                    lObj.setTagTypeExtend(temp);
                    lObj.setTagType(tagTitle);
                    lObj.setTagTypeInt((int) tagTypeInt);
                }
            }
        } catch (Exception ex) {
            System.out.print("recordType ----" + curParentTag);
            ex.printStackTrace();
            throw ex;
        }
        return lObj;
    }

    /**
     * load all params for converting.
     * @throws Exception getWriteMap throws an exception
     *
     * .*/
    public void loadConvertParam() throws Exception {
        String columnName;
        String columnValue;
        String recordStr;
        String[] recordType;
        EricssonRecord ericssonRecord;
        //ParamFileUtil paramFile = new ParamFileUtil(FILE_PROPERTIES);
        //get property
        separate = paramFile.getString("separate");
        dateFormat = paramFile.getString("date_format");
        columnSize = Integer.parseInt(paramFile.getString("column_count"));

        writeSize = Integer.parseInt(paramFile.getString("output_size"));

        String loadRStr = paramFile.getString("GetRecordType");
        if (loadRStr != null && loadRStr.length() > 0) {
            String[] recs = loadRStr.trim().split(",");
            if (recs.length > 0) {
                for (int i = 0; i < recs.length; i++) {
                    getRecord.put(recs[i].toUpperCase(), recs[i]);
                }
            }
        }

        recordStr = paramFile.getString("RecordType");
        recordType = recordStr.split(",");


        String[] tempArr, tempArr1;
        for (String str : recordType) {
            Map<String, Integer> indexMap = new HashMap();
            Map<String, String> typeMap = new HashMap();
            Map<String, Integer> tagMap = new HashMap();

            ericssonRecord = new EricssonRecord();
            String temp = paramFile.getString("Record_" + str + "_tag");

            //split tag
            tempArr = new String[1];
            tempArr = temp.split(",");

            for (String string : tempArr) {
                if (string != null && !string.trim().equals("")) {
                    //int tagvalue = Integer.parseInt(string.trim(), 16);
                    tagMap.put(str + "_" + string.trim(), Integer.parseInt(string, 16));

                    //check and put trigger
                    String string1 = paramFile.getString("Record_" + str + "_" + string + "_tag");
                    if (string1 != null) {
                        tempArr1 = string1.split(",");
                        for (String string2 : tempArr1) {
                            tagMap.put(str + "_" + string + "_" + string2, Integer.parseInt(string2, 16));
                        }
                    }
                }
            }

            if (getRecord.containsKey(str)) {
                for (int i = 1; i <= columnSize; i++) {
                    columnName = "Record_" + str + "_column" + i;
                    columnValue = paramFile.getString(columnName);
                    if (columnValue != null && !columnValue.equals("")) {
                        try {
                            String[] splitArr = columnValue.split(",");
                            indexMap.put(splitArr[0].trim(), i);
                            typeMap.put(splitArr[0].trim(), splitArr[1]);
                        } catch (Exception e) {
                            continue;
                        }
                    }

                }
            }
            ericssonRecord.setIndex(indexMap);
            ericssonRecord.setTagMap(tagMap);
            ericssonRecord.setTypeMap(typeMap);

            recordMap.put(Integer.parseInt(str, 16), ericssonRecord);
        }

        writeTypes = paramFile.getWriteType2();
        writeItems = paramFile.getWriteMap2();

    }

    /**
     * xu ly lay du lieu cua moi block
     * @param obj block need to be processed.
     * @param parentType parents tag
     * .*/
    public void processForValue(TLVLengthType obj, String parentType) throws Exception {

        int idx = parentType.indexOf("_");
        //String[] arr = parentType.split("_");
        int recordType;
        String child;
        if (idx >= 0) {
            recordType = Integer.parseInt(parentType.substring(0, idx), 16);
            child = parentType.substring(idx + 1) + "_" + Integer.toHexString(obj.getTagTypeInt()).toUpperCase();
        } else {
            recordType = Integer.parseInt(parentType, 16);
            child = Integer.toHexString(obj.getTagTypeInt()).toUpperCase();
        }
        Map<String, Integer> indexMap = recordMap.get(recordType).getIndex();
        Map<String, String> typeMap = recordMap.get(recordType).getTypeMap();
        if (indexMap.containsKey(child)) {
            if ((typeMap.get(child).toString()).equals("Integer")) {
                columArr[(int) indexMap.get(child) - 1] =
                        ASNUtil.formatInteger(mBuffer, curIndex + 1, (int) obj.getLength());
//                System.out.print(ASNUtil.formatInteger(mBuffer, mintIndex + 1, (int) obj.getLength()));
            }

            if ((typeMap.get(child).toString()).equals("Date")) {
                if (columArr[(int) indexMap.get(child) - 1] == null) {
//                    ColumArr[(int) indexMap.get(child) - 1] =
//                            ASNUtil.formatDate1(mBuffer, curIndex + 1, (int) obj.getLength());
                    columArr[(int) indexMap.get(child) - 1] =
                            ASNUtil.getBCDDate(mBuffer, curIndex + 1, (int) obj.getLength()).toString();
//                System.out.print(ASNUtil.formatTime(mBuffer, mintIndex + 1, (int) obj.getLength()));
                }
            }
            if ((typeMap.get(child).toString()).equals("Duration")) {
                columArr[(int) indexMap.get(child) - 1] =
                        ASNUtil.formatChargeDuration(mBuffer, curIndex + 1, (int) obj.getLength());
            }
            if ((typeMap.get(child).toString()).equals("Time")) {
//                ColumArr[(int) indexMap.get(child) - 1] =
//                        ASNUtil.formatTime1(mBuffer, curIndex + 1, (int) obj.getLength());
                columArr[(int) indexMap.get(child) - 1] =
                        ASNUtil.getBCDTime(mBuffer, curIndex + 1, (int) obj.getLength()).toString();
//                System.out.print(ASNUtil.formatTime(mBuffer, mintIndex + 1, (int) obj.getLength()));
            }

            if ((typeMap.get(child).toString()).equals("IA5")) {
                byte[] dst = new byte[(int) obj.getLength()];
                System.arraycopy(mBuffer, curIndex + 1, dst, 0, (int) obj.getLength());
                ASN1IA5String decode = new ASN1IA5String();
                columArr[(int) indexMap.get(child) - 1] = decode.convert(dst).trim();
            }

            if ((typeMap.get(child).toString()).equals("Address")) {
                int ntpi = mBuffer[mintIndex + 1];
                byte ton = 0;
                ton = (byte) ((ntpi & 0x70) >> 7); // lay 3 bit 765
                columArr[(int) indexMap.get(child) - 1] =
                        ton + ASNUtil.formatAddressString(mBuffer, curIndex + 1, (int) obj.getLength());
                String strTest = columArr[(int) indexMap.get(child) - 1];
            }

            if ((typeMap.get(child).toString()).equals("ENUMERATED")) {
                byte[] dst = new byte[(int) obj.getLength()];
                System.arraycopy(mBuffer, curIndex + 1, dst, 0, (int) obj.getLength());
                ASN1Integer decode = new ASN1Integer(dst);
                columArr[(int) indexMap.get(child) - 1] = decode.getValue().toString();

            }


            if ((typeMap.get(child).toString()).equals("vi")) {
                CdrChars chars = new CdrChars((int) obj.getLength() - 1, separate);
                columArr[(int) indexMap.get(child) - 1] =
                        chars.unpackToString(mBuffer, curIndex + 2);

            }
            if ((typeMap.get(child).toString()).equals("Octet")) {
                columArr[(int) indexMap.get(child) - 1] =
                        ASNUtil.formatTBCD(mBuffer, curIndex + 1, (int) obj.getLength());
            }
            if ((typeMap.get(child).toString()).equals("BearService")) {
                columArr[(int) indexMap.get(child) - 1] =
                        formatBearService(mBuffer, curIndex + 1, (int) obj.getLength());
            }
            if ((typeMap.get(child).toString()).equals("Location")) {
                columArr[(int) indexMap.get(child) - 1] =
                        formatLocation(mBuffer, curIndex + 1, (int) obj.getLength());
//                BaseCdrField chars = new CdrHuaweiLocation((int) obj.getLength(), separate);
//                ColumArr[(int) indexMap.get(child) - 1] =
//                        chars.unpack(mBuffer, curIndex + 1).toString();

            }
            if ((typeMap.get(child)).equals("CauseForTerminate")) {
                columArr[(int) indexMap.get(child) - 1]
                        = ASNUtil.formatBCD(this.mBuffer, this.curIndex.intValue() + 1, (int) obj.getLength());
            }
            if ((typeMap.get(child).toString()).equals("ByteText")) {
                columArr[(int) indexMap.get(child) - 1]
                        = ASNUtil.getTextByte(mBuffer, curIndex + 1, (int) obj.getLength());
            }
            if ((typeMap.get(child).toString()).equals("EricSuppCode")) {
                columArr[(int) indexMap.get(child) - 1]
                        = ASNUtil.formatEricSuppCode(mBuffer, curIndex + 1, (int) obj.getLength());
//                System.out.print(ASNUtil.formatInteger(mBuffer, mintIndex + 1, (int) obj.getLength()));
            }
        }

    }
    /*
     * Lay length cua bloc
     */

    public static String formatBearService(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        StringBuffer value = new StringBuffer();
        for (int i = iOffset; i < iLastOffset; i++) {
            byte h = (byte) ((btValue[i] & 0xf0) >>> 4);
            if (h < 10) {
                h = (byte) (48 + h);
            } else {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) (btValue[i] & 0xf);
            if (l < 10) {
                l = (byte) (48 + l);
            } else {
                l = (byte) ((65 + l) - 10);
            }
            value.append((char) h);
            value.append((char) l);
        }
        String vReturn = value.toString();
        return vReturn;
    }

    public static String formatLocation(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        StringBuffer value = new StringBuffer();
        for (int i = iOffset; i < iLastOffset; i++) {
            byte h = (byte) ((btValue[i] & 0xf0) >>> 4);
            if (h < 10) {
                h = (byte) (48 + h);
            } else {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) (btValue[i] & 0xf);
            if (l < 10) {
                l = (byte) (48 + l);
            } else {
                l = (byte) ((65 + l) - 10);
            }
            value.append((char) l);
            value.append((char) h);
        }
        String vReturn = value.toString();
        vReturn = vReturn.substring(0, 3) + vReturn.substring(4, vReturn.length());
        return vReturn;
    }

    private TLVLengthType getLength(int curIndex) throws Exception {

        TLVLengthType tlvlenght = new TLVLengthType();
        CdrBits cbit = new CdrBits(1, errorFileName, 7);
        tlvlenght.setType((Integer.parseInt(cbit.unpack(new byte[]{mBuffer[curIndex]}, 0).toString())));

        cbit.setBitPos(0);
        cbit.setLength(7);
        tlvlenght.setLengbits((Integer.parseInt(cbit.unpack(new byte[]{mBuffer[curIndex]}, 0).toString())));

        //bit so 8 bang 0
        if (tlvlenght.getType() == 0) {
            tlvlenght.setLength(tlvlenght.getLengbits());
            //da doc toi bit
            this.curIndex++;
        } else {
            //bit so 8 bang 1
            tlvlenght.setLength(getIntegerValue(mBuffer, tlvlenght.getLengbits(), curIndex + 1));
            //da doc toi bit
            this.curIndex += tlvlenght.getLengbits() + 1;
        }

        return tlvlenght;
    }

    private long getIntegerValue(byte[] bytes, int lenght, int offset) throws Exception {
        long result = 0;
        CdrNumeric cdrNumber = new CdrNumeric(lenght, "Doi sang so", false);
        result = (Long) cdrNumber.unpack(bytes, offset);
        return result;
    }

    /*
     * Load file vao mang byte
     *
     */
    public void loadToBuffer(String strFilePath)
            throws Exception {
        RandomAccessFile fileCDR;
        Exception exception;
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
//            System.out.println(e.getMessage());
            throw new Exception("Error when load file to buffer: " + e.getMessage());
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            throw new Exception("Error when load file to buffer: " + e.getMessage());
        } finally {
            mintIndex = 0;
        }
        try {
            fileCDR.close();
        } catch (Exception e) {
        }

    }

    private void pushEnv() {
//        private String curParentTag;
//        private Integer curTag;
//        private Integer beginIndex;
//        private Integer endIndex;
//        private Integer curIndex;
//        private Integer level;
        envStack.push(level);
//        envStack.push(curIndex);
        envStack.push(endIndex);
        envStack.push(beginIndex);
        envStack.push(curTag);
        envStack.push(curParentTag);
    }

    private void popEnv() {
        curParentTag = (String) envStack.pop();
        curTag = (Integer) envStack.pop();
        beginIndex = (Integer) envStack.pop();
        endIndex = (Integer) envStack.pop();
//        curIndex = (Integer) envStack.pop();
        level = (Integer) envStack.pop();
    }

    public static final Map<String, String> mapDropBssap;//erricsson
    static{
        mapDropBssap = new HashMap<String, String>();
        mapDropBssap.put("7A", "1");
        mapDropBssap.put("76", "1");
        mapDropBssap.put("6C", "1");
        mapDropBssap.put("68", "1");
        mapDropBssap.put("64", "1");
        mapDropBssap.put("59", "1");
        mapDropBssap.put("20", "1");
        mapDropBssap.put("09", "1");
        mapDropBssap.put("00", "1");
    };

    public static final Map<String, String> mapDropCauseCodeDiag;//Huawei + Erricsson
    static{
        mapDropCauseCodeDiag = new HashMap<String, String>();
        mapDropCauseCodeDiag.put("0210;0EA3", "1");
        mapDropCauseCodeDiag.put("020D;09C3", "1");
    };
}
