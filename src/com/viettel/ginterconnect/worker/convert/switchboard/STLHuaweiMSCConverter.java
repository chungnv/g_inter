/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.convert.switchboard;

import com.viettel.ginterconnect.worker.convert.BaseConverter;
import com.viettel.ginterconnect.worker.convert.ConvertOutput;
import com.viettel.ginterconnect.util.ASNUtil;
import com.viettel.ginterconnect.worker.util.ByteArrayUtil;
import com.viettel.ginterconnect.worker.util.convert.stl.WriteItem;
import com.viettel.ginterconnect.worker.util.convert.stl.WriteItemField;
import com.viettel.ginterconnect.worker.util.convert.stl.WriteItemFixField;
import com.viettel.ginterconnect.worker.util.convert.stl.WriteItemFunction;
import com.viettel.ginterconnect.worker.util.convert.stl.WriteItemMap;
import com.viettel.ginterconnect.worker.util.convert.huawei.HuaweiRecord;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import codec.asn1.ASN1IA5String;
import codec.asn1.ASN1Integer;
import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.worker.util.convert.stl.ParamFileUtil;
import com.viettel.ginterconnect.worker.util.convert.vtc.BaseCdrField;
import com.viettel.ginterconnect.worker.util.convert.vtc.CdrBits;
import com.viettel.ginterconnect.worker.util.convert.vtc.CdrChars;
import com.viettel.ginterconnect.worker.util.convert.vtc.CdrHuaweiLastLocation;
import com.viettel.ginterconnect.worker.util.convert.vtc.CdrHuaweiLocation;
import com.viettel.ginterconnect.worker.util.convert.vtc.CdrNumeric;
import com.viettel.ginterconnect.worker.util.convert.vtc.TLVLengthType;

/**
 *
 * @author kienpv
 * File convert cho HuaweiMSC va huawei GMSC
 */
public class STLHuaweiMSCConverter extends BaseConverter {

    //public static final String FILE_PROPERTIES = "D:\\project\\HTDS\\SOURCE\\ProcessUtil\\src\\Huawei_MSC_Config.properties";
    private byte mBuffer[];
    private int mintIndex = -1;
    private int columnSize;
    private int writeSize;
    private String dateFormat;
    private String[] ColumArr;
    private String[] writeArr;
    private Map<Integer, HuaweiRecord> recordMap = new HashMap<Integer, HuaweiRecord>();
    private WriteItem WriteItems[];
    private int writeTypes[];
    private Map<String, String> getRecord = new HashMap<String, String>();
    ParamFileUtil paramFileLocal = null;

