/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.vtz;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This class use to read file similar ResourceBundle
 *
 * @author Le Thanh Cong
 */
public class ParamFileUtil {

    private static final String PARAM_RECORD_HEADER = "record_header";
    private static final String PARAM_DEPEND_SIGNAL = "fields";
    private static final String PARAM_FIXED_RECORD_LENGTH = "fixed_record_length";
    private static final String PARAM_RECORD_LENGTH = "record_length";
    private static final String PARAM_RECORD_HEADER_PART_COUNT = "record_header_part_count";
    private static final String PARAM_RECORD_HEADER_PART_FORMAT = "record_header_part%d";
    private static final boolean debug = false;
    private static final String PARAM_ADDITION_SEPARATE = "#";
    private Properties properties = null;
    private static final String PARAM_NULL_VALUE = "null";
    private static final String PARAM_VALUE_SEPARATE = ";";
    //get common config
    private static final String PARAM_MAP_TOTAL_CODE = "map_code";
    private static final String PARAM_MAP_CODE = "code";
    private static final String PARAM_WRITE_TYPE = "write_type";
    private static final int COLUMN_TYPE_FIX = 0;
    private static final int COLUMN_TYPE_VALUE = 1;
    private static final int COLUMN_TYPE_FUNCTION = 2;
    private static final int COLUMN_TYPE_MAP = 3;
    private static final String COLUMN_TYPE_FIX_TEXT = "fix";
    private static final String COLUMN_TYPE_VALUE_TEXT = "value";
    private static final String COLUMN_TYPE_FUNCTION_TEXT = "function";
    private static final String COLUMN_TYPE_MAP_TEXT = "map";
    private static final String COLUMN_TYPE_FIELD_TEXT = "fields";
    private static final String PARAM_WRITE_MAP_HEADER = "write_map_";

    /**
     * Constructor
     *
     * @param fileName
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public ParamFileUtil(String fileName) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(fileName);
        properties = new Properties();
        properties.load(fis);
        fis.close();
    }

    public ParamFileUtil(InputStream stream) throws IOException {
        properties = new Properties();
        properties.load(stream);
        stream.close();
    }

    /**
     * Read value of a property
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        } else {
            return null;
        }
    }

    /**
     * Read value of a property
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Read value of a property and pack to record header
     *
     * @ex record_header_part_count=2 record_header_part1=5;Record Header Part
     * 1;CdrNumeric#4,CdrNumeric#1 record_header_part2=fields#1
     * record_header_part2_1=149;0x01 Detailed
     * ticket;CdrNumeric#1,CdrBits#2#6,CdrBits#1#5,CdrBits#1#4,CdrBits#1#3,CdrBits#1#2,CdrBits#1#1,CdrBits#1#0,CdrBits#1#7,CdrBits#2#5,CdrBits#1#4,CdrBits#4#0,CdrDateTime#6,CdrNumeric#4,CdrNumeric#4,CdrNumeric#4,CdrBits#2#6,CdrBits#1#5,CdrBits#1#4,CdrBits#1#3,CdrBits#3#0,CdrBits#4#4,CdrBits#4#0,CdrBits#4#4,CdrBits#4#0,CdrNumeric#1,CdrDigits#20,CdrNumeric#1,CdrDigits#20,CdrNumeric#1,CdrDigits#20,CdrNumeric#1,CdrDigits#20,CdrDigits#24,CdrNumeric#2,CdrNumeric#4,CdrNumeric#4,CdrNumeric#1,CdrNumeric#1,CdrNumeric#2,CdrNumeric#2,CdrNumeric#2,CdrNumeric#2,CdrNumeric#1,CdrNumeric#1,CdrNumeric#2,CdrNumeric#2,CdrNumeric#1,CdrNumeric#1,CdrBits#4#4,CdrBits#4#0,CdrNumeric#1,CdrNumeric#2,CdrNumeric#2,CdrNumeric#4,CdrNumeric#4,CdrNumeric#4,CdrNumeric#1,CdrBits#4#4,CdrBits#3#1,CdrBits#1#0,CdrNumeric#2,CdrNumeric#1,CdrNumeric#1,CdrNumeric#1,CdrNumeric#4,CdrNumeric#4,CdrBits#5#3,CdrBits#3#0,CdrBytes#7
     * record_header_part2_2=149;0x02 DBO call record;CdrBytes#149
     * record_header_part2_3=149;0x03 IN record;CdrBytes#149
     * record_header_part2_5=149;0x05 TAX record;CdrBytes#149
     * record_header_part2_6=149;0x06 IN CORE-INAP ticket;CdrBytes#149
     * record_header_part2_240=149;0xF0 Meter table ticket;CdrBytes#149
     * record_header_part2_241=149;0xF1 Meter table statistics;CdrBytes#149
     * record_header_part2_242=149;0xF2 Trunk duration statistics;CdrBytes#149
     * record_header_part2_243=149;0xF3 Free call statistics;CdrBytes#149
     * record_header_part2_244=149;0xF4 SCCP meter table ticket;CdrBytes#149
     * record_header_part2_255=149;0xFF Warn ticket;CdrBytes#149
     * record_header_part2_85=149;0x55 Failed call ticket;CdrBytes#149
     * @note There are three types of key: 1. Length;Description;Fields list 2.
     * fields#index -> Depend value of fields[index] -> If fields[index] = 3,
     * read record_header_partxxx_3 3. null -> Ignore
     * @param result
     * @param recordData
     * @param offset
     * @return
     * @throws java.lang.Exception
     */
    public BasePackager getCdrHeader(BasePackager result, byte[] recordData, int offset) throws Exception {
        if (debug) {
            System.out.println("\nStart Header");
        }

        int partCount = Integer.parseInt(getString(PARAM_RECORD_HEADER_PART_COUNT));
        String key = null;
        String extend = null;
        for (int i = 0; i < partCount; i++) {
            key = String.format(PARAM_RECORD_HEADER_PART_FORMAT, i + 1);
            if (extend != null) {
                key += extend;
            }
            String value = getString(key);
            if (!value.equals(PARAM_NULL_VALUE)) {
                if (value.indexOf(PARAM_DEPEND_SIGNAL) >= 0) {
                    String[] arr = value.split(PARAM_ADDITION_SEPARATE, -1);
                    extend = "_" + ((Long) result.getFields()[Integer.parseInt(arr[1])].getValue()).toString();
                    key += extend;
                    value = getString(key);
                }
                // Read parameters of package in siemen CDR
                BasePackager packager = BasePackager.parsePackager(value);
                result.extendFields(packager);
                result.unpack(recordData, offset);
            }
        }

        if (debug) {
            for (int i = 0; i < result.getFields().length; i++) {
                System.out.println(result.getFields()[i].toString());
            }
            System.out.println("End Header!!!");
        }
        return result;
    }

