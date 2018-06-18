/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.queue;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.handler.ProcessorHandler;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author ubuntu
 */
public class GetJobQueue extends Thread {

    private PriorityBlockingQueue<JobsBO> jobQueue;
//    private ArrayBlockingQueue<JobsBO> jobQueue;
    private Logger logger = Logger.getLogger(GetJobQueue.class);
    ;
    private int size;

    private static GetJobQueue jobQueueThread;

    public synchronized static GetJobQueue getInstance() {
        if (jobQueueThread == null) {
            jobQueueThread = new GetJobQueue();
        }
        return jobQueueThread;
    }

    public void init(int size) {
        ProcessorHandler processHandler = new ProcessorHandler();
        Client.getInstance().setProcessorHandler(processHandler);
//        jobQueue = new ArrayBlockingQueue<>(size);
        jobQueue = new PriorityBlockingQueue<>(size, new JobComparator());
        jobQueueThread.size = size;
        jobQueueThread.start();
    }

    public GetJobQueue() {

    }

    public boolean insertToQueue(JobsBO jobs) {
        while (jobQueue.size() >= this.size) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return jobQueue.add(jobs);
    }

    public JobsBO getFromQueue() {
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
                    Client.getInstance().getJobsRecordByStatus(1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
    
//    public void stop() {
//        
//    }
}