    @Override
    public ConvertLog convertDetail() throws Exception {
        int intTemp;
        logger.info(" Starting for convert file : " + inputFileName);
        paramFileLocal = new ParamFileUtil(configFile);
        ConvertOutput textFile = new ConvertOutput(outputFileName);

        //add by chungdq for get fileIDPostion to replace fileID
        String filePosStr = paramFileLocal.getString("fileID_position");
        if (filePosStr != null) {
            try {
                fileIdPosition = Integer.parseInt(filePosStr.trim());
            } catch (Exception e) {
                if (logger != null) {
                    logger.warn("Can\'t find fileID Position config:" + e.getMessage());
                }
            }
        }

        //fileID
        textFile.setFileIdPosition(fileIdPosition);
        textFile.setFileId(super.getFileId());

        try {
            //Load file CDR
            String filePath = inputFileName;
            loadToBuffer(filePath);
            loadConvertParam();
            //Pass CDR info
            TLVLengthType tlvCDR = TLVBlocProcess(-1, 0, false);
            //pass by header
            TLVLengthType tlvHeader = TLVBlocProcess(this.mintIndex, 0, false);
            this.mintIndex = this.mintIndex + (int) tlvHeader.getLength();
            //pass by SEQUENCE PART
            TLVLengthType tlvSeq = TLVBlocProcess(this.mintIndex, 0, false);
//            System.out.println("Sequence Info ---" + this.mintIndex);
            //read Record
            TLVLengthType tlvRecord;
            TLVLengthType tlvColumn;
            int endIndexColumn = 0;
            while (this.mintIndex < mBuffer.length - 1) {
                //bloc for one record
                tlvRecord = TLVBlocProcess(this.mintIndex, 0, false);
//                if (tlvRecord != null) {
                endIndexColumn = this.mintIndex + (int) tlvRecord.getLength();
//                }
                writeArr = new String[writeSize];
                ColumArr = new String[columnSize];

                //process for recordType
                //danh dau vi tri
                intTemp = this.mintIndex;
                //doc qua lay typeRecord
                //System.out.println(this.mintIndex);
                tlvColumn = TLVBlocProcess(this.mintIndex, 0, false);

                if (tlvColumn == null) {
                    break;
                }
                int recordType = 0;
                recordType = (int) getIntegerValue(mBuffer, (int) tlvColumn.getLength(), mintIndex + 1);

                //for direction 
                if (getRecord.containsKey("" + recordType)) {
                    mintIndex = intTemp;
//                    System.out.println("Record Info ---" + this.mintIndex + "lenght---" + tlvRecord.getLength() + "LengthBis " + tlvRecord.getLengbits() + "Type ad " + tlvRecord.getType() + "Rtype = " + recordType);
                    do {
                        tlvColumn = TLVBlocProcess(this.mintIndex, recordType, true);
                        if (tlvColumn != null) {
                            processForValue(tlvColumn, recordType);
                            this.mintIndex = this.mintIndex + (int) tlvColumn.getLength();
                        } else {
                            this.mintIndex = endIndexColumn;
                        }

                    } while (this.mintIndex < endIndexColumn && endIndexColumn < mBuffer.length);

                    for (int i = 1; i <= writeSize; i++) {
                        if (WriteItems[i - 1].getType() == WriteItem.FIELD_TYPE_FIX) {
                            writeArr[i - 1] = paramFileLocal.getString("Record_" + recordType + "_column" + (((WriteItemFixField) WriteItems[i - 1]).getFieldId()));
                        } else if (WriteItems[i - 1].getType() == WriteItem.FIELD_TYPE_FIELD) {
                            writeArr[i - 1] = ColumArr[((WriteItemField) WriteItems[i - 1]).getFieldId() - 1];
                        } else if (WriteItems[i - 1].getType() == WriteItem.FIELD_TYPE_CONSTANT) {
                            writeArr[i - 1] = WriteItems[i - 1].getValue().toString();
                        } else if (WriteItems[i - 1].getType() == WriteItem.FIELD_TYPE_MAP) {
                            ((WriteItemMap) WriteItems[i - 1]).caculate(ColumArr[((WriteItemMap) WriteItems[i - 1]).getFieldId() - 1]);
                            writeArr[i - 1] = ((WriteItemMap) WriteItems[i - 1]).getValue().toString();
                        } else if (WriteItems[i - 1].getType() == WriteItem.FIELD_TYPE_FUNCTION) {
                            WriteItemFunction f = (WriteItemFunction) WriteItems[i - 1];
                            WriteItem listArg[] = f.getColumns();
                            Object args[] = new Object[f.getDynamicCount()];
                            int dynamicCount = 0;
                            int argCount = 0;
                            while (dynamicCount < args.length && argCount < listArg.length) {
                                if (listArg[argCount].getType() == WriteItem.FIELD_TYPE_FIELD) {
                                    args[dynamicCount] = ColumArr[((WriteItemField) listArg[argCount]).getFieldId() - 1];
                                    dynamicCount++;
                                } else if (listArg[argCount].getType() == WriteItem.FIELD_TYPE_FIX) {
                                    args[dynamicCount] = ColumArr[((WriteItemFixField) listArg[argCount]).getFieldId() - 1];
                                    dynamicCount++;
                                } else if (listArg[argCount].getType() == WriteItem.FIELD_TYPE_MAP) {
                                    ((WriteItemMap) listArg[argCount]).caculate(ColumArr[((WriteItemMap) listArg[argCount]).getFieldId() - 1]);
                                    args[dynamicCount] = ((WriteItemMap) listArg[argCount]).getValue().toString();
                                    dynamicCount++;
                                }
                                argCount++;
                            }
                            f.caculate(args);
                            if (f.getValue() != null) {
                                writeArr[i - 1] = f.getValue().toString();
                            } else {
                                writeArr[i - 1] = "";
                            }
                        } else {
                            writeArr[i - 1] = "";
                        }

                    }
                    totalConverted++;
                    //check record to write
                    String causeCode = writeArr[24];
                    String diaglogic = writeArr[25];
                    //comment 07/01/17 for write all records
//                    if (mapDropCauseCode.containsKey(causeCode + ";" + diaglogic)) {
//                        textFile.addRecord(writeArr);
//                    }
                    textFile.addRecord(writeArr);
                } else {
                    this.mintIndex = endIndexColumn;
                }
            }
            logger.info("Total record: " + totalConverted);
//            logger.info("End for convert file : " + inputFileName);

//            //flush all function cache
//            BaseFunction.FlushFuctionCache();

        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Total record: " + totalConverted);
            logger.info("End for convert file : " + inputFileName);
            textFile.close();
        } finally {
            textFile.close();
        }
        return new ConvertLog();
    }
    /*
     * Xu ly cho tung boc 
     * voi chi so tag = index
     * Ket qua tra ve kieu object chua TYPE, LENGTH va VALUE
     * 
     */