    /**
     * Read file config and get record length
     *
     * @ex Ex1: fixed_record_length=154 -> Record Length = 154 Ex2:
     * record_length=fields#0 record_length_132=record_header;1
     * record_length_1=32 record_length_2=32 record_length_3=32
     * record_length_128=1 record_length_129=record_header;1 record_length_0=32
     * @note There are three types of key: 1. Length;Description;Fields list 2.
     * fields#index -> Depend value of fields[index] -> If fields[index] = 3,
     * read record_header_partxxx_3 3. null -> Ignore
     * @param header
     * @param packagers
     * @return
     */
    public int getRecordLength(BasePackager header, BasePackager[] packagers) {
        String key = PARAM_FIXED_RECORD_LENGTH;
        String value = getString(key);
        int result = 0;
        if (value != null && value.length() > 0) {
            result = Integer.parseInt(value);
        } else {
            key = PARAM_RECORD_LENGTH;
            value = getString(key);
            if (value.indexOf(PARAM_DEPEND_SIGNAL) >= 0) {
                String[] arr = value.split(PARAM_ADDITION_SEPARATE, -1);
                key += "_" + ((Long) header.getFields()[Integer.parseInt(arr[1])].getValue()).toString();
                value = getString(key);
            }
            // Packager identifier; fiel no
            String[] arrStr = value.split(PARAM_VALUE_SEPARATE, -1);
            if (arrStr.length > 1) {
                BasePackager packager = null;
                if (arrStr[0].equals(PARAM_RECORD_HEADER)) {
                    packager = header;
                } else {
                    packager = packagers[Integer.parseInt(arrStr[0])];
                }

                result = ((Long) (packager.getFields()[Integer.parseInt(arrStr[1])].getValue())).intValue();
            } else {
                result = Integer.parseInt(value);

            }
        }
        return result;
    }

