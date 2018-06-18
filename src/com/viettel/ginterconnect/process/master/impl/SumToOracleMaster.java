/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.master.impl;

import com.aerospike.client.query.Filter;
import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.process.bean.MasterJob;
import com.viettel.ginterconnect.process.master.AbstractMasterBusiness;
import com.viettel.ginterconnect.process.queue.InsertJobQueue;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIClient;
import com.viettel.ginterconnect.util.GIUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

/**
 *
 * @author ubuntu
 */
public class SumToOracleMaster extends AbstractMasterBusiness {

    String DB_URL = "";
    String DB_USERNAME = "";
    String DB_PASSWORD = "";
    String DB_TABLE = "";
    String MARKET = "";
    String GROUP_COL = "";
    String CYCLE_DURATION = "1";

    @Override
    public boolean doBusiness() throws Exception {
        if (!validateParam()) {
            logger.error("Master: " + this.getMasterProcess().getMasterProcessBO().getMasterProcessCode() + ": " + "Missing param config");
            return false;
        }
        //kiem tra job
        if (checkExistJob()) {
            logInfo("There is existing job");
            return true;
        }
        createJob();
//        try {
//            logger.info("Thread " + this.getMasterProcess().getMasterProcessBO().getMasterProcessCode() + " sleep..");
//            Thread.sleep(Long.valueOf(CYCLE_DURATION) * 1000);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return false;
//        }
        return true;
    }

//    private String genJobField() {
//        List<String> lstParam = new ArrayList<>();
//        lstParam.add("P_URL");
//        lstParam.add("P_USER_NAME");
//        lstParam.add("P_PASSWORD");
//        lstParam.add("P_TABLE");
//        lstParam.add("P_SUM_DATE");
//        lstParam.add("P_MARKET");
//        lstParam.add("P_GROUP_BY_VALUE");
//        return StringUtils.join(lstParam, Constants.JOB_FLOW_JOIN);
//    }
    private String genJobParam() {
//        List<String> lstParam = new ArrayList<>();

        JSONObject jObject = new JSONObject();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        jObject.put("P_URL", DB_URL);
        jObject.put("P_USER_NAME", DB_USERNAME + "");
        jObject.put("P_PASSWORD", DB_PASSWORD);
        jObject.put("P_TABLE", DB_TABLE);
        jObject.put("P_SUM_DATE", (new SimpleDateFormat("yyyyMMdd")).format(cal.getTime()));
        jObject.put("P_MARKET", MARKET);
        jObject.put("P_GROUP_BY_VALUE", GROUP_COL);

//        lstParam.add(DB_URL);
//        lstParam.add(DB_USERNAME + "");
//        lstParam.add(DB_PASSWORD);
//        lstParam.add(DB_TABLE);
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DATE, -1);
//        lstParam.add((new SimpleDateFormat("yyyyMMdd")).format(cal.getTime()));
//        lstParam.add(MARKET);
//        lstParam.add(GROUP_COL);
//        return StringUtils.join(lstParam, Constants.JOB_FLOW_JOIN);
        return jObject.toString();
    }

    public boolean validateParam() {

        return true;
    }

    @Override
    public JobsBO createJob() {
        try {
//            String nextStep = GIUtils.nextStep(masterProcess.getMasterProcessBO().getFlow(), "");
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
            object.setJobType("ExportToOracle");
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
