/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.query;

import com.viettel.aerospike.bo.MasterThreadBO;
import com.viettel.aerospike.handle.Handler;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ScanCallback;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import com.viettel.aerospike.main.Client;
import com.viettel.ginterconnect.process.main.MasterMain;
import com.viettel.ginterconnect.process.master.MasterJobQueue;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIUtils;
import com.viettel.ginterconnect.util.SystemParam;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author xuanhong95
 */
public class MasterProcessQuery implements ScanCallback {

    private Handler handler;
    private Boolean active;
    protected Logger logger = Logger.getLogger(MasterProcessQuery.class);

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void getInactiveRecord(AerospikeClient client, String namespace, Boolean isActive) {
        try {
            // Java Scan
            ScanPolicy policy = new ScanPolicy();
            policy.concurrentNodes = true;
            policy.priority = Priority.LOW;
            policy.includeBinData = true;
            setActive(isActive);

            client.scanAll(policy, namespace, "master_process", this);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("EXCEPTION - Message: " + e.getStackTrace());
        }
    } //scan

    @Override
    public void scanCallback(Key key, Record record) {
        int status;
        if (active) {
            status = 0;
        } else {
            status = 1;
        }

        if ((record.getString("IP") == null || "".equals(record.getString("IP").trim())) && (Integer.parseInt(record.getString("STATUS")) == 0)) {
            MasterThreadBO mp = new MasterThreadBO();
            try {
                mp.setPrimaryKey(Integer.valueOf(key.userKey.toString()));
                mp.setMasterId(Integer.valueOf(record.getString("MASTER_ID")));
                mp.setMasterProcessCode(record.getString("PROCESS_CODE"));
                mp.setDescrIPtion(record.getString("DESCRIPTION"));
                mp.setProcessClass(record.getString("PROCESS_CLASS"));
                mp.setStopWhenError("1".equals(record.getString("STOP_WHEN_ERR") == null ? "0" : record.getString("STOP_WHEN_ERR")));
                mp.setIsActive("1".equals(record.getString("STATUS") == null ? "0" : record.getString("STATUS")));
                mp.setSWITCH_ID(Long.valueOf(record.getString("SWITCH_ID")));
//              mp.setRunStatus(Integer.parseInt(record.getString("RUN_STATUS") == null ? "0" : record.getString("RUN_STATUS")));
                mp.setIp(record.getString("IP"));
                mp.setRunDir(record.getString("RUN_DIR"));
                mp.setStep1(record.getString("STEP_1"));
                mp.setStep2(record.getString("STEP_2"));
                mp.setStep3(record.getString("STEP_3"));
                mp.setStep4(record.getString("STEP_4"));
                mp.setStep5(record.getString("STEP_5"));
                mp.setStep6(record.getString("STEP_6"));
                mp.setStep7(record.getString("STEP_7"));
                mp.setStep8(record.getString("STEP_8"));
                mp.setStep9(record.getString("STEP_9"));
                mp.setStep10(record.getString("STEP_10"));
            } catch (Exception exx) {
                exx.printStackTrace();
            }

            List<String> lstStep = new ArrayList();
            for (int i = 1; i <= 10; i++) {
                if (record.getString("STEP_" + i) != null
                        && !"".equals(record.getString("STEP_" + i).trim())) {
                    lstStep.add(record.getString("STEP_" + i));
                }
            }
            mp.setLstStep(lstStep);
            //master business param
            mp.setRecord(record);
            if (MasterMain.numberOfThread > SystemParam.NUMBER_OF_MASTER_THREAD) {
                logger.warn("Master Application reach maximum thread");
                return;
            } else {
                this.handler.onHandle(mp);
            }
            //throw new AerospikeException.ScanTerminated();
        }
    }

