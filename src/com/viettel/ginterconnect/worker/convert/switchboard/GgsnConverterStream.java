package com.viettel.ginterconnect.worker.convert.switchboard;

import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.process.exception.ConvertException;
import com.viettel.ginterconnect.worker.convert.BaseConverter;
import com.viettel.ginterconnect.worker.convert.ConvertOutput;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.worker.util.convert.ggsn.AsnFormatFactory;
import com.viettel.ginterconnect.worker.util.convert.ggsn.BasicFormat;
import com.viettel.ginterconnect.worker.util.convert.ggsn.BasicValue;
import com.viettel.ginterconnect.worker.util.convert.ggsn.ChoiceValue;
import com.viettel.ginterconnect.worker.util.convert.ggsn.Field;
import com.viettel.ginterconnect.worker.util.convert.ggsn.PureValue;
import com.viettel.ginterconnect.worker.util.convert.ggsn.Record;
import com.viettel.ginterconnect.worker.util.convert.ggsn.SequenceValue;
import com.viettel.ginterconnect.worker.util.convert.ggsn.SetValue;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

//BaseConverter, TextOutputFile
public class GgsnConverterStream extends BaseConverter {

//    private String separate;
    private byte mBuffer[];
    private int minIndex;
    private int curIndex;
    private int columnCount;
    private List listSeqItemNames;
    private List listSeqNames;
    private List listMapSeqNames;
    private List listNames;
    private ConvertOutput textFile;
    private String seqIteratorItem;
    private Map mapRecordType;
    private Map allRecordType;
    private Map getRecord;
    private int titleLength;
    private FileInputStream fileStream = null;
    private int readLength = 10000;
    private boolean firstRead = true;
    private long currentIndexCdr = 0;
    private boolean isExit = false;
//    private int maxLength = 0;

    public GgsnConverterStream() {
        columnCount = 35;
        listSeqItemNames = new ArrayList();
        listSeqNames = new ArrayList();
        listMapSeqNames = new ArrayList();
        listNames = new ArrayList();
        textFile = null;
        seqIteratorItem = "Item";
        mapRecordType = new HashMap();
        allRecordType = new HashMap();
        getRecord = new HashMap();
    }

