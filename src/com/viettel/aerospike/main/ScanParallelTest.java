/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.main;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ScanCallback;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.ScanPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class ScanParallelTest implements ScanCallback {

    private int recordCount;
    List<Integer> PK = new ArrayList<Integer>();

    public void runTest() throws AerospikeException {
        AerospikeClient client = new AerospikeClient("127.0.0.1", 3000);

        try {
            ScanPolicy policy = new ScanPolicy();
            policy.concurrentNodes = true;
            policy.priority = Priority.LOW;
            policy.includeBinData = false;
            
            client.scanAll(policy, "bar", "master_process", this);
            
        } finally {
            client.close();
        }
    }

    public void scanCallback(Key key, Record record) {
        PK.add(key.userKey.toInteger());
    }
}
