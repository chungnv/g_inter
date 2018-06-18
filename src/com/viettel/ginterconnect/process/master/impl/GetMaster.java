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
import com.viettel.ginterconnect.master.get.GetFileUtils;
import com.viettel.ginterconnect.process.bean.MasterJob;
import com.viettel.ginterconnect.process.master.AbstractMasterBusiness;
import com.viettel.ginterconnect.process.queue.InsertJobQueue;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIClient;
import com.viettel.ginterconnect.util.GIUtils;
import com.viettel.ginterconnect.util.ParseFileNameStandard;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.json.JSONObject;

/**
 *
 * @author
 */
public class GetMaster extends AbstractMasterBusiness {

    private String FTP_HOST = "";
    private int FTP_PORT = 0;
    private String FTP_USERNAME = "";
    private String FTP_PASSWORD = "";
    private String FTP_REMOTE_DIR = "";
    private String FTP_TYPE = "FTP";
    private String FIELD_SPLITER = "";
    private String LOCAL_DIR = "";
    private String OUTPUT_SPLITER = ";";
    private String FTP_MODE = "0";
    private Date CURRENT_TIME = new Date();
    private long CURRENT_SEQ = -1L;
    private long PRIORITY_TYPE = Constants.PRIORITY_JUST_MATCH_NAME;
    private long DEL_AFTER_GET = 0L;
    private long STOP_WHEN_ERR = 0L;
    private long WRITING_CHECK = 0L;
    private long MIN_SEQ = -1L;
    private long MAX_SEQ = -1L;
    private long STEP_SEQ = -1L;
    private String F_NAME_PATTERN = "*";
    private long SWITCH_ID = 1L;
    private String COMPRESS = "";
    private String ZIP_PASSWORD = "";
    private long BATCH_FILE = 1;
    private long SLEEP_TIME = 0;
    private String fileName;
    private String fileSeq;
    private String fileModifedTime;
    private String filenameTime = "";
    private String saveTime;
    private String HEADER = "";
    private String TRANSFER_MODE = "0";

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

