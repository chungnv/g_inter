/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.worker.impl;

import com.viettel.ginterconnect.master.get.AbstractFileTransfer;
import com.viettel.ginterconnect.master.get.GetFilePool;
import com.viettel.ginterconnect.process.worker.AbstractWorkerBusiness;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author ubuntu
 */
public class PUSH extends AbstractWorkerBusiness {

    private String P_FTP_HOST = "";
    private String P_FTP_PORT = "";
    private String P_FTP_USERNAME = "";
    private String P_FTP_PASSWORD = "";
    private String P_FTP_DIR = "";
    private String P_FTP_TMP_DIR = "";
    private String P_LOCAL_DIR = "";
    private String P_FTP_TYPE = "FTP";
    private String P_FTP_MODE = "";
    private String P_TRANSFER_TYPE = "";
    private String P_SWITCH_ID = "";
    private String P_COMPRESS = "";
    private String P_ZIP_PASSWORD = "";
    private String P_FILENAME = "";
    private String P_FILESEQ = "";
    private String P_FILE_TIME = "";
    private String P_NAME_TIME = "";
    private String P_SAVE_TIME = "";
    private String P_IS_BACKUP = "";
    private String P_BACKUP_DIR = "";

    @Override
    public void afterBusiness() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean doBusiness() {

        AbstractFileTransfer getFile = null;
        long startTime = System.currentTimeMillis();
        //1. get connection
        try {
            getFile = GetFilePool.getConnection(P_FTP_TYPE, logger,
                    P_FTP_HOST, Integer.parseInt(P_FTP_PORT), P_FTP_USERNAME,
                    P_FTP_PASSWORD, this.getWorker().getName(), P_FTP_TMP_DIR, P_FTP_MODE, P_TRANSFER_TYPE);
//            getFile.setModeAndTransferType(P_FTP_MODE, P_TRANSFER_TYPE);
            logger.info("create connection to " + P_FTP_HOST + " time: " + (System.currentTimeMillis() - startTime));
            logInfo("Connect FTP Server success");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            if (getFile != null) {
                GetFilePool.releaseConnection(P_FTP_HOST, getFile, logger);
            }
            return false;
        }
        //3. upload
        try {
            startTime = System.currentTimeMillis();
            if (getFile.upload(P_FTP_TMP_DIR, P_FTP_DIR, P_FILENAME,
                    P_LOCAL_DIR + File.separator + P_FILENAME, false)) {
                logger.info(P_FTP_HOST + ": Upload " + P_FILENAME + " time: " + (System.currentTimeMillis() - startTime));
                if (StringUtils.isEmpty(P_IS_BACKUP) || !"1".equals(P_IS_BACKUP.trim()) || StringUtils.isEmpty(P_BACKUP_DIR)) {
                    if (FileUtils.deleteQuietly(new File(P_LOCAL_DIR + File.separator + P_FILENAME))) {
                        logger.info("Delete file: " + (P_LOCAL_DIR + File.separator + P_FILENAME) + ". successfully");
                    } else {
                        logger.error("Delete file: " + (P_LOCAL_DIR + File.separator + P_FILENAME) + ". failed");
                    }
                } else {
                    try {
                        FileUtils.moveFile(new File(P_LOCAL_DIR + File.separator + P_FILENAME),
                                new File(P_BACKUP_DIR + File.separator + P_FILENAME));
                        logger.info("Backup file: " + (P_LOCAL_DIR + File.separator + P_FILENAME) + ". successfully to: " + P_BACKUP_DIR);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        logger.error("Backup file: " + (P_LOCAL_DIR + File.separator + P_FILENAME) + ". failed");
                    }
                }
            } else {
                logger.info("Upload " + P_FILENAME + " fail. Move error job");
                GetFilePool.releaseConnection(P_FTP_HOST, getFile, logger);
                storeFailJob("Upload exception");
                return false;
            }
        } catch (FileNotFoundException fnfe) {
            logger.error(fnfe.getMessage(), fnfe);
            storeFailJob(fnfe.getMessage());
            GetFilePool.releaseConnection(P_FTP_HOST, getFile, logger);
            return false;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            GetFilePool.releaseConnection(P_FTP_HOST, getFile, logger);
            return false;
        }

        return updateSuccessJob();
    }

}
