/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.master;

import com.aerospike.client.Record;
import com.aerospike.client.query.Filter;
import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.process.bean.MasterJob;
import com.viettel.ginterconnect.process.bean.ProcessParamBO;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIClient;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author
 */
public abstract class AbstractMasterBusiness {

    protected String processCode;

    protected RunMasterJob masterProcess;

    protected Logger logger = Logger.getLogger(AbstractMasterBusiness.class);
    protected HashMap<String, ProcessParamBO> mapParam = new HashMap<String, ProcessParamBO>();

    protected String setProperty() {
//        if (CollectionUtils.isEmpty(listParam)) {
//            return null;
//        }
        Record record = this.getMasterProcess().getMasterProcessBO().getRecord();
        Field[] fs = this.getClass().getDeclaredFields();
        for (Field objectField : fs) {
            try {
                objectField.setAccessible(true);
                Class objectType = objectField.getType();
//                switch (param.getParamType().toUpperCase()) {
//                    case "LONG": //kieu long
//                if (objectField.getName().equals("PRIORITY_TYPE")) {
//                    System.out.println("stop");
//                }
                if (record.getValue(objectField.getName()) == null
                        || "".equals(record.getValue(objectField.getName()).toString())) {
                    continue;
                }
                Object obj = objectField.get(this);
                if (obj instanceof Long) {
                    try {
                        objectField.setLong(this,
                                Long.valueOf(record.getString(objectField.getName())));
                    } catch (Exception ex) {
                        logger.error(ex);
                    }
                    continue;
                } else if (obj instanceof Date) {
//                    case "DATE": //kieu Date
                    try {
                        objectField.set(this, (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT))
                                .parse(record.getString(objectField.getName())));
                    } catch (Exception ex) {
                        logger.error(ex);
                    }
                    continue;
                } else if (obj instanceof Double) {
//                    case "DOUBLE": //kieu Double
                    try {
                        objectField.setDouble(this, Double.valueOf(record.getString(objectField.getName())));
                    } catch (Exception ex) {
                        logger.error(ex);
                    }
                    continue;
                } else if (obj instanceof String) {
//                    case "STRING": //kieu String
                    try {
                        objectField.set(this, record.getString(objectField.getName()));
                    } catch (Exception ex) {
                        logger.error(ex);
                    }
                    continue;
                } else if (obj instanceof Integer) {
//                    case "INTEGER": //kieu Int
                    try {
                        objectField.setInt(this, Integer.valueOf(record.getString(objectField.getName())));
                    } catch (IllegalAccessException | IllegalArgumentException ex) {
                        logger.error(ex);
                    }
                    continue;
                } else {
//                    default:
                    logger.error("Not support param type: " + objectType.getName());
                }

            } catch (ClassCastException | IllegalAccessException ex) {
                logger.info(ex.getMessage(), ex);
            }
        }
        return "OK";
    }

    protected boolean checkExistJob() {
        List<MasterJob> lstJob = GIClient.getInstance().getRecordByFilter(MasterJob.class, Filter.equal("CREATE_PROCESS", this.getMasterProcess().getMasterProcessBO().getMasterProcessCode()));
        if (lstJob != null && lstJob.size() > 0) {
            for (MasterJob job : lstJob) {
                if (job.getCREATE_PROCESS().equals(this.getMasterProcess().getMasterProcessBO().getMasterProcessCode())
                        && !"0".equals(job.getJOB_STATUS())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected void logInfo(String mess) {
        logger.info("Master: " + this.getMasterProcess().getMasterProcessBO().getMasterProcessCode() + ": " + mess);
    }
    
    protected void logError(String mess) {
        logger.error("Master: " + this.getMasterProcess().getMasterProcessBO().getMasterProcessCode() + ": " + mess);
    }

    public abstract boolean doBusiness() throws Exception;

    public abstract JobsBO createJob();

    public RunMasterJob getMasterProcess() {
        return masterProcess;
    }

    public void setMasterProcess(RunMasterJob masterProcess) {
        this.masterProcess = masterProcess;
    }

    private Class workerClass;

    public Class getWorkerClass() {
        return workerClass;
    }

    public void setWorkerClass(Class workerClass) {
        this.workerClass = workerClass;
    }

    protected void autoGenJobParam(JobsBO jobs) {
//        JobsBO param = new JobsBO();
        if (workerClass != null) {
            JSONObject jObject = new JSONObject();
            Field[] fs = workerClass.getDeclaredFields();
//            List<String> lstJobField = new ArrayList<>();
//            List<String> lstJobParam = new ArrayList<>();
            for (Field objectField : fs) {
                try {
                    objectField.setAccessible(true);
                    if (objectField.getName().toUpperCase().startsWith(Constants.WORKER_INPUT_FIELD_NAME_PREFIX)) {
//                        lstJobField.add(objectField.getName().toUpperCase());
                        Field current = null;
                        try {
                            current = this.getClass().getDeclaredField(objectField.getName().toUpperCase());
                        } catch (NoSuchFieldException ex) {
                            //do nothing
                        }
                        if (current != null) {
                            current.setAccessible(true);
                            if (current.get(this) != null) {
//                                lstJobParam.add(current.get(this).toString());
                                jObject.put(objectField.getName().toUpperCase(), current.get(this).toString());
                            } else {
                                jObject.put(objectField.getName().toUpperCase(), "");
                            }
                        } else {
                            jObject.put(objectField.getName().toUpperCase(), "");
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException | SecurityException | JSONException ex) {
                    logger.info(ex.getMessage(), ex);
                }
            }
//            jobs.setJobField(StringUtils.join(lstJobField, Constants.JOB_FLOW_JOIN));
//            jobs.setJobParam(StringUtils.join(lstJobParam, Constants.JOB_FLOW_JOIN));
            jobs.setJobParam(jObject.toString());
        }
    }

}
