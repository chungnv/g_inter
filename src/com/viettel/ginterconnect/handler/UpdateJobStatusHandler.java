/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.handler;

import com.aerospike.client.Record;
import com.viettel.aerospike.bo.CdrThreadBO;
import com.viettel.aerospike.bo.JobsBO;
import com.viettel.aerospike.bo.MasterThreadBO;
import com.viettel.aerospike.handle.Handler;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.process.queue.GetJobQueue;
import com.viettel.ginterconnect.process.worker.WorkerThread;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIUtils;
import com.viettel.ginterconnect.util.SystemParam;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author QUANGNH
 */
public class UpdateJobStatusHandler extends Handler {

    private static final Logger logger = Logger.getLogger(UpdateJobStatusHandler.class);

    WorkerThread worker;

    @Override
    public void onHandle(JobsBO jobsbo) {
        //update job
        try {
            String dateCreate = jobsbo.getCreateDate();
            Date createDate = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT)).parse(dateCreate);
            long sysdate = System.currentTimeMillis();
            logger.info("Check jobs: " + jobsbo.getPrimaryKey());
            if (((sysdate - createDate.getTime()) > SystemParam.JOB_TIMEOUT_IN_SEC * 1000)) {
                logger.info("Change jobs: " + jobsbo.getPrimaryKey() + " to new status");
                jobsbo.setJobsStatus(Constants.JOB_NEW_STATUS);
                jobsbo.setIp(SystemParam.SYSTEM_IP);
                jobsbo.setCreateDate(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
                Record jobBo = (Record) Client.getUpdateJobInstance().updateJobsStatus(jobsbo);
                if (jobBo != null && jobBo.getInt("JOB_STATUS") == Constants.JOB_PROCESSING_STATUS) {
                    logger.info("Update jobs: " + jobsbo.getPrimaryKey() + " success");
                } else {
                    logger.info("Update jobs: " + jobsbo.getPrimaryKey() + " fail");
                }
            }
        } catch (ParseException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void onHandle(CdrThreadBO ctbo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public WorkerThread getWorker() {
        return worker;
    }

    public void setWorker(WorkerThread worker) {
        this.worker = worker;
    }

    @Override
    public void onHandle(MasterThreadBO object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onHandleQueryForUpdate(MasterThreadBO object) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
