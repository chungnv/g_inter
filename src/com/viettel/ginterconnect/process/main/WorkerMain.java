/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.main;

import com.viettel.aerospike.ha.CheckWorkerConnectionThread;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.process.filter.TestFilter;
import com.viettel.ginterconnect.process.management.WorkerManagement;
import com.viettel.ginterconnect.process.queue.GetJobQueue;
import com.viettel.ginterconnect.process.worker.WorkerThread;
import com.viettel.ginterconnect.util.SystemParam;
import javax.xml.ws.Endpoint;
import org.apache.log4j.PropertyConfigurator;

/**
 * @author
 */
public class WorkerMain {

    private static CheckWorkerConnectionThread checkConnectionThread;

    public static void main(String args[]) {

        PropertyConfigurator.configure("../config/worker-log.conf");
        System.out.println("start aerospike");
        int numberOfWorker = SystemParam.NUMBER_OF_WORKER; // cau hinh trong file cau hinh
        String processType = "get";
        Client.getInstance().init(SystemParam.AEROSPIKE_IP, SystemParam.AEROSPIKE_PORT,
                SystemParam.AEROSPIKE_USERNAME, SystemParam.AEROSPIKE_PASSWORD,
                SystemParam.NAMESPACE);
        Client.getInsertInstance().init(SystemParam.AEROSPIKE_IP, SystemParam.AEROSPIKE_PORT,
                SystemParam.AEROSPIKE_USERNAME, SystemParam.AEROSPIKE_PASSWORD,
                SystemParam.NAMESPACE);
        Client.getSequenceInstance().init(SystemParam.AEROSPIKE_IP, SystemParam.AEROSPIKE_PORT,
                SystemParam.AEROSPIKE_USERNAME, SystemParam.AEROSPIKE_PASSWORD,
                SystemParam.NAMESPACE);

        System.out.println("start aerospike ping check");
        checkConnectionThread = new CheckWorkerConnectionThread("Thread check connection");
        checkConnectionThread.start();
        System.out.println("start job queue");
        GetJobQueue.getInstance().init(5);
        System.out.println("start workerManagement");
//        WorkerManagement workerManagement = new WorkerManagement("WorkerManagement", "WorkerManagement");
//        workerManagement.start();

        try {
            for (int i = 0; i < numberOfWorker; i++) {
                //1. Khoi tao tien trinh
                WorkerThread workerThread = new WorkerThread(SystemParam.SYSTEM_IP + "-" + SystemParam.SYSTEM_PORT + "-" + i, processType.toUpperCase());
                workerThread.start();
//                workerManagement.addThread(workerThread);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //init webservice test
        if (SystemParam.TEST_PORT > 0) {
            System.out.println("Start filter webservice");
            Endpoint.publish("http://" + SystemParam.SYSTEM_IP + ":" + SystemParam.TEST_PORT + "/ws/filter_test", new TestFilter());
        }
    }
}
