/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.filter;

import com.viettel.ginterconnect.log.LoggerFilter;
import com.viettel.ginterconnect.process.exception.ImportQueueException;
import com.viettel.ginterconnect.process.exception.StandardException;
import com.aerospike.client.AerospikeException;
import com.viettel.ginterconnect.process.exception.FilterException;
import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.log.BaseLog;
import com.viettel.ginterconnect.logfile.FileProcessLog;
import com.viettel.ginterconnect.process.bean.*;
import com.viettel.ginterconnect.process.filter.expression.ObjectProcess;
import com.viettel.ginterconnect.process.filter.function.ExecuteFunction;
import com.viettel.ginterconnect.process.filter.function.IFilterAspectFunction;
import com.viettel.ginterconnect.process.filter.importer.ImportQueue;
import com.viettel.ginterconnect.process.filter.importer.Importer;
import com.viettel.ginterconnect.process.filter.importer.ImporterFactory;
//import com.viettel.ginterconnect.process.worker.impl.FILTER;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIClient;
import com.viettel.ginterconnect.util.GIUtils;
import org.apache.log4j.Logger;
import org.hibernate.tool.hbm2x.StringUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 *
 * @version 1.0
 * @since May 9, 2017
 */
public class BaseFilter {

    private Logger logger = null;

    //params filter
    private String fileName;
    private String inputDir;
    private String inputFileName;
    private String fieldSeparate = ";";
    private String fieldOutSeparate = "|";
    private long ignoredHeader = 0;
    private Long switchId = -1L;
    private String errorFolder;
    private long fileId = -1;
    private String threadCode;
    private String switchType;
    private String structureName;
    private String country;

    //filter cache
    private GICache filterCache;
    private Map<Long, List<SubExpression>> mapEx = null;
    private List<Result> lstResult = new ArrayList<>();
    private List<ImportQueue> listImportQueue = new ArrayList<>();
    private Map<String, Importer> mapImporter = new HashMap<>();
    private Map<String, Importer> mapImporterStandardError = new HashMap<>();
    private List<Importer> listImporter = new ArrayList<>();

    //for test
    Map<Long, List<StandardizeField>> mapResult = new HashMap<>();

    private HashMap<Object, HashMap<Object, Map<Object, Object>>> revCache = new HashMap<>();

    private long totalFilter = 0;
    private long totalCdrInvalid = 0;
    private long totalStandardFail = 0;
    private long totalFilterSuccess = 0;

    //hardcode
    private static final String TYPE_STRING = "STRING";
    private static final String TYPE_LONG = "LONG";
    private static final String TYPE_INTEGER = "INTEGER";
    private static final String TYPE_DATE = "DATE";
    private static final String TYPE_DOUBLE = "DOUBLE";
    private static final String TYPE_FLOAT = "FLOAT";

    //system
    private String workerName;

    //cached
    private HashMap<String, Map> cache = new HashMap<>();

    public BaseFilter(String workerName, Logger logger) {
        this.workerName = workerName;
        this.threadCode = workerName;
        this.logger = new LoggerFilter(logger, workerName);
    }

