package com.viettel.ginterconnect.process.worker.impl;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.process.worker.AbstractWorkerBusiness;
import com.viettel.ginterconnect.util.GIClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hoangsinh on 26/08/2017.
 */
public class EXPORTTOORACLE extends AbstractWorkerBusiness {

    String P_URL = null;
    String P_USER_NAME = null;
    String P_PASSWORD = null;
    String P_TABLE = null;
    String P_SUM_DATE = null;
    String P_MARKET = null;
    String P_GROUP_BY_VALUE = null;

    @Override
    public void afterBusiness() {

    }

    @Override
    public boolean doBusiness() {
        try {
            exportToOracle(currentJob);
            updateSuccessJob();
//            createNewJob();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void exportToOracle(JobsBO job) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
//            String[] paramVaules = job.getJobParam().split(com.viettel.ginterconnect.util.Constants.JOB_FLOW_SPLIT);
//            String[] paramFields = job.getJobField().split(com.viettel.ginterconnect.util.Constants.JOB_FLOW_SPLIT);

            //params


            SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

//            for (int i = 0; i < paramFields.length; i++) {
//                String field = paramFields[i];
//                String value = paramVaules[i];
//                if ("P_URL".equals(field) && !StringUtils.isEmpty(value)) {
//                    P_URL = value;
//                } else if ("P_USER_NAME".equals(field) && !StringUtils.isEmpty(value)) {
//                    P_USER_NAME = value;
//                } else if ("P_PASSWORD".equals(field) && !StringUtils.isEmpty(value)) {
//                    P_PASSWORD = value;
//                } else if ("P_TABLE".equals(field) && !StringUtils.isEmpty(value)) {
//                    P_TABLE = value;
//                } else if ("P_MARKET".equals(field) && !StringUtils.isEmpty(value)) {
//                    P_MARKET = value;
//                } else if ("P_SUM_DATE".equals(field) && !StringUtils.isEmpty(value)) {
//                    P_SUM_DATE = sdf.parse(value);
//                } else if ("P_GROUP_BY_VALUE".equals(field) && !StringUtils.isEmpty(value)) {
//                    P_GROUP_BY_VALUE = Arrays.asList(value.split(";"));
//                }
//            }
            if (P_URL != null && P_USER_NAME != null && P_PASSWORD != null && P_TABLE != null && P_SUM_DATE != null) {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                connection = DriverManager.getConnection(P_URL, P_USER_NAME, P_PASSWORD);
                List<String> groupByVal = Arrays.asList(P_GROUP_BY_VALUE.split(";"));
                long startTime = new Date().getTime();
                Map<String, Map<String, Object>> map = GIClient.getInstance().sumRevenueByDay(P_SUM_DATE, groupByVal, P_MARKET);
                int count = 0;
                if (map != null) {
                    boolean buildStatement = true;
                    for (Map<String, Object> rec : map.values()) {
                        if (buildStatement) {
                            String columnList = "";
                            String valueList = "";
                            for (String column : rec.keySet()) {
                                columnList += column + ",";
                                valueList += "?,";
                            }
                            columnList = columnList.substring(0, columnList.length() - 1);
                            valueList = valueList.substring(0, valueList.length() - 1);
                            String insertSql = "INSERT INTO " + P_TABLE + "(" + columnList + ") VALUES (" + valueList + ")";
                            logInfo(insertSql);
                            statement = connection.prepareStatement(insertSql);
                            buildStatement = false;
                        }
                        int index = 1;
                        for (Object value : rec.values()) {
                            statement.setObject(index, value);
                            index++;
                        }
                        statement.addBatch();
                        count++;
                        if (count % 5000 == 0) {
                            statement.executeBatch();
                        }
                        statement.executeBatch();
                    }
                } else {
                    logInfo("sumRevenueByDay no record");
                }

                logInfo("Insert success " + count + " records Elapsed time: " + (new Date().getTime() - startTime) / 1000 + " seconds");
            } else {
                logInfo("Invalidate parameter!");
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
