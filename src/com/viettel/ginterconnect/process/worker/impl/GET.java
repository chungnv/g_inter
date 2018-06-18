/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.worker.impl;

import com.aerospike.client.query.Filter;
import com.viettel.aerospike.bo.JobsBO;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.compress.FileCodecer;
import com.viettel.ginterconnect.log.BaseLog;
import com.viettel.ginterconnect.master.get.AbstractFileTransfer;
import com.viettel.ginterconnect.master.get.GetFilePool;
import com.viettel.ginterconnect.process.bean.MasterProcessBO;
import com.viettel.ginterconnect.logfile.FileProcessLog;
import com.viettel.ginterconnect.process.worker.AbstractWorkerBusiness;
import com.viettel.ginterconnect.util.GIClient;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.FileUtil;
import com.viettel.ginterconnect.util.GIUtils;
import com.viettel.ginterconnect.util.SystemParam;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author
 */
public class GET extends AbstractWorkerBusiness {

    //for job param
    private String P_FTP_TYPE = "FTP";
    private String P_FTP_HOST = "";
    private String P_FTP_PORT = "";
    private String P_FTP_USERNAME = "";
    private String P_FTP_PASSWORD = "";
    private String P_FTP_REMOTE_DIR = "";
    private String P_LOCAL_DIR = "";
    private String P_FILE_SEQUENCE = "";
    private String P_MODIFIED_FILE_TIME = "";
    private String P_FILENAME_TIME = "";
    private String P_PROCESS_CODE = "";
    private String P_FILENAME = "";
    private String P_SWITCH_ID = "";
    private String P_FTP_COMPRESS = "";
    private String P_DELETE_AFTER_GET = "";
    private String P_WORKING_DIR = "";
    private String P_FILE_ID = "";
    private String P_MASTER_ID = "";
    private String P_FTP_MODE = "0";
    private String P_FTP_TRANSFER_TYPE = "0";
    private String P_ZIP_PASSWORD = "";
    private String P_STOP_WHEN_ERR = "";
    private String P_WRITING_CHECK = "";
    private String P_IN_SEPARATE = "";
    private String P_FILE_OUT_SEPARATE = "";
    private String P_STEP_SEQ = "";
    private String P_SAVE_TIME = "";
    private String P_HEADER = "";
    private String P_FILTER_BACKUP = "";

    public GET() {
//        this.processType = AbstractWorkerBusiness.GET_PROCESS_TYPE;
    }