    public ConvertLog convertDetail()
            throws Exception {
        try {
            loadConvertParam();
            textFile = new ConvertOutput(outputFileName);
            textFile.setFileIdPosition(fileIdPosition);
            textFile.setFileId(super.getFileId());
            loadToBuffer(inputFileName);
            curIndex = minIndex;
            Field curField = null; //field dang duyet
            Record curRecord = null; //ban ghi dang duyet
            BasicValue curValue = null; //value cua Tag dang duyet:name,length,tag,parents fields, children fields
            do {
                if (isExit) {
                    if (fileStream != null) {
                        fileStream.close();
                        fileStream = null;
//                        System.out.println("Max Length = " + maxLength);
                    }
                    break;
                }
                boolean next = false;
                if (curField == null) {
                    curRecord = new Record();
                    curRecord.setMapValues(new HashMap());
                    Field newField = getTag(curField);
                    if (newField == null) {
                        System.out.println((new StringBuilder()).append("newField is null ").append(curField).toString());
                        System.out.println(curIndex);
                    }
                    int length = getLength();
//                    if (length > maxLength)
//                        maxLength = length;
                    next = false;
                    if (getRecord.containsKey(newField.getFieldHEXCode())) {
                        BasicValue recordValue = new SetValue(); //Kieu gia tri ca ban ghi la kieu Set
                        recordValue.setField(newField);
                        recordValue.setIndexValue(curIndex);  //indexValue: vi tri Tag tiep theo
                        recordValue.setLength(length);
                        recordValue.setName(newField.getFieldName());
                        recordValue.setParentValue(null);
                        curRecord.getMapValues().put(newField.getFieldName(), recordValue);
                        curField = newField;
                        curValue = recordValue;
                        PureValue recordNameValue = new PureValue(); //gia tri RecordName: G-CDR
                        recordNameValue.setField(null);
                        recordNameValue.setName("RecordName");
                        recordNameValue.setParentValue(null);
                        recordNameValue.setValue((String) allRecordType.get(newField.getFieldHEXCode()));
                        curRecord.getMapValues().put("RecordName", recordNameValue);
                    } else {
                        curIndex += length;
                        currentIndexCdr += length;
                        next = true;
                    }
                } else if ((curIndex >= curValue.getIndexValue() + curValue.getLength())) {
//                        || ((curValue.getIndexValue() + curValue.getLength()) > readLength) && (curIndex >= (curValue.getIndexValue() + curValue.getLength() - readLength))) {
//                        || ((curIndex + readLength) == (curValue.getIndexValue() + curValue.getLength()))) {
                    curValue = curValue.getParentValue();
                    if (curValue != null) {
                        curField = curValue.getField();
                    } else {
                        curField = null;
                    }
                } else {
                    if (curValue.getListChilds() == null) {
                        curValue.setListChilds(new ArrayList());
                    }
                    Field newField = getTag(curField);

                    if (newField == null) {
                        System.out.println((new StringBuilder()).append("newField is null").append(curField).toString());
                        System.out.println(currentIndexCdr);
                    }
                    int length = getLength();
                    if (newField.getFieldType() == 0) {
                        PureValue value = new PureValue(); //value
                        value.setField(newField);
                        if (curValue.getField().getFieldType() == 2 && newField.getFieldName().equals(seqIteratorItem)) {
                            value.setName((new StringBuilder()).append(curValue.getName()).append(".[").append(curValue.getListChilds().size()).append("]").toString());
                        } else {
                            value.setName((new StringBuilder()).append(curValue.getName()).append(".").append(newField.getFieldName()).toString());
                        }
                        value.setIndexValue(curIndex);
                        value.setLength(length);
                        value.setParentValue(curValue);
                        BasicFormat format = AsnFormatFactory.getAsnFormat(newField.getFormat());
                        //System.out.println("curIndex: " + curIndex);
                        value.setValue(format.decode(mBuffer, curIndex, length));
                        curValue.getListChilds().add(value);
                        curRecord.getMapValues().put(value.getName(), value);
                        curIndex += length;
                        currentIndexCdr += length;
                    } else {
                        switch (newField.getFieldType()) {
                            case 2: // '\002' SEQUENCE
                                SequenceValue value = new SequenceValue();
                                value.setField(newField);
                                if (curValue.getField().getFieldType() == 2 && newField.getFieldName().equals(seqIteratorItem)) {
                                    value.setName((new StringBuilder()).append(curValue.getName()).append(".[").append(curValue.getListChilds().size()).append("]").toString());
                                } else {
                                    value.setName((new StringBuilder()).append(curValue.getName()).append(".").append(newField.getFieldName()).toString());
                                }
                                value.setIndexValue(curIndex);
                                value.setLength(length);
                                value.setParentValue(curValue);
                                curValue.getListChilds().add(value);
                                curRecord.getMapValues().put(value.getName(), value);
                                curValue = value;
                                curField = value.getField();
                                break;

                            case 1: // '\001'  SET
                                SetValue sValue = new SetValue();
                                sValue.setField(newField);
                                if (curValue.getField().getFieldType() == 2 && newField.getFieldName().equals(seqIteratorItem)) {
                                    sValue.setName((new StringBuilder()).append(curValue.getName()).append(".[").append(curValue.getListChilds().size()).append("]").toString());
                                } else {
                                    sValue.setName((new StringBuilder()).append(curValue.getName()).append(".").append(newField.getFieldName()).toString());
                                }
                                sValue.setIndexValue(curIndex);
                                sValue.setLength(length);
                                sValue.setParentValue(curValue);
                                curValue.getListChilds().add(sValue);
                                curRecord.getMapValues().put(sValue.getName(), sValue);
                                curValue = sValue;
                                curField = sValue.getField();
                                break;

                            case 3: // '\003'  CHOICE
                                ChoiceValue cValue = new ChoiceValue();
                                cValue.setField(newField);
                                if (curValue.getField().getFieldType() == 2 && newField.getFieldName().equals(seqIteratorItem)) {
                                    cValue.setName((new StringBuilder()).append(curValue.getName()).append(".[").append(curValue.getListChilds().size()).append("]").toString());
                                } else {
                                    cValue.setName((new StringBuilder()).append(curValue.getName()).append(".").append(newField.getFieldName()).toString());
                                }
                                cValue.setIndexValue(curIndex);
                                cValue.setLength(length);
                                cValue.setParentValue(curValue);
                                curValue.getListChilds().add(cValue);
                                curRecord.getMapValues().put(cValue.getName(), cValue);
                                curValue = cValue;
                                curField = cValue.getField();
                                break;

                            default:
                                throw new Exception("Invalid Field Type");
                        }
                    }
                }
                if ((curField != null || curRecord == null) && curIndex < mBuffer.length) {
                    continue;
                }

                curIndex += titleLength;
                currentIndexCdr += titleLength;
                int totalRecordWrite = 0; //tong so ban ghi duoc ghi
                String writes[] = null; // chua noi dung record de ghi ra
                label0:
                for (int i = 0; i < listSeqNames.size(); i++) {
                    Map seqNames = (Map) listSeqNames.get(i); //seqName: (0):j ung voi ServiceData. j = Record.listOfServiceData[j]
                    Map mapNames = (Map) listMapSeqNames.get(i); //(0)j: ung voi ServiceData. j = Record.listOfServiceData[j].xxx = Record.listOfServiceData[j].xxx
                    List varNames = (List) listSeqItemNames.get(i); //j: [j],chi so tuong ung.
                    int varValues[] = new int[varNames.size()]; //
                    for (int j = 0; j < varValues.length; j++) {
                        varValues[j] = -1;
                    }

                    int curValueIndex = 0;  //index
                    boolean isRecordCheckExit = false;
                    do {
                        do {
                            if (isRecordCheckExit) {
                                continue label0;
                            }
                            varValues[curValueIndex] = varValues[curValueIndex] + 1;
                            List varSeqNames = (List) seqNames.get(varNames.get(curValueIndex));
                            boolean isIndexExist = false;
                            Iterator i$ = varSeqNames.iterator();
                            do {
                                if (!i$.hasNext()) {
                                    break;
                                }
                                String seqName = (String) i$.next();
                                for (int j = 0; j <= curValueIndex; j++) {
                                    seqName = seqName.replace((new StringBuilder()).append("[").append((String) varNames.get(j)).append("]").toString(), (new StringBuilder()).append("[").append(varValues[j]).append("]").toString());
                                }

                                if (!curRecord.getMapValues().containsKey(seqName)) {
                                    continue;
                                }
                                isIndexExist = true;
                                break;
                            } while (true);
                            if (!isIndexExist) {
                                break;
                            }
                            if (curValueIndex == varValues.length - 1) {
                                writes = new String[listNames.size()];
                                for (int j = 0; j < listNames.size(); j++) {
                                    String name = (String) listNames.get(j);
                                    if (name.contains("PARAM:")) {
                                        writes[j] = name.substring(6);
                                    } else {
                                        if (mapNames.containsKey(name)) {
                                            for (int k = 0; k < varValues.length; k++) {
                                                name = name.replace((new StringBuilder()).append("[").append((String) varNames.get(k)).append("]").toString(), (new StringBuilder()).append("[").append(varValues[k]).append("]").toString());
                                            }
                                        }
                                        if (curRecord.getMapValues().get(name) != null && ((BasicValue) curRecord.getMapValues().get(name)).getValue() != null) {
                                            writes[j] = ((BasicValue) curRecord.getMapValues().get(name)).getValue();
                                        } else {
                                            writes[j] = "";
                                        }
                                    }
                                }

                                textFile.addRecord(writes);
                                totalRecordWrite++;
                                totalConverted++;
                                loadToBuffer(inputFileName);

                            } else {
                                curValueIndex++;
                            }
                        } while (true);
                        varValues[curValueIndex] = -1;
                    } while (--curValueIndex >= 0);
                    isRecordCheckExit = true;
                }

                if (totalRecordWrite == 0) {
                    writes = new String[listNames.size()];
                    for (int j = 0; j < listNames.size(); j++) {
                        String name = (String) listNames.get(j);
                        if (curRecord.getMapValues().get(name) != null && ((BasicValue) curRecord.getMapValues().get(name)).getValue() != null) {
                            writes[j] = ((BasicValue) curRecord.getMapValues().get(name)).getValue();
                        } else {
                            writes[j] = "";
                        }
                    }
                    if (!next) {
                        textFile.addRecord(writes);
                        totalConverted++;
                    }
                    loadToBuffer(inputFileName);
                    //load lai record
                }

            } while (true);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            textFile.close();
        }
        textFile.close();
        return new ConvertLog();
    }

