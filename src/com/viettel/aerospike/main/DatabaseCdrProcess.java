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

public class DatabaseCdrProcess extends Example {

    public DatabaseCdrProcess(Console console) {
        super(console);
    }

    @Override
    public void runExample(AerospikeClient client, Parameters params) throws Exception {
        if (!params.hasUdf) {
            console.info("User defined functions are not supported by the connected Aerospike server.");
            return;
        }
        register(client, params);
        createTableMdDataStructure(client, params);
        createTableMdExpression(client, params);
        createTableMdFunction(client, params);
        createTableMdImportTable(client, params);
        createTableMdResult(client, params);
        createTableMdRule(client, params);
        createTableMdStandadize(client, params);
        createTableMdSubExpression(client, params);
        createTableCdrData(client, params);
    }

    private void register(AerospikeClient client, Parameters params) throws Exception {
        RegisterTask task = client.register(null, "../udf/record_example.lua", "record_example.lua", Language.LUA);
        task.waitTillComplete();
    }

    private void createTableMdDataStructure(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create MD Data Structure Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();
        for (int i = 1; i <= 10; i++) {
            Key key = new Key(params.namespace, "md_data_structure", i);
            Bin bin = new Bin("STRUC_ID", 1);
            Bin bin2 = new Bin("STRUC_NAME", "Thử có dấu xem sao");
            Bin bin3 = new Bin("FIELD_NAME", "Đây là mô tả");
            Bin bin4 = new Bin("FIELD_TYPE", "Nghiệp vụ");
            Bin bin5 = new Bin("FIELD_POS", 1);
            Bin bin6 = new Bin("FIELD_MASK", "MASK");
            Bin bin7 = new Bin("DEFAULT_VALUE", "loai tong dai");
            Bin bin8 = new Bin("DESCRIPTION", "Thi truong");
            Bin bin9 = new Bin("IS_DELETE", 1);
            Bin bin10 = new Bin("SWITCH_TYPE", "GET");
            Bin bin11 = new Bin("COUNTRY", "VN");
            Bin bin12 = new Bin("FIELD_FORMAT", "FORMAT");

            client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                    bin7, bin8, bin9, bin10, bin11, bin12);
        }

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }

    private void createTableMdResult(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create MD RESULT Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();

        Key key = new Key(params.namespace, "md_result", 1);
        Bin bin = new Bin("RESULT_ID", 1);
        Bin bin2 = new Bin("LOCAL_FOLDER", "/VAR/DIR");
        Bin bin3 = new Bin("IS_DELETE", 1);
        Bin bin4 = new Bin("RESULT_NAME", "Ten tham so");
        Bin bin5 = new Bin("RULE_ID", 1);
        Bin bin6 = new Bin("F_NAME_TEMP", "Format cua tham so");
        Bin bin7 = new Bin("F_EXTENSION", "Gia tri tham so");
        Bin bin8 = new Bin("RESULT_TYPE", "RESULT");
        Bin bin9 = new Bin("DESCRIPTION", "RESULT");
        Bin bin10 = new Bin("SEQ_NAME", "RESULT");
        Bin bin11 = new Bin("DATA_STRUCT_ID", 1);
        Bin bin12 = new Bin("IMP_TAB_ID", 1);
        Bin bin13 = new Bin("SWITCH_TYPE", "RESULT");
        Bin bin14 = new Bin("COUNTRY", "RESULT");

        client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                bin7, bin8, bin9, bin10, bin11, bin12, bin13, bin14);

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }

    private void createTableMdRule(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create Md Rule Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();

        Key key = new Key(params.namespace, "md_rule", 1);
        Bin bin = new Bin("RULE_ID", 1);
        Bin bin2 = new Bin("SWITCH_TYPE", "GET_1");
        Bin bin3 = new Bin("IS_DELETE", 1);
        Bin bin4 = new Bin("DESCRIPTION", "GET");
        Bin bin5 = new Bin("ALLOWABLE_EXIT", 1);
        Bin bin6 = new Bin("RULE_TYPE", "loai tong dai");
        Bin bin7 = new Bin("COUNTRY", "Thi truong");
        Bin bin8 = new Bin("FUNC_CDR_IDEN", 1);
        Bin bin9 = new Bin("RULE_NAME", "NAME");

        client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                bin7, bin8, bin9);

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }

    private void createTableMdStandadize(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create Md Standadize Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();

        Key key = new Key(params.namespace, "md_standadize", 1);
        Bin bin = new Bin("STAND_ID", 1);
        Bin bin2 = new Bin("RESULT_ID", 1);
        Bin bin3 = new Bin("STAND_FIELD", "FIELD");
        Bin bin4 = new Bin("FUNCTION_ID", 1);
        Bin bin5 = new Bin("IS_DELETE", 1);
        Bin bin6 = new Bin("PRIORITY", 1);
        Bin bin7 = new Bin("SWITCH_TYPE", "RESULT");
        Bin bin8 = new Bin("COUNTRY", "RESULT");

        client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                bin7, bin8);

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }

    private void createTableMdExpression(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create Md Expression Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();

        Key key = new Key(params.namespace, "md_expression", 1);
        Bin bin = new Bin("EX_ID", 1);
        Bin bin2 = new Bin("EX_NAME", "GET_1");
        Bin bin3 = new Bin("IS_DELETE", 1);
        Bin bin4 = new Bin("RULE_NAME", "GET");
        Bin bin5 = new Bin("DESCRIPTION", "DES");
        Bin bin6 = new Bin("SWITCH_TYPE", "RESULT");
        Bin bin7 = new Bin("COUNTRY", "RESULT");

        client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5,
                bin6, bin7);

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }

    private void createTableMdFunction(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create Md Function Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();

        Key key = new Key(params.namespace, "md_function", 1);
        Bin bin = new Bin("FUNC_ID", 1);
        Bin bin2 = new Bin("FUNC_NAME", "GET_1");
        Bin bin3 = new Bin("IS_DELETE", 1);
        Bin bin4 = new Bin("PARAMS", "GET");
        Bin bin5 = new Bin("IS_SCRIPT", 1);
        Bin bin6 = new Bin("FIELD_PARAMS", "loai tong dai");
        Bin bin7 = new Bin("DESCRIPTION", "Thi truong");
        Bin bin8 = new Bin("IMP_PACK", "RESULT");

        client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                bin7);

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }

    private void createTableMdSubExpression(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create Md Sub Expression Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();

        Key key = new Key(params.namespace, "md_sub_expression", 1);
        Bin bin = new Bin("SUB_EX_ID", 1);
        Bin bin2 = new Bin("LEFT_FIELD", "GET_1");
        Bin bin3 = new Bin("LEFT_FUNC_ID", 1);
        Bin bin4 = new Bin("OPERATOR", "GET");
        Bin bin5 = new Bin("CONSTANT", "ABC");
        Bin bin6 = new Bin("RIGHT_FUNC_ID", 1);
        Bin bin7 = new Bin("RIGHT_FIELD", "Thi truong");
        Bin bin8 = new Bin("IS_DELETE", 1);
        Bin bin9 = new Bin("EX_ID", 1);
        Bin bin10 = new Bin("IS_CONSTANT", 1);
        Bin bin11 = new Bin("SUB_EX_NAME", "NAME");
        Bin bin12 = new Bin("SWITCH_TYPE", "RESULT");
        Bin bin13 = new Bin("COUNTRY", "RESULT");

        client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                bin7, bin8, bin9, bin10, bin11, bin12, bin13);

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }

    private void createTableMdImportTable(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create Md Import Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();

        Key key = new Key(params.namespace, "md_import_table", 1);
        Bin bin = new Bin("ID", 1);
        Bin bin2 = new Bin("TABLE_NAME", "TABLE NAME");
        Bin bin3 = new Bin("DATABASE_NAME", "GET");
        Bin bin4 = new Bin("URL", "ABC");
        Bin bin5 = new Bin("USER_NAME", "VIETTEL");
        Bin bin6 = new Bin("PASSWORD", "Thi truong");
        Bin bin7 = new Bin("BATCH_SIZE", 1);
        Bin bin8 = new Bin("MAX_CONN", 1);
        Bin bin9 = new Bin("LOG_INS", 1);
        Bin bin10 = new Bin("LOG_INS_TAB", "NAME");
        Bin bin11 = new Bin("IS_DELETE", 1);

        client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                bin7, bin8, bin9, bin10, bin11);

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }

    private void createTableCdrData(
            AerospikeClient client,
            Parameters params
    ) throws Exception {
        console.info("Create Cdr Data Table records.");

        client.writePolicyDefault.sendKey = true;
        long begin = Calendar.getInstance().getTimeInMillis();

        for (int i = 1; i <= 5; i++) {
            Key key = new Key(params.namespace, "cdr_data", i);
            Bin bin = new Bin("CDR_ID", i);
            Bin bin2 = new Bin("CALLING", "TABLE NAME");
            Bin bin3 = new Bin("CALLED", "GET");
            Bin bin4 = new Bin("START_TIME", "ABC");
            Bin bin5 = new Bin("DURATION", "VIETTEL");
            Bin bin6 = new Bin("TRUNK_IN", "Trunk IN");
            Bin bin7 = new Bin("TRUNK_OUT", "Trunk OUT");
            Bin bin8 = new Bin("SMS_IN", "SMS IN");
            Bin bin9 = new Bin("SMS_OUT", "SMS OUT");

            client.put(params.writePolicy, key, bin, bin2, bin3, bin4, bin5, bin6,
                    bin7, bin8, bin9);

        }

        long end = Calendar.getInstance().getTimeInMillis();

        console.info(
                "Excuted time = " + (end - begin) + "ms");
    }
}
