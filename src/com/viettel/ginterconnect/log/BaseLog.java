/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.log;

import com.viettel.aerospike.main.Client;
import org.openide.util.Exceptions;

/**
 *
 * @author ubuntu
 */
public class BaseLog {

    //FILE_ID|ACTION_TYPE|SOURCE|DESTINATION|RATED_RECORD|SUCCESS|FAIL
    //|RATED_SIZE|FILE_TIME|PROCESS_TIME|FILE_SEQ|
    String logId;
    String fileId;
    String actionType;
    String sourceFile;
    String destinationFile;
    Long ratedRecord;
    Long successRecord;
    Long failRecord;
    Double ratedSize;
    String fileTime;
    String fileModifiedTime;
    String processTime;
    Long fileSeq;
    String workerId;
    String parentLogId;
    String tableName;

    public String getParentLogId() {
        return parentLogId;
    }

    public void setParentLogId(String parentLogId) {
        this.parentLogId = parentLogId;
    }
    Long duration;

    public BaseLog() {
        try {
            this.logId = Client.getSequenceInstance().getSequence("file_log_seq") + "";
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public BaseLog(String logId, String fileId, String actionType, String source, String destination,
            Long ratedRecord, Long successRecord, Long failRecord, Double ratedSize,
            String fileTime, String processTime, Long fileSeq, String workerId, 
            String fileModifiedTime, String parentId, Long duration) {
        this.logId = logId;
        this.fileId = fileId;
        this.actionType = actionType;
        this.sourceFile = source;
        this.destinationFile = destination;
        this.ratedRecord = ratedRecord;
        this.successRecord = successRecord;
        this.failRecord = failRecord;
        this.ratedSize = ratedSize;
        this.fileTime = fileTime;
        this.processTime = processTime;
        this.fileSeq = fileSeq;
        this.workerId = workerId;
        this.fileModifiedTime = fileModifiedTime;
        this.parentLogId = parentId;
        this.duration = duration;
    }
    
    public BaseLog(String logId, String fileId, String actionType, String source, String destination,
            Long ratedRecord, Long successRecord, Long failRecord, Double ratedSize,
            String fileTime, String processTime, Long fileSeq, String workerId, 
            String fileModifiedTime, String parentId, Long duration, String tableName) {
        this.logId = logId;
        this.fileId = fileId;
        this.actionType = actionType;
        this.sourceFile = source;
        this.destinationFile = destination;
        this.ratedRecord = ratedRecord;
        this.successRecord = successRecord;
        this.failRecord = failRecord;
        this.ratedSize = ratedSize;
        this.fileTime = fileTime;
        this.processTime = processTime;
        this.fileSeq = fileSeq;
        this.workerId = workerId;
        this.fileModifiedTime = fileModifiedTime;
        this.parentLogId = parentId;
        this.duration = duration;
        this.tableName = tableName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getDestinationFile() {
        return destinationFile;
    }

    public void setDestinationFile(String destinationFile) {
        this.destinationFile = destinationFile;
    }

    public Long getRatedRecord() {
        return ratedRecord;
    }

    public void setRatedRecord(Long ratedRecord) {
        this.ratedRecord = ratedRecord;
    }

    public Long getSuccessRecord() {
        return successRecord;
    }

    public void setSuccessRecord(Long successRecord) {
        this.successRecord = successRecord;
    }

    public Long getFailRecord() {
        return failRecord;
    }

    public void setFailRecord(Long failRecord) {
        this.failRecord = failRecord;
    }

    public Double getRatedSize() {
        return ratedSize;
    }

    public void setRatedSize(Double ratedSize) {
        this.ratedSize = ratedSize;
    }

    public String getFileTime() {
        return fileTime;
    }

    public void setFileTime(String fileTime) {
        this.fileTime = fileTime;
    }

    public String getProcessTime() {
        return processTime;
    }

    public void setProcessTime(String processTime) {
        this.processTime = processTime;
    }

    public Long getFileSeq() {
        return fileSeq;
    }

    public void setFileSeq(Long fileSeq) {
        this.fileSeq = fileSeq;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getFileModifiedTime() {
        return fileModifiedTime;
    }

    public void setFileModifiedTime(String fileModifiedTime) {
        this.fileModifiedTime = fileModifiedTime;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String toString(String spliter) {
        //FILE_ID|ACTION_TYPE|SOURCE|DESTINATION|RATED_RECORD
        //|SUCCESS|FAIL|RATED_SIZE|FILE_TIME|PROCESS_TIME|FILE_SEQ|
        StringBuilder stb = new StringBuilder();
        stb.append(this.getLogId()== null ? "" : this.getLogId()).append(spliter);
        stb.append(this.getFileId() == null ? "" : this.getFileId()).append(spliter);
        stb.append(this.getActionType() == null ? "" : this.getActionType()).append(spliter);
        stb.append(this.getSourceFile() == null ? "" : this.getSourceFile()).append(spliter);
        stb.append(this.getDestinationFile() == null ? "" : this.getDestinationFile()).append(spliter);
        stb.append(this.getTableName()== null ? "" : this.getTableName()).append(spliter);
        stb.append(this.getRatedRecord() == null ? "" : this.getRatedRecord()).append(spliter);
        stb.append(this.getSuccessRecord() == null ? "" : this.getSuccessRecord()).append(spliter);
        stb.append(this.getFailRecord() == null ? "" : this.getFailRecord()).append(spliter);
        stb.append(this.getRatedSize() == null ? "" : this.getRatedSize()).append(spliter);
        stb.append(this.getFileTime() == null ? "" : this.getFileTime()).append(spliter);
        stb.append(this.getProcessTime() == null ? "" : this.getProcessTime()).append(spliter);
        stb.append(this.getFileSeq() == null ? "" : this.getFileSeq()).append(spliter);
        stb.append(this.getFileModifiedTime() == null ? "" : this.getFileModifiedTime()).append(spliter);
        stb.append(this.getParentLogId()== null ? "" : this.getParentLogId()).append(spliter);
        stb.append(this.getWorkerId()== null ? "" : this.getWorkerId()).append(spliter);
        stb.append(this.getDuration()== null ? "" : this.getDuration()).append(spliter);
        return stb.toString();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    
    

}
