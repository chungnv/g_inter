package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;

import java.util.Date;

/**
 * Created by hoangsinh on 15/08/2017.
 */
@Table(name = "filter_log")
public class FilterLog {
    @Column(name = "JOB_ID", primaryKey = true)
    private Long jobId;
    @Column(name = "WORKER_NAME")
    private String workerName;
    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "START_TIME")
    private String startTime;
    @Column(name = "END_TIME")
    private String endTime;
    @Column(name = "TOTAL_FILTER")
    private long totalFilter;
    @Column(name = "TOTAL_INVALID")
    private long totalCdrInvalid;
    @Column(name = "STANDARD_FAIL")
    private long totalStandardFail;
    @Column(name = "TOTAL_SUCCESS")
    private long totalFilterSuccess;

    public FilterLog(Long jobId, String workerName, String fileName, String startTime) {
        this.jobId = jobId;
        this.workerName = workerName;
        this.fileName = fileName;
        this.startTime = startTime;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getTotalFilter() {
        return totalFilter;
    }

    public void setTotalFilter(long totalFilter) {
        this.totalFilter = totalFilter;
    }

    public long getTotalCdrInvalid() {
        return totalCdrInvalid;
    }

    public void setTotalCdrInvalid(long totalCdrInvalid) {
        this.totalCdrInvalid = totalCdrInvalid;
    }

    public long getTotalStandardFail() {
        return totalStandardFail;
    }

    public void setTotalStandardFail(long totalStandardFail) {
        this.totalStandardFail = totalStandardFail;
    }

    public long getTotalFilterSuccess() {
        return totalFilterSuccess;
    }

    public void setTotalFilterSuccess(long totalFilterSuccess) {
        this.totalFilterSuccess = totalFilterSuccess;
    }
}
