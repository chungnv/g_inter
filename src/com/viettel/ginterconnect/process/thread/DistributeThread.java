/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.thread;

//import com.viettel.ginterconnect.process.bean.JobBO;
import com.viettel.ginterconnect.util.GIQueue;
import com.viettel.mmserver.base.ProcessThreadMX;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 *
 * @author quangnh17
 */
public class DistributeThread extends ProcessThreadMX {

    private static Logger logger = Logger.getLogger(DistributeThread.class);
    private GIQueue jobQueue = null;

    public void setJobQueue(GIQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    public DistributeThread(String threadName) {
        super(threadName);
    }

    @Override
    protected void process() {
        try {
            ArrayList<Object> listObj = jobQueue.dequeue(10);
            if (listObj != null && listObj.size() > 0) {
                for (Object obj : listObj) {
//                    JobBO job = (JobBO) obj;
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

}
