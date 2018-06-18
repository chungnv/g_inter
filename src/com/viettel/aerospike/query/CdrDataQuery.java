/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.query;

import com.viettel.aerospike.handle.Handler;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ScanCallback;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.ScanPolicy;
import com.viettel.aerospike.bo.CdrThreadBO;
import org.apache.log4j.Logger;

public class CdrDataQuery implements ScanCallback {

    private static Logger logger = Logger.getLogger(JobsQuery.class);
    private Handler handler;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void retriveAllRecord(AerospikeClient client, String namespace) {
        try {
            ScanPolicy policy = new ScanPolicy();
            policy.concurrentNodes = true;
            policy.priority = Priority.LOW;
            policy.includeBinData = true;

            client.scanAll(policy, namespace, "cdr_data", this);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    } //scan

    @Override
    public void scanCallback(Key key, Record record) {
            CdrThreadBO cdr = new CdrThreadBO();

            cdr.setPrimaryKey(key.userKey.toInteger());
            cdr.setCdrID(record.getInt("CDR_ID"));
            cdr.setCalling(record.getString("CALLING"));
            cdr.setCalled(record.getString("CALLED"));
            cdr.setStartTime(record.getString("START_TIME"));
            cdr.setDuration(record.getString("DURATION"));
            cdr.setTrunkIn(record.getString("TRUNK_IN"));
            cdr.setTrunkOut(record.getString("TRUNK_OUT"));
            cdr.setSmsIn(record.getString("SMS_IN"));
            cdr.setSmsOut(record.getString("SMS_OUT"));
            
            this.handler.onHandle(cdr);
    }
}