    public void queryAll(AerospikeClient client, String namespace, String set, int status) {

        QueryPolicy policy = new QueryPolicy();
        policy.priority = Priority.MEDIUM;
        Statement statement = new Statement();
        statement.setNamespace(namespace);
        statement.setFilter(Filter.equal("STATUS", status + ""));
        statement.setSetName(set);

        RecordSet rs = client.query(policy, statement);

        while (rs.next()) {
            Record record = rs.getRecord();
            if ((record.getString("IP") == null || "".equals(record.getString("IP").trim())) && (Integer.parseInt(record.getString("STATUS")) == 0)) {
                MasterThreadBO mp = new MasterThreadBO();
                try {
                    mp.setPrimaryKey(Integer.valueOf(record.getString("MASTER_ID")));
                    mp.setMasterId(Integer.valueOf(record.getString("MASTER_ID")));
                    mp.setMasterProcessCode(record.getString("PROCESS_CODE"));
                    mp.setDescrIPtion(record.getString("DESCRIPTION"));
                    mp.setProcessClass(record.getString("PROCESS_CLASS"));
                    mp.setStopWhenError("1".equals(record.getString("STOP_WHEN_ERR") == null ? "0" : record.getString("STOP_WHEN_ERR")));
                    mp.setIsActive("1".equals(record.getString("STATUS") == null ? "0" : record.getString("STATUS")));
                    mp.setSWITCH_ID(Long.valueOf(record.getString("SWITCH_ID")));
//              mp.setRunStatus(Integer.parseInt(record.getString("RUN_STATUS") == null ? "0" : record.getString("RUN_STATUS")));
                    mp.setIp(record.getString("IP"));
                    mp.setRunDir(record.getString("RUN_DIR"));
                    mp.setStep1(record.getString("STEP_1"));
                    mp.setStep2(record.getString("STEP_2"));
                    mp.setStep3(record.getString("STEP_3"));
                    mp.setStep4(record.getString("STEP_4"));
                    mp.setStep5(record.getString("STEP_5"));
                    mp.setStep6(record.getString("STEP_6"));
                    mp.setStep7(record.getString("STEP_7"));
                    mp.setStep8(record.getString("STEP_8"));
                    mp.setStep9(record.getString("STEP_9"));
                    mp.setStep10(record.getString("STEP_10"));
                } catch (Exception exx) {
                    exx.printStackTrace();
                }

                List<String> lstStep = new ArrayList();
                for (int i = 1; i <= 10; i++) {
                    if (record.getString("STEP_" + i) != null
                            && !"".equals(record.getString("STEP_" + i).trim())) {
                        lstStep.add(record.getString("STEP_" + i));
                    }
                }
                mp.setLstStep(lstStep);
                //master business param
                mp.setRecord(record);
                if (MasterMain.numberOfThread > SystemParam.NUMBER_OF_MASTER_THREAD) {
                    logger.warn("Master Application reach maximum thread");
                    return;
                } else {
                    this.handler.onHandle(mp);
                }
                //throw new AerospikeException.ScanTerminated();
            }
        }
    }

