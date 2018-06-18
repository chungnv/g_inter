/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.util;

/**
 *
 * @author ubuntu
 */
public class SystemParam {

    public static String workingDir = null;
    public static long masterDelay = 0;
    public static long workerDelay = 0;
    public static String AEROSPIKE_IP = null;
    public static String AEROSPIKE_USERNAME = null;
    public static String AEROSPIKE_PASSWORD = null;
    public static int AEROSPIKE_PORT = 0;
    public static int NUMBER_OF_WORKER = 0;
    public static String NAMESPACE = null;
    public static int NUMBER_OF_MASTER_THREAD = 0;
    public static String SYSTEM_IP = null;
    public static String OAM_HOST = null;
    public static int SYSTEM_PORT = 0;
    public static int OAM_PORT = 0;
    public static int FTP_CONNECTION_POOL = 0;
    public static long JOB_TIMEOUT_IN_SEC = 600;
    public static long MASTER_JOB_TIMEOUT_IN_SEC = 600;
    public static long TEST_PORT = 0;
    public static String DECOMPRESS_TYPE = null;

    static {
        try {
            Configuration.loadConfig("../config/config.cfg");
            getWorkingDir();
            getWorkerDelay();
            getMasterDelay();
            getAerospikeIp();
            getAerospikePort();
            getNumberOfWorker();
            getNumberOfMasterThread();
            getAerospikeUsername();
            getAerospikePassword();
            getAerospikeNamespace();
            getSystemIp();
            getSystemPort();
            getFtpPoolSize();
            getOAMHost();
            getOAMPort();
            getJobTimeout();
            getMasterJobTimeout();
            getTestPort();
            getDecompressType();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getJobTimeout() {
        String param = Configuration.getString(Constants.JOB_TIMEOUT_IN_SEC, "600");
        JOB_TIMEOUT_IN_SEC = Long.valueOf(param);
    }

    private static void getTestPort() {
        String param = Configuration.getString(Constants.TEST_PORT, "0");
        TEST_PORT = Long.valueOf(param);
    }

    private static void getMasterJobTimeout() {
        String param = Configuration.getString(Constants.MASTER_JOB_TIMEOUT_IN_SEC, "600");
        MASTER_JOB_TIMEOUT_IN_SEC = Long.valueOf(param);
    }

    private static void getWorkingDir() {
        workingDir = Configuration.getString(Constants.SYSTEM_PARAM_WORKING_DIR, "../data");
    }

    private static void getMasterDelay() {
        String s = Configuration.getString(Constants.SYSTEM_MASTER_DELAY, "10000");
        masterDelay = Long.valueOf(s);
    }

    private static void getWorkerDelay() {
        String s = Configuration.getString(Constants.SYSTEM_WORKER_DELAY, "10000");
        workerDelay = Long.valueOf(s);
    }

    private static void getAerospikeIp() {
        AEROSPIKE_IP = Configuration.getString(Constants.SYSTEM_AEROSPIKE_IP, "localhost");
    }

    private static void getAerospikePort() {
        String s = Configuration.getString(Constants.SYSTEM_AEROSPIKE_PORT, "3000");
        AEROSPIKE_PORT = Integer.valueOf(s);
    }

    private static void getNumberOfWorker() {
        String s = Configuration.getString(Constants.SYSTEM_NUMBER_OF_WORKER, "1");
        NUMBER_OF_WORKER = Integer.valueOf(s);
    }

    private static void getNumberOfMasterThread() {
        String s = Configuration.getString(Constants.SYSTEM_NUMBER_OF_MASTER_THREAD, "1");
        NUMBER_OF_MASTER_THREAD = Integer.valueOf(s);
    }

    private static void getAerospikeUsername() {
        AEROSPIKE_USERNAME = Configuration.getString(Constants.SYSTEM_AEROSPIKE_USERNAME);
    }

    private static void getAerospikePassword() {
        AEROSPIKE_PASSWORD = Configuration.getString(Constants.SYSTEM_AEROSPIKE_PASSWORD);
    }
    
    private static void getDecompressType() {
        DECOMPRESS_TYPE = Configuration.getString(Constants.DECOMPRESS_TYPE);
    }

    private static void getAerospikeNamespace() {
        NAMESPACE = Configuration.getString(Constants.SYSTEM_NAMESPACE);
    }

    private static void getSystemIp() {
        try {
            SYSTEM_IP = System.getProperty("com.viettel.mmserver.agent.ip");
            if (SYSTEM_IP == null) {
                SYSTEM_IP = "localhost";
            }
        } catch (Exception ex) {
            SYSTEM_IP = "localhost";
        }
    }

    private static void getSystemPort() {
        String s = System.getProperty("com.viettel.mmserver.agent.port");
        if (s == null) {
            SYSTEM_PORT = -1;
        }
        try {
            SYSTEM_PORT = Integer.valueOf(s);
        } catch (NumberFormatException ex) {
            SYSTEM_PORT = -1;
        }
    }

    private static void getFtpPoolSize() {
        String s = Configuration.getString(Constants.FTP_CONNECTION_POOL, "5");
        FTP_CONNECTION_POOL = Integer.valueOf(s);
    }

    private static void getOAMHost() {
        OAM_HOST = Configuration.getString("OAM_HOST", "localhost");
    }

    private static void getOAMPort() {
        String s = Configuration.getString("OAM_PORT", "-1");
        OAM_PORT = Integer.valueOf(s);
    }
}
