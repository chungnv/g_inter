/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.management;

import com.viettel.ginterconnect.process.worker.WorkerThread;
import com.viettel.mmserver.base.ProcessThreadMX;
import com.viettel.ginterconnect.util.SystemParam;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author ubuntu
 */
public class WorkerManagement extends ProcessThreadMX {

    private ConcurrentHashMap<String, WorkerThread> workerMap = new ConcurrentHashMap<>();

    public WorkerManagement(String name, String description) {
        super(name);
    }

    @Override
    protected void process() {
        //kiem tra co thread nao tat thi bat lai
        try {
            Iterator<String> iteWorkerMap = workerMap.keySet().iterator();
            while (iteWorkerMap.hasNext()) {
                String workerName = iteWorkerMap.next();
                logger.debug("Check thread: " + workerName);
                WorkerThread worker = workerMap.get(workerName);
                restartThreadWithTimeout(worker, 30 * 60 * 1000);
            }
            //send infor to server
            logger.debug("Send ping to server");
            ManagementUtils.send(SystemParam.SYSTEM_IP, SystemParam.SYSTEM_IP + "-" 
                    + SystemParam.SYSTEM_PORT + "-numberOfChildThread:" + workerMap.size(), logger);
            logger.debug("send ping success");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public void addThread(WorkerThread worker) {
        workerMap.put(worker.getName(), worker);
    }

    public void restartThreadWithTimeout(final WorkerThread worker, final long timeout) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                Long lastTime = worker.getLastRunTime();
                boolean restart = false;
                if (!worker.isRunning()) {
                    worker.restart();
                    restart = true;
                }
                if ((System.currentTimeMillis() - lastTime) > timeout && !restart) {
                    worker.stop();
                    worker.start();
                }
                return "OK";
            }
        };
        Future<Object> future = executor.submit(callable);
        try {
            Object obj = future.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException | InterruptedException | ExecutionException timeoutEx) {
            logger.error(timeoutEx.getMessage(), timeoutEx);
            logger.error("Time out when restart thread: " + timeoutEx.getMessage());
        } finally {
            future.cancel(true);
            executor.shutdown();
        }
    }

}