    public void queryAllForUpdate(AerospikeClient client, String namespace, String set, int status) {

        QueryPolicy policy = new QueryPolicy();
        policy.priority = Priority.MEDIUM;
        Statement statement = new Statement();
        statement.setNamespace(namespace);
        statement.setFilter(Filter.equal("STATUS", status + ""));
        statement.setSetName(set);

        RecordSet rs = client.query(policy, statement);

        while (rs.next()) {
            Record record = rs.getRecord();
            if ((record.getString("IP") == null || "".equals(record.getString("IP").trim())) && !(GIUtils.timeInSleepDuration(record.getString("LAST_RUN")))) {
                MasterThreadBO mp = new MasterThreadBO();
                try {
                    mp.setPrimaryKey(Integer.valueOf(record.getString("MASTER_ID")));
                    mp.setMasterId(Integer.valueOf(record.getString("MASTER_ID")));
                    mp.setMasterProcessCode(record.getString("PROCESS_CODE"));
                    mp.setDescrIPtion(record.getString("DESCRIPTION"));
                    mp.setProcessClass(record.getString("PROCESS_CLASS"));
                    mp.setStopWhenError("1".equals(record.getString("STOP_WHEN_ERR") == null ? "0" : record.getString("STOP_WHEN_ERR")));
                    mp.setIsActive("1".equals(record.getString("STATUS") == null ? "0" : record.getString("STATUS")));
                    mp.setSWITCH_ID(record.getString("SWITCH_ID") == null ? 0 : Long.valueOf(record.getString("SWITCH_ID")));
//              mp.setRunStatus(Integer.parseInt(record.getString("RUN_STATUS") == null ? "0" : record.getString("RUN_STATUS")));
                    mp.setIp(record.getString("IP"));
                    mp.setRunDir(record.getString("RUN_DIR"));
                    mp.setStep1(record.getString("STEP_1"));
                    mp.setStep2(record.getString("STEP_2"));
                    mp.setStep3(record.getString("STEP_3"));
                    mp.setStep4(record.getString("STEP_4"));
                    mp.setStep5(record.getString("STEP_5"));
                    mp.setStep6(record.getString("STEP_6"));
                    mp.setStep7(record.getString("STEP_7"));
                    mp.setStep8(record.getString("STEP_8"));
                    mp.setStep9(record.getString("STEP_9"));
                    mp.setStep10(record.getString("STEP_10"));
                    mp.setSleepTime(record.getString("SLEEP_TIME") == null ? 0L : Long.valueOf(record.getString("SLEEP_TIME")));
                    mp.setScheduler(record.getString("SCHEDULER"));
                } catch (Exception exx) {
                    logger.error(exx.getMessage(), exx);
                }

                List<String> lstStep = new ArrayList();
                for (int i = 1; i <= 10; i++) {
                    if (record.getString("STEP_" + i) != null
                            && !"".equals(record.getString("STEP_" + i).trim())) {
                        lstStep.add(record.getString("STEP_" + i));
                    }
                }
                mp.setLstStep(lstStep);
                //master business param
                mp.setRecord(record);
                if (MasterMain.numberOfThread > SystemParam.NUMBER_OF_MASTER_THREAD) {
                    logger.warn("Master Application reach maximum thread");
//                    MasterMain.numberOfThread = 0;
                    throw new AerospikeException.ScanTerminated();
                } else {
                    //ghi vao queue
                    try {
                        mp.setIp(SystemParam.SYSTEM_IP + ":" + SystemParam.SYSTEM_PORT);
                        Record update = (Record) Client.getInstance().updateForSelectMasterProcessRecord(mp);
                        if (update != null) {
                            MasterMain.numberOfThread += 1;
                            MasterJobQueue.getInstance().insertToQueue(mp);
                            logger.info("Number of JobThread: " + MasterMain.numberOfThread);
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
//                    this.handler.onHandleQueryForUpdate(mp);
                }
                //throw new AerospikeException.ScanTerminated();
            }
        }
    }

    public void queryForUpdateTimeout(AerospikeClient client, String namespace, String set, String status) {

        QueryPolicy policy = new QueryPolicy();
        policy.priority = Priority.MEDIUM;
        Statement statement = new Statement();
        statement.setNamespace(namespace);
        statement.setFilter(Filter.equal("STATUS", status));
        statement.setSetName(set);

        RecordSet rs = client.query(policy, statement);

        while (rs.next()) {
            Record record = rs.getRecord();
            logger.info("Check master job: " + record.getString("MASTER_ID"));
            if ((GIUtils.greaterSysdateWithGivenTime(record.getString("LAST_RUN"), SystemParam.MASTER_JOB_TIMEOUT_IN_SEC))) {
                //update
                try {
                    logger.info("Renew master job: " + record.getString("MASTER_ID"));
                    Key key = new Key(namespace, set, record.getString("MASTER_ID"));
                    Record userRecord = client.get(null, key);
                    WritePolicy writePolicy = new WritePolicy();
                    // record generation jobBo != null && jobBo.getInt("JOB_STATUS") == Constants.JOB_NEW_STATUS
                    if (userRecord == null || !(userRecord.getString("STATUS").equals(Constants.MASTER_JOB_PROCESSING))) {
                        continue;
                    }
                    writePolicy.generation = userRecord.generation;
                    writePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
                    Bin statusBin = new Bin("STATUS", Constants.MASTER_JOB_NEW);
                    Bin ipBin = new Bin("IP", " ");
                    client.put(writePolicy, key, statusBin, ipBin);
                } catch (AerospikeException ex) {
                    if (ex.getMessage() != null && ex.getMessage().contains("Error Code 3")) {
                        logger.info("Error code 3: " + record.getString("MASTER_ID"));
                    } else {
                        logger.error(ex.getMessage(), ex);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }
}
