/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.processor;

import com.viettel.aerospike.query.JobsQuery;
import com.viettel.aerospike.bo.JobsBO;
import com.viettel.aerospike.handle.Handler;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Language;
import com.aerospike.client.Record;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.task.RegisterTask;
//import com.viettel.aerospike.handle.QueryHandler;
import com.viettel.aerospike.main.Console;
import com.viettel.aerospike.main.Example;
import com.viettel.aerospike.main.Parameters;
import com.viettel.ginterconnect.util.Constants;
import org.apache.log4j.Logger;

public class JobsProcess extends Example {

    private static Logger logger = Logger.getLogger(JobsProcess.class);
    private Handler handler;

    public JobsProcess(Console console) {
        super(console);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Handler getHandler() {
        return handler;
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

    public void getRecord(AerospikeClient client, String namespace, int status) {
        JobsQuery jobsQuery = new JobsQuery();
//        QueryHandler queryHandler = new QueryHandler();
//        queryHandler.setProcessorHandler(handler);
//        ProcessorHandler handlerProcess = new ProcessorHandler();
        jobsQuery.setHandler(handler);
        // jobsQuery.retriveInactiveRecord(client, namespace, status);
        jobsQuery.queryAll(client, namespace, "jobs", status);
    }

    public void getUpdateJob(AerospikeClient client, String namespace, int status) {
        JobsQuery jobsQuery = new JobsQuery();
//        QueryHandler queryHandler = new QueryHandler();
//        queryHandler.setProcessorHandler(handler);
//        ProcessorHandler handlerProcess = new ProcessorHandler();
        jobsQuery.setHandler(handler);
        // jobsQuery.retriveInactiveRecord(client, namespace, status);
        jobsQuery.queryUpdateJob(client, namespace, "jobs", status);
    }

    //Jobs 4028470 has been   3883884
    public Object updateRecord(AerospikeClient client, Parameters params, JobsBO object) {
        try {
            Key key = new Key(params.getNamespace(), params.getSet(), object.getPrimaryKey());
            Record userRecord = client.get(null, key);
            WritePolicy writePolicy = new WritePolicy();
            // record generation jobBo != null && jobBo.getInt("JOB_STATUS") == Constants.JOB_NEW_STATUS
            if (userRecord == null || (userRecord.getInt("JOB_STATUS") != Constants.JOB_NEW_STATUS)) {
                return null;
            }
            writePolicy.generation = userRecord.generation;
            writePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
            Bin statusBin = new Bin("JOB_STATUS", object.getJobsStatus());
            Bin ipBin = new Bin("IP", object.getIp());
            Bin workerIdBin = new Bin("WORKER_ID", object.getWorkerID());
            Bin processDateBin = new Bin("PROCESS_DATE", object.getProcessDate());
            client.put(writePolicy, key, statusBin, ipBin, workerIdBin, processDateBin);
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

//        register(client, params);
//        //object.setJobsStatus(1);
//        //client.writePolicyDefault.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
//        
//        Object obj = client.execute(writePolicy, key, "record_example", "updateJobsRecord", Value.get(object.getID()), Value.get(object.getCreateProcess()),
//                Value.get(object.getJobsStatus()), Value.get(object.getJobType()), Value.get(object.getFlow()), Value.get(object.getSwitchType()),
//                Value.get(object.getCountry()), Value.get(object.getJobParam()), Value.get(object.getCreateDate()), Value.get(object.getProcessDate()),
//                Value.get(object.getWorkerID()), Value.get(object.getIp()), Value.get(object.getJobField()));
//        logger.debug("Update record with primary key = " + object.getPrimaryKey() + " successfuly!");
    }

    public Object updateJobStatus(AerospikeClient client, Parameters params, JobsBO object) {
        try {
            Key key = new Key(params.getNamespace(), params.getSet(), object.getPrimaryKey());
            Record userRecord = client.get(null, key);
            WritePolicy writePolicy = new WritePolicy();
            // record generation jobBo != null && jobBo.getInt("JOB_STATUS") == Constants.JOB_NEW_STATUS
            if (userRecord == null || (userRecord.getInt("JOB_STATUS") != Constants.JOB_PROCESSING_STATUS)) {
                return null;
            }
            writePolicy.generation = userRecord.generation;
            writePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
            Bin statusBin = new Bin("JOB_STATUS", Constants.JOB_NEW_STATUS);
            Bin ipBin = new Bin("IP", " ");
            Bin createDateBin = new Bin("CREATE_DATE", object.getCreateDate());
//            Bin workerIdBin = new Bin("WORKER_ID", object.getWorkerID());
//            Bin processDateBin = new Bin("PROCESS_DATE", object.getProcessDate());
            client.put(writePolicy, key, statusBin, ipBin, createDateBin);
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

//        register(client, params);
//        //object.setJobsStatus(1);
//        //client.writePolicyDefault.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
//        
//        Object obj = client.execute(writePolicy, key, "record_example", "updateJobsRecord", Value.get(object.getID()), Value.get(object.getCreateProcess()),
//                Value.get(object.getJobsStatus()), Value.get(object.getJobType()), Value.get(object.getFlow()), Value.get(object.getSwitchType()),
//                Value.get(object.getCountry()), Value.get(object.getJobParam()), Value.get(object.getCreateDate()), Value.get(object.getProcessDate()),
//                Value.get(object.getWorkerID()), Value.get(object.getIp()), Value.get(object.getJobField()));
//        logger.debug("Update record with primary key = " + object.getPrimaryKey() + " successfuly!");
    }

    public void insertRecord(AerospikeClient client, Parameters params, String input_records) {
        client.writePolicyDefault.sendKey = true;
        String[] record_arr = input_records.split(";");
        for (int i = 0; i < record_arr.length; i++) {
            String[] single_record = record_arr[i].split(",");

            Key key = new Key(params.getNamespace(), params.getSet(), Integer.parseInt(single_record[0]));
            Bin bin = new Bin("ID", Integer.parseInt(single_record[1]));
            Bin bin2 = new Bin("CREATE_PROCESS", single_record[2]);
            Bin bin3 = new Bin("JOB_STATUS", Integer.parseInt(single_record[3]));
            Bin bin4 = new Bin("JOB_TYPE", single_record[4]);
            Bin bin5 = new Bin("FLOW", single_record[5]);
            Bin bin6 = new Bin("SWITCH_TYPE", single_record[6]);
            Bin bin7 = new Bin("COUNTRY", single_record[7]);
            Bin bin8 = new Bin("JOB_PARAM", Integer.parseInt(single_record[8]));
            Bin bin9 = new Bin("CREATE_DATE", single_record[9]);
            Bin bin10 = new Bin("PROCESS_DATE", single_record[10]);
            Bin bin11 = new Bin("WORKER_ID", single_record[11]);
            Bin bin12 = new Bin("IP", single_record[12]);

            client.put(params.getWritePolicy(), key, bin, bin2, bin3, bin4, bin5, bin6, bin7, bin8, bin9, bin10, bin11, bin12);

        }
        logger.debug("Insert " + record_arr.length + " records thành công");
    }

    public void insertJobRecord(AerospikeClient client, Parameters params, Long pkey, JobsBO object)
            throws AerospikeException {
        client.writePolicyDefault.sendKey = true;

        Key key = new Key(params.getNamespace(), params.getSet(), pkey);
        Bin bin = new Bin("ID", object.getID());
        Bin bin2 = new Bin("CREATE_PROCESS", object.getCreateProcess());
        Bin bin3 = new Bin("JOB_STATUS", object.getJobsStatus());
        Bin bin4 = new Bin("JOB_TYPE", object.getJobType());
        Bin bin5 = new Bin("FLOW", object.getFlow());
        Bin bin6 = new Bin("SWITCH_TYPE", object.getSwitchType());
        Bin bin7 = new Bin("COUNTRY", object.getCountry());
        Bin bin8 = new Bin("JOB_PARAM", object.getJobParam());
        Bin bin9 = new Bin("CREATE_DATE", object.getCreateDate());
        Bin bin10 = new Bin("PROCESS_DATE", object.getProcessDate());
        Bin bin11 = new Bin("WORKER_ID", object.getWorkerID());
        Bin bin12 = new Bin("IP", object.getIp());
        Bin bin13 = new Bin("JOB_FIELD", object.getJobField());

        try {
            client.put(params.getWritePolicy(), key, bin, bin2, bin3, bin4, bin5,
                    bin6, bin7, bin8, bin9, bin10, bin11, bin12, bin13);
        } catch (AerospikeException ae) {
            throw ae;
        }
    }

    public void insertSuccessRecord(AerospikeClient client, Parameters params, Long pkey, JobsBO object, String message)
            throws AerospikeException {
        client.writePolicyDefault.sendKey = true;

        Key key = new Key(params.getNamespace(), params.getSet(), pkey);
        Bin bin = new Bin("ID", object.getID());
        Bin bin2 = new Bin("CREATE_PROCESS", object.getCreateProcess());
        Bin bin3 = new Bin("JOB_STATUS", object.getJobsStatus());
        Bin bin4 = new Bin("JOB_TYPE", object.getJobType());
        Bin bin5 = new Bin("FLOW", object.getFlow());
        Bin bin6 = new Bin("SWITCH_TYPE", object.getSwitchType());
        Bin bin7 = new Bin("COUNTRY", object.getCountry());
        Bin bin8 = new Bin("JOB_PARAM", object.getJobParam());
        Bin bin9 = new Bin("CREATE_DATE", object.getCreateDate());
        Bin bin10 = new Bin("PROCESS_DATE", object.getProcessDate());
        Bin bin11 = new Bin("WORKER_ID", object.getWorkerID());
        Bin bin12 = new Bin("IP", object.getIp());
        Bin bin13 = new Bin("JOB_FIELD", object.getJobField());
        if (message != null && !"".equals(message)) {
            Bin bin14 = new Bin("JOB_MSG", message);
            try {
                client.put(params.getWritePolicy(), key, bin, bin2, bin3, bin4, bin5,
                        bin6, bin7, bin8, bin9, bin10, bin11, bin12, bin13, bin14);
            } catch (AerospikeException ae) {
                throw ae;
            }
        } else {

            try {
                client.put(params.getWritePolicy(), key, bin, bin2, bin3, bin4, bin5,
                        bin6, bin7, bin8, bin9, bin10, bin11, bin12, bin13);
            } catch (AerospikeException ae) {
                throw ae;
            }
        }
    }

    public boolean deleteRecord(AerospikeClient client, Parameters params, long pk) {
        try {
            logger.info("Delete job key: " + pk);
            Key key = new Key(params.getNamespace(), params.getSet(), pk);
            WritePolicy writePolicy = new WritePolicy();
//            writePolicy.durableDelete = true;
            boolean result = client.delete(writePolicy, key);
//            boolean result = client.delete(params.getWritePolicy(), key);
            logger.debug("Delete " + params.getSet() + " record successful!" + pk);
            return true;
        } catch (AerospikeException ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
