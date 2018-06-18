/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.worker.impl;

import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.process.worker.AbstractWorkerBusiness;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.FileUtil;
//import com.viettel.ginterconnect.util.ParseFileName;
import com.viettel.ginterconnect.util.ParseFileNameStandard;
import com.viettel.ginterconnect.worker.merge.MergeFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FileUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author ubuntu
 */
public class MERGE extends AbstractWorkerBusiness {

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
    public void afterBusiness() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean doBusiness() {

        int numberMergedFile = 0;
        boolean isSuccess = false;
        //kiem tra thu muc temp, neu chua co thi tao, neu co roi
        String tempDir = P_OUTPUT_DIR + Constants.MERGE_TEMP_DIR + P_SWITCH_ID + File.separator;
        if (new File(tempDir).exists()) {
            logInfo("Clear temp dir; " + tempDir);
            try {
                FileUtil.moveAllGrandChildFile(tempDir, P_INPUT_DIR);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return false;
            }
        }
        FileUtil.mkdirs(P_OUTPUT_DIR);
        //start merges
        ParseFileNameStandard parseFileName = new ParseFileNameStandard(logger);
        parseFileName.setPatternFileName(P_IN_PATTERN);
        ConcurrentHashMap<String, MergeFile> mergingDays = new ConcurrentHashMap<>();
        long start = System.currentTimeMillis();
        try {
            while ((System.currentTimeMillis() - start) < P_WAIT_TIME * 1000) {
                //doc file
                File files[] = new File(P_INPUT_DIR).listFiles();
                if (files != null) {
                    logInfo("List file size: " + P_INPUT_DIR + ". size: " + files.length);
                    for (File file : files) {
                        if (parseFileName.Parse(file.getName())) {
                            //rename to temp
//                        FileWriter fileWriter = null;
                            Date fileDate = parseFileName.getTimestamp();
                            if (fileDate == null) {
                                fileDate = new Date();
                            }
                            String fileDateStr = (new SimpleDateFormat("yyyyMMdd").format(fileDate));
                            String tempSwitchPath = tempDir + fileDateStr + File.separator;
                            File tempSwitchDir = new File(tempSwitchPath);
                            MergeFile merge = null;
                            if (mergingDays.containsKey(fileDateStr)) {
                                merge = mergingDays.get(fileDateStr);
//                            fileWriter = merge.getFileWriter();
                            } else {
                                //kiem tra co thu muc con chua, neu co roi, move lai du lieu cu
                                if (tempSwitchDir.exists()) {
                                    logInfo("Move all file of temp dir " + tempSwitchPath);
                                    FileUtil.moveFileJava(tempSwitchPath, "*", P_INPUT_DIR, logger);
                                } else {
                                    if (!tempSwitchDir.mkdirs()) {
                                        logger.error("Mkdir " + tempSwitchDir + " error");
                                        return false;
                                    }
                                }
                                try {
                                    String outputFileName = ParseFileNameStandard.parse(P_OUT_PATTERN + Constants.FILENAME_TMP, Client.getSequenceInstance().getSequence(P_OUTPUT_SEQ), (Date) fileDate);
                                    logInfo("Start merge to file: " + P_OUTPUT_DIR + File.separator + outputFileName);
                                    FileWriter fileWriter = new FileWriter(P_OUTPUT_DIR + File.separator + outputFileName);
                                    merge = new MergeFile(fileWriter, numberMergedFile, P_OUTPUT_DIR + File.separator + outputFileName);
                                    merge.setMergingDay(fileDateStr);
                                    mergingDays.put(fileDateStr, merge);
                                } catch (Exception ex) {
                                    throw ex;
                                }
                            }
                            //move file to tempDir
                            if (FileUtil.moveFileJava(P_INPUT_DIR, file.getName(), tempSwitchPath, logger)) {
                                merge.increateNumberOfFile(1);
                                logger.info("Merge: " + file.getName() + " to file: " + merge.getFilename());
                                mergeOneFile(tempSwitchPath + file.getName(), merge.getFileWriter());
                                if (merge.getNumberOfFiles() >= P_NUMBER_FILES) {
                                    logInfo(fileDateStr + " reach number of file " + P_NUMBER_FILES);
                                    //1. close writer
                                    if (merge.getFileWriter() != null) {
                                        merge.getFileWriter().close();
                                    }
                                    //delete temp
                                    logInfo("Delete temp directory");
                                    FileUtils.deleteDirectory(new File(tempDir + merge.getMergingDay()));
                                    //rename temp file
                                    FileUtils.moveFile(new File(merge.getFilename()), new File(merge.getFilename().replaceFirst(".tmp", "")));
                                    logInfo("Rename success file");
                                    //remove from map
                                    mergingDays.remove(fileDateStr);
                                }
                            } else {
                                logError("Merge file: " + merge.getFilename() + ": Move input file error: " + P_INPUT_DIR + file.getName());
                            }
                        }
                    }
                } else {
                    logWarn("List file size: " + P_INPUT_DIR + " null ");
                }
                Thread.sleep(1000);
            }
            isSuccess = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            storeFailJob(ex.getMessage());
            return false;
        } finally {
            processAfterMerge(mergingDays, tempDir, isSuccess);
        }
        return updateSuccessJob();
    }

    private void processAfterMerge(ConcurrentHashMap<String, MergeFile> mergingDays, String tempDir, boolean success) {
        Iterator<String> ite = mergingDays.keySet().iterator();
        while (ite.hasNext()) {
            String day = ite.next();
            MergeFile merge = mergingDays.get(day);
            try {
                merge.getFileWriter().close();
                //rename successfile
                if (success) {
                    //delete temp
                    logInfo("Delete Directory: " + tempDir + merge.getMergingDay());
                    FileUtils.deleteDirectory(new File(tempDir + merge.getMergingDay()));
                    //rename temp file
                    FileUtils.moveFile(new File(merge.getFilename()), new File(merge.getFilename().replaceFirst(".tmp", "")));
                    //remove from map
                    logInfo("Merge file success: " + merge.getFilename());
                    ite.remove();
                } else {
                    logError("Merge file error: " + merge.getFilename() + " job id: " + this.getCurrentJob().getPrimaryKey());
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void mergeOneFile(String inputFile, FileWriter fileWriter) throws Exception {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(inputFile));
            String line;
            long totalLine = 0;
            while ((line = br.readLine()) != null) {
                totalLine++;
                fileWriter.write(line + "\n");
            }
            logInfo("Merge file: " + inputFile + ". success " + totalLine);
            fileWriter.flush();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

}