    public Map<String, Map<String, String>> getReaderMap() {
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

        try {
            String mapCode = getString(PARAM_MAP_TOTAL_CODE);
            if (mapCode != null && mapCode.trim().length() > 0) {
                String strs[] = mapCode.trim().split(",");
                if (strs.length > 0) {

                    for (int i = 0; i < strs.length; i++) {
                        //get map item
                        String mapItems = getString(PARAM_MAP_CODE + "_" + strs[i].trim());
                        String items[] = mapItems.split(",");
                        Map<String, String> itemMap = new HashMap<String, String>();
                        for (int j = 0; j < items.length; j++) {
                            String key = items[j].trim();
                            String value = getString(PARAM_MAP_CODE + "_" + strs[i].trim() + "_" + items[j]).trim();
                            itemMap.put(key, value);
                        }
                        map.put(strs[i].trim(), itemMap);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return map;
        }
    }

    public int[] getWriteType2() {
        String typeTexts[] = null;
        int outputSize = Integer.parseInt(getString("output_size"));
        int[] type = new int[outputSize];
        for (int i = 0; i < outputSize; i++) {
            try {
                typeTexts = getString("write_map_" + getString("OUT_" + (i + 1))).split(";");
            } catch (Exception ex) {
//                System.out.println("Get string exception 01: " + ex.getMessage() + ". Position: OUT_" + (i + 1));
            }
            if (typeTexts.length != 2) {
                continue;
            }
            if (typeTexts[1].equals(COLUMN_TYPE_FIX_TEXT)) {
                type[i] = COLUMN_TYPE_FIX;
            } else if (typeTexts[1].equals(COLUMN_TYPE_FUNCTION_TEXT)) {
                type[i] = COLUMN_TYPE_FUNCTION;
            } else if (typeTexts[1].equals(COLUMN_TYPE_MAP_TEXT)) {
                type[i] = COLUMN_TYPE_MAP;
            } else if (typeTexts[1].equals(COLUMN_TYPE_VALUE_TEXT)) {
                type[i] = COLUMN_TYPE_VALUE;
            } else {
                type[i] = COLUMN_TYPE_FIX;
            }
        }
        return type;

    }

    /**
     * return array of type of
     */
    public int[] getWriteType() {
        String typeTexts[];
        int types[] = null;

        try {
            String columnTypes = getString(PARAM_WRITE_TYPE);
            if (columnTypes != null) {
                typeTexts = columnTypes.trim().split(",");
                types = new int[typeTexts.length];
                for (int i = 0; i < typeTexts.length; i++) {
                    if (typeTexts[i].equals(COLUMN_TYPE_FIX_TEXT)) {
                        types[i] = COLUMN_TYPE_FIX;
                    } else if (typeTexts[i].equals(COLUMN_TYPE_FUNCTION_TEXT)) {
                        types[i] = COLUMN_TYPE_FUNCTION;
                    } else if (typeTexts[i].equals(COLUMN_TYPE_MAP_TEXT)) {
                        types[i] = COLUMN_TYPE_MAP;
                    } else if (typeTexts[i].equals(COLUMN_TYPE_VALUE_TEXT)) {
                        types[i] = COLUMN_TYPE_VALUE;
                    } else {
                        types[i] = COLUMN_TYPE_FIX;
                    }
                }
            }
        } finally {
            return types;
        }
    }

    public WriteItem[] getWriteMap2() throws Exception {
        int writeTypes[] = getWriteType2();
        Map<String, Map<String, String>> map = getReaderMap();
        String functionPakage = getString("function_pakage");

        WriteItem items[] = new WriteItem[writeTypes.length];
        for (int i = 0; i < items.length; i++) {
            try {
                String arrStr[] = getString("write_map_" + getString("OUT_" + (i + 1))).split(";");
                String str = arrStr[0];
                items[i] = null;
                if (str != null) {
                    if (writeTypes[i] == COLUMN_TYPE_FIX) {
                        String arr[] = str.trim().split("#");
                        if (arr.length == 2 && arr[0].equals(COLUMN_TYPE_FIELD_TEXT)) {
                            int fieldId = Integer.parseInt(arr[1]);
                            items[i] = new WriteItemFixField();
                            ((WriteItemFixField) items[i]).setFieldId(fieldId);
                        }
                    } else if (writeTypes[i] == COLUMN_TYPE_VALUE) {
                        String arr[] = str.trim().split("#");
                        if (arr.length == 2 && arr[0].equals(COLUMN_TYPE_FIELD_TEXT)) {
                            int fieldId = Integer.parseInt(arr[1]);
                            items[i] = new WriteItemField();
                            ((WriteItemField) items[i]).setFieldId(fieldId);
                        }
                    } else if (writeTypes[i] == COLUMN_TYPE_MAP) {
                        String arr[] = str.trim().split("#");
                        if (arr.length == 3 && arr[0].equals("map")) {
                            int fieldId = Integer.parseInt(arr[2]);
                            if (map.containsKey(arr[1])) {
                                WriteItemMap item = new WriteItemMap();
                                item.setMap(map.get(arr[1]));
                                item.setFieldId(fieldId);
                                items[i] = item;
                            }
                        }
                    } else if (writeTypes[i] == COLUMN_TYPE_FUNCTION) {

                        //String arr[] = str.trim().split(",");
                        //split function String
                        List<String> arrList = new ArrayList<String>();
                        boolean inStr = false;
                        String tempArg = "";
                        for (int is = 0; is < str.length(); is++) {
                            if (str.charAt(is) == '"') {
                                if (!inStr) {
                                    inStr = true;
                                } else {
                                    inStr = false;
                                }
                                tempArg += '"';
                            } else if (str.charAt(is) == ',') {
                                arrList.add(tempArg);
                                tempArg = "";
                            } else {
                                tempArg += str.charAt(is);
                            }
                            if (is == str.length() - 1) {
                                arrList.add(tempArg);
                            }
                        }


                        if (arrList.size() >= 1) {
                            BaseFunction function = BaseFunction.getFunction(functionPakage, arrList.get(0));
                            //get argument list
                            WriteItem args[] = new WriteItem[arrList.size() - 1];
                            for (int j = 0; j < args.length; j++) {
                                String arrArg[] = arrList.get(j + 1).split("#");
                                //default value
                                args[j] = new WriteItem(WriteItem.FIELD_TYPE_CONSTANT);
                                args[j].setValue("");

                                if (arrArg.length >= 2) {
                                    if (arrArg[0].trim().equals(COLUMN_TYPE_FIELD_TEXT)) {
                                        if (arrArg.length == 2) {
                                            int fieldId = Integer.parseInt(arrArg[1]);
                                            args[j] = null;
                                            args[j] = new WriteItemField();
                                            ((WriteItemField) args[j]).setFieldId(fieldId);
                                        }
                                    } else if (arrArg[0].trim().equals(COLUMN_TYPE_MAP_TEXT)) {
                                        if (arrArg.length == 3) {
                                            int fieldId = Integer.parseInt(arrArg[2]);
                                            if (map.containsKey(arrArg[1])) {
                                                WriteItemMap item = new WriteItemMap();
                                                item.setMap(map.get(arrArg[1]));
                                                item.setFieldId(fieldId);
                                                args[j] = item;
                                            }
                                        }
                                    } else {
                                        // check constant
                                        //not yet check Date dataType
                                        if (arrArg.length == 2) {
                                            if (arrArg[0].trim().equals("int")) {
                                                args[j] = null;
                                                args[j] = new WriteItem(WriteItem.FIELD_TYPE_CONSTANT);
                                                args[j].setValue(Long.parseLong(arrArg[1]));
                                            } else if (arrArg[0].trim().equals("string")) {
                                                args[j] = null;
                                                args[j] = new WriteItem(WriteItem.FIELD_TYPE_CONSTANT);
                                                args[j].setValue(arrArg[1].substring(1, arrArg[1].length() - 1));
                                            }
                                        }
                                    }
                                }
                            }
                            //end get WriteItem
                            items[i] = new WriteItemFunction();
                            ((WriteItemFunction) items[i]).setColumns(args);
                            ((WriteItemFunction) items[i]).setFunction(function);
                        }
                    }
                }
            } catch (Exception e) {
                if (items[i] == null) {
                    items[i] = new WriteItem(WriteItem.FIELD_TYPE_CONSTANT);
                    items[i].setValue("");
                }
            }
        }
        return items;
    }

    public WriteItem[] getWriteMap() throws Exception {
        int writeTypes[] = getWriteType();
        Map<String, Map<String, String>> map = getReaderMap();
        String functionPakage = getString("function_pakage");

        WriteItem items[] = new WriteItem[writeTypes.length];
        for (int i = 0; i < items.length; i++) {
            if (i == 9) {
                System.out.println();
            }
            try {
                String str = getString(PARAM_WRITE_MAP_HEADER + (i + 1));
                items[i] = null;
                if (str != null) {
                    if (writeTypes[i] == COLUMN_TYPE_FIX) {
                        String arr[] = str.trim().split("#");
                        if (arr.length == 2 && arr[0].equals(COLUMN_TYPE_FIELD_TEXT)) {
                            int fieldId = Integer.parseInt(arr[1]);
                            items[i] = new WriteItemFixField();
                            ((WriteItemFixField) items[i]).setFieldId(fieldId);
                        }
                    } else if (writeTypes[i] == COLUMN_TYPE_VALUE) {
                        String arr[] = str.trim().split("#");
                        if (arr.length == 2 && arr[0].equals(COLUMN_TYPE_FIELD_TEXT)) {
                            int fieldId = Integer.parseInt(arr[1]);
                            items[i] = new WriteItemField();
                            ((WriteItemField) items[i]).setFieldId(fieldId);
                        }
                    } else if (writeTypes[i] == COLUMN_TYPE_MAP) {
                        String arr[] = str.trim().split("#");
                        if (arr.length == 3 && arr[0].equals("map")) {
                            int fieldId = Integer.parseInt(arr[2]);
                            if (map.containsKey(arr[1])) {
                                WriteItemMap item = new WriteItemMap();
                                item.setMap(map.get(arr[1]));
                                item.setFieldId(fieldId);
                                items[i] = item;
                            }
                        }
                    } else if (writeTypes[i] == COLUMN_TYPE_FUNCTION) {

                        //String arr[] = str.trim().split(",");
                        //split function String
                        List<String> arrList = new ArrayList<String>();
                        boolean inStr = false;
                        String tempArg = "";
                        for (int is = 0; is < str.length(); is++) {
                            if (str.charAt(is) == '"') {
                                if (!inStr) {
                                    inStr = true;
                                } else {
                                    inStr = false;
                                }
                                tempArg += '"';
                            } else if (str.charAt(is) == ',') {
                                arrList.add(tempArg);
                                tempArg = "";
                            } else {
                                tempArg += str.charAt(is);
                            }
                            if (is == str.length() - 1) {
                                arrList.add(tempArg);
                            }
                        }


                        if (arrList.size() >= 1) {
                            BaseFunction function = BaseFunction.getFunction(functionPakage, arrList.get(0));
                            //get argument list
                            WriteItem args[] = new WriteItem[arrList.size() - 1];
                            for (int j = 0; j < args.length; j++) {
                                String arrArg[] = arrList.get(j + 1).split("#");
                                //default value
                                args[j] = new WriteItem(WriteItem.FIELD_TYPE_CONSTANT);
                                args[j].setValue("");

                                if (arrArg.length >= 2) {
                                    if (arrArg[0].trim().equals(COLUMN_TYPE_FIELD_TEXT)) {
                                        if (arrArg.length == 2) {
                                            int fieldId = Integer.parseInt(arrArg[1]);
                                            args[j] = null;
                                            args[j] = new WriteItemField();
                                            ((WriteItemField) args[j]).setFieldId(fieldId);
                                        }
                                    } else if (arrArg[0].trim().equals(COLUMN_TYPE_MAP_TEXT)) {
                                        if (arrArg.length == 3) {
                                            int fieldId = Integer.parseInt(arrArg[2]);
                                            if (map.containsKey(arrArg[1])) {
                                                WriteItemMap item = new WriteItemMap();
                                                item.setMap(map.get(arrArg[1]));
                                                item.setFieldId(fieldId);
                                                args[j] = item;
                                            }
                                        }
                                    } else {
                                        // check constant
                                        //not yet check Date dataType
                                        if (arrArg.length == 2) {
                                            if (arrArg[0].trim().equals("int")) {
                                                args[j] = null;
                                                args[j] = new WriteItem(WriteItem.FIELD_TYPE_CONSTANT);
                                                args[j].setValue(Long.parseLong(arrArg[1]));
                                            } else if (arrArg[0].trim().equals("string")) {
                                                args[j] = null;
                                                args[j] = new WriteItem(WriteItem.FIELD_TYPE_CONSTANT);
                                                args[j].setValue(arrArg[1].substring(1, arrArg[1].length() - 1));
                                            }
                                        }
                                    }
                                }
                            }
                            //end get WriteItem
                            items[i] = new WriteItemFunction();
                            ((WriteItemFunction) items[i]).setColumns(args);
                            ((WriteItemFunction) items[i]).setFunction(function);
                        }
                    }
                }
            } catch (Exception e) {
                if (items[i] == null) {
                    items[i] = new WriteItem(WriteItem.FIELD_TYPE_CONSTANT);
                    items[i].setValue("");
                }
            }
        }
        return items;
    }

    //TuanNA67 begin add
    public List<String> getAllKeys() {
        List<String> results = new ArrayList<String>();
        for (Object str : properties.keySet()) {
            results.add(str.toString());
        }
        return results;
    }
    //TuanNA67 end
}
