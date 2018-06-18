/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.thread;

//import com.viettel.ginterconnect.process.bean.JobBO;
import com.viettel.aerospike.bo.MasterThreadBO;
import com.viettel.ginterconnect.util.GIQueue;
import com.viettel.mmserver.base.ProcessThreadMX;
import org.apache.log4j.Logger;

/**
 *
 * @author quangnh17
 */
public class MasterProcessThread extends ProcessThreadMX {

    private final GIQueue queueJob = new GIQueue(0);
    MasterThreadBO masterProcessBO = null;

    public MasterProcessThread(String threadName, String description) {
        super(threadName, description);
        logger = Logger.getLogger(MasterProcessThread.class);
    }

//    public void pushJob(JobBO job) {
//        queueJob.enqueue(job);
//    }

    public MasterThreadBO getMasterProcessBO() {
        return masterProcessBO;
    }

    public void setMasterProcessBO(MasterThreadBO masterProcessBO) {
        this.masterProcessBO = masterProcessBO;
    }

    @Override
    protected void process() {
        try {
            Object obj = queueJob.dequeue();
            if (obj != null) {
//                JobBO job = (JobBO) obj;
                // Excute job o day
                //excute(job);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

}
