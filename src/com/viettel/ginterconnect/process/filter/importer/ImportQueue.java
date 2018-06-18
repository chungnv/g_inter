/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.filter.importer;

import com.viettel.ginterconnect.process.exception.ImportQueueException;
import com.viettel.ginterconnect.process.exception.FilterException;
import com.aerospike.client.Bin;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.bean.DataStructureField;
import com.viettel.ginterconnect.process.filter.GICache;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIClient;
import com.viettel.ginterconnect.util.ParseFileNameStandard;
import com.viettel.ginterconnect.util.ThreadUtil;
import com.viettel.library.dataobject.DataObjectException;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.commons.io.FileUtils;

public class ImportQueue extends Thread {

    public static final int capacity = 1000000;
    private String threadCode = null;
    private String tableName = null;
    private String tableErrorName = null;
    private BlockingQueue<CdrObject> queue;
    private TextOutputFile fileName = null;
    private String ratedFileName = null;
    private List<DataStructureField> importerFields;
    private String filenameTemplate;
    private String fileInsert;
    private int importedCount;
    private int successImported;
    private int errorImported;
    private int duplicateImported;
    private Importer importer;
    private ImporterConnectionPool connectionPool;
    private int BatchSize;
    private boolean isImportSuccess = true;
    private boolean isOnlyFile = false;
    private String fieldOutSeparate = ";";
    private boolean isCdrFieldInFilename = false;
    private HashMap<String, TextOutputFile> mapFilenameField = null;
    private HashMap<String, InsertLog> mapFilterLogDetail = null;
    private InsertLog inserLog = new InsertLog();
    private boolean insertException = false;
    private Logger logger;
    private GICache cache;

    public ImportQueue(Importer importer, String threadCode, String tableName, String tableErrorName, String fieldOutSeparate, String fileName) {
        this.fieldOutSeparate = fieldOutSeparate;
        this.importer = importer;
        this.connectionPool = importer.getConnectionPool();
        this.BatchSize = importer.getBatchSize();
        this.threadCode = threadCode;
        this.tableName = tableName;
        this.tableErrorName = tableErrorName;
        this.filenameTemplate = fileName;
        this.queue = new ArrayBlockingQueue<>(capacity);
        this.setName(tableName + "_" + threadCode);
    }

    public void insertRecordOracle(CdrObject obj, List<DataStructureField> importerField) throws ImportQueueException {
        boolean isPut = false;
        importerFields = importerField;
        while (!isPut) {
            try {
                queue.put(obj);
                isPut = true;
                if (queue.size() >= BatchSize) {
                    isNewInsert = true;
                    synchronized (insertEvent) {
                        insertEvent.notifyAll();
                    }
                }
                if (insertException) {
                    insertException = false;
                    throw new ImportQueueException("exception while import");
                }
                break;
            } catch (InterruptedException ex) {
                logger.info("Import queue interrupted");
//                ex.printStackTrace();
            }
        }
    }

