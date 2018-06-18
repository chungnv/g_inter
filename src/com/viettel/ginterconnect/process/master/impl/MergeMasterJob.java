/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.master.impl;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.process.master.AbstractMasterBusiness;
import com.viettel.ginterconnect.process.queue.InsertJobQueue;
import com.viettel.ginterconnect.process.worker.impl.MERGE;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIUtils;

/**
 *
 * @author ubuntu
 */
public class MergeMasterJob extends AbstractMasterBusiness {

    String P_INPUT_DIR = "";
    String P_OUTPUT_DIR = "";
    String P_IN_PATTERN = "";
    String P_OUT_PATTERN = "";
    String P_MERGE_TYPE = "";
    String P_OUTPUT_SEQ = "";
    String P_SWITCH_ID = "";
    long P_NUMBER_FILES = 1;
    long P_WAIT_TIME = 60;

    @Override
    public boolean doBusiness() throws Exception {
        if (!validateParam()) {
            logger.error("Master: " + this.getMasterProcess().getMasterProcessBO().getMasterProcessCode() + ": " + "Missing param config");
            return false;
        }
        //kiem tra job
        if (checkExistJob()) {
            logInfo("There is existing job");
            return true;
        }
        
        this.setWorkerClass(MERGE.class);
//        P_SWITCH_ID = SWITCH_ID + "";
        if (createJob() != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateParam() {
        return true;
    }

    @Override
    public JobsBO createJob() {
        try {
            JobsBO object = new JobsBO();
            object.setCreateProcess(this.getMasterProcess().getMasterProcessBO().getMasterProcessCode());
            object.setProcessCode(this.getMasterProcess().getMasterProcessBO().getMasterProcessCode());
            object.setCreateDate(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
            object.setFlow(masterProcess.getMasterProcessBO().getFlow());
            object.setJobsStatus(Constants.JOB_NEW_STATUS);
            autoGenJobParam(object);
            object.setJobType("Merge");
            object.setProcessDate(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
            InsertJobQueue.getInstance(50).insertToQueue(object);
            return object;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

}
