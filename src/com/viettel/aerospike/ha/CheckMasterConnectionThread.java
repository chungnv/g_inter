package com.viettel.aerospike.ha;

import com.viettel.aerospike.main.Client;
import com.viettel.mmserver.base.ProcessThreadMX;
import org.apache.log4j.Logger;

public class CheckMasterConnectionThread
        extends ProcessThreadMX {

    private static final Logger logger = Logger.getLogger(CheckMasterConnectionThread.class.getName());

    public CheckMasterConnectionThread(String threadName) {
        super(threadName);
    }

    protected void process() {
        try {
            Client.getInstance().checkAndReconnect();
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }
        try {
            Client.getSequenceInstance().checkAndReconnect();
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }
        try {
            Client.getUpdateJobInstance().checkAndReconnect();
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }
        try {
            Thread.sleep(30000);
        } catch (Throwable e) {
            logger.error(e.getMessage());
        }
    }
}
