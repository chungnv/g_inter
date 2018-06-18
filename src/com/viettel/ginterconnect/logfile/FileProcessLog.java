/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.logfile;

import com.viettel.ginterconnect.log.BaseLog;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class FileProcessLog extends Logger {

    private String name;
    private String spliter;
    private Logger logger = Logger.getLogger(FileProcessLog.class);

    public FileProcessLog(String name, String spliter) {
        super(name);
        this.name = name;
        this.spliter = spliter;
    }

    @Override
    public void info(Object obj) {
        //FILE_ID|ACTION_TYPE|SOURCE|DESTINATION|RATED_RECORD|SUCCESS|FAIL|RATED_SIZE|FILE_TIME|PROCESS_TIME|FILE_SEQ|
        BaseLog getLog = (BaseLog) obj;
        logger.info(getLog.toString(this.spliter));
    }
    
    @Override
    public void debug(Object obj) {
        //FILE_ID|ACTION_TYPE|SOURCE|DESTINATION|RATED_RECORD|SUCCESS|FAIL|RATED_SIZE|FILE_TIME|PROCESS_TIME|FILE_SEQ|
        BaseLog getLog = (BaseLog) obj;
        logger.debug(getLog.toString(this.spliter));
    }

}
