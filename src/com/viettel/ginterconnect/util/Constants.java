/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.util;

/**
 *
 * @author ubuntu
 */
public class Constants {

    //1. field param
    public static int PARAM_TYPE_INT = 5;
    public static int PARAM_TYPE_STRING = 4;
    public static int PARAM_TYPE_DOUBLE = 3;
    public static int PARAM_TYPE_DATE = 2;
    public static int PARAM_TYPE_LONG = 1;

    //2. compress
    public static final int STANDARD_FILEID_LENGTH = 15;
    public static final Long ADD_FILE_ID = 1L;
    public static final String STANDARD_FILEID_PREFIX = "000000000000000";

    //3. Get worker param
    public static String FTP_TYPE = "FTP_TYPE";
    public static String FTP_HOST = "FTP_HOST";
    public static String FTP_PORT = "FTP_PORT";
    public static String FTP_USERNAME = "FTP_USERNAME";
    public static String FTP_PASSWORD = "FTP_PASSWORD";
    public static String PROCESS_CODE = "PROCESS_CODE";
    public static String FTP_REMOTE_DIR = "FTP_REMOTE_DIR";
    public static String FTP_FILENAME = "FTP_FILENAME";
    public static String DELETE_AFTER_GET = "DELETE_AFTER_GET";
    public static String FTP_COMPRESS = "FTP_COMPRESS";

    public static String DOWNLOAD_TEMP = "/ftp/download_temp/";
    public static String DOWNLOAD_OK = "/ftp/download/";
    public static String DECOMPRESS_TEMP = "/ftp/compress_temp/";
    public static String DECOMPRESS_OK = "/ftp/decompress/";

    public static long PRIORITY_MODIFIED_TIME = 1;
    public static long PRIORITY_SEQUENCE_AND_MODIFIED_TIME = 2;
    public static long PRIORITY_JUST_MATCH_NAME = 3;
    public static long PRIORITY_CHECK_FILENAME_FROM_LOG = 4;
    public static long PRIORITY_MODIFIED_TIME_AND_MIN_SEQ = 5;
    public static long PRIORITY_NEXT_SEQUENCE = 6;
    public static long PRIORITY_SEQUENCE_AND_FILENAME_TIME = 7;
    public static long PRIORITY_SEQUENCE_ONLY = 8;
    public static long PRIORITY_OTHERS = 9;

    //4. Convert
    public static String CONVERT_INPUT_DIR = "INPUT_DIR";
    public static String CONVERT_FILENAME = "FILENAME";
    public static String CONVERT_SWITCH_TYPE = "SWITCH_TYPE";
    public static String CONVERT_COUNTRY = "COUNTRY";
    public static String CONVERT_TEMP_DIR = "/data/convert_temp/";
    public static String CONVERT_ERR_DIR = "/data/convert_err/";
    public static String CONVERT_OK = "/data/convert/";
    public static String GGSN_DEFAULT_DATE_FORMAT = "yyyyMMddHHmmss";
    
    //5.merge
    public static String MERGE_TEMP_DIR = "/temp/";

    //5. job property
    public static final int JOB_SUCCESS_STATUS = 0;
    public static final int JOB_NEW_STATUS = 1;
    public static final int JOB_PROCESSING_STATUS = 2;
    public static final int JOB_FAIL_STATUS = -1;
//    public static final String JOB_FLOW_SPLIT = "@A@";
//    public static final String JOB_FLOW_JOIN = "@A@";

    //master
    public static final String MASTER_JOB_NEW = "0";
    public static final String MASTER_JOB_PROCESSING = "2";
    
    //6. map direction cdr
    //111. all
    public static String PARAM_SEPERATE = "|";
    public static String FLOW_SEPERATE = "|";
    public static final String DEFAULT_DATE_FORMAT = "yyyyMMddHHmmss";

    public static String MASTER_GET_IMPL_PACKAGE_NAME = "com.viettel.ginterconnect.process.master.impl.";
    public static String WORKER_IMPL_PACKAGE = "com.viettel.ginterconnect.process.worker.impl.";
    public static String WORKER_INPUT_FIELD_NAME_PREFIX = "P_";
    public static String WORKER_CONVERT_IMPL_PCKG = "com.viettel.ginterconnect.worker.convert.switchboard.";
    public static String FILTER_FUNCTION_PCKG = "com.viettel.ginterconnect.process.filter.function.";
    public static String FILTER_FUNCTION_IMPL_PCKG = "com.viettel.ginterconnect.process.filter.function.impl.";
    public static String FILTER_ASPECT_FUNCTION_IMPL_PCKG = "com.viettel.ginterconnect.process.filter.aspect.impl.";

    public static String SYSTEM_PARAM_WORKING_DIR = "WORKING_DIR";
    public static String SYSTEM_MASTER_DELAY = "MASTER_SLEEP";
    public static String SYSTEM_WORKER_DELAY = "WORKER_SLEEP";
    public static String SYSTEM_AEROSPIKE_IP = "AEROSPIKE_IP";
    public static String SYSTEM_AEROSPIKE_PORT = "AEROSPIKE_PORT";
    public static String SYSTEM_NUMBER_OF_WORKER = "NUMBER_OF_WORKER";
    public static String SYSTEM_NUMBER_OF_MASTER_THREAD = "NUMBER_OF_MASTER_THREAD";
    public static String SYSTEM_AEROSPIKE_USERNAME = "AEROSPIKE_USERNAME";
    public static String SYSTEM_AEROSPIKE_PASSWORD = "AEROSPIKE_PASSWORD";
    public static String SYSTEM_NAMESPACE = "AEROSPIKE_NAMESPACE";
    public static String FTP_CONNECTION_POOL = "FTP_CONNECTION_POOL";
    public static String JOB_TIMEOUT_IN_SEC = "JOB_TIMEOUT_IN_SEC";
    public static String MASTER_JOB_TIMEOUT_IN_SEC = "MASTER_JOB_TIMEOUT_IN_SEC";
    public static String TEST_PORT = "TEST_PORT";
    public static String DECOMPRESS_TYPE = "DECOMPRESS_TYPE";

    //Result output type
    public static final String RESULT_TYPE_AS = "1";
    public static final String RESULT_NO_WRITE = "0";
    public static final String RESULT_TYPE_FILE = "2";
    public static final String RESULT_TYPE_ORA = "3";

    //number function
    public static final int NUMBER_FUNCTION_SMS = 1;
    public static final int NUMBER_FUNCTION_CALL = 2;
    public static final int NUMBER_FUNCTION_ALL = 3;
    
    //filter constant
    public static final String FILENAME_TMP = ".tmp";
    public static final String FILENAME_ERR = ".ERR";
    public static final String FILTER_FIELD_SWITCH_ID = "SWITCH_ID";
    public static final String FILTER_FIELD_FILE_ID = "FILE_ID";
    public static final String FILTER_FIELD_FILE_NAME = "FILE_NAME";
    public static final String FILTER_FIELD_DIR = "DIR";
    public static final String FILTER_FIELD_MODIFIED_DATE = "MODIFIED_DATE";
    
    //otehr
    public static final String CONFIG_FILE_HEADER = "file_header";

}
