///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.viettel.aerospike.handle;
//
//import com.aerospike.client.AerospikeClient;
//import com.viettel.aerospike.bo.CdrThreadBO;
//import com.viettel.aerospike.bo.JobsBO;
//import com.viettel.aerospike.bo.MasterThreadBO;
//import com.viettel.aerospike.main.Client;
//import com.viettel.aerospike.main.Parameters;
//import org.apache.log4j.Logger;
//
///**
// *
// * @author QUANGNH
// */
//public class QueryHandler extends Handler {
//
//    private static Logger logger = Logger.getLogger(QueryHandler.class);
//    private Handler processorHandler;
//    private AerospikeClient client;
//    private Parameters params;
//    
//    public void setProcessorHandler(Handler handler) {
//        this.processorHandler = handler;
//    }
//    
//    public void setClient(AerospikeClient client){
//        this.client = client;
//    }
//    
//    public void setParameters(Parameters params){
//        this.params = params;
//    }
//
//    @Override
//    public void onHandle(MasterThreadBO object) {
//        logger.debug("MasterId: " + object.getMasterId());
//        System.out.println("MasterId: " + object.getMasterId());
//        //Client.getInstance().updateMasterProcessRecord(object);
//        this.processorHandler.onHandle(object);
//    }
//
//    @Override
//    public void onHandle(JobsBO object) {
//        logger.debug("JobID: " + object.getID());
//        System.out.println("JobID: " + object.getID());
//        //Client.getInstance().updateJobsRecord(object);
//        this.processorHandler.onHandle(object);
//    }
//    
//    @Override
//    public void onHandle(CdrThreadBO object) {
//        logger.debug("Cdr: " + object.getCdrID());
//        System.out.println("Cdr: " + object.getCdrID());
//        //Client.getInstance().updateThreadProcessRecord(object);
//        this.processorHandler.onHandle(object);
//    }
//
//}