    @Override
    public boolean doBusiness() {
        AbstractFileTransfer getFile = null;
        //valiate
        if (!validateParam()) {
            logger.error("Master: " + this.getMasterProcess().getMasterProcessBO().getMasterProcessCode() + ": " + "Missing param config");
            return false;
        }

        List<String> downloadingFiles = new ArrayList<>();
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
                    downloadingFiles.add(filename);
//                }
                }
            }
        }
        ParseFileNameStandard parseFileName = new ParseFileNameStandard(logger);
        parseFileName.setPatternFileName(F_NAME_PATTERN);
        try {
            //TODO
            //1. connect to ftp server
//            getFile = GetFileUtils.buildGetfileClass(FTP_TYPE.toUpperCase(), logger, FTP_HOST, FTP_PORT,
//                    FTP_USERNAME, FTP_PASSWORD, processCode, FTP_REMOTE_DIR);
//            getFile.connect();
            getFile = GetFilePool.getConnection(FTP_TYPE.toUpperCase(), logger, FTP_HOST, FTP_PORT,
                    FTP_USERNAME, FTP_PASSWORD, processCode, FTP_REMOTE_DIR, FTP_MODE, TRANSFER_MODE);
            if (getFile == null) {
                logger.error("Cannot get connection");
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (getFile != null) {
                GetFilePool.releaseConnection(FTP_HOST, getFile, logger);
            }
            return false;
        }
        //2. list file
        FTPFile[] ftpFiles = null;
        try {
            logInfo("List file: " + FTP_REMOTE_DIR);
            ftpFiles = getFile.listFile(FTP_REMOTE_DIR);
            logInfo("List file size: " + ftpFiles.length);
        } catch (Exception ex) {
            logger.error("Master thread " + this.getMasterProcess().getThreadName() + " list dir: " + FTP_REMOTE_DIR + " error");
            logger.error(ex.getMessage(), ex);
            return false;
//            throw new DownloadFileException("LIST_FILE_FAIL", "List file fail");
        } finally {
            GetFilePool.releaseConnection(FTP_HOST, getFile, logger);
        }
        //3. check file
        if (CURRENT_SEQ > -1L && STEP_SEQ >= -1) {
            CURRENT_SEQ += STEP_SEQ;
        }

        List<Integer> lstExpectedFile = GetFileUtils.findExpectFileIndex(CURRENT_SEQ, MIN_SEQ, MAX_SEQ, STEP_SEQ,
                PRIORITY_TYPE, ftpFiles, CURRENT_TIME, parseFileName, logger, SWITCH_ID, BATCH_FILE, downloadingFiles);
        // if ok, sinh job
        if (lstExpectedFile != null && lstExpectedFile.size() > 0) {
            for (int expectFileIndex : lstExpectedFile) {
                fileProcess(ftpFiles, parseFileName, expectFileIndex);
            }
        } else {
            logInfo("There is no file matched... ");
        }
//        if (SLEEP_TIME > 0) {
//            try {
//                Thread.sleep(SLEEP_TIME);
//            } catch (InterruptedException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        }
        //ghi log
        return true;
    }

    private void fileProcess(final FTPFile[] ftpFiles, final ParseFileNameStandard parseFileName, int expectFileIndex) {
        FTPFile expectedFile = ftpFiles[expectFileIndex];
        fileName = expectedFile.getName();
        parseFileName.Parse(fileName);
        fileSeq = parseFileName.getSeq() + "";
        fileModifedTime = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT)).format(expectedFile.getTimestamp().getTime());
        if (parseFileName.getTimestamp() != null) {
            filenameTime = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT)).format(parseFileName.getTimestamp());
        }
        if (PRIORITY_TYPE == Constants.PRIORITY_SEQUENCE_AND_FILENAME_TIME) {
            saveTime = filenameTime;
        } else {
            saveTime = fileModifedTime;
        }
        logInfo("Create job download file: " + fileName);
        createJob();
    }

    @Override
    public JobsBO createJob() {
        //insert ban ghi vao bang Jobs
        try {
            String nextStep = GIUtils.nextStep(masterProcess.getMasterProcessBO().getFlow(), "");
            JobsBO object = new JobsBO();
//            Long id = Client.getSequenceInstance().getSequence(null);
//            object.setID(id);
            object.setCreateProcess(this.getMasterProcess().getMasterProcessBO().getMasterProcessCode());
            object.setProcessCode(this.getMasterProcess().getMasterProcessBO().getMasterProcessCode());
            object.setCreateDate(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
            object.setFlow(masterProcess.getMasterProcessBO().getFlow());
            object.setJobsStatus(Constants.JOB_NEW_STATUS);
            object.setJobParam(genJobParam());
//            object.setJobField(genJobField());
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

    private String genJobParam() {

        JSONObject jObject = new JSONObject();
        jObject.put("P_FTP_HOST", FTP_HOST);
        jObject.put("P_FTP_PORT", FTP_PORT + "");
        jObject.put("P_FTP_USERNAME", FTP_USERNAME);
        jObject.put("P_FTP_PASSWORD", FTP_PASSWORD);
        jObject.put("P_FTP_REMOTE_DIR", FTP_REMOTE_DIR);
        jObject.put("P_FILENAME", fileName);
        jObject.put("P_PROCESS_CODE", this.getMasterProcess().getMasterProcessBO().getMasterProcessCode());
        jObject.put("P_FTP_TYPE", FTP_TYPE);
        jObject.put("P_FTP_COMPRESS", COMPRESS);
        jObject.put("P_FTP_MODE", FTP_MODE);
        jObject.put("P_FTP_TRANSFER_TYPE", TRANSFER_MODE);
        jObject.put("P_SWITCH_ID", this.getMasterProcess().getMasterProcessBO().getSWITCH_ID() + "");
        jObject.put("P_FILE_SEQUENCE", fileSeq);
        jObject.put("P_MODIFIED_FILE_TIME", fileModifedTime);
        jObject.put("P_MASTER_ID", this.getMasterProcess().getMasterProcessBO().getMasterId() + "");
        jObject.put("P_FILENAME_TIME", filenameTime);
        jObject.put("P_ZIP_PASSWORD", ZIP_PASSWORD);
        jObject.put("P_DELETE_AFTER_GET", DEL_AFTER_GET + "");
        jObject.put("P_STOP_WHEN_ERR", STOP_WHEN_ERR + "");
        jObject.put("P_WRITING_CHECK", WRITING_CHECK + "");
        jObject.put("P_STEP_SEQ", STEP_SEQ + "");
        jObject.put("P_SAVE_TIME", saveTime);
        jObject.put("P_LOCAL_DIR", LOCAL_DIR);
        if (FIELD_SPLITER != null) {
            jObject.put("P_IN_SEPARATE", FIELD_SPLITER);
            jObject.put("P_OUT_SEPARATE", OUTPUT_SPLITER);
        }
        if (!StringUtils.isEmpty(HEADER)) {
            jObject.put("P_HEADER", HEADER);
        }
        return jObject.toString();
    }

    public boolean validateParam() {
        if (PRIORITY_TYPE != Constants.PRIORITY_JUST_MATCH_NAME) {
            if (MAX_SEQ == -1L || MIN_SEQ == -1L || STEP_SEQ == -1L || CURRENT_SEQ == -1L) {
                return false;
            }
        }
        return true;
    }

}
