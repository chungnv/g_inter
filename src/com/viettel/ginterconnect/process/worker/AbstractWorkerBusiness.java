/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.worker;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.logfile.SuccessJobsLogger;
import com.viettel.ginterconnect.process.queue.InsertJobQueue;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIUtils;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openide.util.Exceptions;

/**
 * @author
 */
public abstract class AbstractWorkerBusiness {

    public static final String GET_PROCESS_TYPE = "GET";
    public static final String CONVERT_PROCESS_TYPE = "CONVERT";
    public static final String FILTER_PROCESS_TYPE = "FILTER";

    protected Logger logger = Logger.getLogger(AbstractWorkerBusiness.class);
    protected HashMap<String, String> jobParam = new HashMap<String, String>();

    protected String processType;
    protected JobsBO currentJob;

    private WorkerThread worker;

//    protected HashMap<String, String> parseJobParam(String jobField, String jobParam) {
//        HashMap<String, String> mapParam = new HashMap<String, String>();
//        if (StringUtils.isEmpty(jobParam) || StringUtils.isEmpty(jobField)) {
//            return mapParam;
//        }
//        String arrJobParam[] = jobParam.split("\\|");
//        String arrJobField[] = jobField.split("\\|");
//        if (arrJobParam.length != arrJobField.length) {
//            return mapParam;
//        }
//        for (int i = 0; i < arrJobParam.length; i++) {
//            mapParam.put(arrJobField[i], arrJobParam[i]);
//        }
//        return mapParam;
//    }
    public void readParam() {
//        String paramJob = currentJob.getJobParam();
//        int addX = 0;
//        if (paramJob != null && paramJob.endsWith(Constants.JOB_FLOW_JOIN)) {
//            paramJob += Constants.JOB_FLOW_JOIN + "x";
//            addX = 1;
//        }
//        String jobField[] = currentJob.getJobField().split(Constants.JOB_FLOW_SPLIT);
//        String jobParam[] = paramJob.split(Constants.JOB_FLOW_SPLIT);
//        if (jobField.length != (jobParam.length - addX)) {
//            return;
//        }
        JSONObject jObject = new JSONObject(currentJob.getJobParam());
        Iterator<String> keys = jObject.keySet().iterator();
        while (keys.hasNext()) {
//        for (int i = 0; i < jobField.length; i++) {
//            String s = jobField[i];
            String s = keys.next();
            try {
                Field field = this.getClass().getDeclaredField(s.toUpperCase());
                if (field != null) {
                    field.setAccessible(true);
                    Object obj = field.get(this);
                    String val = jObject.getString(s);
                    if (obj instanceof String) {
//                        field.set(this, jobParam[i]);
                        field.set(this, val);
                    } else if (obj instanceof Long) {
//                        field.set(this, (jobParam[i] == null ? 0 : Long.valueOf(jobParam[i])));
                        field.set(this, (val == null ? 0 : Long.valueOf(val)));
                    }
                }
            } catch (NoSuchFieldException ex) {
                ;//pass
            } catch (SecurityException | IllegalAccessException sie) {
                Exceptions.printStackTrace(sie);
            }
        }
    }

    public JobsBO createNewJob() {
        String remainFlow = GIUtils.extractFlow(currentJob.getFlow(), currentJob.getJobType());
        String nextStep = GIUtils.nextStep(currentJob.getFlow(), currentJob.getJobType());
        if (remainFlow == null) {
            logger.info("Jobs: " + currentJob.getPrimaryKey() + " flow finish");
            return new JobsBO();
        }
        if (nextStep == null) {
            logger.info("Jobs: " + currentJob.getPrimaryKey() + " has no next step");
            return new JobsBO();
        }
        JobsBO object = new JobsBO();
        try {
            object.setCreateProcess(worker.getName());
            object.setCountry(currentJob.getCountry());
            object.setCreateDate(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
            object.setFlow(remainFlow);
            object.setJobsStatus(Constants.JOB_NEW_STATUS);
            object.setJobType(nextStep);
            object.setSwitchType(currentJob.getSwitchType());
            object.setWorkerID("");
            setJobParamAndField(object, remainFlow, nextStep);
//            Client.getInsertInstance().insertJobsRecord(Id, object);
            InsertJobQueue.getInstance(30).insertToQueue(object);
            return object;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    SuccessJobsLogger successJobLog = new SuccessJobsLogger("successjob", "#");
    
    public boolean updateSuccessJob() {
        currentJob.setJobsStatus(Constants.JOB_SUCCESS_STATUS);
        currentJob.setProcessDate(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
        currentJob.setWorkerID(worker.getName());
        try {
            Client.getInstance().deleteJobRecord(currentJob.getPrimaryKey());
//            Client.getInsertInstance().insertSuccessJobsRecord(currentJob.getID(), currentJob);
            successJobLog.info(currentJob);
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
//        Object obj = Client.getInstance().updateJobsRecord(currentJob);
    }

    public boolean storeFailJob(String message) {
        currentJob.setJobsStatus(Constants.JOB_FAIL_STATUS);
        currentJob.setProcessDate(GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
        currentJob.setWorkerID(worker.getName());
        try {
            Client.getInsertInstance().insertFailJobsRecord(currentJob.getID(), currentJob, message);
            Client.getInstance().deleteJobRecord(currentJob.getPrimaryKey());
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    protected JobsBO setJobParamAndField(JobsBO jobs, String flow, String type) {
        JSONObject jObject = new JSONObject();
        if (type != null) {
            try {
                AbstractWorkerBusiness nextWorker = (AbstractWorkerBusiness) Class.forName(Constants.WORKER_IMPL_PACKAGE + type).newInstance();
                Field[] workerFields = nextWorker.getClass().getDeclaredFields();
                for (Field field : workerFields) {
                    field.setAccessible(true);
                    if (field.getName().toUpperCase().startsWith(Constants.WORKER_INPUT_FIELD_NAME_PREFIX)) {
                        try {
                            Field current = this.getClass().getDeclaredField(field.getName().toUpperCase());
                            if (current != null) {
                                current.setAccessible(true);
                                if (current.get(this) != null) {
                                    jObject.put(field.getName().toUpperCase(), current.get(this).toString());
                                } else {
                                    jObject.put(field.getName().toUpperCase(), "");
                                }
                            } else {
                                jObject.put(field.getName().toUpperCase(), "");
                            }
                        } catch (NoSuchFieldException | SecurityException ex) {
                            jObject.put(field.getName().toUpperCase(), "");
                        }
                    }
                }
                jobs.setJobParam(jObject.toString());
            } catch (InstantiationException | ClassNotFoundException | IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return jobs;
    }

    protected void logInfo(String mess) {
        logger.info("Worker: " + worker.getName() + ": " + mess);
    }
    
    protected void logWarn(String mess) {
        logger.warn("Worker: " + worker.getName() + ": " + mess);
    }
    
    protected void logError(String mess) {
        logger.error("Worker: " + worker.getName() + ": " + mess);
    }

    public abstract void afterBusiness();

    public abstract boolean doBusiness();

    public JobsBO getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(JobsBO currentJob) {
        this.currentJob = currentJob;
    }

    public WorkerThread getWorker() {
        return worker;
    }

    public void setWorker(WorkerThread worker) {
        this.worker = worker;
    }

}
