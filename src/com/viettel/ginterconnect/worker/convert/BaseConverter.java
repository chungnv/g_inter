/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.convert;

import com.viettel.ginterconnect.process.bean.ConvertLog;
import com.viettel.ginterconnect.process.exception.ConvertException;
import com.viettel.ginterconnect.worker.util.ParamFileUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public abstract class BaseConverter {

    private static final String MSG_WARNING_EXCEPTION = "Exception: ";
    private static final String PARAM_DATE_FORMAT = "date_format";
    private static final String PARAM_FILEID_POSITION = "fileID_position";
    protected String inputFileName = null;
    protected String outputFileName = null;
    protected String errorFileName = null;
    protected Logger logger = null;
    protected ParamFileUtil paramFile = null;
    protected ConvertOutput fos = null;
    protected FileOutputStream fos_error = null;
    protected boolean cdrError = false;
    protected DateFormat dateFormat = null;
    private Long fileId = -1L;
    protected int fileIdPosition;
    protected int totalConverted = 0;
    protected String separate;
    protected double timezone;
    protected String configFile = null;

    /**
     * Convert file
     *
     * @param inputDir
     * @param outputDir
     * @param errorDir
     * @param filename
     * @param filenameExt
     * @param logger
     * @param configFile
     *
     * @throws IOException,
     *
     * @return
     */
    public ConvertLog convert(String inputDir, String outputDir, String errorDir, String filename,
            String filenameExt, Logger logger, String configFile) throws Exception {
        Boolean result = false;
        ConvertLog convertLog = new ConvertLog();
        try {
            // Prepare environment
            String fileName = filename;
            inputFileName = inputDir + File.separator + fileName;
            errorFileName = errorDir + File.separator + fileName;
            outputFileName = outputDir + File.separator + fileName + ".tmp" + filenameExt;
            File tmpOutputFile = new File(outputFileName);
            if (tmpOutputFile.exists()) {
                tmpOutputFile.delete();
            }
            this.logger = logger;
            paramFile = new ParamFileUtil(configFile);
            this.configFile = configFile;
            dateFormat = new SimpleDateFormat(paramFile.getString(PARAM_DATE_FORMAT));

            //add by chungdq for get fileIDPostion to replace fileID
            String filePosStr = paramFile.getString(PARAM_FILEID_POSITION);
            if (filePosStr != null) {
                try {
                    fileIdPosition = Integer.parseInt(filePosStr.trim());
                } catch (NumberFormatException e) {
                    if (logger != null) {
                        logger.warn("Can\'t find fileID Position config:" + e.getMessage());
                    }
                }
            }

            // Call convert detail function
            convertLog = convertDetail();
            // Note that we have not created fos and fos_error yet here!!!
            // Only create them in convert detail function if they have data
            //rename output file to valid name
            File tmpOutFile = new File(outputFileName);
            if (tmpOutFile.exists()) {
                result = tmpOutFile.renameTo(new File(outputDir + File.separator + fileName + "." + filenameExt));
            } else {
                result = false;
            }
        } catch (TimeoutException ex) {
            logger.error("Time out when convert file " + inputFileName);
        } catch (ConvertException ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Error when convert file: " + inputFileName + ".", ex);
            throw ex;
//            result = false;
//            File file = new File(inputFileName);
//            if (file.exists()) {
//                if (errorFileName != null && !"".equals(errorFileName.trim())) {
//                    if (file.renameTo(new File(errorFileName + ".ERR"))) {
//                        logger.warn("Move file to Err dir successfully!");
//                    } else {
//                        if (file.renameTo(new File(inputFileName + ".ERR"))) {
//                            logger.warn("Rename file .ERR successfully!");
//                        }
//                    }
//                } else {
//                    if (file.renameTo(new File(inputFileName + ".ERR"))) {
//                        logger.warn("Rename file .ERR successfully!");
//                    }
//                }
//            }
//            logger.warn(MSG_WARNING_EXCEPTION + ex.getMessage());
//            return null;
        } finally {
            // Release and finish
            if (fos != null) {
                fos.close();
            }
            if (fos_error != null) {
                fos_error.close();
            }
        }
        convertLog.setConvertTotal(new Long(totalConverted));
        return convertLog;
    }

//    protected void initParamFile(String configFile) throws Exception {
//        paramFile = new ParamFileUtil(configFile);
//    }
    /**
     * Base type of CDR, we will have several convert detail functions
     *
     * @return
     * @throws java.lang.Exception
     */
    public abstract ConvertLog convertDetail() throws Exception;

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public int getTotalConverted() {
        return totalConverted;
    }

    public String getSeparate() {
        return separate;
    }

    public void setSeparate(String separate) {
        this.separate = separate;
    }

    public double getTimezone() {
        return timezone;
    }

    public void setTimezone(double timezone) {
        this.timezone = timezone;
    }
}
