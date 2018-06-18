/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.master;

import com.viettel.aerospike.bo.MasterThreadBO;
import com.viettel.ginterconnect.process.main.MasterMain;
import com.viettel.ginterconnect.process.management.ManagementUtils;
import com.viettel.ginterconnect.util.SystemParam;
import com.viettel.mmserver.base.ProcessThreadMX;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ubuntu
 */
public class MasterThread extends ProcessThreadMX {

    private String name;
    private int numberOfMaster = 1;
    private static MasterThread instance = null;
    BlockingQueue q;
    ThreadPoolExecutor threadPool;

    public MasterThread(String name, String description) {
        super(name);
        this.name = name;
    }

    public static synchronized MasterThread getInstance(String name, String description) {
        if (instance == null) {
            instance = new MasterThread(name, description);
        }
        return instance;
    }

    public void init(int numberOfMaster) {
        this.numberOfMaster = numberOfMaster;
        q = new ArrayBlockingQueue(numberOfMaster);
    }

    @Override
    protected void process() {
        try {
            //send management infor
            try {
                logger.info("send master ping oam");
                ManagementUtils.send(SystemParam.SYSTEM_IP + "-" + SystemParam.SYSTEM_PORT, "OK", logger);
                logger.info("send master ping done");
            } catch (Exception ex) {
            }

            MasterThreadBO masterProcessBO;
            threadPool = new ThreadPoolExecutor(10, numberOfMaster, 5, TimeUnit.SECONDS, q);

            while ((masterProcessBO = MasterJobQueue.getInstance().getFromQueue()) != null) {
                RunMasterJob masterProcess = new RunMasterJob("GI");
                masterProcess.setMasterProcessBO(masterProcessBO);
                try {
                    threadPool.execute(masterProcess);
                } catch (RejectedExecutionException re) {
                    MasterMain.numberOfThread = 1;
                    logger.warn("Pool full");
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                if (threadPool != null) {
                    threadPool.shutdown();
                    threadPool.awaitTermination(120, TimeUnit.SECONDS);
                }
                MasterMain.numberOfThread = 0;
                logger.info("Sleep...");
                Thread.sleep(SystemParam.masterDelay);
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

}