    private Field getTag(Field curField)
            throws Exception {
        int iValue = 0;
        String tempTag[] = new String[3];
        if (curIndex < mBuffer.length - 1) {
            iValue |= mBuffer[curIndex] & 0xff;
            String temp = Integer.toHexString(iValue).toUpperCase();
            if (temp.length() == 1) {
                temp = (new StringBuilder()).append("0").append(temp).toString();
            }
            tempTag[0] = temp;
        } else {
            tempTag[0] = null;
        }
        if (curIndex + 1 < mBuffer.length - 1) {
            iValue = 0;
            iValue |= mBuffer[curIndex + 1] & 0xff;
            String temp = Integer.toHexString(iValue).toUpperCase();
            if (temp.length() == 1) {
                temp = (new StringBuilder()).append("0").append(temp).toString();
            }
            tempTag[1] = (new StringBuilder()).append(tempTag[0]).append(temp).toString();
        } else {
            tempTag[1] = null;
        }
        if (curIndex + 2 < mBuffer.length - 1) {
            iValue = 0;
            iValue |= mBuffer[curIndex + 2] & 0xff;
            String temp = Integer.toHexString(iValue).toUpperCase();
            if (temp.length() == 1) {
                temp = (new StringBuilder()).append("0").append(temp).toString();
            }
            tempTag[2] = (new StringBuilder()).append(tempTag[1]).append(temp).toString();
        } else {
            tempTag[2] = null;
        }
        if (curField == null) {
            String recordType = null;
            if (tempTag[0] != null && allRecordType.containsKey(tempTag[0])) {
                recordType = tempTag[0];
                curIndex++;
                currentIndexCdr++;
            } else if (tempTag[1] != null && allRecordType.containsKey(tempTag[1])) {
                recordType = tempTag[1];
                curIndex += 2;
                currentIndexCdr += 2;
            } else if (allRecordType.containsKey(tempTag[2])) {
                recordType = tempTag[2];
                curIndex += 3;
                currentIndexCdr += 3;
            } else {
                return null;
            }
            if (recordType != null && mapRecordType.containsKey(recordType)) {
                return (Field) mapRecordType.get(recordType);
            } else {
                return null;
            }
        }
        String tag = null;
        if (curField.getMapChilds() == null) {
            return null;
        }
        if (tempTag[0] != null && curField.getMapChilds().containsKey(tempTag[0])) {
            tag = tempTag[0];
            curIndex++;
            currentIndexCdr++;
        } else if (tempTag[1] != null && curField.getMapChilds().containsKey(tempTag[1])) {
            tag = tempTag[1];
            curIndex += 2;
            currentIndexCdr += 2;
        } else if (curField.getMapChilds().containsKey(tempTag[2])) {
            tag = tempTag[2];
            curIndex += 3;
            currentIndexCdr += 3;
        } else {
            System.out.println("Check bytes: " + "record: " + curField.getFieldHEXCode() + " tags: " + tempTag[0]);
            return null;
        }
        if (tag != null && curField.getMapChilds().containsKey(tag)) {
            return (Field) curField.getMapChilds().get(tag);
        } else {
            System.out.println("Check bytes: " + "record: " + curField.getFieldHEXCode() + " tags: " + tempTag[0]);
            return null;
        }
    }

