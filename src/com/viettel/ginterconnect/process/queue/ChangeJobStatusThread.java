/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.queue;

import com.viettel.aerospike.ha.CheckMasterConnectionThread;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.handler.UpdateJobStatusHandler;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.SystemParam;
import org.openide.util.Exceptions;

/**
 *
 * @author ubuntu
 */
public class ChangeJobStatusThread extends Thread {

    public ChangeJobStatusThread(String name) {
        UpdateJobStatusHandler handler = new UpdateJobStatusHandler();
        Client.getUpdateJobInstance().setProcessorHandler(handler);
        Client.getUpdateJobInstance().init(SystemParam.AEROSPIKE_IP, SystemParam.AEROSPIKE_PORT,
                SystemParam.AEROSPIKE_USERNAME, SystemParam.AEROSPIKE_PASSWORD,
                SystemParam.NAMESPACE);
    }

    @Override
    public void run() {
        while (true) {
//            while (jobQueue.size() < this.size) {
            System.out.println("run");
            try {
                Client.getUpdateJobInstance().getUpdateJob(2);
                Client.getUpdateJobInstance().getMasterProcessRecordForUpdate(Constants.MASTER_JOB_PROCESSING);
            } catch (Exception ie) {
                ie.printStackTrace();
            } finally {
                try {
                    Thread.sleep(120 * 1000);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    public static void main(String args[]) {
//        Client.getInstance().init(SystemParam.AEROSPIKE_IP, SystemParam.AEROSPIKE_PORT,
//                SystemParam.AEROSPIKE_USERNAME, SystemParam.AEROSPIKE_PASSWORD,
//                SystemParam.NAMESPACE);
//        Client.getSequenceInstance().init(SystemParam.AEROSPIKE_IP, SystemParam.AEROSPIKE_PORT,
//                SystemParam.AEROSPIKE_USERNAME, SystemParam.AEROSPIKE_PASSWORD,
//                SystemParam.NAMESPACE);
        ChangeJobStatusThread s = new ChangeJobStatusThread("s");
        s.start();
        CheckMasterConnectionThread checkConnectionThread = new CheckMasterConnectionThread("Thread check connection");
        checkConnectionThread.start();
    }

}
