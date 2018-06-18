/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.master;

import com.viettel.aerospike.bo.MasterThreadBO;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author
 */
public class RunMasterJob implements Runnable {

    AbstractMasterBusiness masterBusiness;
    MasterThreadBO masterProcessBO;
    String threadName;
    private Logger logger = Logger.getLogger(RunMasterJob.class);

    public RunMasterJob(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void run() {
        try {
            long start = System.currentTimeMillis();
            if (masterProcessBO != null) {
                logger.info(this.getThreadName() + " run for " + masterProcessBO.getMasterId());
                doMasterJob();
                logger.warn(this.getThreadName() + " run for " + masterProcessBO.getMasterId() + " process time: " + (System.currentTimeMillis() - start));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void doMasterJob() {
        try {
            this.setMasterProcessBO(masterProcessBO);
            this.setMasterBusiness(getMasterClass(Constants.MASTER_GET_IMPL_PACKAGE_NAME + masterProcessBO.getProcessClass()));
            masterBusiness.setMasterProcess(this);
            masterBusiness.setProperty();
            masterBusiness.doBusiness();
            //update lastRunTime
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            logger.info("Set last time..");

            prepareForNextRun(getNextRunDate());

            updateMasterBOAfterRun();
        }
    }

    private void prepareForNextRun(Date nextDate) {
        masterProcessBO.setLastRunTime(GIUtils.genDateStr(Constants.DEFAULT_DATE_FORMAT, nextDate));
        masterProcessBO.setRunStatus(0);
        masterProcessBO.setIp("");
        logger.info("Thread sleep..");
    }

    private Date getNextRunDate() {
        if (!StringUtils.isEmpty(masterProcessBO.getScheduler())) {
            Date previousDate = new Date();
            if (!StringUtils.isEmpty(masterProcessBO.getLastRunTime())) {
                try {
                    previousDate = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT)).parse(masterProcessBO.getScheduler());
                } catch (ParseException exx) {
                    logger.error(exx.getMessage(), exx);
                }
            }
            return GIUtils.getStartTime(masterProcessBO.getScheduler(), previousDate);
        } else {
            long sleepTime = masterProcessBO.getSleepTime() == null ? 10000 : masterProcessBO.getSleepTime();
            return new Date(System.currentTimeMillis() + sleepTime);
        }
    }

    private void updateMasterBOAfterRun() {
        if (Client.getInstance().updateMasterBOAfterRun(masterProcessBO) == null) {
            logger.info("Update master process failed. retry ");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                logger.error(ex.getMessage(), ex);
            }
            Client.getInstance().updateMasterBOAfterRun(masterProcessBO);
        }
    }

    private AbstractMasterBusiness getMasterClass(String className) {
        Class masterClass;
        AbstractMasterBusiness masterObject = null;
        try {
            masterClass = (Class) Class.forName(className);
            if (masterClass == null) {
                return null;
            } else {
                masterObject = (AbstractMasterBusiness) masterClass.newInstance();
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return masterObject;
    }

    private AbstractMasterBusiness getMasterBusiness() {
        return masterBusiness;
    }

    private void setMasterBusiness(AbstractMasterBusiness masterBusiness) {
        this.masterBusiness = masterBusiness;
    }

    public MasterThreadBO getMasterProcessBO() {
        return masterProcessBO;
    }

    public void setMasterProcessBO(MasterThreadBO masterProcessBO) {
        this.masterProcessBO = masterProcessBO;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}