    private int getLength() {
        byte temp = mBuffer[curIndex];
        int iValue = 0;
        iValue |= temp & 0x80;
        if (iValue == 0) {
            iValue = 0;
            iValue |= temp & 0x7f;
            curIndex++;
            currentIndexCdr++;
            return iValue;
        }
        iValue = 0;
        iValue |= temp & 0x7f;
        curIndex++;
        currentIndexCdr++;
        int length = 0;
        for (int i = 0; i < iValue; i++) {
            length <<= 8;
            length |= mBuffer[curIndex] & 0xff;
            curIndex++;
            currentIndexCdr++;
        }
        return length;
    }

    public void loadConvertParam()
            throws Exception {
        separate = paramFile.getString("separate");
        titleLength = Integer.parseInt(paramFile.getString("titleLength"));
        dateFormat = new SimpleDateFormat(paramFile.getString("date_format"));
        String strDateFormat = paramFile.getString("date_format");
        if (strDateFormat != null && strDateFormat.length() > 0) {
            Constants.GGSN_DEFAULT_DATE_FORMAT = strDateFormat;
        }
        if (paramFile.getString("SequenceIteratorItemName") != null) {
            seqIteratorItem = paramFile.getString("SequenceIteratorItemName").trim();
        }
        String loadRStr = paramFile.getString("RecordType");
        if (loadRStr != null && loadRStr.length() > 0) {
            String recs[] = loadRStr.trim().split(";");
            if (recs.length > 0) {
                for (int i = 0; i < recs.length; i++) {
                    allRecordType.put(recs[i].toUpperCase(), paramFile.getString((new StringBuilder()).append("Record_").append(recs[i].trim()).append("_Name").toString()));
                }

            }
        }
        loadRStr = paramFile.getString("GetRecordType");
        if (loadRStr != null && loadRStr.length() > 0) {
            String recs[] = loadRStr.trim().split(";");
            if (recs.length > 0) {
                for (int i = 0; i < recs.length; i++) {
                    getRecord.put(recs[i].toUpperCase(), recs[i]);
                }

            }
        }
        String record;
        Field field;
        for (Iterator ites = getRecord.keySet().iterator(); ites.hasNext(); mapRecordType.put(record, field)) {
            record = (String) ites.next();
            record = record.trim().toUpperCase();
            field = new Field();
            field.setFieldHEXCode(record);
            field.setParentField(null);
            String str = paramFile.getString((new StringBuilder()).append("Record_").append(record).append("_Type").toString());
            String arr[] = str.trim().split(";");
            field.setFieldName(arr[0].trim());
            if (arr[1].trim().equals("Set")) {
                field.setFieldType(1);
                continue;
            }
            if (arr[1].trim().equals("Seq")) {
                field.setFieldType(2);
                continue;
            }
            if (arr[1].trim().equals("Choice")) {
                field.setFieldType(3);
                continue;
            }
            if (arr[1].trim().equals("Value")) {
                field.setFieldType(0);
            } else {
                throw new Exception("Invalid field type");
            }
        }

        Stack stackRecordLoad = new Stack();
        Iterator iteRecord = mapRecordType.keySet().iterator();
        String recordHex = null;
        String fullRecordHex = null;
        for (; iteRecord.hasNext(); stackRecordLoad.push(recordHex)) {
            recordHex = (String) iteRecord.next();
            fullRecordHex = recordHex;
            stackRecordLoad.push(fullRecordHex);
        }

        do {
            if (stackRecordLoad.size() <= 0) {
                break;
            }
            recordHex = (String) stackRecordLoad.pop();
            fullRecordHex = (String) stackRecordLoad.pop();
            if (recordHex == null || fullRecordHex == null) {
                throw new Exception("Invalid configuration");
            }
            Field curField = (Field) mapRecordType.get(fullRecordHex);
            if (curField.getFieldType() != 0) {
                String str = paramFile.getString((new StringBuilder()).append("Record_").append(fullRecordHex).append("_Tag").toString());
                String arr[] = str.trim().split(";");
                if (arr != null && arr.length > 0) {
                    Map mapChilds = new HashMap();
                    for (int i = 0; i < arr.length; i++) {
                        String str1 = paramFile.getString((new StringBuilder()).append("Record_").append(fullRecordHex).append("_").append(arr[i].trim().toUpperCase()).append("_Type").toString());
                        String arr1[] = null;
                        try {
                            arr1 = str1.trim().split(";");
                        } catch (Exception ex) {
                            System.out.println((new StringBuilder()).append("Record_").append(fullRecordHex).append("_").append(arr[i].trim().toUpperCase()).append("_Type").toString());
                            throw ex;
                        }
                        field = new Field();
                        field.setFieldName(arr1[0].trim());
                        if (arr1[1].trim().equals("Set")) {
                            field.setFieldType(1);
                        } else if (arr1[1].trim().equals("Seq")) {
                            field.setFieldType(2);
                        } else if (arr1[1].trim().equals("Choice")) {
                            field.setFieldType(3);
                        } else if (arr1[1].trim().equals("Value")) {
                            field.setFieldType(0);
                            if (arr1[2] != null) {
                                field.setFormat(arr1[2].trim());
                            } else {
                                throw new Exception((new StringBuilder()).append("Tag ").append(fullRecordHex).append("_").append(arr[i].trim().toUpperCase()).append(" not map to any format").toString());
                            }
                        } else {
                            System.out.println();
                            System.out.println((new StringBuilder()).append("hoho ").append(arr1[0]).toString());
                            throw new Exception("Invalid field type");
                        }
                        mapRecordType.put((new StringBuilder()).append(fullRecordHex).append("_").append(arr[i].trim().toUpperCase()).toString(), field);
                        mapChilds.put(arr[i].trim().toUpperCase(), field);
                        field.setParentField(curField);
                        stackRecordLoad.push((new StringBuilder()).append(fullRecordHex).append("_").append(arr[i].trim().toUpperCase()).toString());
                        stackRecordLoad.push(arr[i]);
                    }

                    curField.setMapChilds(mapChilds);
                }
            }
        } while (true);
        Iterator iteStr = allRecordType.keySet().iterator();
        do {
            if (!iteStr.hasNext()) {
                break;
            }
            record = (String) iteStr.next();
            if (!getRecord.containsKey(record)) {
                field = new Field();
                field.setFieldName("Record");
                field.setFieldType(1);
                field.setFieldHEXCode(record);
                mapRecordType.put(record, field);
            }
        } while (true);
        if (paramFile.getString("column_count") != null) {
            columnCount = Integer.parseInt(paramFile.getString("column_count").trim());
        }
        for (int i = 0; i < columnCount; i++) {
            //listName: chua cac ten truong lay ra
            listNames.add(paramFile.getString((new StringBuilder()).append("column_").append(i).toString()).trim());
        }

        int seqGroupCount = Integer.parseInt(paramFile.getString("listitem_group_count").trim());
        for (int i = 0; i < seqGroupCount; i++) {
            String seqItemNames[] = paramFile.getString((new StringBuilder()).append("listitem_group_").append(i).toString()).trim().split(",");
            List lstItemNames = new ArrayList();
            for (int j = 0; j < seqItemNames.length; j++) {
                lstItemNames.add(seqItemNames[j]);
            }

            listSeqItemNames.add(lstItemNames);
            if (seqItemNames == null) {
                continue;
            }
            Map seqNames = new HashMap(); // Map i[j] voi cac column tuong ung
            Map mapSeqNames = new HashMap(); // Map column column
            for (int j = 0; j < seqItemNames.length; j++) {
                Map mSeqNames = new HashMap();
                String itemName = seqItemNames[j];
                List lstNames = new ArrayList();
                Iterator i$ = listNames.iterator();
                do {
                    if (!i$.hasNext()) {
                        break;
                    }
                    String name = (String) i$.next();
                    if (name.indexOf((new StringBuilder()).append(".[").append(itemName).append("]").toString()) >= 0) {
                        String subStr = name.substring(0, name.indexOf((new StringBuilder()).append(".[").append(itemName).append("]").toString()));
                        if (!mSeqNames.containsKey((new StringBuilder()).append(subStr).append(".[").append(itemName).append("]").toString())) {
                            lstNames.add((new StringBuilder()).append(subStr).append(".[").append(itemName).append("]").toString());
                            mSeqNames.put((new StringBuilder()).append(subStr).append(".[").append(itemName).append("]").toString(), (new StringBuilder()).append(subStr).append(".[").append(itemName).append("]").toString());
                        }
                        mapSeqNames.put(name, name);
                    }
                } while (true);
                seqNames.put(itemName, lstNames);
            }

            listSeqNames.add(seqNames); //i,j:
            listMapSeqNames.add(mapSeqNames);//i,j:
        }

    }