    public void insertRecordAerospike(CdrObject cdrBO, List<DataStructureField> importerField, String databaseName) throws ImportQueueException {
//        Key key = null;
        Object keyString = null;
        try {
            List<Bin> lst = new ArrayList<>();
            for (int i = 0; i < importerField.size(); i++) {
                if (!cdrBO.containsProperty(importerField.get(i).getFieldName())) {
                    continue;
                }
                if ("PK".equals(importerField.get(i).getFieldName().trim())) {
                    keyString = cdrBO.get("PK");
                    continue;
                }
                Object object = cdrBO.get(importerField.get(i).getFieldName());
                String fieldName = importerField.get(i).getFieldName();
                if (fieldName.length() > 14) {
                    fieldName = fieldName.substring(0, 13);
                }

                if (object instanceof Date) {
                    SimpleDateFormat sdf = new SimpleDateFormat(importerField.get(i).getFieldFormat());
                    lst.add(new Bin(fieldName, sdf.format((Date) object)));
                } else if (object instanceof Long) {
                    lst.add(new Bin(fieldName, object.toString().trim()));
                } else if (object instanceof Integer) {
                    lst.add(new Bin(fieldName, object.toString().trim()));
                } else if (object instanceof Double) {
                    lst.add(new Bin(fieldName, object.toString().trim()));
                } else {
                    lst.add(new Bin(fieldName, object != null ? object.toString().trim() : ""));
                }
            }
            if (cdrBO.getTypeError() == CdrObject.TYPE_ERROR_CDR_STANDADIZE_FAIL) {
                String schema = tableErrorName.split("\\.")[0];
                String tab = tableErrorName.split("\\.")[1];
                if (keyString == null) {
                    GIClient.getInstance(databaseName).insertToSet(schema, tab, lst.toArray(new Bin[lst.size()]));
                } else {
                    GIClient.getInstance(databaseName).insertToSet(schema, tab, keyString.toString(), lst.toArray(new Bin[lst.size()]));
                }
            } else if (cdrBO.getTypeError() == CdrObject.TYPE_NOT_ERROR) {
                String schema = tableName.split("\\.")[0];
                String tab = tableName.split("\\.")[1];
                if (keyString == null) {
                    GIClient.getInstance(databaseName).insertToSet(schema, tab, lst.toArray(new Bin[lst.size()]));
                } else {
                    GIClient.getInstance(databaseName).insertToSet(schema, tab, keyString.toString(), lst.toArray(new Bin[lst.size()]));
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
//            throw ex;
        }
    }

    public void fileRecordConfigDB(CdrObject obj, String fileName1, List<DataStructureField> importerFields) throws Exception {
        try {
            //if (ratedFileName == null || ratedFileName != null && !ratedFileName.equals(fileName1)) {
            if (isCdrFieldInFilename) {
                File fileTest;
                if (mapFilenameField == null) {
                    mapFilterLogDetail = new HashMap<>();
                    mapFilenameField = new HashMap<>();
                }
                Object cdrDateField = obj.get(importer.getFieldInName()) == null ? new Date() : obj.get(importer.getFieldInName());
                String field = getStrDateFromPattern((Date) cdrDateField, filenameTemplate);
                if (mapFilenameField.containsKey(field)) {
                    fileName = mapFilenameField.get(field);
                    inserLog = mapFilterLogDetail.get(field);
                } else {
                    if (importer.getSeqName() != null) {
                        Long seq = getSequence(importer.getSeqName());
                        if (seq == null) {
                            throw new FilterException("Get sequence exception");
                        }
                        ratedFileName = ParseFileNameStandard.parse(filenameTemplate + Constants.FILENAME_TMP, seq, (Date) cdrDateField);
                        inserLog = new InsertLog();
                        inserLog.setDuplicate(0);
                        inserLog.setError(0);
                        inserLog.setImported(0);
                        inserLog.setSuccess(0);
                        fileTest = new File(ParseFileNameStandard.parse(filenameTemplate + this.importer.getFileExtension(), seq, (Date) cdrDateField));
                        if (fileTest.exists()) {
                            try {
                                fileTest.delete();
                                fileTest = new File(ParseFileNameStandard.parse(filenameTemplate + Constants.FILENAME_TMP, seq, (Date) cdrDateField));
                                if (fileTest.exists()) {
                                    fileTest.delete();
                                }
                            } catch (Exception ex) {
                                logger.error(ex.getMessage(), ex);
                            }
                        }
                    } else if (obj.getTypeError() == CdrObject.TYPE_ERROR_CDR_INVALID) {
                        //file error
                        ratedFileName = fileName1;
                        fileTest = new File(fileName1);
                        if (fileTest.exists()) {
                            try {
                                fileTest.delete();
                            } catch (Exception ex) {
                                logger.error(ex.getMessage(), ex);
                            }
                        }
                    } else {
                        throw new FilterException("Get sequence exception");
                    }
                    this.importerFields = importerFields;
                    try {
                        fileName = new TextOutputFile(ratedFileName, fieldOutSeparate);
                    } catch (IOException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                    mapFilenameField.put(field, fileName);
                    mapFilterLogDetail.put(field, inserLog);
                }
                try {
                    ImportUtils.addRecordToTextConfigDB(fileName, obj, importerFields, logger);
                    if (isOnlyFile) {
                        importedCount = inserLog.getImported() + 1;
                        successImported = inserLog.getSuccess() + 1;
                        inserLog.setImported(importedCount);
                        inserLog.setSuccess(successImported);
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    if (isOnlyFile) {
//                        errorImported = errorImported + 1;
                        inserLog.setError(inserLog.getError() + 1);
                    }
                    //                throw ex;
                    throw new FilterException("have error in save record to file ", FilterException.PRIORITY_NORMAL);
                }
            } else {
                if (ratedFileName == null || "".equals(ratedFileName)) {
                    File fileTest;
                    if (importer.getSeqName() != null) {
                        Long seq = getSequence(importer.getSeqName());
                        if (seq == null) {
                            throw new FilterException("Get sequence exception");
                        }
                        ratedFileName = ParseFileNameStandard.parse(filenameTemplate + Constants.FILENAME_TMP, seq);
                        fileTest = new File(ParseFileNameStandard.parse(filenameTemplate + this.importer.getFileExtension(), seq));
                        if (fileTest.exists()) {
                            try {
                                fileTest.delete();
                                fileTest = new File(ParseFileNameStandard.parse(filenameTemplate + Constants.FILENAME_TMP, seq));
                                if (fileTest.exists()) {
                                    fileTest.delete();
                                }
                            } catch (Exception ex) {
                                logger.error(ex.getMessage(), ex);
                            }
                        }
                    } else if (obj.getTypeError() == CdrObject.TYPE_ERROR_CDR_INVALID
                            || obj.getTypeError() == CdrObject.TYPE_ERROR_CDR_STANDADIZE_FAIL) {
                        //file error
                        ratedFileName = fileName1 + Constants.FILENAME_TMP;
                        fileTest = new File(fileName1 + this.importer.getFileExtension());
                        if (fileTest.exists()) {
                            try {
                                fileTest.delete();
                            } catch (Exception ex) {
                                logger.error(ex.getMessage(), ex);
                            }
                        }
                    } else {
                        throw new FilterException("Get sequence exception");
                    }
                    this.importerFields = importerFields;
                    try {
                        fileName = new TextOutputFile(ratedFileName, fieldOutSeparate);
                    } catch (IOException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
                try {
                    ImportUtils.addRecordToTextConfigDB(fileName, obj, importerFields, logger);
                    if (isOnlyFile) {
                        importedCount = importedCount + 1;
                        successImported = successImported + 1;
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    if (isOnlyFile) {
                        errorImported = errorImported + 1;
                    }
                    //                throw ex;
                    throw new FilterException("have error in save record to file", FilterException.PRIORITY_NORMAL);
                }
            }
        } catch (FilterException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private boolean isTerminateWait = false;
    private boolean isThreadWait = false;

    public boolean terminateInsert() {
        isTerminate = true;
        synchronized (insertEvent) {
            insertEvent.notifyAll();
        }
        while (isTerminate) {
            synchronized (insertEvent) {
                try {
                    if (isTerminate) {
//                        isTerminateWait = true;
//                        if (changeStatus(1)) {
                        insertEvent.wait();
//                        } else {
//                            insertEvent.notifyAll();
//                        }
//                        isTerminateWait = false;
                    }
                } catch (InterruptedException ex) {
                    ThreadUtil.delay(50L);
                }
            }
        }
        return isImportSuccess;
    }

    public void terminateFile() {
        if (isCdrFieldInFilename) {
            Iterator<String> iteDate = mapFilenameField.keySet().iterator();
            while (iteDate.hasNext()) {
                String date = iteDate.next();
                fileName = mapFilenameField.get(date);
                inserLog = mapFilterLogDetail.get(date);
                if (fileName == null) {
                    logger.info("file null");
                } else {
                    ratedFileName = fileName.getFilename();
                    File f = new File(ratedFileName);
                    logger.info("Rated file: " + ratedFileName);
                    if (f.exists()) {
                        try {
                            FileUtils.moveFile(f, new File(ratedFileName.substring(0, ratedFileName.indexOf(Constants.FILENAME_TMP)) + "." + this.importer.getFileExtension()));
                            inserLog.setFileName(ratedFileName.substring(0, ratedFileName.indexOf(Constants.FILENAME_TMP)) + "." + this.importer.getFileExtension());
                            logger.info("Rename success");
                        } catch (IOException ex) {
                            logger.error(ex.getMessage(), ex);
                            inserLog.setFileName(ratedFileName);
                            logger.info("rename failed");
                        }
                    }
                    fileName.close();
                    fileName = null;
                    ratedFileName = null;
                }
            }
            mapFilenameField = null;
        } else {
            if (fileName == null) {
                logger.info("file null");
            }
            if (fileName != null) {
                File f = new File(ratedFileName);
                logger.info("Rated file: " + ratedFileName);
                if (f.exists()) {
                    try {
                        FileUtils.moveFile(f, new File(ratedFileName.substring(0, ratedFileName.indexOf(Constants.FILENAME_TMP)) + "." + this.importer.getFileExtension()));
                        this.setFileInsert(ratedFileName.substring(0, ratedFileName.indexOf(Constants.FILENAME_TMP)) + "." + this.importer.getFileExtension());
                        logger.info("Rename success");
                    } catch (IOException ex) {
                        logger.error(ex.getMessage(), ex);
                        this.setFileInsert(ratedFileName);
                        logger.info("rename failed");
                    }
                }
                fileName.close();
                fileName = null;
                ratedFileName = null;
            }
        }
    }

    private boolean isNewInsert = false;
    private boolean isTerminate = false;
    private final Object insertEvent = new Object();

    private synchronized boolean changeStatus(int type) throws InterruptedException {
        if (type == 1) {//goi tu threadngoai
            if (isThreadWait) {//import dang wait
                return (isTerminateWait = false); //gan bang false, va tra ve ko thay doi duoc
            } else {
                return (isTerminateWait = true); //gan bang true, thay doi duoc --> sang wait
            }
        }
        if (type == 2) {//goi tu thread import
            if (isTerminateWait) {//thread dang wait
                return (isThreadWait = false); //gan bang false, tra ve ko thay doi duoc, chay tiep
            } else {
                return (isThreadWait = true);//gan bang true, thay doi duoc --> sang wait
            }
        }
        return false;
    }

    @Override
    public void run() {
        while (true) {
            while (!isNewInsert && !isTerminate) {
                synchronized (insertEvent) {
                    try {
                        if (!isTerminate) {
//                            isThreadWait = true;
//                            if (changeStatus(2)) {
                                insertEvent.wait();             //wait
//                            } else {
//                                isTerminate = true;
//                                insertEvent.notifyAll(); //
//                            }
//                            isThreadWait = false;
                        }
                    } catch (InterruptedException ex) {
                        ThreadUtil.delay(50L);
                    }

                }
            }

            //neu nhu goi cho viec ket thuc
            if (isTerminate) {
                try {
                    processImportQueue(true);
                    isNewInsert = false;
                    isTerminate = false;
                    synchronized (insertEvent) {
                        insertEvent.notifyAll(); //
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                    insertException = true;
                }
            }
            if (isNewInsert) {
                try {
                    isNewInsert = false;
                    processImportQueue(false);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                    insertException = true;
                }
            }
        }
    }

    protected void processImportQueue(boolean isTerminate) throws Exception {
        try {
            if (isImportSuccess) {
                //get connection to import
                ImporterConnection importConnection = connectionPool.getConnection();
                Connection conn = importConnection.getConnection();
                int[] result = null;

                conn.setAutoCommit(false);
                PreparedStatement stmt = importConnection.getInsertStmt();
                //queue
                CdrObject basic;
                //Bien tang dan de tinh so luong cac Record duoc addBatch
                int count = 0;
                boolean isInsert = false;
                while (queue.size() > 0 && (basic = queue.poll()) != null) {
                    count++;
                    importer.setParameterConfigDB(stmt, basic);
                    stmt.addBatch();
                    if (count >= BatchSize) {     //BatchSize: so luong ban ghi import 1 lan.
                        //Neu nhu da addBatch >= batchSize thi insert
                        result = stmt.executeBatch();
                        conn.commit();
                        stmt.clearBatch();
                        isInsert = true;
                        //Cap nhat cac gia tri da duoc insert
                        int duplicate = 0, error = 0;

                        if (importer.isLogInsert()) {
                            InsertLog log = checkDuplicateCount(importConnection);
                            duplicate = log.getDuplicate();
                            error += log.getError();
                            successImported = successImported + result.length - duplicate - log.getError();
                        } else {
                            successImported = successImported + result.length;
                        }
                        importedCount = importedCount + result.length;
                        duplicateImported = duplicateImported + duplicate;
                        errorImported = errorImported + error;
                    }

                    if (isInsert) //Neu da insert
                    {
                        //Kiem tra xem co import tiep hay khong
                        if (queue.size() >= BatchSize) {
                            //Neu nhu queue van con size >= batchSize
                            //reset count and continue import
                            count = 0;
                            isInsert = false;
                        } else if (queue.size() >= 0 && isTerminate) {
                            //Neu nhu so luong trong queue >= 0
                            //va day la lan insert cuoi
                            //reset count and continue import
                            count = 0;
                            isInsert = false;
                        } else {
                            //Tam thoi dung import queue nay doi den luc du batchSize hoac goi TerminateInsert
                            break;
                        }
                    }
                }

                if (count > 0) {
                    //Neu nhu la terminateInsert to commit cac ban ghi con lai vua duoc addBatch,
                    result = stmt.executeBatch();
                    conn.commit();
                    stmt.clearBatch();
                    isInsert = true;

                    //Cap nhat cac gia tri da duoc insert
                    int duplicate = 0, error = 0;

                    if (importer.isLogInsert()) {
                        InsertLog log = checkDuplicateCount(importConnection);
                        duplicate = log.getDuplicate();
                        error += log.getError();
                        successImported = successImported + result.length - duplicate - log.getError();
                    } else {
                        successImported = successImported + result.length;
                    }
                    importedCount = importedCount + result.length;
                    duplicateImported = duplicateImported + duplicate;
                    errorImported = errorImported + error;
                }

                //realse resource
                connectionPool.releaseConnection(importConnection);
            } else {
                this.queue.clear();
            }
        } catch (SQLException | DataObjectException ex) {
            isImportSuccess = false;
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    // GET DUPLICATE COUNT
    public InsertLog checkDuplicateCount(ImporterConnection connection) {
        int dup = 0, err = 0;
        try {
            //check duplicate
            ResultSet result = connection.getDuplicateStmt().executeQuery();

            if (result.next()) {
                dup = result.getInt("CNT");
            }
            result.close();

            //check error
            result = connection.getErrorStmt().executeQuery();
            if (result.next()) {
                err = result.getInt("CNT");
            }
            result.close();
            PreparedStatement truncateStatement = connection.getConnection().prepareStatement(importer.getTruncateQuery());
            truncateStatement.executeUpdate();
            connection.getConnection().commit();
            truncateStatement.close();
            truncateStatement = null;
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new InsertLog(dup, err);
    }

    private String getStrDateFromPattern(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        String strPattern = "";
        if (pattern.contains("{YEAR")) {
            strPattern += "yyyy";
        }
        if (pattern.contains("{MON")) {
            strPattern += "MM";
        }
        if (pattern.contains("{DAY")) {
            strPattern += "dd";
        }
        if (pattern.contains("{HOUR")) {
            strPattern += "HH";
        }
        if (pattern.contains("{MIN")) {
            strPattern += "mm";
        }
        if (pattern.contains("{SEC")) {
            strPattern += "ss";
        }
        return new SimpleDateFormat(strPattern).format(date);
    }

    //public ImportQueue(String threadCode, List<FilterField> listImportFields) {
    public ImportQueue(String threadCode) {
        this.threadCode = threadCode;
        //this.listImportFields = listImportFields;
    }

    public int getDuplicateImported() {
        return duplicateImported;
    }

    public void setDuplicateImported(int duplicateImported) {
        this.duplicateImported = duplicateImported;
    }

    public int getErrorImported() {
        return errorImported;
    }

    public void setErrorImported(int errorImported) {
        this.errorImported = errorImported;
    }

    public int getImportedCount() {
        return importedCount;
    }

    public void setImportedCount(int importedCount) {
        this.importedCount = importedCount;
    }

    public BlockingQueue<CdrObject> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<CdrObject> queue) {
        this.queue = queue;
    }

    public int getSuccessImported() {
        return successImported;
    }

    public void setSuccessImported(int successImported) {
        this.successImported = successImported;
    }

    public String getThreadCode() {
        return threadCode;
    }

    public void setThreadCode(String threadCode) {
        this.threadCode = threadCode;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isIsImportSuccess() {
        return isImportSuccess;
    }

    public void setIsImportSuccess(boolean isImportSuccess) {
        this.isImportSuccess = isImportSuccess;
    }

    public boolean isIsOnlyFile() {
        return isOnlyFile;
    }

    public void setIsOnlyFile(boolean isOnlyFile) {
        this.isOnlyFile = isOnlyFile;
    }

    public String getFileInsert() {
        return fileInsert;
    }

    public void setFileInsert(String fileInsert) {
        this.fileInsert = fileInsert;
    }

    public void refreshQueue() {
        if (this.queue != null) {
            this.queue.clear();
        } else {
            this.queue = new ArrayBlockingQueue<>(capacity);
        }
    }

    public Long getSequence(String seqName) throws Exception {
        return Client.getSequenceInstance().getSequence(seqName);
    }

    public boolean isIsCdrFieldInFilename() {
        return isCdrFieldInFilename;
    }

    public void setIsCdrFieldInFilename(boolean isCdrFieldInFilename) {
        this.isCdrFieldInFilename = isCdrFieldInFilename;
    }

    public HashMap<String, InsertLog> getLstFilterLogDetail() {
        return mapFilterLogDetail;
    }

    public void setLstFilterLogDetail(HashMap<String, InsertLog> lstFilterLogDetail) {
        this.mapFilterLogDetail = lstFilterLogDetail;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public GICache getCache() {
        return cache;
    }

    public void setCache(GICache cache) {
        this.cache = cache;
    }
}
