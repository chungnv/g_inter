/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.logfile;

import com.viettel.aerospike.bo.JobsBO;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class SuccessJobsLogger extends Logger {

//    private String name;
    private String spliter;
    private Logger logger = Logger.getLogger(SuccessJobsLogger.class);

    public SuccessJobsLogger(String name, String spliter) {
        super(name);
        this.name = name;
        this.spliter = spliter;
    }

    @Override
    public void info(Object obj) {
        JobsBO getLog = (JobsBO) obj;
        logger.info(getLog.toString(this.spliter));
    }
}
