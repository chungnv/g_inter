/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.processor;

import com.viettel.aerospike.query.MasterProcessQuery;
import com.viettel.aerospike.bo.MasterThreadBO;
import com.viettel.aerospike.handle.Handler;
//import com.viettel.aerospike.handle.QueryHandler;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Language;
import com.aerospike.client.Record;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.task.RegisterTask;
import com.viettel.aerospike.main.Console;
import com.viettel.aerospike.main.Example;
import com.viettel.aerospike.main.Parameters;
import com.viettel.ginterconnect.process.main.MasterMain;
import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIUtils;
import org.apache.log4j.Logger;

public class MasterProcess extends Example {

    private static Logger logger = Logger.getLogger(MasterProcess.class);
    private Handler handler;

    public MasterProcess(Console console) {
        super(console);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void runExample(AerospikeClient client, Parameters params) {
        if (!params.isHasUdf()) {
            console.info("User defined functions are not supported by the connected Aerospike server.");
            return;
        }
        //getInactiveRecord(client, params);
    }

    private void register(AerospikeClient client, Parameters params) {
        RegisterTask task = client.register(null, "../udf/record_example.lua", "record_example.lua", Language.LUA);
        task.waitTillComplete();
    }

    public void getRecord(AerospikeClient client, String namespace, Boolean active) {
        MasterProcessQuery masterProcessQuery = new MasterProcessQuery();
//        QueryHandler queryHandler = new QueryHandler();
//        queryHandler.setProcessorHandler(handler);
//        masterProcessQuery.setHandler(queryHandler);
        masterProcessQuery.setHandler(handler);
//        masterProcessQuery.getInactiveRecord(client, namespace, true);
        try {
//            if (MasterMain.numberOfThread == 0) {
            masterProcessQuery.queryAllForUpdate(client, namespace, "master_process", 0);
//            } else {
//                logger.info("Master queue reach maximum");
//            }
//            masterProcessQuery.queryAll(client, namespace, "master_process", 0);
        } catch (AerospikeException.ScanTerminated ste) {
            logger.info("Master queue reach maximum");
        }
    }

    public void getRecordForUpdateTimeout(AerospikeClient client, String namespace, String active) {
        MasterProcessQuery masterProcessQuery = new MasterProcessQuery();
        masterProcessQuery.setHandler(handler);
        try {
            masterProcessQuery.queryForUpdateTimeout(client, namespace, "master_process", active);
        } catch (AerospikeException.ScanTerminated ste) {
            logger.info("Master queue reach maximum");
        }
    }

    public Object updateRecord(AerospikeClient client, Parameters params, MasterThreadBO object) {
        try {
            Key key = new Key(params.getNamespace(), params.getSet(), object.getPrimaryKey() + "");
            Record userRecord = client.get(null, key);
            if (userRecord == null) {
                return null;
            }
            WritePolicy writePolicy = new WritePolicy();
            // record generation
            writePolicy.generation = userRecord.generation;
            writePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
            Bin statusBin = new Bin("STATUS", object.getRunStatus() + "");
            Bin lastTimeBin = new Bin("LAST_RUN", object.getLastRunTime());
            client.put(writePolicy, key, statusBin, lastTimeBin);
            logger.info("Update master_process = " + object.getPrimaryKey() + " successfully");
            return userRecord;
        } catch (AerospikeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Error Code 3")) {
                logger.info("Error code 3: " + object.getPrimaryKey());
                return null;
            } else {
                logger.error(ex.getMessage(), ex);
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public Object updateAfterRun(AerospikeClient client, Parameters params, MasterThreadBO object) {
        try {
            Key key = new Key(params.getNamespace(), params.getSet(), object.getPrimaryKey() + "");
            Record userRecord = client.get(null, key);
            if (userRecord == null) {
                logger.info(object.getPrimaryKey() + " not found");
                return null;
            }
            WritePolicy writePolicy = new WritePolicy();
            // record generation
            // writePolicy.generation = userRecord.generation;
//            writePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
            writePolicy.generationPolicy = GenerationPolicy.NONE;
            Bin ipBin = new Bin("IP", object.getIp());
            Bin statusBin = new Bin("STATUS", "0");
            Bin lastTimeBin = new Bin("LAST_RUN", object.getLastRunTime());
            client.put(writePolicy, key, ipBin, lastTimeBin, statusBin);
            logger.info("Update master_process = " + object.getPrimaryKey() + " successfully");
            return userRecord;
        } catch (AerospikeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Error Code 3")) {
                logger.info("Error code 3: " + object.getPrimaryKey());
                return null;
            } else {
                logger.error(ex.getMessage(), ex);
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    public Object updateForSelect(AerospikeClient client, Parameters params, MasterThreadBO object) {
        try {
            Key key = new Key(params.getNamespace(), params.getSet(), object.getPrimaryKey() + "");
            Record userRecord = client.get(null, key);
            if (userRecord == null
                    || (userRecord.getString("IP") != null && !"".equals(userRecord.getString("IP").trim())
                    && (GIUtils.timeInSleepDuration(userRecord.getString("LAST_RUN"))))) {
                return null;
            }
            WritePolicy writePolicy = new WritePolicy();
            // record generation
            writePolicy.generation = userRecord.generation;
            writePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
            Bin ipBin = new Bin("IP", object.getIp() + "");
            Bin statusBin = new Bin("STATUS", "2");
            Bin lastTimeBin = new Bin("LAST_RUN", GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT));
            client.put(writePolicy, key, ipBin, lastTimeBin, statusBin);
            logger.info("Update master_process = " + object.getPrimaryKey() + " successfully");
            return userRecord;
        } catch (AerospikeException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Error Code 3")) {
                logger.info("Error code 3: " + object.getPrimaryKey());
                return null;
            } else {
                logger.error(ex.getMessage(), ex);
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

}
