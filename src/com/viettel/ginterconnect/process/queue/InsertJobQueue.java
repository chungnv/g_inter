/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.queue;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.aerospike.main.Client;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class InsertJobQueue extends Thread {

    private ArrayBlockingQueue<JobsBO> insertJobQueue;
    private Logger logger = Logger.getLogger(InsertJobQueue.class);;
    private int size;

    private static InsertJobQueue jobQueueThread;
    
    public synchronized static InsertJobQueue getInstance(int size) {
        if (jobQueueThread == null) {
            jobQueueThread = new InsertJobQueue(size);
            jobQueueThread.setSize(size);
            jobQueueThread.start();
        }
        return jobQueueThread;
    }
    
    public InsertJobQueue(int queueSize) {
        insertJobQueue = new ArrayBlockingQueue<>(queueSize);
    }
    
    public synchronized int insertToQueue(JobsBO jobs) {
        while (insertJobQueue.size() >= this.getSize()) {
            logger.info("JobQueue is full");
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
        insertJobQueue.add(jobs);
        return 1;
    }

    @Override
    public void run() {
        while (true) {
            if (!insertJobQueue.isEmpty()) {
                JobsBO jobs = insertJobQueue.poll();
                try {
                    Long id = Client.getSequenceInstance().getSequence(null);
                    jobs.setID(id);
                    Client.getInstance().insertJobsRecord(id, jobs);
                    logger.info(jobs.getCreateProcess() + " Create success jobId: " + id);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error("insert job: " + jobs.getCreateProcess() + " fail");
                }
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}
