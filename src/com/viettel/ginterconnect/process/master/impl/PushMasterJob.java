/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.master.impl;

import com.aerospike.client.query.Filter;
import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.master.get.AbstractFileTransfer;
import com.viettel.ginterconnect.master.get.GetFilePool;
import com.viettel.ginterconnect.process.bean.MasterJob;
import com.viettel.ginterconnect.process.master.AbstractMasterBusiness;
import com.viettel.ginterconnect.process.queue.InsertJobQueue;
import com.viettel.ginterconnect.process.worker.impl.PUSH;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIClient;
import com.viettel.ginterconnect.util.GIUtils;
import com.viettel.ginterconnect.util.ParseFileNameStandard;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author ubuntu
 */
public class PushMasterJob extends AbstractMasterBusiness {

    //param config
    private String P_FTP_HOST = "";
    private int P_FTP_PORT = 0;
    private String P_FTP_USERNAME = "";
    private String P_FTP_PASSWORD = "";
    private String P_FTP_DIR = "";
    private String P_FTP_TMP_DIR = "";
    private String P_LOCAL_DIR = "";
    private String P_FTP_TYPE = "FTP";
    private long P_FTP_MODE = 0L;
    private long P_TRANSFER_TYPE = 0L;
//    private Date P_CURRENT_TIME = new Date();
//    private long P_CURRENT_SEQ = -1L;
//    private long P_PRIORITY_TYPE = Constants.PRIORITY_JUST_MATCH_NAME;
    private long P_STOP_WHEN_ERR = 0L;
//    private long P_MIN_SEQ = -1L;
//    private long P_MAX_SEQ = -1L;
    private long P_IS_BACKUP = -1L;
    private String P_BACKUP_DIR = "";
    private String P_NAME_PATTERN = "*";
    private long P_SWITCH_ID = 1L;
    private String P_COMPRESS = "";
    private String P_ZIP_PASSWORD = "";
    private long BATCH_FILE = 1;
    //param parse
    private String P_FILENAME = "";
    private String P_FILESEQ = "";
    private String P_FILE_TIME = "";
    private String P_NAME_TIME = "";
    private String P_SAVE_TIME = "";

    @Override
    public boolean doBusiness() throws Exception {
        AbstractFileTransfer getFile = null;
        //valiate
        if (!validateParam()) {
            logger.error("Master: " + this.getMasterProcess().getMasterProcessBO().getMasterProcessCode() + ": " + "Missing param config");
            return false;
        }

        List<String> waitingFile = new ArrayList<>();
        List<MasterJob> lstJob = GIClient.getInstance().getRecordByFilter(MasterJob.class, Filter.equal("CREATE_PROCESS",
                this.getMasterProcess().getMasterProcessBO().getMasterProcessCode()));
        if (lstJob != null && lstJob.size() > 0) {
            if (lstJob.size() > 10) {
                logInfo("There is existing job");
                return true;
            } else {
                for (MasterJob job : lstJob) {
                    String filename = getFileNameFromJobs(job);
                    if (filename == null) {
                        logInfo("There is existing job. get filename null");
                        return true;
                    }
                    waitingFile.add(filename);
                }
            }
        }

        ParseFileNameStandard parseFileName = new ParseFileNameStandard(logger);
        parseFileName.setPatternFileName(P_NAME_PATTERN);
        //
        File[] lstLocalFiles = (new File(P_LOCAL_DIR)).listFiles();
        List<Integer> lstExpectedFile = findExpectedFiles(parseFileName, lstLocalFiles, waitingFile, BATCH_FILE);

//        try {
//            getFile = GetFilePool.getConnection(P_FTP_TYPE.toUpperCase(), logger, P_FTP_HOST, P_FTP_PORT,
//                    P_FTP_USERNAME, P_FTP_PASSWORD, processCode, P_FTP_DIR);
//            if (getFile == null) {
//                logger.error("Cannot get connection");
//                return false;
//            }
//        } catch (Exception ex) {
//            logger.error(ex.getMessage(), ex);
//            if (getFile != null) {
//                GetFilePool.releaseConnection(P_FTP_HOST, getFile, logger);
//            }
//            return false;
//        }

        if (lstExpectedFile != null && lstExpectedFile.size() > 0) {
            for (int expectFileIndex : lstExpectedFile) {
                fileProcess(lstLocalFiles, parseFileName, expectFileIndex);
            }
        } else {
            logInfo("There is no file matched... ");
        }

        return true;
    }

    public boolean validateParam() {
        return true;
    }

    private String getFileNameFromJobs(MasterJob job) {
//        String jobParam[] = job.getJOB_FIELD().split(Constants.JOB_FLOW_SPLIT);
        JSONObject jObject = new JSONObject(job.getJOB_PARAM());
//        for (int i = 0; i < jobParam.length; i++) {
//            if ("P_FILENAME".equals(jobParam[i])) {
//                return job.getJOB_PARAM().split(Constants.JOB_FLOW_SPLIT)[i];
//            }
//        }
        if (jObject.has("P_FILENAME")) {
            return jObject.getString("P_FILENAME");
        }
        return null;
    }

    private List<Integer> findExpectedFiles(ParseFileNameStandard parseFileName, File[] listFile, List<String> waitingFile, long batchFile) {
        List<Integer> lstMatchedFile = new ArrayList<>();
        for (int i = 0; i < listFile.length; i++) {
            String fileName = listFile[i].getName();
            if (parseFileName.Parse(fileName)) {
                if (waitingFile == null || !waitingFile.contains(fileName)) {
                    lstMatchedFile.add(i);
                    if (lstMatchedFile.size() == batchFile) {
                        break;
                    }
                }
            }
        }
        return lstMatchedFile;
    }

    private void fileProcess(final File[] files, final ParseFileNameStandard parseFileName, int expectFileIndex) {
        File expectedFile = files[expectFileIndex];
        P_FILENAME = expectedFile.getName();
        parseFileName.Parse(P_FILENAME);
        P_FILESEQ = parseFileName.getSeq() + "";
        P_FILE_TIME = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT)).format(expectedFile.lastModified());
        if (parseFileName.getTimestamp() != null) {
            P_NAME_TIME = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT)).format(parseFileName.getTimestamp());
        }
//        if (P_PRIORITY_TYPE == Constants.PRIORITY_SEQUENCE_AND_FILENAME_TIME) {
//            P_SAVE_TIME = P_NAME_TIME;
//        } else {
        P_SAVE_TIME = P_FILE_TIME;
//        }
        logInfo("Create job push file: " + P_FILENAME);
        this.setWorkerClass(PUSH.class);
        createJob();
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
