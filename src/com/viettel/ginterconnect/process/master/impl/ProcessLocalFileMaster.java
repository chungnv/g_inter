/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.master.impl;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.process.master.AbstractMasterBusiness;
import com.viettel.ginterconnect.process.queue.InsertJobQueue;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIUtils;
import com.viettel.ginterconnect.util.ParseFileNameStandard;
import java.io.File;

/**
 *
 * @author
 */
public class ProcessLocalFileMaster extends AbstractMasterBusiness {

    private String P_WORKING_DIR = "";
    private String P_NAME_PATTERN = "";
    private String P_SWITCH_ID = "";
    private String P_OUT_SEPARATE = "";
    private String P_IN_SEPARATE = "";
    private String P_FILE_ID = "";
    private String P_HEADER = "";
    private long P_BATCH_FILE = 1;
    private String P_FILENAME = "";

    @Override
    public boolean doBusiness() {
        //list file
        if (checkExistJob()) {
            logInfo("There is existing job");
            return true;
        }
        try {
            //list file
            this.setWorkerClass(Class.forName("com.viettel.ginterconnect.process.worker.impl."
                    + this.getMasterProcess().getMasterProcessBO().getStep1().trim().toUpperCase()));
        } catch (ClassNotFoundException ex) {
            logError("Worker not found: " + "com.viettel.ginterconnect.process.worker.impl."
                    + this.getMasterProcess().getMasterProcessBO().getStep1().trim().toUpperCase());
        }
        File[] inputFiles = (new File(P_WORKING_DIR)).listFiles();

        ParseFileNameStandard parseFileName = new ParseFileNameStandard(logger);
        parseFileName.setPatternFileName(P_NAME_PATTERN);

        int jobNumber = 0;
        for (File file : inputFiles) {
            if (parseFileName.Parse(file.getName())) {
                jobNumber++;
                P_FILENAME = file.getName();
                try {
                    P_FILE_ID = Client.getSequenceInstance().getSequence("md_file_id") + "";
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    logError("Cannot generate fileId");
                    return false;
                }
                createJob();
                if (jobNumber >= P_BATCH_FILE) {
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public JobsBO createJob() {
        //insert ban ghi vao bang Jobs
        try {
            String nextStep = GIUtils.nextStep(masterProcess.getMasterProcessBO().getFlow(), "");
            JobsBO object = new JobsBO();
            object.setCreateProcess(this.getMasterProcess().getMasterProcessBO().getMasterProcessCode());
            object.setProcessCode(this.getMasterProcess().getMasterProcessBO().getMasterProcessCode());
            object.setCreateDate(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
            object.setFlow(masterProcess.getMasterProcessBO().getFlow());
            object.setJobsStatus(Constants.JOB_NEW_STATUS);
            autoGenJobParam(object);
            object.setJobType(nextStep);
            object.setProcessDate(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
//            Client.getInstance().insertJobsRecord(id, object);
            InsertJobQueue.getInstance(50).insertToQueue(object);
            return object;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

}
