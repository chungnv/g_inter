/*
 * Copyright 2012-2017 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more contributor
 * license agreements WHICH ARE COMPATIBLE WITH THE APACHE LICENSE, VERSION 2.0.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.viettel.aerospike.main;

import com.viettel.aerospike.processor.JobsProcess;
import com.viettel.aerospike.processor.MasterProcess;
import com.viettel.aerospike.handle.Handler;
import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Host;
import com.aerospike.client.Key;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.CommitLevel;
import com.aerospike.client.policy.TlsPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.viettel.aerospike.bo.JobsBO;
import com.viettel.aerospike.bo.MasterThreadBO;
import com.viettel.aerospike.processor.CdrDataProcess;
import com.viettel.aerospike.processor.Sequence;
import com.viettel.ginterconnect.util.DateTimeUtils;
import com.viettel.ginterconnect.util.GIUtils;
import java.util.Date;
import org.apache.log4j.Logger;

public class Client {

    private static Logger logger = Logger.getLogger(Client.class);

    AerospikeClient client = null;
    protected static final int DEFAULT_TIMEOUT_MS = 1000;
    private static Client Instance = new Client();
    private static Client UpdateJobInstance = new Client();
    private static Client InsertInstance = new Client();
    private static Client SequenceInstance = new Client();
    MasterProcess master;
    JobsProcess jobs;
    CdrDataProcess cdr_data;
    Parameters params;
    Sequence sequence;
    Handler processorHandler;
    String namespace = null;
    String ip;
    int port;

    public void setProcessorHandler(Handler handler) {
        this.processorHandler = handler;
        master.setHandler(processorHandler);
        jobs.setHandler(processorHandler);
    }

    public Client() {
        master = new MasterProcess(null);
        jobs = new JobsProcess(null);
        cdr_data = new CdrDataProcess(null);
        sequence = new Sequence();
    }

    public static Client getInstance() {
        if (null == Instance) {
            Instance = new Client();
        }
        return Instance;
    }

    public static Client getUpdateJobInstance() {
        if (null == UpdateJobInstance) {
            UpdateJobInstance = new Client();
        }
        return UpdateJobInstance;
    }

    public static Client getInsertInstance() {
        if (null == InsertInstance) {
            InsertInstance = new Client();
        }
        return InsertInstance;
    }

    public static Client getSequenceInstance() {
        if (null == SequenceInstance) {
            SequenceInstance = new Client();
        }
        return SequenceInstance;
    }

    private boolean sendPingAER() {
        try {
            String ipAdress = GIUtils.getIpAddressLstString();
            String dateStr = DateTimeUtils.convertDateToString(new Date(), "dd/MM/yyyy hh:mm:ss");
            if (dateStr != null) {
                WritePolicy writePolicy = new WritePolicy();
                writePolicy.commitLevel = CommitLevel.COMMIT_MASTER;
                writePolicy.socketTimeout = 5000;
                writePolicy.maxRetries = 1;
                writePolicy.sleepBetweenRetries = 50;

                Key key = new Key(namespace, "PingInfo", ipAdress);
                Bin[] bins = new Bin[2];
                bins[0] = new Bin("IP", ipAdress);
                bins[1] = new Bin("TimePing", dateStr);
                client.put(writePolicy, key, bins);
                return true;
            }
            logger.error("Cant not convert date to string for ping");
            return false;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    public void checkAndReconnect() {

        if (client == null) {
            if (connect()) {
                logger.debug("Connect success");
            } else {
                logger.debug("Connect failure");
            }
        } else if (sendPingAER()) {
//            logger.info("Ping server success");
        } else if (connect()) {
            logger.info("Reconnect success");
        } else {
            logger.info("Reconnect failure");
//            try {
//                if (client != null)
//                    client.close();
//            } catch (Exception ex) {}
//            client = null;
        }
    }

    public void init(String ip, int port, String username, String password, String namespace) {
        try {
            this.ip = ip;
            this.port = port;
            this.namespace = namespace;
            params = parseParameters(ip, port, username, password);

            connect();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            //client.close();
        }
    }

    private boolean connect() {
        try {
            if (params != null) {
                ClientPolicy policy = new ClientPolicy();
                policy.batchPolicyDefault.maxConcurrentThreads = 20;
                policy.user = params.user;
                policy.password = params.password;
                policy.tlsPolicy = params.tlsPolicy;
                params.policy = policy.readPolicyDefault;
                params.writePolicy = policy.writePolicyDefault;
                String s[] = ip.split(",");
                Host[] aeroHost = new Host[s.length];
                for (int i = 0; i < s.length; i++) {
                    Host host = new Host(s[i], port);
                    aeroHost[i] = host;
                }
                client = new AerospikeClient(policy, aeroHost);
                params.setServerSpecific(client);
                return true;
            } else {
                logger.warn("Param null");
                return true;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        } finally {
            //client.close();
        }
    }

    private Parameters parseParameters(String ip, int port, String username, String password) {
        String host = ip;
        //String host = "127.0.0.1";
//        String portString = "3000";
//        int port = Integer.parseInt(portString);
        String namespace = this.namespace;
        String set = "jobs";

        if (set.equals("empty")) {
            set = "";
        }

        TlsPolicy tlsPolicy = null;

        return new Parameters(tlsPolicy, host, port, username, password, namespace, set);
    }

    public void insertJobsRecord(Long key, JobsBO object) throws AerospikeException {
//        String input_cdr_data = "1,1,CALLING,CALLED,2017-07-24,3,2,4,5,7;2,2,CALLING,CALLED,2017-07-24,3,2,4,5,7;3,3,CALLING,CALLED,2017-07-24,3,2,4,5,7";
//        cdr_data.insertRecord(client, params, input_cdr_data);

//          String input_job_data = "1,1,Create process,0,Jobtype,Flow,SwitchType,Vietnam,3,2017-07-24,2017-07-24,32143-23,127.0.0.1;"
//                  + "2,2,Create process,0,Jobtype,Flow,SwitchType,Vietnam,3,2017-07-24,2017-07-24,32143-23,127.0.0.1;"
//                  + "3,3,Create process,0,Jobtype,Flow,SwitchType,Vietnam,3,2017-07-24,2017-07-24,32143-23,127.0.0.1;";
        try {
            params.setSet("jobs");
            params.setNamespace(this.namespace);
            jobs.insertJobRecord(client, params, key, object);
        } catch (AerospikeException ae) {
            throw ae;
        }
    }

    public void insertSuccessJobsRecord(Long key, JobsBO object) throws AerospikeException {
//        String input_cdr_data = "1,1,CALLING,CALLED,2017-07-24,3,2,4,5,7;2,2,CALLING,CALLED,2017-07-24,3,2,4,5,7;3,3,CALLING,CALLED,2017-07-24,3,2,4,5,7";
//        cdr_data.insertRecord(client, params, input_cdr_data);

//          String input_job_data = "1,1,Create process,0,Jobtype,Flow,SwitchType,Vietnam,3,2017-07-24,2017-07-24,32143-23,127.0.0.1;"
//                  + "2,2,Create process,0,Jobtype,Flow,SwitchType,Vietnam,3,2017-07-24,2017-07-24,32143-23,127.0.0.1;"
//                  + "3,3,Create process,0,Jobtype,Flow,SwitchType,Vietnam,3,2017-07-24,2017-07-24,32143-23,127.0.0.1;";
        try {
            params.setSet("success_jobs");
            params.setNamespace(this.namespace);
            jobs.insertSuccessRecord(client, params, key, object, null);
        } catch (AerospikeException ae) {
            throw ae;
        }
    }

    public void insertFailJobsRecord(Long key, JobsBO object, String message) throws AerospikeException {
//        String input_cdr_data = "1,1,CALLING,CALLED,2017-07-24,3,2,4,5,7;2,2,CALLING,CALLED,2017-07-24,3,2,4,5,7;3,3,CALLING,CALLED,2017-07-24,3,2,4,5,7";
//        cdr_data.insertRecord(client, params, input_cdr_data);

//          String input_job_data = "1,1,Create process,0,Jobtype,Flow,SwitchType,Vietnam,3,2017-07-24,2017-07-24,32143-23,127.0.0.1;"
//                  + "2,2,Create process,0,Jobtype,Flow,SwitchType,Vietnam,3,2017-07-24,2017-07-24,32143-23,127.0.0.1;"
//                  + "3,3,Create process,0,Jobtype,Flow,SwitchType,Vietnam,3,2017-07-24,2017-07-24,32143-23,127.0.0.1;";
        try {
            params.setSet("failed_jobs");
            params.setNamespace(this.namespace);
            jobs.insertSuccessRecord(client, params, key, object, message);
        } catch (AerospikeException ae) {
            throw ae;
        }
    }

    public void getJobsRecordByStatus(int status) {
        jobs.getRecord(client, this.namespace, status);
    }

    public void getUpdateJob(int status) {
        jobs.getUpdateJob(client, this.namespace, status);
    }

    public Object updateJobsRecord(JobsBO object) {
        params.setSet("jobs");
        params.setNamespace(this.namespace);
        return jobs.updateRecord(client, params, object);
    }

    public Object updateJobsStatus(JobsBO object) {
        params.setSet("jobs");
        params.setNamespace(this.namespace);
        return jobs.updateJobStatus(client, params, object);
    }

    public boolean deleteJobRecord(long key) throws Exception {
        return jobs.deleteRecord(client, params, key);
    }

    public void getMasterProcessRecord(boolean isActive) {
        master.getRecord(client, this.namespace, isActive);
    }

    public void getMasterProcessRecordForUpdate(String status) {
        master.getRecordForUpdateTimeout(client, this.namespace, status);
    }

    public Object updateMasterProcessRecord(MasterThreadBO object) {
        params.setSet("master_process");
        params.setNamespace(this.namespace);
        return master.updateRecord(client, params, object);
    }

    public Object updateForSelectMasterProcessRecord(MasterThreadBO object) {
        params.setSet("master_process");
        params.setNamespace(this.namespace);
        return master.updateForSelect(client, params, object);
    }

    public Object updateMasterBOAfterRun(MasterThreadBO object) {
        params.setSet("master_process");
        params.setNamespace(this.namespace);
        return master.updateAfterRun(client, params, object);
    }

    public long getSequence(String seqName) throws Exception {
        String name = (seqName == null ? "dual" : seqName);
        params.setSet(name);
        params.setNamespace(this.namespace);
        return sequence.getSequence(client, params);
    }

    protected Console console;

    public Client(Console console) {
        this.console = console;
    }

}
