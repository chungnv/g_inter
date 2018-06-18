/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.worker.impl;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.log.BaseLog;
import com.viettel.ginterconnect.logfile.FileProcessLog;
import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.process.bean.MapConvert;
import com.viettel.ginterconnect.process.bean.Switchboard;
import com.viettel.ginterconnect.process.exception.ConvertException;
import com.viettel.ginterconnect.process.worker.AbstractWorkerBusiness;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.FileUtil;
import com.viettel.ginterconnect.util.GIClient;
import com.viettel.ginterconnect.util.GIUtils;
import com.viettel.ginterconnect.util.SystemParam;
import com.viettel.ginterconnect.worker.convert.BaseConverter;
import java.io.File;

/**
 *
 * @author
 */
public class CONVERT extends AbstractWorkerBusiness {

    private BaseConverter baseConvert;

    //for param
    private String P_FILENAME = "";
    private String P_WORKING_DIR = "";
    private String P_OUTPUT_DIR = "";
    private String P_FILE_ID = "";
    private String P_IN_SEPARATE = "";
    private String P_OUT_SEPARATE = "";
    private String P_SWITCH_ID = "";
    private String P_FILTER_BACKUP = "";

    @Override
    public boolean doBusiness() {
        String configFile = "";
        //1. Get className and Config File
        long beginTime = System.currentTimeMillis();
        if (P_SWITCH_ID == null) {
            logger.error("JobID " + currentJob.getID() + " null switchboard");
            return false;
        }
        Switchboard switchboard = GIClient.getInstance().getSwitchboard(P_SWITCH_ID);
        if (switchboard == null) {
            logger.error("Switchboard Id " + P_SWITCH_ID + " is not found");
            return false;
        }
        MapConvert mapConvert = GIClient.getInstance()
                .getMapConvert(switchboard.getType(), switchboard.getCountry());
        if (mapConvert == null) {
            logger.error("MapConvert for " + P_SWITCH_ID + " is not found");
            return false;
        }
        try {
            baseConvert = (BaseConverter) Class.forName(Constants.WORKER_CONVERT_IMPL_PCKG
                    + mapConvert.getConvertClassName()).newInstance();
            baseConvert.setTimezone(mapConvert.getTimeZone());
            configFile = mapConvert.getConvertConfigPath();
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex);
            return false;
        }
        String errorDir = SystemParam.workingDir + Constants.CONVERT_ERR_DIR + P_SWITCH_ID + File.separator;
        String convertDir = SystemParam.workingDir + Constants.CONVERT_OK + P_SWITCH_ID + File.separator;
        FileUtil.mkdirs(errorDir, convertDir);

        P_OUTPUT_DIR = convertDir;
//        String errorDir = SystemParam.getWorkingDir() + Constants.CONVERT_ERR_DIR + P_SWITCH_ID;
        try {
//            logger.info("Start convert file: " + P_WORKING_DIR + P_FILENAME);
            String inputFile = P_WORKING_DIR + File.separator + P_FILENAME;
            ConvertLog convertLog = baseConvert.convert(P_WORKING_DIR, P_OUTPUT_DIR, errorDir,
                    P_FILENAME, "txt", logger, configFile);
            P_IN_SEPARATE = baseConvert.getSeparate();
            if (convertLog != null) {
                logInfo("Convert successfully: " + P_WORKING_DIR + P_FILENAME);
                //update job cu
                try {
                    FileProcessLog getLog = new FileProcessLog("getfilelog", "|");
                    BaseLog baseLog = new BaseLog();
                    baseLog.setFileId(P_FILE_ID);
                    baseLog.setSourceFile(P_WORKING_DIR + File.separator + P_FILENAME);
                    baseLog.setDestinationFile(P_OUTPUT_DIR + File.separator + P_FILENAME + ".txt");
                    baseLog.setRatedRecord(Long.valueOf(baseConvert.getTotalConverted()));
                    baseLog.setSuccessRecord(Long.valueOf(baseConvert.getTotalConverted()));
                    baseLog.setProcessTime(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
                    baseLog.setWorkerId(this.getWorker().getName());
                    baseLog.setActionType("CONVERT");
                    baseLog.setDuration(System.currentTimeMillis() - beginTime);
                    getLog.debug(baseLog);
                } catch (NumberFormatException ex) {
                    logger.error(ex.getMessage(), ex);
                }
                if (updateSuccessJob()) {
                    logInfo("Process success jobs: " + currentJob.getPrimaryKey());
                    //sinh job moi
                    P_FILENAME = P_FILENAME + ".txt";
                    P_WORKING_DIR = P_OUTPUT_DIR;
                    JobsBO job = createNewJob();
                    if (job == null) {
                        logger.error("Create new job fail for job: " + currentJob.getPrimaryKey());
                        return false;
                    } else {
                        //delete input file
                        File deleteInputFile = new File(inputFile);
                        if (deleteInputFile.exists()) {
                            if (deleteInputFile.delete()) {
                                logger.info("Delete success file " + inputFile);
                            }
                        }
                    }
                    return true;
                }
            }
        } catch (ConvertException ce) {
            if (ce.getPriority() == ConvertException._FAIL) {
                logger.error(ce.getMessage() + this.getCurrentJob().getPrimaryKey(), ce);
                //move to fail
                storeFailJob(ce.getMessage());
            } else if (ce.getPriority() == ConvertException._CONFIG_FAIL) {
                logger.error(ce.getMessage(), ce);
            }
            return false;
        } catch (Exception ex) {
            logger.error(ex.getMessage() + this.getCurrentJob().getPrimaryKey(), ex);
            //move to fail
            storeFailJob(ex.getMessage());
        }
        return false;
    }

    @Override
    public void afterBusiness() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
