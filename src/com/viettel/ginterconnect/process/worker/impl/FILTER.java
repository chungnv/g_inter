package com.viettel.ginterconnect.process.worker.impl;

import com.viettel.ginterconnect.process.exception.FilterException;
import com.viettel.ginterconnect.process.filter.BaseFilter;
import com.viettel.ginterconnect.process.worker.AbstractWorkerBusiness;
import java.io.File;

/**
 * Created by hoangsinh on 20/07/2017.
 */
public class FILTER extends AbstractWorkerBusiness {

    private String P_FILENAME = "";
    private String P_WORKING_DIR = "";
    private String P_SWITCH_ID = "";
    private String P_OUT_SEPARATE = "";
    private String P_IN_SEPARATE = "";
    private String P_FILE_ID = "";
    private String P_HEADER = "";
    private String P_FILTER_BACKUP = "";

    public FILTER() {
        this.processType = FILTER_PROCESS_TYPE;
    }

    @Override
    public boolean doBusiness() {
        try {
            logger.info(" Worker: " + this.getWorker().getName() + " do business");
            BaseFilter filter = new BaseFilter(this.getWorker().getName(), logger);
            if (!P_WORKING_DIR.endsWith(File.separator)) {
                P_WORKING_DIR += File.separator;
            }
            filter.init(P_FILENAME, P_WORKING_DIR, P_SWITCH_ID, P_HEADER, 
                    P_OUT_SEPARATE, P_IN_SEPARATE, P_FILE_ID);
            filter.filter(currentJob);
            //xoa file goc
            File f = new File(P_WORKING_DIR + P_FILENAME);
            if (f.isFile()) {
                if (f.delete()) {
                    logger.info("Delete success input file: " + P_WORKING_DIR + P_FILENAME);
                } else {
                    logger.error("Delete failed input file: " + P_WORKING_DIR + P_FILENAME);
                    throw new FilterException("Delete file fail", FilterException._FAIL);
                }
//                File f2 = new File("/gfs2_data01/bigdata/data/backup/" + P_FILENAME);
//                f.renameTo(f2);
//                f.delete();
            } else {
                logger.error("Not File: input file: " + P_WORKING_DIR + P_FILENAME);
                throw new FilterException("Not File", FilterException._FAIL);
            }
            updateSuccessJob();
            createNewJob();
            return true;
        } catch (FilterException fe) {
            if (fe.getPriority() == FilterException._FAIL) {
                //move to fail
                logger.error(fe.getMessage(), fe);
                storeFailJob(fe.getMessage());
            } else if (fe.getPriority() == FilterException._CONFIG_FAIL) {
                logger.error(fe.getMessage(), fe);
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }

    @Override
    public void afterBusiness() {

    }
}
