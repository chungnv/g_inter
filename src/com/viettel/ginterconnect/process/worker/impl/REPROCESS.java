/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.worker.impl;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.master.get.AbstractFileTransfer;
import com.viettel.ginterconnect.master.get.GetFilePool;
import com.viettel.ginterconnect.process.worker.AbstractWorkerBusiness;
import com.viettel.ginterconnect.util.ParseFileNameStandard;
import java.io.File;
import org.openide.util.Exceptions;

/**
 *
 * @author ubuntu
 */
public class REPROCESS extends AbstractWorkerBusiness {

    private String P_FILENAME = "";
    private String P_WORKING_DIR = "";
    private String P_NAME_PATTERN = "";
    private String P_SWITCH_ID = "";
    private String P_OUT_SEPARATE = "";
    private String P_IN_SEPARATE = "";
    private String P_FILE_ID = "";
    private String P_HEADER = "";
    private long P_BATCH_FILE = 1;
    private String P_FILTER_BACKUP = "";

    @Override
    public void afterBusiness() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean doBusiness() {
        //list file
        ParseFileNameStandard parseFileName = new ParseFileNameStandard(P_NAME_PATTERN, logger);
        File file[] = new File(P_WORKING_DIR).listFiles();
        long count = 0;
        for (File f : file) {
            if (parseFileName.Parse(f.getName())) {
                count++;
                if (count <= P_BATCH_FILE) {
                    //create job
                    P_FILENAME = f.getName();
                    try {
                        P_FILE_ID = Client.getSequenceInstance().getSequence("md_file_id") + "";
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                        return false;
                    }
                    JobsBO job = createNewJob();
                    if (job == null) {
                        logger.error("Create new job fail for job: " + currentJob.getPrimaryKey());
                        return false;
                    }
                } else {
                    break;
                }
            }
        }
        return true;
    }

}
