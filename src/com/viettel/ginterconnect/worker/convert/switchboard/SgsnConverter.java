package com.viettel.ginterconnect.worker.convert.switchboard;

import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.process.filter.importer.TextOutputFile;
import com.viettel.ginterconnect.worker.convert.BaseConverter;
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

public class SgsnConverter extends BaseConverter {

    private byte endOfTagUnknowLength[];
    private byte mBuffer[];
    private int minIndex;
    private int curIndex;
    private int columnCount;
    private List listSeqItemNames;
    private List listSeqNames;
    private List listMapSeqNames;
    private List listNames;
    private TextOutputFile textFile;
    private String seqIteratorItem;
    private Map mapRecordType;
    private Map allRecordType;
    private Map getRecord;

    public SgsnConverter() {
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

    @Override
    public ConvertLog convertDetail()
            throws Exception {
        try {
            loadConvertParam();
            textFile = new TextOutputFile(outputFileName);
            textFile.setFileIdPosition(fileIdPosition);
            textFile.setFileId(super.getFileId());
            loadToBuffer(inputFileName);
            curIndex = minIndex;
            Field curField = null;
            Record curRecord = null;
            BasicValue curValue = null;
            do {
                if (curIndex > mBuffer.length - 1) {
                    break;
                }
                if (curField == null) {
                    curRecord = new Record();
                    curRecord.setMapValues(new HashMap());
                    Field newField = getTag(curField);
                    int length = getLength();
                    if (getRecord.containsKey(newField.getFieldHEXCode())) {
                        BasicValue recordValue = new SetValue();
                        recordValue.setField(newField);
                        recordValue.setIndexValue(curIndex);
                        recordValue.setLength(length);
                        recordValue.setName(newField.getFieldName());
                        recordValue.setParentValue(null);
                        curRecord.getMapValues().put(newField.getFieldName(), recordValue);
                        curField = newField;
                        curValue = recordValue;
                        PureValue recordNameValue = new PureValue();
                        recordNameValue.setField(null);
                        recordNameValue.setName("RecordName");
                        recordNameValue.setParentValue(null);
                        recordNameValue.setValue((String) allRecordType.get(newField.getFieldHEXCode()));
                        curRecord.getMapValues().put("RecordName", recordNameValue);
                    } else {
                        curIndex += length;
                    }
                } else if (curValue.getLength() != -1 && curIndex >= curValue.getIndexValue() + curValue.getLength()) {
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
                    if (newField == null && curValue.getLength() == -1 && hexStartWith(mBuffer, endOfTagUnknowLength, curIndex)) {
                        curValue = curValue.getParentValue();
                        if (curValue != null) {
                            curField = curValue.getField();
                        } else {
                            curField = null;
                        }
                        curIndex += endOfTagUnknowLength.length;
                    } else if (newField != null) {
                        int length = getLength();
                        if (newField.getFieldType() == 0) {
                            PureValue value = new PureValue();
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
                            value.setValue(format.decode(mBuffer, curIndex, length));
                            curValue.getListChilds().add(value);
                            curRecord.getMapValues().put(value.getName(), value);
                            curIndex += length;
                        } else {
                            switch (newField.getFieldType()) {
                                case 2: // '\002'
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

                                case 1: // '\001'
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

                                case 3: // '\003'
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
                    } else if (newField == null) {
                        curIndex += getLength();
                    }
                }
                if ((curField != null || curRecord == null) && curIndex < mBuffer.length) {
                    continue;
                }
                int totalRecordWrite = 0;
                String writes[];
                for (int i = 0; i < listSeqNames.size(); i++) {
                    Map seqNames = (Map) listSeqNames.get(i);
                    Map mapNames = (Map) listMapSeqNames.get(i);
                    List varNames = (List) listSeqItemNames.get(i);
                    int varValues[] = new int[varNames.size()];
                    for (int j = 0; j < varValues.length; j++) {
                        varValues[j] = -1;
                    }

                    int curValueIndex = 0;
                    do {
                        do {
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
                            } else {
                                curValueIndex++;
                            }
                        } while (true);
                        varValues[curValueIndex] = -1;
                    } while (--curValueIndex >= 0);
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

                    textFile.addRecord(writes);
                    totalConverted++;
                }
            } while (true);
        } catch (Exception ex) {
            System.out.println("Cur Index: " + curIndex);
            logger.error(ex.getMessage(), ex);
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
            String recordType;
            if (tempTag[0] != null && allRecordType.containsKey(tempTag[0])) {
                recordType = tempTag[0];
                curIndex++;
            } else if (tempTag[1] != null && allRecordType.containsKey(tempTag[1])) {
                recordType = tempTag[1];
                curIndex += 2;
            } else if (allRecordType.containsKey(tempTag[2])) {
                recordType = tempTag[2];
                curIndex += 3;
            } else {
                return null;
            }
            if (recordType != null && mapRecordType.containsKey(recordType)) {
                return (Field) mapRecordType.get(recordType);
            } else {
                String parentTag = "";
                if (curField.getParentField() != null) {
                    if (curField.getParentField().getParentField() != null) {
                        parentTag = curField.getParentField().getParentField().getFieldHEXCode();
                    } else {
                        parentTag = curField.getParentField().getFieldHEXCode();
                    }
                } else {
                    parentTag = curField.getFieldHEXCode();
                }
                System.out.println("New tags. Parent: " + parentTag + ". CurIndex: " + curIndex + ". Check Bytes:" + tempTag[2]);
                return null;
            }
        }
        String tag;
        if (curField.getMapChilds() == null) {
            return null;
        }
        if (tempTag[0] != null && curField.getMapChilds().containsKey(tempTag[0])) {
            tag = tempTag[0];
            curIndex++;
        } else if (tempTag[1] != null && curField.getMapChilds().containsKey(tempTag[1])) {
            tag = tempTag[1];
            curIndex += 2;
        } else if (curField.getMapChilds().containsKey(tempTag[2])) {
            tag = tempTag[2];
            curIndex += 3;
        } else {
//            System.out.println("New tags: " + curField.getParentField().getFieldHEXCode() + ". CurIndex: " + curIndex + ". Check Bytes:" + tempTag[0] + tempTag[1] + tempTag[2]);
            String parentTag = "";
            if (curField.getParentField() != null) {
                if (curField.getParentField().getParentField() != null) {
                    parentTag = curField.getParentField().getParentField().getFieldHEXCode();
                } else {
                    parentTag = curField.getParentField().getFieldHEXCode();
                }
            } else {
                parentTag = curField.getFieldHEXCode();
            }
            System.out.println("New tags. Parent: " + parentTag + ". CurIndex: " + curIndex + ". Check Bytes:" + tempTag[2]);
            return null;
        }
        if (tag != null && curField.getMapChilds().containsKey(tag)) {
            return (Field) curField.getMapChilds().get(tag);
        } else {
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
            return iValue;
        }
        iValue = 0;
        iValue |= temp & 0x7f;
        curIndex++;
        int length = 0;
        if (iValue == 0) {
            return -1;
        }
        for (int i = 0; i < iValue; i++) {
            length <<= 8;
            length |= mBuffer[curIndex] & 0xff;
            curIndex++;
        }

        return length;
    }

    public void loadConvertParam()
            throws Exception {
        separate = paramFile.getString("separate");
        dateFormat = new SimpleDateFormat(paramFile.getString("date_format"));
        if (paramFile.getString("SequenceIteratorItemName") != null) {
            seqIteratorItem = paramFile.getString("SequenceIteratorItemName").trim();
        }
        String loadRStr = paramFile.getString("RecordType");
        if (loadRStr != null && loadRStr.length() > 0) {
            String recs[] = loadRStr.trim().split(";");
            if (recs.length > 0) {
                for (String rec : recs) {
                    allRecordType.put(rec.toUpperCase(), paramFile.getString((new StringBuilder()).append("Record_").append(rec.trim()).append("_Name").toString()));
                }
            }
        }
        loadRStr = paramFile.getString("GetRecordType");
        if (loadRStr != null && loadRStr.length() > 0) {
            String recs[] = loadRStr.trim().split(";");
            if (recs.length > 0) {
                for (String rec : recs) {
                    getRecord.put(rec.toUpperCase(), rec);
                }
            }
        }
        String etukLength = paramFile.getString("EndOfTagUnknowLength");
        if (etukLength != null) {
            etukLength = etukLength.trim();
            endOfTagUnknowLength = convertHexStringToByte(etukLength);
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
        String recordHex;
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
                if (str != null) {
                    String arr[] = str.trim().split(";");
                    if (arr != null && arr.length > 0) {
                        Map mapChilds = new HashMap();
                        for (String arr2 : arr) {
                            String str1 = paramFile.getString((new StringBuilder()).append("Record_").append(fullRecordHex).append("_").append(arr2.trim().toUpperCase()).append("_Type").toString());
                            String arr1[] = null;
                            try {
                                arr1 = str1.trim().split(";");
                            } catch (Exception ex) {
                                System.out.println((new StringBuilder()).append("Record_").append(fullRecordHex).append("_").append(arr2.trim().toUpperCase()).append("_Type").toString());
                                throw ex;
                            }
                            field = new Field();
                            field.setFieldName(arr1[0].trim());
                            switch (arr1[1].trim()) {
                                case "Set":
                                    field.setFieldType(1);
                                    break;
                                case "Seq":
                                    field.setFieldType(2);
                                    break;
                                case "Choice":
                                    field.setFieldType(3);
                                    break;
                                case "Value":
                                    field.setFieldType(0);
                                    if (arr1[2] != null) {
                                        field.setFormat(arr1[2].trim());
                                    } else {
                                        throw new Exception((new StringBuilder()).append("Tag ").append(fullRecordHex).append("_").append(arr2.trim().toUpperCase()).append(" not map to any format").toString());
                                    }
                                    break;
                                default:
                                    throw new Exception("Invalid field type");
                            }
                            mapRecordType.put((new StringBuilder()).append(fullRecordHex).append("_").append(arr2.trim().toUpperCase()).toString(), field);
                            mapChilds.put(arr2.trim().toUpperCase(), field);
                            field.setParentField(curField);
                            stackRecordLoad.push((new StringBuilder()).append(fullRecordHex).append("_").append(arr2.trim().toUpperCase()).toString());
                            stackRecordLoad.push(arr2);
                        }

                        curField.setMapChilds(mapChilds);
                    }
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
            listNames.add(paramFile.getString((new StringBuilder()).append("column_").append(i).toString()).trim());
        }

        int seqGroupCount = Integer.parseInt(paramFile.getString("listitem_group_count").trim());
        for (int i = 0; i < seqGroupCount; i++) {
            String seqItemNames[] = paramFile.getString((new StringBuilder()).append("listitem_group_").append(i).toString()).trim().split(",");
            List lstItemNames = new ArrayList();
            lstItemNames.addAll(Arrays.asList(seqItemNames));
            listSeqItemNames.add(lstItemNames);
            if (seqItemNames == null) {
                continue;
            }
            Map seqNames = new HashMap();
            Map mapSeqNames = new HashMap();
            for (String seqItemName : seqItemNames) {
                Map mSeqNames = new HashMap();
                String itemName = seqItemName;
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

            listSeqNames.add(seqNames);
            listMapSeqNames.add(mapSeqNames);
        }

    }

    private void loadToBuffer(String strFilePath)
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
            throw new Exception((new StringBuilder()).append("Error when load file to buffer: ").append(e.getMessage()).toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception((new StringBuilder()).append("Error when load file to buffer: ").append(e.getMessage()).toString());
        } finally {
            minIndex = 0;
        }
        minIndex = 0;
        try {
            fileCDR.close();
        } catch (IOException e) {
        }
    }

    private byte[] convertHexStringToByte(String input) {
        String inStr = input.replace(" ", "");
        inStr = inStr.toUpperCase();
        byte result[] = new byte[(inStr.length() + 1) / 2];
        for (int i = 0; i < (inStr.length() + 1) / 2; i++) {
            char c = inStr.charAt(inStr.length() - 2 * i - 1);
            byte cB = 0;
            switch (c) {
                case 48: // '0'
                    cB = 0;
                    break;

                case 49: // '1'
                    cB = 1;
                    break;

                case 50: // '2'
                    cB = 2;
                    break;

                case 51: // '3'
                    cB = 3;
                    break;

                case 52: // '4'
                    cB = 4;
                    break;

                case 53: // '5'
                    cB = 5;
                    break;

                case 54: // '6'
                    cB = 6;
                    break;

                case 55: // '7'
                    cB = 7;
                    break;

                case 56: // '8'
                    cB = 8;
                    break;

                case 57: // '9'
                    cB = 9;
                    break;

                case 65: // 'A'
                    cB = 10;
                    break;

                case 66: // 'B'
                    cB = 11;
                    break;

                case 67: // 'C'
                    cB = 12;
                    break;

                case 68: // 'D'
                    cB = 13;
                    break;

                case 69: // 'E'
                    cB = 14;
                    break;

                case 70: // 'F'
                    cB = 15;
                    break;

                case 58: // ':'
                case 59: // ';'
                case 60: // '<'
                case 61: // '='
                case 62: // '>'
                case 63: // '?'
                case 64: // '@'
                default:
                    throw new RuntimeException("Invalid char, not in 0123456789ABCDEF");
            }
            result[result.length - i - 1] = cB;
            if (inStr.length() - 2 * i - 2 < 0) {
                continue;
            }
            c = inStr.charAt(inStr.length() - 2 * i - 2);
            switch (c) {
                case 48: // '0'
                    cB = 0;
                    break;

                case 49: // '1'
                    cB = 1;
                    break;

                case 50: // '2'
                    cB = 2;
                    break;

                case 51: // '3'
                    cB = 3;
                    break;

                case 52: // '4'
                    cB = 4;
                    break;

                case 53: // '5'
                    cB = 5;
                    break;

                case 54: // '6'
                    cB = 6;
                    break;

                case 55: // '7'
                    cB = 7;
                    break;

                case 56: // '8'
                    cB = 8;
                    break;

                case 57: // '9'
                    cB = 9;
                    break;

                case 65: // 'A'
                    cB = 10;
                    break;

                case 66: // 'B'
                    cB = 11;
                    break;

                case 67: // 'C'
                    cB = 12;
                    break;

                case 68: // 'D'
                    cB = 13;
                    break;

                case 69: // 'E'
                    cB = 14;
                    break;

                case 70: // 'F'
                    cB = 15;
                    break;

                case 58: // ':'
                case 59: // ';'
                case 60: // '<'
                case 61: // '='
                case 62: // '>'
                case 63: // '?'
                case 64: // '@'
                default:
                    throw new RuntimeException("Invalid char, not in 0123456789ABCDEF");
            }
            cB = (byte) (cB << 4 & 0xff);
            result[result.length - i - 1] |= cB;
        }

        return result;
    }

    private boolean hexStartWith(byte parent[], byte child[], int start) {
        if (start > parent.length - 1 || start < 0) {
            return false;
        }
        if (parent.length - start < child.length) {
            return false;
        }
        for (int i = 0; i < child.length; i++) {
            if (child[i] != parent[start + i]) {
                return false;
            }
        }

        return true;
    }
}
