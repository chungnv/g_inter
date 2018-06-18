/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.worker;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.process.queue.GetJobQueue;
import com.viettel.ginterconnect.util.SystemParam;
import com.viettel.mmserver.base.ProcessThreadMX;
import org.openide.util.Exceptions;

/**
 * @author ubuntu
 */
public class WorkerThread extends ProcessThreadMX {

    private String name;
    private String type;
    private Long lastRunTime;

    AbstractWorkerBusiness workerBusiness;

    public WorkerThread(String name, String type) {
        super(name, type);
        this.name = name;
        this.type = type;
    }

    @Override
    public void prepareStart() {
        //TO DO
    }

    @Override
    public void process() {
        lastRunTime = System.currentTimeMillis();
        try {
            processJob();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            logger.info("Worker sleep..");
            try {
                Thread.sleep(SystemParam.workerDelay);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private boolean processJob() {
        try {
            JobsBO jobs = null;
            while ((jobs = GetJobQueue.getInstance().getFromQueue()) != null) {
//            JobsBO jobs = GetJobQueue.getInstance().getFromQueue();
                long start = System.currentTimeMillis();
                if (jobs != null) {
                    logger.warn(this.getName() + " Jobs " + jobs.getID() + " is fetched. " + jobs.getJobsStatus());
                    if (!(new Task(this)).execute(jobs)) {
                        break;
                    }
                    logger.warn(this.getName() + " Jobs " + jobs.getID() + " process time: " + (System.currentTimeMillis() - start));
                    Thread.sleep(50);
                }
            }
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(Long lastRunTime) {
        this.lastRunTime = lastRunTime;
    }
}
