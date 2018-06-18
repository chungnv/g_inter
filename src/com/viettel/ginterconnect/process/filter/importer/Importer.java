/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.importer;

import com.aerospike.client.query.Filter;
import com.viettel.ginterconnect.process.bean.*;
import com.viettel.ginterconnect.process.filter.GICache;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIClient;
import com.viettel.ginterconnect.process.exception.ImportQueueException;
import com.viettel.library.dataobject.DataObjectException;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import org.hibernate.tool.hbm2x.StringUtils;

/**
 * @author
 */
public class Importer implements BaseImporter {

    // LOAD CACHE WHEN THIS PROGRAM INIT
    private String dataBaseName;
//    private String threadCode;
    private String tableName;
    private String tableErrorName;
    private String fieldOutSeparate;
    private String duplicateTable;
    private boolean logInsert;
    private String insertQuery = null;
    private String duplicateQuery = null;
    private String errorQuery = null;
    private String truncateQuery = null;
    // PARAMETER
    private String fileName;
    private int BatchSize = 1000;
//    private boolean isInsert = true;
//    private boolean isFile = true;
    private String fileExtension = "txt";
    private List<DataStructureField> importFields = new ArrayList<>();
    protected Map<String, ImportQueue> mapQueue = new HashMap<>();
    //CONNECTION POOL
    private ImporterConnectionPool connectionPool = null;
    private DatabaseSid databaseImport = null;
    private String seqName;
    private boolean isCdrFieldInFilename = false;
    private String fieldInName;
    private Logger logger;
    private GICache cacheDate;
    private String group;
    private String writeType;
    private String importName;

    public ImportQueue getDataQueue(String threadCode, String fieldOutSeparate) {
//        this.threadCode = threadCode;
        if (mapQueue.containsKey(threadCode)) {
            return mapQueue.get(threadCode);
        } else {
            ImportQueue queue = new ImportQueue(this, threadCode, this.getTableName(), this.getTableErrorName(), fieldOutSeparate, this.fileName);
            mapQueue.put(threadCode, queue);
            return queue;
        }
    }

    //preprocess importer
    @Override
    public boolean createDataQueue(String threadCode) {

        ImportQueue queue;
        if (mapQueue.containsKey(threadCode)) {
            queue = mapQueue.get(threadCode);
        } else {
            queue = new ImportQueue(this, threadCode, this.getTableName(), this.getTableErrorName(), fieldOutSeparate, this.fileName);
            queue.setLogger(logger);
//            queue.setCache(cacheDate);
            queue.setIsCdrFieldInFilename(isCdrFieldInFilename);
            mapQueue.put(threadCode, queue);
            if (Constants.RESULT_TYPE_ORA.equals(this.getWriteType())) {
                queue.start();
            }
        }
        //reset all env variables
        queue.setIsImportSuccess(true);
        queue.refreshQueue();

        queue.setDuplicateImported(0);
        queue.setErrorImported(0);
        queue.setImportedCount(0);
        queue.setSuccessImported(0);

        return true;
    }

    public void loadDBConfig(Result result) throws Exception {
        //load field importer
        loadFieldImporter(result);
        //load DB
        loadDbImporter(result);
    }

    public void loadFieldImporter(Result result) throws ImportQueueException {
        List<DataStructureField> lst = new ArrayList<>();
        if (result.getTypeError() == Result.TYPE_NOT_ERROR) {
            lst = cacheDate.loadImportField(result.getDataStructureName());

        } else if (result.getTypeError() == Result.TYPE_ERROR_CDR_STANDADIZE_FAIL) {
            lst = cacheDate.loadImportField(result.getDataStructureName());
        }

        if (lst == null || lst.isEmpty()) {
            throw new ImportQueueException("Result don't have any config field");
        }

        for (DataStructureField dsf : lst) {
            if (dsf.getStructureName() != null && dsf.getStructureName().toUpperCase().equals(result.getDataStructureName().toUpperCase())) {
                importFields.add(dsf);
            }
        }
        Collections.sort(importFields, new Comparator<DataStructureField>() {
            @Override
            public int compare(DataStructureField o1, DataStructureField o2) {
                return o1.getFieldPos().compareTo(o2.getFieldPos());
            }
        });
        if (!(result.getTypeError() == Result.TYPE_NOT_ERROR)) {
            DataStructureField cdrError = new DataStructureField();
            cdrError.setFieldName("ERROR");
            importFields.add(cdrError);
        }
    }

