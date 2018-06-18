/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.master;

import com.viettel.ginterconnect.process.queue.*;
import com.viettel.aerospike.bo.MasterThreadBO;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.handler.ProcessorHandler;
import com.viettel.ginterconnect.process.main.MasterMain;
import com.viettel.ginterconnect.util.SystemParam;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class MasterJobQueue extends Thread {

    private ArrayBlockingQueue<MasterThreadBO> jobQueue;
    private Logger logger = Logger.getLogger(GetJobQueue.class);
    private int size;

    private static MasterJobQueue jobQueueThread;

    public synchronized static MasterJobQueue getInstance() {
        if (jobQueueThread == null) {
            jobQueueThread = new MasterJobQueue();
        }
        return jobQueueThread;
    }

    public void init(int size) {
        jobQueue = new ArrayBlockingQueue<>(size);
        jobQueueThread.size = size;
        jobQueueThread.start();
    }

    public MasterJobQueue() {

    }

    public boolean insertToQueue(MasterThreadBO jobs) {
        while (jobQueue.size() >= this.size) {
            try {
//                logger.info("MasterJob Queue is full..");
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return jobQueue.add(jobs);
    }

    public MasterThreadBO getFromQueue() {
        if (!jobQueue.isEmpty()) {
            return jobQueue.poll();
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            while (jobQueue.size() < this.size) {
                try {
                    ProcessorHandler processHandler = new ProcessorHandler();
                    Client.getInstance().setProcessorHandler(processHandler);
                    if (MasterMain.numberOfThread == 0) {
                        logger.info("get masterJob");
                        Client.getInstance().getMasterProcessRecord(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    Thread.sleep(SystemParam.masterDelay);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
