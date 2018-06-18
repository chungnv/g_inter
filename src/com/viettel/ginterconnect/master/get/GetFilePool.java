/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.master.get;

import com.viettel.ginterconnect.util.SystemParam;
import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class GetFilePool {

    private static final int retryTime = 10;

    private static Hashtable<String, BlockingQueue<AbstractFileTransfer>> mapQueueConnectionPool = new Hashtable<>();

    public static synchronized AbstractFileTransfer getConnection(String ftpType, Logger logger, String host,
            int port, String username, String password, String processCode,
            String ftpRemoteDir, String ftpMode, String transferMode) throws Exception {
        int times = 0;
        AbstractFileTransfer connection = null;
        while (connection == null) {
            times++;
            connection = getAbstractFileTransfer(ftpType, logger, host, port, 
                    username, password, processCode, ftpRemoteDir, ftpMode, transferMode);
            if (connection == null) {
                logger.info("host: " + host + " is out of connection, wait to get. " + " Try time: " + times);
                Thread.sleep(times * 1000);
            } else {
                if (connection.connect()) {
                    return connection;
                }
            }
        }
        return null;
    }
    
//    public static synchronized AbstractFileTransfer getConnection(String ftpType, Logger logger, String host,
//            int port, String username, String password, String processCode,
//            String ftpRemoteDir) throws Exception {
//        int times = 0;
//        AbstractFileTransfer connection = null;
//        while (connection == null) {
//            times++;
//            if (!mapQueueConnectionPool.containsKey(host)) {
//                mapQueueConnectionPool.put(host, initPool(ftpType, logger, host,
//                        port, username, password, processCode, ftpRemoteDir));
//            }
//            BlockingQueue<AbstractFileTransfer> selectedPool = mapQueueConnectionPool.get(host);
//            connection = selectedPool.poll();
//            logger.info("Host: " + host + "after poll pool size: " + selectedPool.size());
//            if (connection == null) {
//                logger.info("host: " + host + " is out of connection, wait to get. " + " Try time: " + times);
//                Thread.sleep(times * 1000);
//            } else {
//                if (connection.connect()) {
//                    return connection;
//                }
//            }
//        }
//        return null;
//    }

    public static void releaseConnection(String host, AbstractFileTransfer connection, Logger logger) {
        try {
            if (connection == null) {
                return;
            }
            connection.disconnect();
            connection = null;
//            if (!mapQueueConnectionPool.containsKey(host)) {
//                logger.error("Pool is not init for host: " + host);
//            } else {
//                mapQueueConnectionPool.get(host).offer(connection);
//                logger.info("Host: " + host + " after release pool size: " + mapQueueConnectionPool.get(host).size());
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static BlockingQueue<AbstractFileTransfer> initPool(String ftpType, Logger logger, String host,
            int port, String username, String password, String processCode,
            String ftpRemoteDir, String ftpMode, String transferMode) throws Exception {
        BlockingQueue<AbstractFileTransfer> pool = new ArrayBlockingQueue<>(SystemParam.FTP_CONNECTION_POOL);
        while (pool.size() < SystemParam.FTP_CONNECTION_POOL) {
            pool.offer(GetFileUtils.buildGetfileClass(ftpType, logger, host, port,
                    username, password, processCode, ftpRemoteDir, ftpMode, transferMode));
        }
        return pool;
    }
    
    private static AbstractFileTransfer getAbstractFileTransfer(String ftpType, Logger logger, String host,
            int port, String username, String password, String processCode,
            String ftpRemoteDir, String ftpMode, String transferMode) throws Exception {
        return GetFileUtils.buildGetfileClass(ftpType, logger, host, port,
                    username, password, processCode, ftpRemoteDir, ftpMode, transferMode);
    }
}
