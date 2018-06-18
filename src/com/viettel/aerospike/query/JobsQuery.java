/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.query;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.aerospike.handle.Handler;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.ScanCallback;
import com.aerospike.client.policy.Priority;
import com.aerospike.client.policy.QueryPolicy;
import com.aerospike.client.policy.ScanPolicy;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.KeyRecord;
import com.aerospike.client.query.RecordSet;
import com.aerospike.client.query.Statement;
import java.util.Iterator;
import static javax.xml.stream.XMLStreamConstants.NAMESPACE;
import org.apache.log4j.Logger;

public class JobsQuery implements ScanCallback {

    private static Logger logger = Logger.getLogger(JobsQuery.class);
    private Handler handler;
    private int status = 0;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void retriveInactiveRecord(AerospikeClient client, String namespace, int status) {
        try {
            ScanPolicy policy = new ScanPolicy();
            policy.concurrentNodes = true;
            policy.priority = Priority.LOW;
            policy.includeBinData = true;
            this.status = status;
            client.scanAll(policy, namespace, "jobs", this);
        } catch (AerospikeException.ScanTerminated as) {
            //do nothing
            logger.info("break loop callback");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    } //scan

    @Override
    public void scanCallback(Key key, Record record) {
        int status = this.status;

        if ((record.getString("IP") == null || "".equals(record.getString("IP").trim())) && (record.getInt("JOB_STATUS") == status)) {
            JobsBO job = new JobsBO();
            job.setPrimaryKey(key.userKey.toLong());
            job.setID(record.getLong("ID"));
            job.setCreateProcess(record.getString("CREATE_PROCESS"));
            job.setJobsStatus(record.getInt("JOB_STATUS"));
            job.setJobType(record.getString("JOB_TYPE"));
            job.setFlow(record.getString("FLOW"));
            job.setSwitchType(record.getString("SWITCH_TYPE"));
            job.setCountry(record.getString("COUNTRY"));
            job.setJobParam(record.getString("JOB_PARAM"));
            job.setJobField(record.getString("JOB_FIELD"));
            job.setCreateDate(record.getString("CREATE_DATE"));
            job.setProcessDate(record.getString("PROCESS_DATE"));
            job.setWorkerID(record.getString("WORKER_ID"));
            job.setIp(record.getString("IP"));

            this.handler.onHandle(job);

//            throw new AerospikeException.ScanTerminated();
        }
    }

    public void queryAll(AerospikeClient client, String namespace, String set, int jobStatus) {
        QueryPolicy policy = new QueryPolicy();
        policy.priority = Priority.MEDIUM;
        Statement statement = new Statement();
        statement.setNamespace(namespace);
        statement.setFilter(Filter.equal("JOB_STATUS", jobStatus));
        statement.setSetName(set);

        RecordSet rs = client.query(policy, statement);
        while (rs.next()) {
            Record record = rs.getRecord();
            if ((record.getString("IP") == null || "".equals(record.getString("IP").trim())) && (record.getInt("JOB_STATUS") == jobStatus)) {
                JobsBO job = new JobsBO();
                Key key = rs.getKey();
                job.setPrimaryKey(key.userKey.toLong());
                job.setID(record.getLong("ID"));
                job.setCreateProcess(record.getString("CREATE_PROCESS"));
                job.setJobsStatus(record.getInt("JOB_STATUS"));
                job.setJobType(record.getString("JOB_TYPE"));
                job.setFlow(record.getString("FLOW"));
                job.setSwitchType(record.getString("SWITCH_TYPE"));
                job.setCountry(record.getString("COUNTRY"));
                job.setJobParam(record.getString("JOB_PARAM"));
                job.setJobField(record.getString("JOB_FIELD"));
                job.setCreateDate(record.getString("CREATE_DATE"));
                job.setProcessDate(record.getString("PROCESS_DATE"));
                job.setWorkerID(record.getString("WORKER_ID"));
                job.setIp(record.getString("IP"));

                this.handler.onHandle(job);

//            throw new AerospikeException.ScanTerminated();
            }
        }
    }

    public void queryUpdateJob(AerospikeClient client, String namespace, String set, int jobStatus) {
        QueryPolicy policy = new QueryPolicy();
        policy.priority = Priority.MEDIUM;
        Statement statement = new Statement();
        statement.setNamespace(namespace);
        statement.setFilter(Filter.equal("JOB_STATUS", jobStatus));
        statement.setSetName(set);

        System.out.println("start scan " + jobStatus);

        RecordSet rs = client.query(policy, statement);
        while (rs.next()) {
            Record record = rs.getRecord();
            if ((record.getInt("JOB_STATUS") == jobStatus)) {
                JobsBO job = new JobsBO();
                Key key = rs.getKey();
                job.setPrimaryKey(key.userKey.toLong());
                job.setID(record.getLong("ID"));
                job.setCreateProcess(record.getString("CREATE_PROCESS"));
                job.setJobsStatus(record.getInt("JOB_STATUS"));
                job.setJobType(record.getString("JOB_TYPE"));
                job.setFlow(record.getString("FLOW"));
                job.setSwitchType(record.getString("SWITCH_TYPE"));
                job.setCountry(record.getString("COUNTRY"));
                job.setJobParam(record.getString("JOB_PARAM"));
                job.setJobField(record.getString("JOB_FIELD"));
                job.setCreateDate(record.getString("CREATE_DATE"));
                job.setProcessDate(record.getString("PROCESS_DATE"));
                job.setWorkerID(record.getString("WORKER_ID"));
                job.setIp(record.getString("IP"));

                this.handler.onHandle(job);

//            throw new AerospikeException.ScanTerminated();
            }
        }
    }
}
