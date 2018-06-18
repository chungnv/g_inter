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
import org.apache.log4j.Logger;

/**
 *
 * @author QUANGNH
 */
public class ProcessorHandler extends Handler {

    private static final Logger logger = Logger.getLogger(ProcessorHandler.class);

    @Override
    public void onHandle(MasterThreadBO mtbo) {
//        if (mtbo.getLastRunTime() != null) {
//            Date lastRun = GIUtils.strToDate(mtbo.getLastRunTime(), Constants.DEFAULT_DATE_FORMAT);
//            if (lastRun != null) {
//                if ((System.currentTimeMillis() - lastRun.getTime()) > 30 * 60 * 1000) {
//                    //update run time
//                    mtbo.setLastRunTime(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
//                    if (Client.getInstance().updateMasterProcessRecord(mtbo) != null) {
//                        MasterManagement.getInstance().startMasterThread(mtbo);
//                    } else {
//                        logger.info("Master Thread " + mtbo.getMasterProcessCode() + " has been already started");
//                    }
//                }
//            } else {
//                logger.info("Master Thread " + mtbo.getMasterProcessCode() + " last_run wrong");
//            }
//        } else {
//        mtbo.setLastRunTime(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
//        mtbo.setRunStatus((short) 1);
////        mtbo.setRunStatus((short) 0);
//        mtbo.setIp(SystemParam.SYSTEM_IP);
//        if (Client.getInstance().updateMasterProcessRecord(mtbo) != null) {
//            MasterManagement.getInstance().startMasterThread(mtbo);
//            logger.info("MasterProcessID " + mtbo.getMasterId() + " start");
//        } else {
//            logger.info("Master Thread " + mtbo.getMasterProcessCode() + " has been already started");
//        }
//        }
    }

    WorkerThread worker;

    @Override
    public void onHandle(JobsBO jobsbo) {
        //update job
        jobsbo.setJobsStatus(Constants.JOB_PROCESSING_STATUS);
        jobsbo.setIp(SystemParam.SYSTEM_IP);
        Record jobBo = (Record)Client.getInstance().updateJobsRecord(jobsbo);
        if (jobBo != null && jobBo.getInt("JOB_STATUS") == Constants.JOB_NEW_STATUS) {
//            logger.warn(worker.getName() + " Jobs " + jobsbo.getID() + " is update. " + jobBo.getInt("JOB_STATUS"));
//            (new Task(worker)).execute(jobsbo);
            GetJobQueue.getInstance().insertToQueue(jobsbo);
            
        } else {
            logger.warn(" Jobs " + jobsbo.getID() + " has been fetched by another process. ");
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
    public void onHandleQueryForUpdate(MasterThreadBO mtbo) {
//        mtbo.setLastRunTime(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
//        mtbo.setRunStatus((short) 1);
////        mtbo.setRunStatus((short) 0);
//        mtbo.setIp(SystemParam.SYSTEM_IP);
//        if (Client.getInstance().updateMasterProcessRecord(mtbo) != null) {
//            MasterManagement.getInstance().startMasterThread(mtbo);
//            logger.info("MasterProcessID " + mtbo.getMasterId() + " start");
//        } else {
//            logger.info("Master Thread " + mtbo.getMasterProcessCode() + " has been already started");
//        }
    }

}
