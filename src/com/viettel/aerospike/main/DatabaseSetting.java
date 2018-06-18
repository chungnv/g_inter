/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.main;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Language;
import com.aerospike.client.Record;
import com.aerospike.client.task.RegisterTask;
import java.util.Calendar;

public class DatabaseSetting extends Example {

    public DatabaseSetting(Console console) {
        super(console);
    }

    @Override
    public void runExample(AerospikeClient client, Parameters params) throws Exception {
        if (!params.hasUdf) {
            console.info("User defined functions are not supported by the connected Aerospike server.");
            return;
        }
        register(client, params);
        createTableMasterProcess(client, params);
        createTableProcessParam(client, params);
        createTableJobs(client, params);
        //readData(client, params);

    }

    private void register(AerospikeClient client, Parameters params) throws Exception {
        RegisterTask task = client.register(null, "../udf/record_example.lua", "record_example.lua", Language.LUA);
        task.waitTillComplete();
    }

    private void createTableMasterProcess(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create MasterProcess Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();
        for (int i = 1; i <= 5; i++) {
            Key key = new Key(params.namespace, "master_process", i);
            Bin bin = new Bin("MASTER_ID", i);
            Bin bin2 = new Bin("PROCESS_CODE", "Thử có dấu xem sao");
            Bin bin3 = new Bin("DESCRIPTION", "Đây là mô tả");
            Bin bin4 = new Bin("PROCESS_CLASS", "Nghiệp vụ");
            Bin bin5 = new Bin("WHEN_ERROR", 1);
            Bin bin6 = new Bin("STATUS", 1);
            Bin bin7 = new Bin("SWITCH_TYPE", "loai tong dai");
            Bin bin8 = new Bin("COUNTRY", "Thi truong");
            Bin bin9 = new Bin("RUN_STATUS", 1);
            Bin bin10 = new Bin("STEP_1", "GET");
            Bin bin11 = new Bin("STEP_2", "");
            Bin bin12 = new Bin("STEP_3", "");
            Bin bin13 = new Bin("STEP_4", "");
            Bin bin14 = new Bin("STEP_5", "");
            Bin bin15 = new Bin("STEP_6", "");
            Bin bin16 = new Bin("STEP_7", "");
            Bin bin17 = new Bin("STEP_8", "");
            Bin bin18 = new Bin("STEP_9", "");
            Bin bin19 = new Bin("STEP_10", "");
            Bin bin20 = new Bin("IP", "10..1.1.1");
            Bin bin21 = new Bin("RUN_DIR", "/var/dir");

            client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                    bin7, bin8, bin9, bin10, bin11, bin12, bin13, bin14, bin15,
                    bin16, bin17, bin18, bin19, bin20, bin21);
        }
        for (int i = 5; i <= 10; i++) {
            Key key = new Key(params.namespace, "master_process", i);
            Bin bin = new Bin("MASTER_ID", i);
            Bin bin2 = new Bin("PROCESS_CODE", "Thử có dấu xem sao");
            Bin bin3 = new Bin("DESCRIPTION", "Đây là mô tả");
            Bin bin4 = new Bin("PROCESS_CLASS", "Nghiệp vụ");
            Bin bin5 = new Bin("WHEN_ERROR", 1);
            Bin bin6 = new Bin("STATUS", 0);
            Bin bin7 = new Bin("SWITCH_TYPE", "loai tong dai");
            Bin bin8 = new Bin("COUNTRY", "Thi truong");
            Bin bin9 = new Bin("RUN_STATUS", 1);
            Bin bin10 = new Bin("STEP_1", "GET");
            Bin bin11 = new Bin("STEP_2", "");
            Bin bin12 = new Bin("STEP_3", "");
            Bin bin13 = new Bin("STEP_4", "");
            Bin bin14 = new Bin("STEP_5", "");
            Bin bin15 = new Bin("STEP_6", "");
            Bin bin16 = new Bin("STEP_7", "");
            Bin bin17 = new Bin("STEP_8", "");
            Bin bin18 = new Bin("STEP_9", "");
            Bin bin19 = new Bin("STEP_10", "");
            Bin bin20 = new Bin("IP", "");
            Bin bin21 = new Bin("RUN_DIR", "/var/dir");

            client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                    bin7, bin8, bin9, bin10, bin11, bin12, bin13, bin14, bin15,
                    bin16, bin17, bin18, bin19, bin20, bin21);
        }

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }

    private void createTableProcessParam(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create ProcessParam Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();

        Key key = new Key(params.namespace, "process_param", 1);
        Bin bin = new Bin("ID", 1);
        Bin bin2 = new Bin("TYPE", "Thử có dấu xem sao");
        Bin bin3 = new Bin("MASTER_ID", 1);
        Bin bin4 = new Bin("PARAM_NAME", "Ten tham so");
        Bin bin5 = new Bin("PARAM_TYPE", 1);
        Bin bin6 = new Bin("PARAM_FORMAT", "Format cua tham so");
        Bin bin7 = new Bin("PARAM_VALUE", "Gia tri tham so");
        Bin bin8 = new Bin("STATUS", 1);

        client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6, bin7, bin8);

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }

    private void createTableJobs(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create Jobs Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();

        for (int i = 1; i <= 5; i++) {
            Key key = new Key(params.namespace, "jobs", i);
            Bin bin = new Bin("ID", i);
            Bin bin2 = new Bin("CREATE_PROCESS", "GET_1");
            Bin bin3 = new Bin("JOB_STATUS", 0);
            Bin bin4 = new Bin("JOB_TYPE", "GET");
            Bin bin5 = new Bin("FLOW", "GET|CONVERT|FILTER");
            Bin bin6 = new Bin("SWITCH_TYPE", "loai tong dai");
            Bin bin7 = new Bin("COUNTRY", "Thi truong");
            Bin bin8 = new Bin("JOB_PARAM", 1);
            Bin bin9 = new Bin("CREATE_DATE", "2017-01-01");
            Bin bin10 = new Bin("PROCESS_DATE", "2017-01-01");
            Bin bin11 = new Bin("WORKER_ID", "8612343-123");
            Bin bin12 = new Bin("IP", "");

            client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                    bin7, bin8, bin9, bin10, bin11, bin12);
        }
        for (int i = 5; i <= 10; i++) {
            Key key = new Key(params.namespace, "jobs", i);
            Bin bin = new Bin("ID", i);
            Bin bin2 = new Bin("CREATE_PROCESS", "GET_1");
            Bin bin3 = new Bin("JOB_STATUS", 1);
            Bin bin4 = new Bin("JOB_TYPE", "GET");
            Bin bin5 = new Bin("FLOW", "GET|CONVERT|FILTER");
            Bin bin6 = new Bin("SWITCH_TYPE", "loai tong dai");
            Bin bin7 = new Bin("COUNTRY", "Thi truong");
            Bin bin8 = new Bin("JOB_PARAM", 1);
            Bin bin9 = new Bin("CREATE_DATE", "2017-01-01");
            Bin bin10 = new Bin("PROCESS_DATE", "2017-01-01");
            Bin bin11 = new Bin("WORKER_ID", "8612343-123");
            Bin bin12 = new Bin("IP", "10.1.1.1");

            client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                    bin7, bin8, bin9, bin10, bin11, bin12);
        }

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }
}
