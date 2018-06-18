/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.main;

import com.viettel.aerospike.main.ScanParallelTest;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Language;
import com.aerospike.client.Record;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.ResultSet;
import com.aerospike.client.query.Statement;
import com.aerospike.client.task.RegisterTask;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Random;

public class ReadRecord extends Example {

    public ReadRecord(Console console) {
        super(console);
    }

    @Override
    public void runExample(AerospikeClient client, Parameters params) throws Exception {
        if (!params.hasUdf) {
            console.info("User defined functions are not supported by the connected Aerospike server.");
            return;
        }
        ScanParallelTest scan = new ScanParallelTest();
        scan.runTest();
        
        register(client, params);
        readFirstRecord(client, params, scan);
        readRandomRecord(client, params, scan);
        deleteRecord(client, params, 4);
    }

    private void register(AerospikeClient client, Parameters params) throws Exception {
        RegisterTask task = client.register(null, "udf/record_example.lua", "record_example.lua", Language.LUA);
        task.waitTillComplete();
    }

    private void readFirstRecord(
            AerospikeClient client,
            Parameters params,
            ScanParallelTest scan
    ) throws Exception {
        Collections.sort(scan.PK);

        Key key = new Key(params.namespace, params.set, scan.PK.get(0));
        Record record = client.get(params.writePolicy, key, "MASTER_ID");
        
        console.info("Giá trị đầu tiên là " + record);
    }

    private void readRandomRecord(
            AerospikeClient client,
            Parameters params,
            ScanParallelTest scan
    ) throws Exception {
        Random rd = new Random();
        
        Key key = new Key(params.namespace, params.set, rd.nextInt(scan.PK.size() + 1));
        Record record = client.get(params.writePolicy, key, "MASTER_ID");
        
        console.info("Giá trị random là " + record);
    }
    
    private void deleteRecord(
            AerospikeClient client,
            Parameters params,
            int primary_key
    ) throws Exception {
        Key key = new Key(params.namespace, params.set, primary_key);
        client.delete(params.writePolicy, key);
        
        console.info("Xóa thành công record có key = " + primary_key);
    }
}