    public CdrObject createCdrObjectWithParam(String cdr, String spliter, List<DataStructureField> lstConfig) {

        CdrObject co = new CdrObject(cdr);
        String tmp = cdr;
        if (cdr.endsWith(spliter)) {
            tmp += "0";
        }
        String[] arrCdr = tmp.split(Pattern.quote(spliter));
        if (cdr.endsWith(spliter)) {
            arrCdr[arrCdr.length - 1] = "";
        }

        for (DataStructureField fieldConfig : lstConfig) {
            String value = null;
            try {
                switch (fieldConfig.getFieldType().toUpperCase()) {
                    case TYPE_STRING:
                        if (fieldConfig.getFieldPos() != null) {
                            value = arrCdr[fieldConfig.getFieldPos() - 1];
                            if (value == null || value.trim().length() == 0) {
                                co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue());
                            } else {
                                co.set(fieldConfig.getFieldName(), value);
                                if (fieldConfig.getValidRegex() != null && !value.matches(fieldConfig.getValidRegex())) {
                                    return setCdrErrorValidate(co, value, fieldConfig);
                                }
                            }
                        } else {
                            co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue());
                        }
                        break;
                    case TYPE_LONG:
                        if (fieldConfig.getFieldPos() != null) {
                            value = arrCdr[fieldConfig.getFieldPos() - 1];
                            if (value == null || value.trim().length() == 0) {
                                try {
                                    co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue() == null ? null : Long.valueOf(fieldConfig.getDefaultValue()));
                                } catch (NumberFormatException ex) {
                                    return setCdrErrorDefault(co, value, fieldConfig);
                                }
                            } else {
                                try {
                                    co.set(fieldConfig.getFieldName(), Long.parseLong(value));
                                    if (fieldConfig.getValidRegex() != null && !value.matches(fieldConfig.getValidRegex())) {
                                        return setCdrErrorValidate(co, value, fieldConfig);
                                    }
                                } catch (NumberFormatException ex) {
                                    return setCdrErrorValidate(co, value, fieldConfig);
                                }
                            }
                        } else {
                            try {
                                co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue() == null ? null : Long.valueOf(fieldConfig.getDefaultValue()));
                            } catch (NumberFormatException ex) {
                                return setCdrErrorDefault(co, value, fieldConfig);
                            }
                        }
                        break;
                    case TYPE_DOUBLE:
                        if (fieldConfig.getFieldPos() != null) {
                            value = arrCdr[fieldConfig.getFieldPos() - 1];
                            if (value == null || value.trim().length() == 0) {
                                try {
                                    co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue() == null ? null : Double.valueOf(fieldConfig.getDefaultValue()));
                                } catch (NumberFormatException ex) {
                                    return setCdrErrorDefault(co, value, fieldConfig);
                                }
                            } else {
                                try {
                                    co.set(fieldConfig.getFieldName(), Double.parseDouble(value));
                                    if (fieldConfig.getValidRegex() != null && !value.matches(fieldConfig.getValidRegex())) {
                                        return setCdrErrorValidate(co, value, fieldConfig);
                                    }
                                } catch (NumberFormatException ex) {
                                    return setCdrErrorValidate(co, value, fieldConfig);
                                }
                            }
                        } else {
                            try {
                                co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue() == null ? null : Double.valueOf(fieldConfig.getDefaultValue()));
                            } catch (NumberFormatException ex) {
                                return setCdrErrorValidate(co, value, fieldConfig);
                            }
                        }
                        break;
                    case TYPE_FLOAT:
                        if (fieldConfig.getFieldPos() != null) {
                            value = arrCdr[fieldConfig.getFieldPos() - 1];
                            if (value == null || value.trim().length() == 0) {
                                try {
                                    co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue() == null ? null : Float.valueOf(fieldConfig.getDefaultValue()));
                                } catch (NumberFormatException ex) {
                                    return setCdrErrorDefault(co, value, fieldConfig);
                                }
                            } else {
                                try {
                                    co.set(fieldConfig.getFieldName(), Float.parseFloat(value));
                                    if (fieldConfig.getValidRegex() != null && !value.matches(fieldConfig.getValidRegex())) {
                                        return setCdrErrorValidate(co, value, fieldConfig);
                                    }
                                } catch (NumberFormatException ex) {
                                    return setCdrErrorValidate(co, value, fieldConfig);
                                }
                            }
                        } else {
                            try {
                                co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue() == null ? null : Float.valueOf(fieldConfig.getDefaultValue()));
                            } catch (NumberFormatException ex) {
                                return setCdrErrorDefault(co, value, fieldConfig);
                            }
                        }
                        break;
                    case TYPE_INTEGER:
                        if (fieldConfig.getFieldPos() != null) {
                            value = arrCdr[fieldConfig.getFieldPos() - 1];
                            if (value == null || value.trim().length() == 0) {
                                try {
                                    co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue() == null ? null : Integer.parseInt(fieldConfig.getDefaultValue()));
                                } catch (NumberFormatException ex) {
                                    return setCdrErrorDefault(co, value, fieldConfig);
                                }
                            } else {
                                try {
                                    co.set(fieldConfig.getFieldName(), Integer.parseInt(value));
                                    if (fieldConfig.getValidRegex() != null && !value.matches(fieldConfig.getValidRegex())) {
                                        return setCdrErrorValidate(co, value, fieldConfig);
                                    }
                                } catch (NumberFormatException ex) {
                                    return setCdrErrorValidate(co, value, fieldConfig);
                                }
                            }
                        } else {
                            try {
                                co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue() == null ? null : Integer.parseInt(fieldConfig.getDefaultValue()));
                            } catch (NumberFormatException ex) {
                                return setCdrErrorDefault(co, value, fieldConfig);
                            }
                        }
                        break;
                    case TYPE_DATE:
                        String strDatePattern;
                        if (fieldConfig.getFieldFormat() != null) {
                            strDatePattern = fieldConfig.getFieldFormat();
                        } else {
                            co.setTypeError(CdrObject.TYPE_ERROR_CDR_REJECT);
                            co.setError("Validate fail. Field format date cannot null");
                            return co;
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat(strDatePattern);

                        if (fieldConfig.getFieldPos() != null) {
                            value = arrCdr[fieldConfig.getFieldPos() - 1];
                            if (value == null || value.trim().length() == 0) {
//                                try {
                                co.set(fieldConfig.getFieldName(), null);
//                                } catch (ParseException ex) {
//                                    return setCdrErrorDefault(co, value, fieldConfig);
//                                }
                            } else {
                                try {
                                    if (fieldConfig.getValidRegex() != null && !value.matches(fieldConfig.getValidRegex())) {
                                        return setCdrErrorValidate(co, value, fieldConfig);
                                    }
                                    co.set(fieldConfig.getFieldName(), sdf.parse(value));
                                } catch (ParseException ex) {
                                    return setCdrErrorValidate(co, value, fieldConfig);
                                }
                            }
                        } else {
                            try {
                                co.set(fieldConfig.getFieldName(), fieldConfig.getDefaultValue() == null ? null : sdf.parse(fieldConfig.getDefaultValue()));
                            } catch (ParseException ex) {
                                return setCdrErrorDefault(co, value, fieldConfig);
                            }
                        }
                        break;
                    default:
                        co.setTypeError(CdrObject.TYPE_ERROR_CDR_REJECT);
                        co.setError("Can't create CdrObject. Wrong field datatype input: " + fieldConfig.getFieldType() + ". FieldName: " + fieldConfig.getFieldName());
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                co.setTypeError(CdrObject.TYPE_ERROR_CDR_REJECT);
                co.setError("Can't parse CdrObject. Wrong field config position: " + fieldConfig.getFieldPos() + " ,data_type: " + fieldConfig.getFieldType() + ". FieldName: " + fieldConfig.getFieldName());
            }
        }
        setCdrSystemProperty(co);
        return co;
    }

    public CdrObject createCdrObjectWithDbConfig(String cdr, String fieldSeparate) {

//        List<DataStructureField> lstConfig = filterCache.loadFieldConfig(switchType, country);
        List<DataStructureField> lstConfig = filterCache.loadFieldConfig(structureName);
        return createCdrObjectWithParam(cdr, fieldSeparate, lstConfig);

    }

    private CdrObject setCdrErrorValidate(CdrObject co, String value, DataStructureField fieldConfig) {
        co.setTypeError(CdrObject.TYPE_ERROR_CDR_REJECT);
        co.setError("Validate fail. Switch: " + switchId
                + " Position: " + fieldConfig.getFieldPos()
                + " DataType: " + fieldConfig.getFieldType().toUpperCase()
                + " Mask: " + fieldConfig.getValidRegex()
                + " FieldName: " + fieldConfig.getFieldName()
                + " Field Value: " + value);
        return co;
    }

    private CdrObject setCdrErrorDefault(CdrObject co, String value, DataStructureField fieldConfig) {
        co.setTypeError(CdrObject.TYPE_ERROR_CDR_REJECT);
        co.setError("Validate fail. Switch: " + switchId + ". Default value: " + fieldConfig.getDefaultValue()
                + " pattern: " + fieldConfig.getFieldFormat()
                + " Position: " + fieldConfig.getFieldPos()
                + " DataType: " + fieldConfig.getFieldType().toUpperCase()
                + " FieldName: " + fieldConfig.getFieldName());
        return co;
    }

    public void init(String filename, String workingDir, String pSwitchId, String pHeader,
            String pFieldOutSeperate, String pFieldSeperate, String pFileId) {
        if (filename != null) {
            fileName = filename;
        }
        if (workingDir != null) {
            inputDir = workingDir;
        }
        if (!StringUtils.isEmpty(pSwitchId)) {
            switchId = Long.valueOf(pSwitchId);
        }
        if (!StringUtils.isEmpty(pHeader)) {
            ignoredHeader = Long.valueOf(pHeader);
        }
        if (pFieldOutSeperate != null) {
            fieldOutSeparate = pFieldOutSeperate.replaceAll("\"", "");
        }
        if (pFieldSeperate != null) {
            fieldSeparate = pFieldSeperate.replaceAll("\"", "");
        }
        if (!StringUtils.isEmpty(pFileId)) {
            fileId = Long.valueOf(pFileId);
        }
    }

    //load config from database & param
    private void loadParam(JobsBO currentJob) {
        Switchboard switchboard = GIClient.getInstance().getSwitchboard(switchId + "");
        if (switchboard == null) {
            logger.error(" switchboard is not found: " + switchId);
            return;
        }

        switchType = switchboard.getType();
        country = switchboard.getCountry();
        structureName = switchboard.getStructureIn();

        File inputFolder = new File(inputDir);
//        ratedFolder = inputFolder.getParent() + "/RATED/" + switchId + "/";
        errorFolder = inputFolder.getParent() + "/ERROR/" + switchId + "/";
        File errorDir = new File(errorFolder);
        if (!errorDir.exists()) {
            errorDir.mkdirs();
        }
        inputFileName = inputDir + "/" + fileName;
    }

    public void loadCache(String switchType, String country) throws Exception {
        filterCache = GICache.getInstance(switchType, country, logger);
    }

    private void setCdrSystemProperty(CdrObject co) {
        Date modifiedDate = new Date((new File(inputFileName)).lastModified());
        if (co != null && !co.containsProperty(Constants.FILTER_FIELD_SWITCH_ID)) {
            co.set(Constants.FILTER_FIELD_SWITCH_ID, switchId);
        }
        if (co != null && fileId > 0) {
            co.set(Constants.FILTER_FIELD_FILE_ID, fileId);
        }
        if (co != null && !co.containsProperty(Constants.FILTER_FIELD_MODIFIED_DATE)) {
            co.set(Constants.FILTER_FIELD_MODIFIED_DATE, modifiedDate);
        }
        if (co != null && !co.containsProperty(Constants.FILTER_FIELD_FILE_NAME)) {
            co.set(Constants.FILTER_FIELD_FILE_NAME, fileName);
            co.set(Constants.FILTER_FIELD_DIR, inputDir);
        }
    }

    public void executePreFunction(JobsBO currentJob, Object mapOutput) throws Exception {
        MapConvert mapFilterFunction = GIClient.getInstance()
                .getMapConvert(switchType, country);
        if (mapFilterFunction == null || mapFilterFunction.getPreFilterFunction() == null) {
            return;
        }
        try {
            Class instance = Class.forName(Constants.FILTER_ASPECT_FUNCTION_IMPL_PCKG + mapFilterFunction.getPreFilterFunction());
            if (instance.newInstance() instanceof IFilterAspectFunction) {
                IFilterAspectFunction aspect = (IFilterAspectFunction) instance.newInstance();
                aspect.filterAspectFunction(currentJob, mapOutput);
            }
        } catch (InstantiationException | IllegalAccessException iie) {
            throw new FilterException("Init pre-function fail", FilterException._CONFIG_FAIL);
        } catch (ClassNotFoundException | FilterException ex) {
            logger.error(ex.getMessage(), ex);
            throw new FilterException("Execute pre-function fail", FilterException._FAIL);
        }
    }

    public void executePostFunction(JobsBO currentJob, Object mapOutput) throws FilterException, Exception {
        MapConvert mapFilterFunction = GIClient.getInstance()
                .getMapConvert(switchType, country);
        long start = System.currentTimeMillis();
        if (mapFilterFunction == null || mapFilterFunction.getPostFilterFunction() == null) {
            logger.info(workerName + " has no post function");
            return;
        } else {
            logger.info("Process post function: " + mapFilterFunction.getPostFilterFunction());
        }
        try {
            Class instance = Class.forName(Constants.FILTER_ASPECT_FUNCTION_IMPL_PCKG + mapFilterFunction.getPostFilterFunction());
            if (instance.newInstance() instanceof IFilterAspectFunction) {
                IFilterAspectFunction aspect = (IFilterAspectFunction) instance.newInstance();
                int tryTime = 1;
                while (tryTime <= 3) {
                    try {
                        aspect.filterAspectFunction(currentJob, mapOutput);
                        break;
                    } catch (AerospikeException stae) {
                        logger.info(this.workerName + " try execute post function " + stae.getMessage());
                        tryTime++;
                        if (tryTime == 3) {
                            throw stae;
                        }
                        Thread.sleep(tryTime * 1000);
                    } catch (FilterException ex) {
                        throw ex;
                    }
                }
            }
            logger.info("Finish post function: " + mapFilterFunction.getPostFilterFunction() + ". Time: " + (System.currentTimeMillis() - start));
        } catch (InstantiationException | IllegalAccessException iie) {
            throw new FilterException("Init post-function fail", FilterException._CONFIG_FAIL);
        } catch (AerospikeException stae) {
            logger.error(stae);
            throw new FilterException(stae.getMessage(), FilterException._FAIL);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new FilterException("Execute post-function fail", FilterException._FAIL);
        }
    }

    private void init(JobsBO currentJob, long beginTime) {
        //load cache.
        try {
            loadParam(currentJob);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new FilterException("Load param exception", FilterException._FAIL);
        }
        try {
            loadCache(switchType, country);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new FilterException("Load cache exception", FilterException._FAIL);
        }
        logger.info("load cache time for: " + switchType + ". country: " + country + " time: " + (System.currentTimeMillis() - beginTime));
        //load param
    }

