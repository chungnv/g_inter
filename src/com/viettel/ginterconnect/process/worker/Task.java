/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.worker;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.util.Constants;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class Task {
    
    private static final Logger logger = Logger.getLogger(Task.class);
    
    WorkerThread tworker;
    
    public Task(WorkerThread worker) {
        this.tworker = worker;
    }
    
    public boolean execute(JobsBO job) {
        AbstractWorkerBusiness worker = null;
        try {
            //1. check job type --> startJob
            worker = (AbstractWorkerBusiness)Class.forName(Constants.WORKER_IMPL_PACKAGE +
                    job.getJobType().toUpperCase()).newInstance();
            worker.setWorker(tworker);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return false;
        }
        //2. dobusiness
        worker.setCurrentJob(job);
        worker.readParam();
        if (worker.doBusiness()) {
            logger.info("process success job PK: " + job.getPrimaryKey());
        } else {
            //move to fail jobs
            logger.info("process fail job PK: " + job.getPrimaryKey());
        }
        return true;
    }
    
}