    private void loadToBuffer(String strFilePath)
            throws Exception {

        try {
            //add load 1000 byte 
            if (fileStream == null) {
                fileStream = new FileInputStream(strFilePath);
                mBuffer = new byte[readLength];
            }
            //read 1000k byte
            if (curIndex == 0) {//neu la doc lan dau
                if (firstRead) {
                    fileStream.read(mBuffer);
                    firstRead = false;
                }
            } else {
                if (curIndex == readLength) {
                    fileStream.read(mBuffer);
                    curIndex = 0;
                } else {
                    System.arraycopy(mBuffer, curIndex, mBuffer, 0, readLength - curIndex);
                    byte bTmp[] = new byte[curIndex];
                    if (fileStream.available() > 0) {
                        fileStream.read(bTmp);
                        //copy vao mBuffer
                        //                fileCurrentIndex += curIndex;
                    }
                    System.arraycopy(bTmp, 0, mBuffer, readLength - curIndex, curIndex);
                    curIndex = 0;
                    if (isBufferNull(mBuffer, titleLength)) {
                        isExit = true;
                    }
                }
            }

        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            throw new Exception((new StringBuilder()).append("Error when load file to buffer: ").append(e.getMessage()).toString());
            throw new ConvertException("Error when load file to buffer", ConvertException._FAIL);
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            throw new Exception((new StringBuilder()).append("Error when load file to buffer: ").append(e.getMessage()).toString());
        } finally {
            minIndex = titleLength;
        }
        minIndex = titleLength;
//        try {
////            fileCDR.close();
//        } catch (Exception e) {
//        }
        return;
    }

    private boolean isBufferNull(byte[] b, int length) {
        if (b.length <= length + 3) {
            return false;
        }
        for (int i = 0; i < length + 3; i++) {
            if (b[i] != 0) {
                return false;
            }
        }
        return true;
    }
}