    @Override
    public boolean doBusiness() {
        //1. connect ftp
        AbstractFileTransfer getFile = null;
        long startTime = System.currentTimeMillis();
        long beginTime = System.currentTimeMillis();
        try {
            getFile = GetFilePool.getConnection(P_FTP_TYPE, logger,
                    P_FTP_HOST, Integer.parseInt(P_FTP_PORT), P_FTP_USERNAME,
                    P_FTP_PASSWORD, P_PROCESS_CODE, P_FTP_REMOTE_DIR, P_FTP_MODE, P_FTP_TRANSFER_TYPE);
            logger.info("create connection to " + P_FTP_HOST + " time: " + (System.currentTimeMillis() - startTime));
            logInfo("Connect FTP Server success");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            if (getFile != null) {
                GetFilePool.releaseConnection(P_FTP_HOST, getFile, logger);
            }
//            storeFailJob("Connect FTP Exception");
            return false;
        }
        //2. tao dir local
        if (StringUtils.isEmpty(P_LOCAL_DIR)) {
            P_WORKING_DIR = SystemParam.workingDir + Constants.DOWNLOAD_OK + P_SWITCH_ID + File.separator;
        } else {
            P_WORKING_DIR = P_LOCAL_DIR;
        }
        String tmpLocalDir = SystemParam.workingDir + Constants.DOWNLOAD_TEMP + P_SWITCH_ID + File.separator;
        String tmpCompressDir = SystemParam.workingDir + Constants.DECOMPRESS_TEMP + P_SWITCH_ID + File.separator + P_FILENAME + File.separator;
        String decompressDir = SystemParam.workingDir + Constants.DECOMPRESS_OK + P_SWITCH_ID + File.separator;
        FileUtil.mkdirs(P_WORKING_DIR, tmpLocalDir, tmpCompressDir, decompressDir);
        //3. download
        try {
            startTime = System.currentTimeMillis();
            if (getFile.download(P_FTP_REMOTE_DIR, P_FILENAME,
                    tmpLocalDir + P_FILENAME, false)) {
                File downloadedFile = new File(P_WORKING_DIR + P_FILENAME);
                (new File(tmpLocalDir + P_FILENAME)).renameTo(downloadedFile);
                logger.info(P_FTP_HOST + ": Download " + P_FILENAME + " time: " + (System.currentTimeMillis() - startTime));
            } else {
                logger.info("Download " + P_FILENAME + " fail. Move error job");
                GetFilePool.releaseConnection(P_FTP_HOST, getFile, logger);
                storeFailJob("Download exception");
                return false;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            GetFilePool.releaseConnection(P_FTP_HOST, getFile, logger);
//            storeFailJob(ex.getMessage());
            return false;
        }
        startTime = System.currentTimeMillis();
        try {
            P_FILE_ID = Client.getSequenceInstance().getSequence("md_file_id") + "";
        } catch (Exception ex) {
            return exceptionFailReturn(ex, getFile);
        }
        //4. xu ly giai nen
        List<String> lstFileDecompress = new ArrayList<>();
        boolean isCompress = (!StringUtils.isEmpty(P_FTP_COMPRESS) && "1".equals(P_FTP_COMPRESS));
        if (isCompress) {
            logInfo("decompress file " + P_FILENAME);
            try {
                lstFileDecompress = decompressAndMoveFile(tmpCompressDir, decompressDir);
            } catch (Exception ex) {
                return exceptionFailReturn(ex, getFile);
            }
        }
        //5. check delete and backup
        if (P_DELETE_AFTER_GET != null && "1".equals(P_DELETE_AFTER_GET)) {
            try {
                if (getFile.delete(P_FTP_REMOTE_DIR, P_FILENAME)) {
                    logger.info(this.getWorker().getName() + " Delete file success: " + P_FTP_REMOTE_DIR + File.separator + P_FILENAME);
                } else {
                    logger.info("Delete " + P_FILENAME + " fail. Move error job");
                    storeFailJob("Delete fail");
                    return false;
                }
            } catch (Exception ex) {
                return exceptionFailReturn(ex, getFile);
            }
        }
        logger.info("Gen seq, delefile " + P_FILENAME + " time: " + (System.currentTimeMillis() - startTime));
        GetFilePool.releaseConnection(P_FTP_HOST, getFile, logger);
        //5. ghi log
        startTime = System.currentTimeMillis();
        //6. update param
        updateParam(P_FILE_SEQUENCE, P_SAVE_TIME, startTime);
        //7. Update job success
        startTime = System.currentTimeMillis();
        if (updateSuccessJob()) {
            logInfo("Process success jobs: " + currentJob.getPrimaryKey());
            //8. Create new job
            storeLog(beginTime, "GET", P_FILE_ID,
                        P_FTP_REMOTE_DIR + File.separator + P_FILENAME, P_WORKING_DIR + File.separator + P_FILENAME, null);
            if (isCompress) {
                P_WORKING_DIR = decompressDir;
                String srcFile = P_FILENAME;
                String srcFileId = P_FILE_ID;
                for (String fname : lstFileDecompress) {
                    P_FILENAME = fname;
                    try {
                        P_FILE_ID = Client.getSequenceInstance().getSequence("md_file_id") + "";
                    } catch (Exception ex) {
                        return exceptionFailReturn(ex, getFile);
                    }
                     storeLog(beginTime, "DECOMPRESS", P_FILE_ID,
                        srcFile, P_WORKING_DIR + File.separator + P_FILENAME, srcFileId);
                    JobsBO job = createNewJob();
                    if (job == null) {
                        logger.error("Create new job fail for job: " + currentJob.getPrimaryKey());
                        return false;
                    }
                }
                //delete decompress dir
            } else {
                JobsBO job = createNewJob();
                if (job == null) {
                    logger.error("Create new job fail for job: " + currentJob.getPrimaryKey());
                    return false;
                }
            }
            logger.info("Update, create job " + P_FILENAME + " time: " + (System.currentTimeMillis() - startTime));
        } else {
            logger.error("Update status fail jobs: " + currentJob.getPrimaryKey());
            return false;
        }
        return true;
    }

    private void storeLog(Long beginTime, String type, String fileId, String srcFile, String desFile, String parentLogId) {
        FileProcessLog getLog = new FileProcessLog("getfilelog", "|");
        try {
            BaseLog baseLog = new BaseLog();
            baseLog.setFileId(fileId);
            baseLog.setParentLogId(parentLogId);
            baseLog.setSourceFile(srcFile);
            baseLog.setDestinationFile(desFile);
            baseLog.setFileSeq(Long.valueOf(P_FILE_SEQUENCE));
            baseLog.setFileTime(P_FILENAME_TIME);
            baseLog.setFileModifiedTime(P_MODIFIED_FILE_TIME);
            baseLog.setProcessTime(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
            baseLog.setWorkerId(this.getWorker().getName());
            baseLog.setActionType(type);
            baseLog.setDuration(System.currentTimeMillis() - beginTime);
//            GIClient.getInstance().insertObject(getFileLog);
            getLog.info(baseLog);
        } catch (NumberFormatException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private boolean exceptionFailReturn(Exception ex, AbstractFileTransfer getFile) {
        logger.error(ex.getMessage(), ex);
        GetFilePool.releaseConnection(P_FTP_HOST, getFile, logger);
        storeFailJob(ex.getMessage());
        return false;
    }

    private List<String> decompressAndMoveFile(String tmpCompressDir, String decompressDir) throws Exception {
        logInfo("decompress file " + P_FILENAME);
        FileUtils.cleanDirectory(new File(tmpCompressDir));
        if (SystemParam.DECOMPRESS_TYPE != null
                && SystemParam.DECOMPRESS_TYPE.toUpperCase().equals("LIB")) {
            FileCodecer fileCodecer = new FileCodecer();
            fileCodecer.setLogger(this.logger);
            fileCodecer.extractMultiple(P_WORKING_DIR + P_FILENAME, tmpCompressDir, 1L,
                    0L, P_ZIP_PASSWORD);
        } else {
            GIUtils.extractByCmd(P_WORKING_DIR + P_FILENAME, tmpCompressDir, 1L,
                    0L, P_ZIP_PASSWORD);
        }
        List<String> lstFileDecompress = processSuccessfulExtractedFiles(P_FILENAME, P_WORKING_DIR, decompressDir, null, tmpCompressDir, 1L);
        //delete temp dir
        FileUtils.deleteDirectory(new File(tmpCompressDir));
        logInfo("decompress file " + P_FILENAME + " successfully. Delete temp dir success");
        return lstFileDecompress;
    }

    private boolean checkNumberOfFilesInFolder(String folderPath, Long threshold) {
        boolean result = true;
        if (threshold != null && !threshold.equals(0L)) {
            File folder = new File(folderPath);
            int nof = folder.list().length;
            if (nof == threshold.intValue()) {
                result = true;
            } else {
                this.logger.error("The number of extracted files (" + nof + ") is not equal with expectation (" + threshold.intValue() + ")");
                result = false;
            }
        }
        return result;
    }

    private List<String> processSuccessfulExtractedFiles(
            String fileName, String inputPath, String outputFilePath, String backupFilePath, String tmpOutputPath,
            Long fileId) throws Exception {
        //Backup
        if (backupFilePath != null) {
            if (!FileUtil.moveFileToFolder(inputPath, fileName, backupFilePath, logger)) {
                this.logger.error("Can't save " + inputPath + " for backup");
//                System.out.println("Can't save " + inputPath + " for backup");
            }
        } else {
            boolean isDelete = false;
            while (!isDelete) {
                isDelete = (new File(inputPath + File.separator + fileName)).delete();
                this.logger.info("Delete file " + inputPath + File.separator + fileName + " successfully!");
            }
        }
        //ghi log
//        for (String tmpOutputFile : (new File(tmpOutputPath)).list()) {
//            CodecLogBO codecLogBO = createCodecLogBO(fileId, processParamBO, tmpOutputFile, Constant.LOG_TYPE_SUCCESS);
//            codecLogBOList.add(codecLogBO);
//        }
//        return FileUtil.moveDecompressFile(tmpOutputPath, outputFilePath, fileId);
        List<String> lstMoveFile = new ArrayList<>();
        FileUtil.processFileDir(tmpOutputPath, outputFilePath, fileId, lstMoveFile);
        return lstMoveFile;
    }

    @Override
    public void afterBusiness() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void updateParam(String currentSeq, String currentTime, Long startTime) {
        List<MasterProcessBO> lstMasterProcessBO = GIClient.getInstance().getRecordByFilter(MasterProcessBO.class,
                Filter.equal("MASTER_ID", P_MASTER_ID));
        if (lstMasterProcessBO != null) {
            for (MasterProcessBO mtbo : lstMasterProcessBO) {
                if (mtbo.getMasterId().equals(P_MASTER_ID)) {
//                    if (mtbo.getCurrentSeq() != null && mtbo.getCurrentTime() != null) {
                    mtbo.setCurrentSeq(currentSeq);
                    mtbo.setCurrentTime(currentTime);
                    GIClient.getInstance().insertObject(mtbo);
//                    }
                }
            }
        }
        logger.info("Gen seq, delefile " + P_FILENAME + " time: " + (System.currentTimeMillis() - startTime));
    }
}
