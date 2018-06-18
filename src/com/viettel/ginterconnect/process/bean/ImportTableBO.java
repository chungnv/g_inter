package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;

/**
 * Created by sinhhv on 9/30/2016.
 */
@Table(name = "md_import_table")
public class ImportTableBO {
    @Column(name = "ID", primaryKey = true)
    String importTableId;
    @Column(name = "DATABASE_NAME")
    String databaseName;
    @Column(name = "URL")
    String url;
    @Column(name = "USER_NAME")
    String userName;
    @Column(name = "PASSWORD")
    String password;
    @Column(name = "TABLE_NAME")
    String tableName;
    @Column(name = "TABLE_ERROR")
    String tableErrorName;
    @Column(name = "BATCH_SIZE")
    Integer batchSize;
    @Column(name = "MAX_CONN")
    Integer maxConnection;
    @Column(name = "LOG_INS")
    String logInsert;
    @Column(name = "LOG_INS_TAB")
    String logInsertTable;
    @Column(name = "STATUS")
    Long status;

    public String getImportTableId() {
        return importTableId;
    }

    public void setImportTableId(String importTableId) {
        this.importTableId = importTableId;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableErrorName() {
        return tableErrorName;
    }

    public void setTableErrorName(String tableErrorName) {
        this.tableErrorName = tableErrorName;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getMaxConnection() {
        return maxConnection;
    }

    public void setMaxConnection(Integer maxConnection) {
        this.maxConnection = maxConnection;
    }

    public String getLogInsert() {
        return logInsert;
    }

    public void setLogInsert(String logInsert) {
        this.logInsert = logInsert;
    }

    public String getLogInsertTable() {
        return logInsertTable;
    }

    public void setLogInsertTable(String logInsertTable) {
        this.logInsertTable = logInsertTable;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