    public void loadDbImporter(Result result) throws Exception {
//        List<ImportTableBO> lst = new ArrayList<>();
        if (result.getImportTableId() == null) {
            return;
        }
        List<ImportTableBO> allImportTable = GIClient.getInstance().getRecordByFilter(ImportTableBO.class, Filter.equal("ID", result.getImportTableId()));

        if (allImportTable != null && allImportTable.size() > 0) {
            for (ImportTableBO itb : allImportTable) {
//                if (itb.getImportTableId() != null && itb.getImportTableId() == result.getImportTableId()) {
                this.dataBaseName = itb.getDatabaseName();
                this.tableName = itb.getTableName();
                this.tableErrorName = itb.getTableErrorName();
                if (Constants.RESULT_TYPE_AS.equals(this.getWriteType())) {
                    GIClient.getInstance(itb);
                }
                if (Constants.RESULT_TYPE_ORA.equals(this.getWriteType())) {
                    if (!StringUtils.isEmpty(itb.getLogInsert())) {
                        logInsert = true;
                    }
                    int maxCon = itb.getMaxConnection() == null ? 1 : itb.getMaxConnection();
                    this.BatchSize = itb.getBatchSize() == null ? 1000 : itb.getBatchSize();
                    this.setDuplicateTable(itb.getLogInsertTable());
                    this.databaseImport = new DatabaseSid(itb.getUrl(), itb.getUserName(), itb.getPassword());
                    insertQuery = createBatchInsertQueryConfigDB(importFields);
                    duplicateQuery = "SELECT count(*) CNT FROM " + this.getDuplicateTable() + " where ORA_ERR_NUMBER$ = " + 1;
                    errorQuery = "SELECT count(*) CNT FROM " + this.getDuplicateTable() + " where ORA_ERR_NUMBER$ != " + 1;
                    truncateQuery = "truncate table " + getDuplicateTable();
                    //create connection pool
                    connectionPool = new ImporterConnectionPool(this, maxCon);
                    //start connection pool thread
                    connectionPool.start();

                }
                break;
//                }
            }
        }

    }

