package com.viettel.ginterconnect.log;

import org.apache.log4j.Logger;

/**
 * Created by hoangsinh on 09/08/2017.
 */
public class LoggerFilter extends Logger {
    String workerName;
    Logger logger;

    public LoggerFilter(Logger logger, String workerName) {
        super(logger.getName());
        this.workerName = workerName;
        this.logger = logger;
    }

    @Override
    public void error(Object message) {
        String className = new Exception().getStackTrace()[1].toString();
//        logger.error("Worker: " + workerName + "|Date: " + new Date() + "|Class: " + className + "\nMessage: " + message);
        logger.error("Worker: " + workerName + "|Message: " + message);
    }

    @Override
    public void error(Object message, Throwable t) {
        String className = new Exception().getStackTrace()[1].toString();
//        logger.error("Worker: " + workerName + "|Date: " + new Date() + "|Class: " + className + "\nMessage: " + message, t);
        logger.error("Worker: " + workerName + "|Message: " + message, t);
    }

    @Override
    public void info(Object message) {
        String className = new Exception().getStackTrace()[1].toString();
//        logger.info("Worker: " + workerName + "|Date: " + new Date() + "|Class: " + className + "\nMessage: " + message);
//        logger.info("Worker: " + workerName + "|Date: " + new Date() + "|Class: " + className + "\nMessage: " + message);
        logger.info("Worker: " + workerName + "|Message: " + message);
    }
}