//    private CdrObject cdrPrepare(String cdrRecord) {
//        CdrObject co = createCdrObjectWithDbConfig(cdrRecord, fieldSeparate);
//        totalFilter++;
//        if (co != null) {
//            setCdrSystemProperty(co);
//            return co;
//        } else {
//            return null;
//        }
//    }
    private void processCdrError(CdrObject co) throws Exception {
        totalCdrInvalid++;
        logger.info(co.getError());
        processSaveCdrNotValid(co);
    }

    private void printSuccessLog() {
        logger.info("Total filter: " + totalFilter);
        logger.info("Total cdr invalid: " + totalCdrInvalid);
        logger.info("Total standard fail: " + totalStandardFail);
        logger.info("Total filter success: " + totalFilterSuccess);
    }

    public void filter(JobsBO currentJob) throws Exception {

        long beginTime = System.currentTimeMillis();

        init(currentJob, beginTime);

        logger.info(this.getWorkerName() + " process file: " + fileName + ". switchId: " + switchId);

        BufferedReader bfr = null;
        try {
            try {
                bfr = new BufferedReader(new FileReader(inputFileName));
            } catch (FileNotFoundException ex) {
                logger.error(ex.getMessage(), ex);
                throw new FilterException("Read file exception: " + inputFileName, FilterException._FAIL);
            }

            String strRecord = bfr.readLine();
            long headerTmp = 0;

            executePreFunction(currentJob, revCache);
            while (strRecord != null) {
                headerTmp++;
                if (headerTmp <= ignoredHeader) {
                    strRecord = bfr.readLine();
                    continue;
                }
                //create CdrObject
                if ("".equals(strRecord.trim())) {
                    strRecord = bfr.readLine();
                    continue;
                }
                CdrObject co = createCdrObjectWithDbConfig(strRecord, fieldSeparate);
                totalFilter++;
                if (co == null) {
                    continue;
                }

                if (filterCache.loadRuleExpression(switchType, country) == null) {
                    throw new FilterException("No rule config for switchId " + switchId, FilterException._CONFIG_FAIL);
                }

                Iterator<Long> ruleIterator = filterCache.loadRuleExpression(switchType, country).keySet().iterator();

                while (ruleIterator.hasNext()) {
                    long ruleId = ruleIterator.next();

                    mapEx = filterCache.loadRuleExpression(switchType, country).get(ruleId);

                    lstResult = filterCache.loadResultByRule().get(ruleId);
                    if (co.getTypeError() == CdrObject.TYPE_NOT_ERROR) {
                        //Save
                        Rule rule = filterCache.getAllMapRule(switchType, country).get(ruleId);
                        CdrContainer cdrCon = null;
                        try {
                            cdrCon = filterByRule(co, ruleId, false);
                        } catch (StandardException se) {
                            if (se.getPriority() == StandardException.PRIORITY_REJECT_CDR) {
                                saveRejectCdr(cdrCon, co, se);
                                break;
                            } else if (se.getPriority() == StandardException.PRIORITY_BREAK_FLOW) {
                                processSave(cdrCon);
                                break;
                            } else {
                                throw se;
                            }
                        }
                        processSave(cdrCon);
                        if (rule != null && rule.getAllowableExit() != null && rule.getAllowableExit() == 1L) {
                            if (cdrCon.isRuleFlag()) {
                                break;
                            }
                        }
                    } else if (co.getTypeError() == CdrObject.TYPE_ERROR_CDR_REJECT) {
                        //Save cdr error
                        processCdrError(co);
                    } else {
                        logger.warn("CDR parse type not support");
                    }
                }
                strRecord = bfr.readLine();
            }
            executePostFunction(currentJob, revCache);
            printSuccessLog();
            processSaveTerminate();
            saveFilterLog(beginTime);
        } catch (FilterException | ImportQueueException fe) {
            throw fe;
        } catch (Exception ecx) {
            throw ecx;
        } finally {
            if (bfr != null) {
                bfr.close();
            }
            revCache = null;
        }
    }

    public void processSaveCdrNotValid(CdrObject cdr) throws Exception {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("CDR", cdr.getSourceCdr());
        hashMap.put("ERROR", cdr.getError());
        cdr.setHashmap(hashMap);
        Importer importer;
        if (mapImporter.containsKey("ERROR_" + switchId)) {
            importer = mapImporter.get("ERROR_" + switchId);
        } else {
//            hasError = true;
            List<DataStructureField> lstField = new ArrayList<>();
            DataStructureField cdrField = new DataStructureField();
            cdrField.setFieldName("CDR");
            lstField.add(cdrField);
            DataStructureField cdrError = new DataStructureField();
            cdrError.setFieldName("ERROR");
            lstField.add(cdrError);
            importer = ImporterFactory.getImporterFromDBConfig("ERROR_" + switchId, null, fieldSeparate, lstField, Constants.RESULT_TYPE_FILE, filterCache);
            importer.setGroup("FILE_ERROR");
//            importer.setWriteType(Constants.RESULT_TYPE_FILE);
//            importer.setIsInsert(false);
            importer.setLogger(logger);
            importer.setFileExtension("ERR");
            importer.setFileName(errorFolder + File.separator + fileName);
            importer.setSeqName(null);
            importer.createDataQueue("ERROR_" + threadCode);
            mapImporter.put("ERROR_" + switchId, importer);
            listImporter.add(importer);
        }
        importer.insertRecord("ERROR_" + threadCode, cdr, importer.getFileName(), Result.TYPE_ERROR_CDR_INVALID);
    }

    /**
     * Ghi ket qua theo cac rule cau hinh.
     *
     * @param cdrCon Kieu CdrContainer chua thong tin ve ket qua ghi ra cua CDR
     * @throws java.lang.Exception
     */
    public void processSave(CdrContainer cdrCon) throws Exception {
        if (cdrCon == null || !cdrCon.isRuleFlag()) {
            return;
        }
        List<CdrSaveItem> lstSaveItem = cdrCon.getCdrSaveItem();
        if (lstSaveItem != null && lstSaveItem.size() > 0) {
            for (CdrSaveItem cdrSaveItem : lstSaveItem) {
                CdrObject cdrObject = cdrSaveItem.getCdrObject();
                Result result = cdrSaveItem.getResult();
                try {
                    processResult(result, cdrObject);
                } catch (ImportQueueException ex) {
                    throw ex;
                }
            }
        }
    }

    private void processResult(Result result, CdrObject cdrObject) throws Exception {
        boolean isInsertAero = false, isFile = false, isInsertOracle = false, isErrorCdr = false;
        String fieldInFileName = null;
        String filenameTemplate = null;
        if (result.getResultType() != null) {
            switch (result.getResultType().trim()) {
                case Constants.RESULT_TYPE_AS:
                    isInsertAero = true;
                    break;
                case Constants.RESULT_TYPE_FILE:
                    isFile = true;
//                isInsertAero = true;
                    break;
                case Constants.RESULT_TYPE_ORA:
                    isInsertOracle = true;
//                isInsertAero = true;
                    break;
                case Constants.RESULT_NO_WRITE:
                    break;
            }
        }

        if (result.getTypeError() == Result.TYPE_ERROR_CDR_STANDADIZE_FAIL) {
            isErrorCdr = true;
            if (!isInsertAero && !isInsertOracle) {
                isFile = true;
            }
        }

        if (isFile || isErrorCdr) {
            filenameTemplate = result.getFileNameTemplate();
            if (filenameTemplate != null && filenameTemplate.contains("{CDR:")) {
                String y = filenameTemplate.substring(filenameTemplate.indexOf("{CDR:") + 5);
                fieldInFileName = y.substring(0, y.indexOf("}"));
                filenameTemplate = filenameTemplate.replace("{CDR:", "").replace(fieldInFileName + "}", "");
            }
            //tao thu muc
            if (result.getLocalFolder() != null) {
                File f = new File(result.getLocalFolder());
                if (!f.exists()) {
                    if (!f.mkdirs()) {
                        logger.error("Result: Create local folder failed");
                        throw new FilterException("Result: Create local folder failed", FilterException._CONFIG_FAIL);
                    }
                }
            }
        }

        if (isFile || isInsertAero || isInsertOracle || isErrorCdr) {
            Importer importer = null;
            String insertThreadCode = null;
            if (result.getTypeError() == Result.TYPE_NOT_ERROR) {
                insertThreadCode = threadCode;
                if (mapImporter.containsKey(result.getResultName())) {
                    importer = mapImporter.get(result.getResultName());
                } else {
                    logger.info("Create importer normal: " + result.getResultName());
                    importer = ImporterFactory.getImporterFromDBConfig(result.getResultName(), result, fieldOutSeparate, null, result.getResultType().trim(), filterCache);
                    importer.setGroup("NORMAL");
                    importer.setLogger(logger);
                    if (isFile) {
                        if (fieldInFileName != null) {
                            importer.setIsCdrFieldInFilename(true);
                            importer.setFieldInName(fieldInFileName);
                        }
                        importer.setFileName(result.getLocalFolder() + File.separator + filenameTemplate);
                        importer.setFileExtension(result.getFileExtension());
                        importer.setSeqName(result.getSeqName());
                    }
                    importer.createDataQueue(threadCode);
                    mapImporter.put(result.getResultName(), importer);
                    listImporter.add(importer);
                }
            } else if (result.getTypeError() == Result.TYPE_ERROR_CDR_STANDADIZE_FAIL) {
                insertThreadCode = "FAIL_" + threadCode;
                if (mapImporterStandardError.containsKey(result.getResultName())) {
                    importer = mapImporterStandardError.get(result.getResultName());
                } else {
                    logger.info("Create importer standard fail: " + result.getResultName());
                    importer = ImporterFactory.getImporterFromDBConfig(result.getResultName(), result, fieldSeparate, null, Constants.RESULT_TYPE_FILE, filterCache);
                    importer.setGroup("STANDARD_ERROR");
                    importer.setCacheDate(filterCache);
                    importer.setLogger(logger);
                    if (isFile) {
                        importer.setFileExtension("CDR_INVALID");
                        if (fieldInFileName != null) {
                            importer.setIsCdrFieldInFilename(true);
                            importer.setFieldInName(fieldInFileName);
                        }
                        importer.setFileName(result.getLocalFolder() + File.separator + filenameTemplate);
                        importer.setSeqName(result.getSeqName());
                    }
                    importer.createDataQueue(insertThreadCode);
                    mapImporterStandardError.put(result.getResultName(), importer);
                    listImporter.add(importer);
                }
            } else {
                logger.error("Not support new type of result: " + result.getTypeError() + ". resultName: " + result.getResultName());
                System.exit(1);
            }
            try {
                if (importer != null) {
                    importer.insertRecord(insertThreadCode, cdrObject, importer.getFileName(), result.getTypeError());
                }
            } catch (Exception iex) {
                throw iex;
            }
        }
    }

    public void processSaveTerminate() throws Exception {
        if (listImporter == null) {
            return;
        }
        for (Importer importer : listImporter) {
            switch (importer.getGroup()) {
                case "FILE_ERROR": {
                    ImportQueue queue = importer.terminateInsert("ERROR_" + threadCode);
                    if (queue != null) {
                        listImportQueue.add(queue);
                    }
                    break;
                }
                case "NORMAL": {
//                    logger.info("Terminate import oracle. Code: " + threadCode);
                    ImportQueue queue = importer.terminateInsert(threadCode);
                    listImportQueue.add(queue);
                    break;
                }
                case "STANDARD_ERROR": {
                    ImportQueue queue = importer.terminateInsert("FAIL_" + threadCode);
                    listImportQueue.add(queue);
                    break;
                }
            }
        }
    }

    private BaseLog saveFilterLog(long beginTime) {
        try {
            FileProcessLog fileLog = new FileProcessLog("getfilelog", "|");
            BaseLog baseLog = new BaseLog();
            baseLog.setSourceFile(inputFileName);
            baseLog.setRatedRecord(totalFilter);
            baseLog.setFailRecord(totalStandardFail);
            baseLog.setSuccessRecord(totalFilterSuccess);
            baseLog.setProcessTime(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
            baseLog.setWorkerId(workerName);
            baseLog.setActionType("FILTER");
            baseLog.setDuration(System.currentTimeMillis() - beginTime);
            fileLog.info(baseLog);
            for (ImportQueue queue : listImportQueue) {
                saveFilterLogDetail(queue, baseLog.getLogId(), fileLog);
            }
            return baseLog;
        } catch (NumberFormatException ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    private void saveFilterLogDetail(ImportQueue queue, String logFilterId, FileProcessLog fileLog) {
        try {
            BaseLog baseLog = new BaseLog();
            baseLog.setParentLogId(logFilterId);
            baseLog.setDestinationFile(queue.getFileInsert());
            baseLog.setSuccessRecord(Long.valueOf(queue.getSuccessImported()));
            baseLog.setProcessTime(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
            baseLog.setWorkerId(workerName);
            baseLog.setActionType("FILTER_DETAIL");
            baseLog.setTableName(queue.getTableName());
            fileLog.info(baseLog);
        } catch (NumberFormatException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * Loc theo rule
     *
     * @param cdrObject CdrObject input
     * @param ruleId processing rule
     * @return CdrContainer ket qua tra ve la 1 CdrContainer gom co cdr ghi ra,
     * thong tin ve thu muc, bang du lieu ghi vao ...
     * @throws Exception
     */
    public CdrContainer filterByRule(CdrObject cdrObject, long ruleId, boolean isTest) throws Exception {
        CdrContainer cdrCon = new CdrContainer();
        cdrCon.setCdrObject(cdrObject);
        //Mot rule gom nhieu Exp co quan he AND (&)
        //duyet theo Expression: Exp1 & Exp2 ...
        boolean exFlag = true; //if Exp[i] = false => exFlag = false.
        if (mapEx != null) {
            Iterator<Long> iteExId = mapEx.keySet().iterator();
            while (iteExId.hasNext()) {
                Long expressId = iteExId.next();
                List<SubExpression> lstSubEx = mapEx.get(expressId);
                //Expression gom nhieu SubEx co quan he OR (||)
                //duyet theo SubExpression: SubEx co dang bieu thuc: ve trai ve phai toan tu..
                boolean subExFlag = false; //if SubEx[i] = true => subExFlag = true.
                Iterator<SubExpression> iteSubEx = lstSubEx.iterator();
                while (iteSubEx.hasNext()) {
                    SubExpression subEx = iteSubEx.next();
                    //get left Function
                    FunctionBO leftFunction;
                    if (filterCache.loadFunction() != null) {
                        leftFunction = filterCache.loadFunction().get(subEx.getLeftFunctionId());
                    } else {
                        leftFunction = null;
                    }
                    ExecuteFunction exFunction = new ExecuteFunction();

                    exFunction.setObject(cdrObject);
                    exFunction.setLogger(logger);
                    //xu ly ve trai
                    Object leftResult = null;
                    if (subEx.getLeftField() != null && !cdrObject.containsProperty(subEx.getLeftField().trim())) {
                        throw new FilterException("SubEx: CdrObject doesn't contain property: " + subEx.getLeftField() + ". Expression: " + expressId, FilterException._CONFIG_FAIL);
                    }
                    if (leftFunction != null) {
                        exFunction.setFunction(leftFunction);
//                        if (subEx.getLeftField() != null && subEx.getLeftField().trim().length() > 0) {
                        if (cdrObject.containsProperty(subEx.getLeftField())) {
                            exFunction.setFieldName(subEx.getLeftField());
                        }
                        if (exFunction.getFunction().getIsScript() != null && exFunction.getFunction().getIsScript() == 1) {
                            exFunction.getFunction().setInput(subEx.getLeftField());
                            leftResult = filterCache.executeJavaScriptFuntion(exFunction.getFunction().getFunctionName(), cdrObject, exFunction.getFunction().getInput(), exFunction.getFunction().getParams(), exFunction.getFunction().getFieldParams());
                        } else {
                            //leftResult = exFunction.execute();
                            exFunction.getFunction().setInput(subEx.getLeftField());
                            leftResult = exFunction.execute();
                        }
//                            exFunction = null;
//                        }
                    } else if (subEx.getLeftField() != null && subEx.getLeftField().trim().length() > 0) {
                        if (cdrObject.containsProperty(subEx.getLeftField())) {
                            if (cdrObject.get(subEx.getLeftField()) == null) {
                                leftResult = null;
                            } else {
                                leftResult = cdrObject.get(subEx.getLeftField());//;
                            }
                        } else {
                            throw new FilterException("SubEx: CdrObject doesn't contain property: " + subEx.getLeftField() + ". Expression: " + expressId, FilterException._CONFIG_FAIL);
                        }
                    }
                    if (subEx.getOperator() == null || subEx.getOperator().length() < 1) {
                        logger.error("SubExID: " + subEx.getSubExpressionId() + ": Operator must be not-null");
                        return null;
                    }
                    //kiem tra gia tri constant
                    if (subEx.getIsConstant() == 1) {
                        //constant
                        ObjectProcess oProcess = new ObjectProcess();
                        subExFlag = oProcess.process(leftResult, subEx.getConstant(), subEx.getOperator().trim(), true, logger);
                    } else { //khong phai constant
                        //xu ly ve phai
                        FunctionBO rightFunction;
                        if (filterCache.loadFunction() == null) {
                            rightFunction = null;
                        } else {
                            rightFunction = filterCache.loadFunction().get(subEx.getRightFunctionId());
                        }
                        exFunction = new ExecuteFunction();

                        Object rightResult = null;
                        if (rightFunction != null) {
                            exFunction.setFunction(rightFunction);
                            exFunction.setObject(cdrObject);
                            exFunction.setLogger(logger);
                            if (subEx.getRightField() != null && subEx.getRightField().trim().length() > 0) {
                                if (cdrObject.containsProperty(subEx.getRightField())) {
                                    exFunction.setFieldName(subEx.getRightField());
                                } else {
                                    throw new FilterException("SubEx: CdrObject doesn't contain property: " + subEx.getRightField() + ". Expression: " + expressId, FilterException._CONFIG_FAIL);
                                }
                                if (exFunction.getFunction().getIsScript() != null && exFunction.getFunction().getIsScript() == 1) {
                                    exFunction.getFunction().setInput(subEx.getRightField());
                                    rightResult = filterCache.executeJavaScriptFuntion(exFunction.getFunction().getFunctionName(), cdrObject, exFunction.getFunction().getInput(), exFunction.getFunction().getParams(), exFunction.getFunction().getFieldParams());
                                } else {
                                    exFunction.getFunction().setInput(subEx.getRightField());
                                    rightResult = exFunction.execute();
                                }
//                                exFunction = null;
                            }
                        } else if (cdrObject.containsProperty(subEx.getRightField())) {
                            if (cdrObject.get(subEx.getRightField()) == null) {
                                rightResult = null;
                            } else {
                                rightResult = cdrObject.get(subEx.getRightField());//.toString();
                            }
                        } else {
                            rightResult = null;
                        }
                        ObjectProcess oProcess = new ObjectProcess();
                        subExFlag = oProcess.process(leftResult, rightResult, subEx.getOperator().trim(), false, logger);
                    }
                    if (subExFlag == true) {
                        break;
                    }
                } //ket thuc duyet subEx
                if (subExFlag == false) {
                    exFlag = false;
                    break;
                }
            } //ket thuc duyet Ex
        } //filter ket thuc
        cdrCon.setRuleFlag(exFlag);
        if (exFlag) { //OK. Dieu kien dung, ghi ket qua
            if (!isTest) {
                if (lstResult != null && !lstResult.isEmpty()) {
                    Iterator<Result> iteResult = lstResult.iterator();
                    while (iteResult.hasNext()) {
                        Result result = iteResult.next();
//                        result.setTypeError(Result.TYPE_NOT_ERROR);
                        CdrObject cdrResult = cdrObject;
                        boolean isInsert = false;
                        try {
                            ExecuteFunction ex = new ExecuteFunction();
                            ex.setLogger(logger);
                            List<StandardizeField> lstStandardize = filterCache.loadStandardizeByResult().get(result.getResultId());
                            if (lstStandardize != null) {
                                for (StandardizeField standardizeField : lstStandardize) {
                                    cdrResult = standardizeCdrObject(ex, cdrResult, standardizeField, false);
                                }
                            }
                            totalFilterSuccess++;
                        } catch (StandardException se) {
                            totalStandardFail++;
                            if (se.getPriority() == StandardException.PRIORITY_NORMAL) {
                                insertFailRecord(result, cdrCon, cdrResult, se);
                                isInsert = true;
                            } else if (se.getPriority() == StandardException.PRIORITY_LOW) {
                                //do nothing
                            } else if (se.getPriority() == StandardException.PRIORITY_REJECT_CDR) {
                                throw se;
                            } else if (se.getPriority() == StandardException.PRIORITY_BREAK_FLOW) {
                                insertFailRecord(result, cdrCon, cdrResult, se);
                                isInsert = true;
                                throw se;
                            }
                        } catch (Exception ex) {
                            logger.error("Error while standardize result: " + result.getResultId());
                            totalStandardFail++;
                            logger.error(ex.getMessage(), ex);
                            result.setTypeError(Result.TYPE_ERROR_CDR_STANDADIZE_EXCEPTION);
                            cdrResult.set("ERROR", ex.getMessage());
                            cdrCon.getCdrSaveItem().add(new CdrSaveItem(result.getResultName(), cdrResult, result));
                            isInsert = true;
                        }
                        if (!isInsert) {
                            cdrCon.getCdrSaveItem().add(new CdrSaveItem(result.getResultName(), cdrResult, result));
                        }
                    }
                } else {
                    logger.error("Cant find any result for output for rule: " + ruleId);
                }
            } else {
                processTestResult(cdrCon, cdrObject, ruleId);
            }
        }
        return cdrCon;
    }

    private void processTestResult(CdrContainer cdrCon, CdrObject cdrResult, long ruleId) {
        if (mapResult != null && !mapResult.isEmpty()) {
            Iterator<Long> iteResult = mapResult.keySet().iterator();
            while (iteResult.hasNext()) {
                Long resultId = iteResult.next();
                Result result = new Result();
                result.setResultId(resultId);
                try {
                    ExecuteFunction ex = new ExecuteFunction();
                    ex.setLogger(logger);
                    List<StandardizeField> lstStandardize = mapResult.get(resultId);
                    if (lstStandardize != null) {
                        for (StandardizeField standardizeField : lstStandardize) {
                            cdrResult = standardizeCdrObject(ex, cdrResult, standardizeField, true);
                        }
                    }
                } catch (StandardException se) {
                    logger.error("Test cdr failed");
                    logger.error(se.getMessage(), se);
                    cdrResult.set("ERROR", se.getMessage());
                } catch (Exception ex) {
                    logger.error("Test cdr exception");
                    logger.error(ex.getMessage(), ex);
                    cdrResult.set("ERROR", ex.getMessage());
                }
                cdrCon.getCdrSaveItem().add(new CdrSaveItem("TEST_FROM_WS", cdrResult, result));
            }
        } else {
            logger.error("Cant find any result for output for rule: " + ruleId);
        }
    }

    /**
     * standardize fields in cdrObject
     *
     * @param exFunction
     * @param cdrObject
     * @param standardizeField
     * @return
     * @throws java.lang.Exception
     */
    public CdrObject standardizeCdrObject(ExecuteFunction exFunction, CdrObject cdrObject,
            StandardizeField standardizeField, boolean isTest) throws Exception {
        if (filterCache.loadFunction().get(standardizeField.getFunctionId()) == null) {
            return cdrObject;
        }

        CdrObject returnCdr = cdrObject;

        FunctionBO function = filterCache.loadFunction().get(standardizeField.getFunctionId());

        exFunction.setFunction(function);
        exFunction.setFilterCache(filterCache);
        exFunction.setObject(cdrObject);

        if ((exFunction.getFunction().getScript() != null) && (exFunction.getFunction().getScript().trim().length() > 1) && (exFunction.getFunction().getIsScript() == 1)) {
            Object obj = filterCache.executeJavaScriptFuntion(function.getFunctionName(), cdrObject, null, null, null);
//            if (obj instanceof CdrObject) {
//                returnCdr = (CdrObject) obj;
//            } else {
            returnCdr.set(standardizeField.getStandardizeField(), obj);
//            }
        } else {
            Object obj = exFunction.execute(revCache);
            if (obj instanceof CdrObject) {
                returnCdr = (CdrObject) obj;
            } else {
                returnCdr.set(standardizeField.getStandardizeField(), obj);
            }
        }
        return returnCdr;
    }

    public static void main(String[] args) {
        try {
            BaseFilter filter = new BaseFilter("worker", Logger.getLogger(BaseFilter.class));
            JobsBO job = new JobsBO();
//            job.setJobParam("in98_G_8_000969550_20170802.s|/home/viettq/Downloads/Gora/u02/dar/cdr/ra/voice|2000|0|\"|\"|\"\\|\"|-1");
//            job.setJobField("P_FILENAME|P_WORKING_DIR|P_SWITCH_ID|IGNORE_HEADER|P_FILE_OUT_SEPARATE|P_FIELD_SEPARATE|P_FILE_ID");
            job.setJobParam("in98_G_8_000969550_20170802.s@A@/home/hoangsinh/Downloads/u02/dar/cdr/ra/voice@A@2000@A@0@A@;@A@;@A@-1");
            job.setJobField("P_FILENAME@A@P_WORKING_DIR@A@P_SWITCH_ID@A@IGNORE_HEADER@A@P_FILE_OUT_SEPARATE@A@P_FIELD_SEPARATE@A@P_FILE_ID");
            filter.filter(job);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setHeader(String header) throws Exception {
        try {
            ignoredHeader = Long.valueOf(header);
        } catch (NumberFormatException ex) {
            throw new FilterException("Parse Header fail", FilterException._FAIL);
        }
    }

    private void insertFailRecord(Result result, CdrContainer cdrCon, CdrObject cdrResult, Exception se) {
        Result resultFail = (Result) result.clone();
        resultFail.setTypeError(Result.TYPE_ERROR_CDR_STANDADIZE_FAIL);
        cdrResult.set("ERROR", se.getMessage());
        cdrCon.getCdrSaveItem().add(new CdrSaveItem(resultFail.getResultName(), cdrResult, resultFail));
    }

    private void saveRejectCdr(CdrContainer cdrCon, CdrObject co, Exception se) throws Exception {
        logger.error(se.getMessage(), se);
        cdrCon = new CdrContainer();
        cdrCon.setRuleFlag(false);
        co.setError(se.getMessage());
        processCdrError(co);
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public Map<Long, List<SubExpression>> getMapEx() {
        return mapEx;
    }

    public void setMapEx(Map<Long, List<SubExpression>> mapEx) {
        this.mapEx = mapEx;
    }

    public Map<Long, List<StandardizeField>> getMapResult() {
        return mapResult;
    }

    public void setMapResult(Map<Long, List<StandardizeField>> mapResult) {
        this.mapResult = mapResult;
    }
}
