
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;
import com.viettel.ginterconnect.log.BaseLog;

/**
 *
 * @author ubuntu
 */
@Table(name = "get_file_log")
public class GetFileLog extends BaseLog {

    @Column(name = "FILE_ID", primaryKey = true)
    private Long fileId;
    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "ZIP_FILE_NAME")
    private String zipFileName;
    @Column(name = "FILE_SEQ")
    private Long fileSeq;
    @Column(name = "REMOTE_PATH")
    private String remotePath;
    @Column(name = "LOCAL_PATH")
    private String localPath;
    @Column(name = "FILE_SIZE")
    private Long fileSize;
    @Column(name = "GET_DURATION")
    private Long getDuration;
    @Column(name = "GET_TIME")
    private String getFileTime;
    @Column(name = "FILE_TIME")
    private String modifiedFileTime;
    @Column(name = "WORKER_ID")
    private String workerId;
//    @Column(name = "FILE_TIME")
//    private String modifiedFileTime;

    public GetFileLog() {
        super();
    }

    public GetFileLog(String logId, String fileId, String actionType, String source, String destination, Long ratedRecord, Long successRecord, Long failRecord, Double ratedSize, String fimeTime,
            String processTime, Long fileSeq, String workerId, String fileModifiedTime, String parentId, Long duration) {
        super(logId, fileId, actionType, source, destination, ratedRecord, successRecord, failRecord,
                ratedSize, fimeTime, processTime, fileSeq, workerId, fileModifiedTime, parentId, duration);
    }

//    public Long getFileId() {
//        return fileId;
//    }
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSeq() {
        return fileSeq;
    }

    public void setFileSeq(Long fileSeq) {
        this.fileSeq = fileSeq;
    }

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getGetDuration() {
        return getDuration;
    }

    public void setGetDuration(Long getDuration) {
        this.getDuration = getDuration;
    }

    public String getGetFileTime() {
        return getFileTime;
    }

    public void setGetFileTime(String getFileTime) {
        this.getFileTime = getFileTime;
    }

    public String getModifiedFileTime() {
        return modifiedFileTime;
    }

    public void setModifiedFileTime(String modifiedFileTime) {
        this.modifiedFileTime = modifiedFileTime;
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String toString(String spliter) {
        StringBuilder builder = new StringBuilder();
        builder.append(fileId.toString()).append(spliter);
        builder.append(fileSeq).append(spliter);
        builder.append(fileName).append(spliter);
        builder.append(zipFileName).append(spliter);
        builder.append(remotePath).append(spliter);
        builder.append(localPath).append(spliter);
        builder.append(fileSize).append(spliter);
        builder.append(getDuration).append(spliter);
        builder.append(getFileTime).append(spliter);
        builder.append(modifiedFileTime).append(spliter);
        builder.append(workerId);
        return builder.toString();
    }
}
