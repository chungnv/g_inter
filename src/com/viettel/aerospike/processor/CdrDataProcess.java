/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.processor;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Language;
import com.aerospike.client.task.RegisterTask;
import com.viettel.aerospike.handle.Handler;
//import com.viettel.aerospike.handle.QueryHandler;
import com.viettel.aerospike.main.Console;
import com.viettel.aerospike.main.Example;
import com.viettel.aerospike.main.Parameters;
import com.viettel.aerospike.query.CdrDataQuery;
import org.apache.log4j.Logger;

/**
 *
 * @author xuanhong95
 */
public class CdrDataProcess extends Example {

    private static Logger logger = Logger.getLogger(JobsProcess.class);
    private Handler handler;

    public CdrDataProcess(Console console) {
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

    public void getInactiveRecord(AerospikeClient client, String namespace) {
        CdrDataQuery cdrQuery = new CdrDataQuery();
//        QueryHandler queryHandler = new QueryHandler();
//        queryHandler.setProcessorHandler(handler);
//        cdrQuery.setHandler(queryHandler);
        cdrQuery.setHandler(handler);
        cdrQuery.retriveAllRecord(client, namespace);
    }

    public void insertRecord(AerospikeClient client, Parameters params, String input_records) {
        client.writePolicyDefault.sendKey = true;
        String[] record_arr = input_records.split(";");
        for (int i = 0; i < record_arr.length; i++) {
            String[] single_record = record_arr[i].split(",");

            Key key = new Key(params.getNamespace(), params.getSet(), Integer.parseInt(single_record[0]));
            Bin bin = new Bin("CDR_ID", Integer.parseInt(single_record[1]));
            Bin bin2 = new Bin("CALLING", single_record[2]);
            Bin bin3 = new Bin("CALLED", single_record[3]);
            Bin bin4 = new Bin("START_TIME", single_record[4]);
            Bin bin5 = new Bin("DURATION", single_record[5]);
            Bin bin6 = new Bin("TRUNK_IN", single_record[6]);
            Bin bin7 = new Bin("TRUNK_OUT", single_record[7]);
            Bin bin8 = new Bin("SMS_IN", single_record[8]);
            Bin bin9 = new Bin("SMS_OUT", single_record[9]);

            client.put(params.getWritePolicy(), key, bin, bin2, bin3, bin4, bin5, bin6, bin7, bin8, bin9);
            
        }
        System.out.println("Insert " + record_arr.length + " records thành công");
    }
}
