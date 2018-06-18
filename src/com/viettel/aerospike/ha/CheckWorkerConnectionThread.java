package com.viettel.aerospike.ha;

import com.viettel.aerospike.main.Client;
import com.viettel.mmserver.base.ProcessThreadMX;
import org.apache.log4j.Logger;

public class CheckWorkerConnectionThread
        extends ProcessThreadMX {

    private static final Logger logger = Logger.getLogger(CheckWorkerConnectionThread.class.getName());

    public CheckWorkerConnectionThread(String threadName) {
        super(threadName);
    }

    protected void process() {
        try {
            Client.getInstance().checkAndReconnect();
            Client.getSequenceInstance().checkAndReconnect();
            Client.getInsertInstance().checkAndReconnect();
            Thread.sleep(10000);
        } catch (Throwable e) {
            logger.error(e, e);
        }
    }
}
