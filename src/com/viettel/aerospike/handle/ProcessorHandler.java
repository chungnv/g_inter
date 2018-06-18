///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.viettel.aerospike.handle;
//
//import com.viettel.aerospike.bo.CdrThreadBO;
//import com.viettel.aerospike.bo.JobsBO;
//import com.viettel.aerospike.bo.MasterThreadBO;
//import com.viettel.aerospike.query.JobsQuery;
//import org.apache.log4j.Logger;
//
///**
// *
// * @author QUANGNH
// */
//public class ProcessorHandler extends Handler {
//    
//    private static Logger logger = Logger.getLogger(JobsQuery.class);
//    
//    @Override
//    public void onHandle(MasterThreadBO object) {
//        logger.debug(object);
//    }
//    
//    @Override
//    public void onHandle(JobsBO object) {
//        //
//        logger.info("Go to job");
//    }
//    
//    @Override
//    public void onHandle(CdrThreadBO object) {
//        
//    }
//}