    //INSERT BATCH RECORD
    @Override
    public boolean insertRecord(String threadCode, CdrObject recordBO, String fileName, int resultType) throws Exception {

        // Tim Insert Queue
        //Neu ko tim duoc thi khoi tao insert queue -> cho vao map cac insert queue
        // Dat Record vao queue vua tim duoc hoac vua tao ra
        // Kiem tra queue neu ma > batchSize (1000) tien hanh import.
        // Sau khi import tien hanh cong cac gia tri cua import duoc vao insert queue.
        // Neu co Exception thi tra false
        // Neu ko kiem tra cac thuoc tinh cua import. (duplicate + fail + success = batchSize)
        // Neu dung thi tra true
        // Sai tra false
        try {
            ImportQueue importQueue = getDataQueue(threadCode, fieldOutSeparate);
            if (Constants.RESULT_TYPE_AS.equals(this.getWriteType())) {
                importQueue.insertRecordAerospike(recordBO, importFields, dataBaseName);
            }
            if (Constants.RESULT_TYPE_ORA.equals(this.getWriteType())) {
                importQueue.insertRecordOracle(recordBO, importFields);
            }
            if (Constants.RESULT_TYPE_FILE.equals(this.getWriteType())) {
//                if (!isInsert) {
                importQueue.setIsOnlyFile(true);
//                }
//                importQueue.fileRecord(recordBO, fileName, importerColumns);
                if (resultType == Result.TYPE_NOT_ERROR) {
                    importQueue.fileRecordConfigDB(recordBO, fileName, importFields);
                } else if (resultType == Result.TYPE_ERROR_CDR_INVALID
                        || resultType == Result.TYPE_ERROR_CDR_STANDADIZE_FAIL) {
//                    List<DataStructureField> lstImportFieldError = new ArrayList<>();
//                    DataStructureField fieldCdr = new DataStructureField();
//                    fieldCdr.setFieldName("CDR_SRC");
//                    fieldCdr.setFieldPos(0);
//                    fieldCdr.setFieldType("STRING");
//                    lstImportFieldError.add(fieldCdr);
//                    recordBO.set("CDR_SRC", recordBO.getSourceCdr());
//
//                    DataStructureField fieldError = new DataStructureField();
//                    fieldError.setFieldName("ERROR");
//                    fieldError.setFieldPos(1);
//                    fieldError.setFieldType("STRING");
//                    lstImportFieldError.add(fieldError);
//                    importQueue.setLogger(logger);
//                    importQueue.fileRecordConfigDB(recordBO, fileName, lstImportFieldError);
                    importQueue.fileRecordConfigDB(recordBO, fileName, importFields);
                }
            }
        } catch (ImportQueueException iex) {
            logger.error(iex.getMessage(), iex);
            throw iex;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    @Override
    public ImportQueue terminateInsert(String threadCode) {
        // Khi ma thread goi phan nay co nghia la thread ket thuc nghiep vu

        //clear queue. (Chac chan queue la rong)
        try {
            ImportQueue importQueue = getDataQueue(threadCode, fieldOutSeparate);
            importQueue.setLogger(logger);
            if (Constants.RESULT_TYPE_ORA.equals(this.getWriteType())) {
                //import all record in Queue
                importQueue.terminateInsert();
//                logger.info("Terminate import oracle success");
//                importQueue.stop();
            }
            if (Constants.RESULT_TYPE_FILE.equals(this.getWriteType())) {
                importQueue.terminateFile();
                logger.info("Terminate file success");
            }
            return importQueue;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public void setParameterConfigDB(PreparedStatement query, CdrObject cdrObject) throws DataObjectException {
        if (cdrObject == null) {
            return;
        }
        try {
            for (int i = 0; i < importFields.size(); i++) {
                if (!cdrObject.containsProperty(importFields.get(i).getFieldName())) {
                    query.setNull(i + 1, java.sql.Types.VARCHAR);
                    continue;
                }
                Object object = cdrObject.get(importFields.get(i).getFieldName());
                if (object == null) {
                    query.setNull(i + 1, java.sql.Types.VARCHAR);
                } else if (object instanceof String) {
                    query.setString(i + 1, object.toString());
                } else if (object instanceof Long) {
                    query.setLong(i + 1, ((Long) object).longValue());
                } else if (object instanceof Integer) {
                    query.setInt(i + 1, ((Integer) object).intValue());
                } else if (object instanceof Double) {
                    query.setDouble(i + 1, ((Double) object).doubleValue());
                } else if (object instanceof Date) {
                    query.setTimestamp(i + 1, new java.sql.Timestamp(((Date) object).getTime()));
                }
            }
        } catch (SQLException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    // CREATE BATCH INSERT QUERY
    public String createBatchInsertQuery(List<String> importerColumns) throws DataObjectException {
        String duplicateLog = "";
        if (logInsert) {
            duplicateLog = "LOG ERRORS INTO " + this.getDuplicateTable() + " REJECT LIMIT UNLIMITED";
        }
        String sqlQuery = "INSERT /*+ APPEND */ INTO " + this.getTableName() + " ( ";
        String sqlValue = ") VALUES ( ";
        int count = 0;
        for (int i = 0; i < importerColumns.size(); i++) {
            sqlQuery += importerColumns.get(i);
            sqlValue += "?";
            count++;
            if (count < importerColumns.size()) {
                sqlQuery += ",";
                sqlValue += ",";
            }
        }
        sqlQuery += sqlValue;
        sqlQuery += " ) ";
        sqlQuery += duplicateLog;
        return sqlQuery;
    }

    public String createBatchInsertQueryConfigDB(List<DataStructureField> importerColumns) throws DataObjectException {
        String duplicateLog = "";
        if (logInsert) {
            duplicateLog = "LOG ERRORS INTO " + this.getDuplicateTable() + " REJECT LIMIT UNLIMITED";
        }
        String sqlQuery = "INSERT /*+ APPEND */ INTO " + this.getTableName() + " ( ";
        String sqlValue = ") VALUES ( ";
        int count = 0;
        for (int i = 0; i < importerColumns.size(); i++) {
            sqlQuery += importerColumns.get(i).getFieldName();
            sqlValue += "?";
            count++;
            if (count < importerColumns.size()) {
                sqlQuery += ",";
                sqlValue += ",";
            }
        }
        sqlQuery += sqlValue;
        sqlQuery += " ) ";
        sqlQuery += duplicateLog;
        return sqlQuery;
    }

    //Create importer with DB config
//    public Importer(Result result, String fieldOutSeparate, List<DataStructureField> lstField, boolean isInsertDb, GICache cache) {
    public Importer(Result result, String fieldOutSeparate, List<DataStructureField> lstField, GICache cache, String writeType) {
        try {
            this.importName = result.getResultName();
            this.writeType = writeType;
            if (lstField == null) {
                this.cacheDate = cache;
                this.loadDBConfig(result);
            } else {
                this.importFields = lstField;
//                this.isInsert = isInsertDb;
            }
            if (fieldOutSeparate == null || "".equals(fieldOutSeparate.trim())) {
                this.fieldOutSeparate = ";";
            } else {
                this.fieldOutSeparate = fieldOutSeparate;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
//            logger.error(ex.getMessage(), ex);
        }
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDuplicateTable() {
        return duplicateTable;
    }

    public void setDuplicateTable(String duplicateTable) {
        this.duplicateTable = duplicateTable;
    }

    public String getDuplicateQuery() {
        return duplicateQuery;
    }

    public String getErrorQuery() {
        return errorQuery;
    }

    public String getInsertQuery() {
        return insertQuery;
    }

    public String getTruncateQuery() {
        return truncateQuery;
    }

    public ImporterConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public int getBatchSize() {
        return BatchSize;
    }

    public boolean isLogInsert() {
        return logInsert;
    }

    public void setLogInsert(boolean logInsert) {
        this.logInsert = logInsert;
    }

    public DatabaseSid getDatabaseImport() {
        return databaseImport;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSeqName() {
        return seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    public boolean isIsCdrFieldInFilename() {
        return isCdrFieldInFilename;
    }

    public void setIsCdrFieldInFilename(boolean isCdrFieldInFilename) {
        this.isCdrFieldInFilename = isCdrFieldInFilename;
    }

    public String getFieldInName() {
        return fieldInName;
    }

    public void setFieldInName(String fieldInName) {
        this.fieldInName = fieldInName;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getTableErrorName() {
        return tableErrorName;
    }

    public void setTableErrorName(String tableErrorName) {
        this.tableErrorName = tableErrorName;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public GICache getCacheDate() {
        return cacheDate;
    }

    public void setCacheDate(GICache cacheDate) {
        this.cacheDate = cacheDate;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getWriteType() {
        return writeType;
    }

    public void setWriteType(String writeType) {
        this.writeType = writeType;
    }

    public String getImportName() {
        return importName;
    }

    public void setImportName(String importName) {
        this.importName = importName;
    }
}
