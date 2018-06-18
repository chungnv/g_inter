/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.main;

import com.viettel.aerospike.ha.CheckMasterConnectionThread;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.process.master.MasterJobQueue;
import com.viettel.ginterconnect.process.master.MasterThread;
import com.viettel.ginterconnect.util.Configuration;
import com.viettel.ginterconnect.util.SystemParam;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author
 */
public class MasterMain {

//    public static MasterProcess master;
    public static int numberOfThread = 0;

    private static Logger logger = Logger.getLogger(MasterMain.class);
    private static CheckMasterConnectionThread checkConnectionThread;

    public static void main(String args[]) throws Exception {
        PropertyConfigurator.configure("../config/master-log.conf");
        Configuration.loadConfig("../config/config.cfg");

        try {
//            Client.getInstance().setProcessorHandler(new ProcessorHandler());
            Client.getInstance().init(SystemParam.AEROSPIKE_IP, SystemParam.AEROSPIKE_PORT,
                    SystemParam.AEROSPIKE_USERNAME, SystemParam.AEROSPIKE_PASSWORD,
                    SystemParam.NAMESPACE);
            Client.getSequenceInstance().init(SystemParam.AEROSPIKE_IP, SystemParam.AEROSPIKE_PORT,
                    SystemParam.AEROSPIKE_USERNAME, SystemParam.AEROSPIKE_PASSWORD,
                    SystemParam.NAMESPACE);
            logger.info("System start");
            MasterJobQueue.getInstance().init(SystemParam.NUMBER_OF_MASTER_THREAD);
            checkConnectionThread = new CheckMasterConnectionThread("Thread check connection");
            checkConnectionThread.start();
            logger.info("start master thread");
            MasterThread master = MasterThread.getInstance("Master", "Master");
            master.init(SystemParam.NUMBER_OF_MASTER_THREAD);
            master.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