    public TLVLengthType TLVBlocProcess(int mintIndex1, int recordType, boolean hasRecordType) throws Exception {

        try {

            byte[] temp = new byte[3];
            long tagTypeInt = 0;
            int headerType = 0;
            CdrBits cbit = null;
            if (mintIndex1 > 0) {
                this.mintIndex = mintIndex1 + 1;
            }
            byte tagTitle;
            if (this.mintIndex >= mBuffer.length) {
                return null;
            }
            tagTitle = mBuffer[this.mintIndex];
            if (hasRecordType == false) {
                // vietthem
                cbit = new CdrBits(5, errorFileName, 0);
                headerType = Integer.parseInt(cbit.unpack(new byte[]{
                            mBuffer[this.mintIndex]
                        }, 0).toString());

                if (headerType < 31) {
                    tagTypeInt = ByteArrayUtil.byteToUnsignedByte(tagTitle);
                } else {
                    this.mintIndex += 1;
                    if (this.mintIndex >= mBuffer.length) {
                        return null;
                    }
                    cbit.setBitPos(7);
                    cbit.setLength(1);
                    headerType = Integer.parseInt(cbit.unpack(new byte[]{
                                mBuffer[this.mintIndex]
                            }, 0).toString());

                    if (headerType == 0) {
                        tagTypeInt = ByteArrayUtil.byteToUnsignedByte(mBuffer[this.mintIndex]) + ByteArrayUtil.byteToUnsignedByte(mBuffer[this.mintIndex - 1]);
                    } else {
                        cbit.setBitPos(0);
                        cbit.setLength(7);
                        headerType = Integer.parseInt(cbit.unpack(new byte[]{
                                    mBuffer[this.mintIndex]
                                }, 0).toString());
                        this.mintIndex += headerType;
                    }
                }

            } else {

                if (recordMap.get(recordType) != null) {

                    Map tagMap = recordMap.get(recordType).getTagMap();

                    temp[0] = mBuffer[this.mintIndex];
                    temp[1] = mBuffer[this.mintIndex + 1];
                    temp[2] = mBuffer[this.mintIndex + 2];

                    tagTypeInt = ByteArrayUtil.byteToUnsignedByte(temp[0]);
                    if (tagMap.containsKey((int) tagTypeInt)) {
                        //doNothing
                    } else {

                        tagTypeInt = Integer.parseInt(ASNUtil.formatInteger(temp, 0, 2));

                        if (tagMap.containsKey((int) tagTypeInt)) {
                            this.mintIndex += 1;
                        } else {
                            tagTypeInt = Integer.parseInt(ASNUtil.formatInteger(temp, 0, 3));
                            if (tagMap.containsKey((int) tagTypeInt)) {
                                this.mintIndex += 2;
                            } else {
                                //tag moi
                                System.out.println("Encounter new tags! RecordType = " + recordType +" Vi tri: " + mintIndex + ". Check bytes:" + ASNUtil.getStrByte(temp[0]) + ASNUtil.getStrByte(temp[1]) + ASNUtil.getStrByte(temp[2]));
                                return null;
                            }
                        }
                    }
                } else {
                    return null;
                }

            }
//        System.out.print("---" + tagTypeInt);
            TLVLengthType lObj = getLength(this.mintIndex + 1);
            lObj.setTagTypeExtend(temp);
            lObj.setTagType(tagTitle);
            lObj.setTagTypeInt((int) tagTypeInt);
            return lObj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /*
     * 
     * Lay gia tri cua bloc
     * 
     * 
     */
    public void getBlocValue(byte[] abc, int minIndex, int length) {

        //System.out.println(ASNUtil.formatAddressString(abc, minIndex, length));
    }
    /*
     * 
     * load all param for convert 
     * 
     */

    public void loadConvertParam() throws Exception {
        String columnName;
        String columnValue;
        String recordStr;
        String[] recordType;
        HuaweiRecord huaweiRecord;

        //get column type


        //ParamFileUtil paramFileLocal = new ParamFileUtil(FILE_PROPERTIES);
        //get property
        separate = paramFileLocal.getString("separate");
        dateFormat = paramFileLocal.getString("date_format");
        columnSize = Integer.parseInt(paramFileLocal.getString("column_count"));
        writeSize = Integer.parseInt(paramFileLocal.getString("output_size"));
        String loadRStr = paramFileLocal.getString("GetRecordType");
        if (loadRStr != null && loadRStr.length() > 0) {
            String[] recs = loadRStr.trim().split(",");
            if (recs.length > 0) {
                for (int i = 0; i < recs.length; i++) {
                    getRecord.put(recs[i], recs[i]);
                }
            }
        }

        recordStr = paramFileLocal.getString("RecordType");
        recordType = recordStr.split(",");


        String[] tempArr;
        for (String str : recordType) {

            if (getRecord.containsKey(str)) {
                Map<Integer, Integer> indexMap = new HashMap();
                Map<Integer, String> typeMap = new HashMap();
                Map<Integer, Integer> tagMap = new HashMap();

                huaweiRecord = new HuaweiRecord();
                String temp = paramFileLocal.getString("Record_" + str + "_tag");

                //split tag 
                tempArr = temp.split(",");

                for (String string : tempArr) {
                    int tagvalue = Integer.parseInt(string, 16);
                    tagMap.put(tagvalue, Integer.parseInt(str));
                }

                for (int i = 1; i <= columnSize; i++) {
                    columnName = "Record_" + str + "_column" + i;
                    columnValue = paramFileLocal.getString(columnName);

                    if (columnValue != null && !columnValue.equals("")) {
                        try {
                            String[] splitArr = columnValue.split(",");
                            indexMap.put(Integer.parseInt(splitArr[0], 16), i);
                            typeMap.put(Integer.parseInt(splitArr[0], 16), splitArr[1]);
                        } catch (Exception e) {
                            continue;
                        }
                    }

                }

                huaweiRecord.setIndex(indexMap);
                huaweiRecord.setTagMap(tagMap);
                huaweiRecord.setTypeMap(typeMap);

                recordMap.put(Integer.parseInt(str), huaweiRecord);
            }
        }

        writeTypes = paramFileLocal.getWriteType2();
        WriteItems = paramFileLocal.getWriteMap2();
    }
    /*
     * xu ly khi dua du lieu ra 
     * 
     */

    public void processForValue(TLVLengthType obj, int recordType) throws Exception {


        Map<Integer, Integer> indexMap = recordMap.get(recordType).getIndex();
        Map<Integer, String> typeMap = recordMap.get(recordType).getTypeMap();

        if (indexMap.containsKey(obj.getTagTypeInt())) {
            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("Integer")) {
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        ASNUtil.formatInteger(mBuffer, mintIndex + 1, (int) obj.getLength());
//                System.out.print(ASNUtil.formatInteger(mBuffer, mintIndex + 1, (int) obj.getLength()));
            }

            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("Date")) {
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        ASNUtil.formatHuaWeiTimeStamp(dateFormat, mBuffer, mintIndex + 1, (int) obj.getLength(), 0, this.getTimezone());
//                System.out.print(ASNUtil.formatTime(mBuffer, mintIndex + 1, (int) obj.getLength()));
            }

            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("IA5")) {

                byte[] dst = new byte[(int) obj.getLength()];
                System.arraycopy(mBuffer, mintIndex + 3, dst, 0, (int) obj.getLength() - 2);
                ASN1IA5String decode = new ASN1IA5String();
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] = decode.convert(dst).trim();
            }

            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("Address")) {
//                System.out.println(mintIndex);
                int napi = mBuffer[mintIndex + 1];
                byte nai = 0;
                nai = (byte) ((napi & 0x70) >> 4); // lay bit 765

                if (nai > 2) {
                    nai = 0; // if ton # unknown, international, national thi gan cho la unknown
                }
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        nai + ASNUtil.formatAddressString(mBuffer, mintIndex + 1, (int) obj.getLength());
            }

            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("Octet")) {
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        ASNUtil.formatTBCD(mBuffer, mintIndex + 1, (int) obj.getLength());
            }
            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("OctetEE")) {
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        ASNUtil.formatTBCD(mBuffer, mintIndex + 1, (int) obj.getLength());
            }
            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("BasicService")) {
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        formatBasicService(mBuffer, mintIndex + 1, (int) obj.getLength());
            }
            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("ENUMERATED")) {

                byte[] dst = new byte[(int) obj.getLength()];
                System.arraycopy(mBuffer, mintIndex + 1, dst, 0, (int) obj.getLength());
                ASN1Integer decode = new ASN1Integer(dst);
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] = decode.getValue().toString();

            }

            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("vi")) {
                CdrChars chars = new CdrChars((int) obj.getLength() - 1, separate);
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        chars.unpackToString(mBuffer, mintIndex + 2);

            }
            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("Location")) {
                BaseCdrField chars = new CdrHuaweiLocation((int) obj.getLength(), separate);
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        chars.unpack(mBuffer, mintIndex + 1).toString();
            }
            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("LastLocation")) {
                BaseCdrField chars = new CdrHuaweiLastLocation((int) obj.getLength(), separate);
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        chars.unpack(mBuffer, mintIndex + 1).toString();
            }
            if ((typeMap.get(obj.getTagTypeInt()).toString()).equals("CauseForTerminate")) {
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        ASNUtil.formatBCD(mBuffer, mintIndex + 1, (int) obj.getLength());
            }
            if ((typeMap.get(obj.getTagTypeInt()).toString()).contains("Sequence")) {
                String seqType = typeMap.get(obj.getTagTypeInt()).toString();
                int seqTypeInt = Integer.parseInt(seqType.split("_")[1]);
                String pattern = seqType.split("_")[2];
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        ASNUtil.formatSequence(dateFormat, mBuffer, mintIndex + 1, (int) obj.getLength(), seqTypeInt, pattern, this.getTimezone());
            }
            if ((typeMap.get(obj.getTagTypeInt()).toString()).contains("String")) {
                ColumArr[(int) indexMap.get(obj.getTagTypeInt()) - 1] =
                        ASNUtil.formatByteToString(mBuffer, mintIndex + 1, (int) obj.getLength());
            }
        }

    }
    /*
     * 
     * Lay length cua bloc
     * 
     */

    public static String formatBasicService(byte btValue[], int iOffset, int iLength) {
        int iLastOffset = iOffset + iLength;
        if (btValue.length < iLastOffset || iLength < 1) {
            return "";
        }
        StringBuilder value = new StringBuilder();
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
        return value.toString().substring(3, value.length());
    }

    private TLVLengthType getLength(int mintIndex) throws Exception {

        TLVLengthType tlvlenght = new TLVLengthType();
        CdrBits cbit = new CdrBits(1, errorFileName, 7);
        tlvlenght.setType((Integer.parseInt(cbit.unpack(new byte[]{
                    mBuffer[mintIndex]
                }, 0).toString())));

        cbit.setBitPos(0);
        cbit.setLength(7);
        tlvlenght.setLengbits((Integer.parseInt(cbit.unpack(new byte[]{
                    mBuffer[mintIndex]
                }, 0).toString())));

        //bit so 8 bang 0
        if (tlvlenght.getType() == 0) {
            tlvlenght.setLength(tlvlenght.getLengbits());
            //da doc toi bit
            this.mintIndex += 1;
        } else {
            //bit so 8 bang 1
            tlvlenght.setLength(getIntegerValue(mBuffer, tlvlenght.getLengbits(), mintIndex + 1));
            this.mintIndex += tlvlenght.getLengbits() + 1;
        }
        return tlvlenght;
    }

    private long getIntegerValue(byte[] bytes, int lenght, int offset) throws Exception {
        try {
            long result = 0;
            CdrNumeric cdrNumber = new CdrNumeric(lenght, "Doi sang so", false);
            result = (Long) cdrNumber.unpack(bytes, offset);
            return result;

        } catch (Exception e) {
            //System.out.println(offset);
//            throw e;
            //Hien tai tra ve 0 de convert thanh cong
            return 0;
        }

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
            e.printStackTrace();
        }
    }

    public static final Map<String, String> mapDropCauseCode;//Huawei + Erricsson
    static{
        mapDropCauseCode = new HashMap<String, String>();
        mapDropCauseCode.put("00;8002009B", "1");
        mapDropCauseCode.put("00;800200AC", "1");
    };

}
