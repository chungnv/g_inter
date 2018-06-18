/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.bean.DataStructureField;
import com.viettel.ginterconnect.process.bean.Expression;
import com.viettel.ginterconnect.process.bean.Result;
import com.viettel.ginterconnect.process.bean.Rule;
import com.viettel.ginterconnect.process.bean.StandardizeField;
import com.viettel.ginterconnect.process.bean.SubExpression;
import com.viettel.ginterconnect.process.filter.importer.ImportUtils;
import com.viettel.ginterconnect.process.filter.object.TestResponse;
import com.viettel.ginterconnect.process.filter.object.TestExpression;
import com.viettel.ginterconnect.process.filter.object.TestRequest;
import com.viettel.ginterconnect.process.filter.object.TestResult;
import com.viettel.ginterconnect.process.filter.object.TestFilterRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebMethod;
import javax.jws.WebService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openide.util.Exceptions;

/**
 *
 * @author ubuntu
 */
@WebService
public class TestFilter {

    protected Logger logger = Logger.getLogger(TestFilter.class);

    @WebMethod
    public TestResponse testRule(TestRequest request) {
        return parseRule(request);
    }

    private TestResponse parseRule(TestRequest request) {
        TestResponse response = new TestResponse();
        List<TestFilterRule> lstTestRule = new ArrayList<>();
        response.setRules(lstTestRule);
        BaseFilter baseFilter = new BaseFilter("test_from_ws", logger);
        try {
            baseFilter.loadCache(request.getSwitchType(), request.getCountry());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        int i = 0;
        for (TestFilterRule testRule : request.getRules()) {
            i++;
            TestFilterRule ruleRestul = testRule.copyWithoutResult();
            HashMap<Long, TestResult> mapTestResult = testRule.getMapTestResult();
            lstTestRule.add(ruleRestul);
            Rule rule = new Rule();
            rule.setRuleId(Long.valueOf(i));
            rule.setRuleName(testRule.getName());
            Map<Long, List<SubExpression>> mapEx;
            HashMap<Long, List<DataStructureField>> mapResultOut = new HashMap<>();
            HashMap<Long, Result> mapResult = new HashMap<>();
            Map<Long, List<StandardizeField>> mapResultStandard;
            try {
                mapEx = parseExpression(testRule.getExpressions());
                mapResultStandard = parseResult(testRule.getResults(), mapResultOut, mapResult);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                testRule.setReturnCode("PARSE_CONFIG_EXCEPTION: " + ex.getMessage());
                return response;
            }
            baseFilter.setMapEx(mapEx);
            baseFilter.setMapResult(mapResultStandard);
            CdrObject co;
            try {
                co = baseFilter.createCdrObjectWithParam(request.getCdr(), request.getSpliter(), request.getStructureFilters());
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                testRule.setReturnCode("CREATE_CDR_FAIL");
                return response;
            }
            try {
                CdrContainer cdrCon = baseFilter.filterByRule(co, i, true);
                //kiem tra ket qua
                if (cdrCon.isRuleFlag()) {
                    //filter ok
                    testRule.setReturnCode("OK");
                    processResult(cdrCon, mapResultOut, ruleRestul, request.getOutputSplitter(), mapTestResult);
                } else {
                    //fail
                    testRule.setReturnCode("FILTER_NOT_OK");
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                testRule.setReturnCode("FILTER_EXCEPTOIN: " + ex.getMessage());
            }
        }
        return response;
    }

    private void processResult(CdrContainer cdrCon, HashMap<Long, List<DataStructureField>> mapResultOut, TestFilterRule testRule, 
            String spliter, HashMap<Long, TestResult> mapTestResult) {
        if (cdrCon == null) {
            return;
        }
        List<CdrSaveItem> lstSaveItem = cdrCon.getCdrSaveItem();
        if (lstSaveItem != null && lstSaveItem.size() > 0) {
            for (CdrSaveItem cdrSaveItem : lstSaveItem) {
                CdrObject cdrObject = cdrSaveItem.getCdrObject();
                Result result = cdrSaveItem.getResult();
                TestResult testResult = new TestResult();
                testResult.setName(mapTestResult.get(result.getResultId()).getName());
                testRule.getResults().add(testResult);
                try {
                    String arr[] = ImportUtils.parseOutputCdr(cdrObject, mapResultOut.get(result.getResultId()), logger);
                    testResult.setCdrOut(StringUtils.join(arr, spliter));
                    testResult.setReturnCode("OK");
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    testResult.setCdrOut(null);
                    testResult.setReturnCode("Exception");
                }
            }
        }
    }

    private Map<Long, List<SubExpression>> parseExpression(JSONArray jarrEx) {
        Map<Long, List<SubExpression>> mapEx = new HashMap<>();
        for (int i = 0; i < jarrEx.length(); i++) {
            JSONObject jObj = jarrEx.getJSONObject(i);
            Expression subEx = new Expression();
            subEx.setExpressionId(Long.valueOf(i));
            subEx.setExpressionName(jObj.getString("name"));
            mapEx.put(Long.valueOf(i), parseSubExpression(jObj.getJSONArray("subExpressions")));
        }
        return mapEx;
    }

    private Map<Long, List<SubExpression>> parseExpression(List<TestExpression> testExs) {
        Map<Long, List<SubExpression>> mapEx = new HashMap<>();
        int i = 0;
        for (TestExpression tEx : testExs) {
            i++;
            Expression subEx = new Expression();
            subEx.setExpressionId(Long.valueOf(i));
            subEx.setExpressionName(tEx.getName());
            mapEx.put(Long.valueOf(i), tEx.getSubExpressions());
        }
        return mapEx;
    }

    private List<SubExpression> parseSubExpression(JSONArray jarrSubEx) {
        List<SubExpression> lstSub = new ArrayList<>();
        for (int i = 0; i < jarrSubEx.length(); i++) {
            JSONObject jObj = jarrSubEx.getJSONObject(i);
            SubExpression subEx = new SubExpression();
            subEx.setSubExpressionId(Long.valueOf(i));
            subEx.setSubExpressionName(jObj.getString("name"));
            subEx.setLeftField(jObj.getString("leftField"));
            subEx.setIsConstant(Long.valueOf(jObj.getString("isConstant")));
            subEx.setConstant(jObj.getString("constant"));
            subEx.setOperator(jObj.getString("operator"));
            lstSub.add(subEx);
        }
        return lstSub;
    }

    private Map<Long, List<StandardizeField>> parseResult(JSONArray jarrResult,
            HashMap<Long, List<DataStructureField>> mapStructureOut, HashMap<Long, Result> mapResultById) {
        Map<Long, List<StandardizeField>> mapResult = new HashMap<>();
        for (int i = 0; i < jarrResult.length(); i++) {
            JSONObject jObj = jarrResult.getJSONObject(i);
            Result result = new Result();
            result.setResultId(Long.valueOf(i));
            result.setResultName(jObj.getString("name"));
            if (jObj.has("standardFields")) {
                mapResult.put(result.getResultId(), parseStandardField(jObj.getJSONArray("standardFields")));
            } else {
                List<StandardizeField> lstStd = new ArrayList<>();
                mapResult.put(result.getResultId(), lstStd);
            }
            mapStructureOut.put(result.getResultId(), parseStructureField(jObj.getJSONArray("structureOut")));
            mapResultById.put(result.getResultId(), result);
        }
        return mapResult;
    }

    private Map<Long, List<StandardizeField>> parseResult(List<TestResult> testResults,
            HashMap<Long, List<DataStructureField>> mapStructureOut, HashMap<Long, Result> mapResultById) {
        Map<Long, List<StandardizeField>> mapResult = new HashMap<>();
//        int i = 0;
        for (TestResult testResult : testResults) {
//            i++;
            Result result = new Result();
            result.setResultId(testResult.getId());
            result.setResultName(testResult.getName());
            mapResult.put(result.getResultId(), testResult.getStandardFields());
            mapStructureOut.put(result.getResultId(), testResult.getStructureOut());
            mapResultById.put(result.getResultId(), result);
        }
        return mapResult;
    }

    private List<StandardizeField> parseStandardField(JSONArray jarrStandardField) {
        List<StandardizeField> lstStd = new ArrayList<>();
        for (int i = 0; i < jarrStandardField.length(); i++) {
            JSONObject jObj = jarrStandardField.getJSONObject(i);
            StandardizeField stdField = new StandardizeField();
            stdField.setStandardizeId(Long.valueOf(i));
            stdField.setStandardizeField(jObj.getString("fieldName"));
            stdField.setFunctionId(Long.valueOf(jObj.getString("functionId")));
            lstStd.add(stdField);
        }
        return lstStd;
    }

    private List<DataStructureField> parseStructureField(JSONArray jarrStructureField) {
        List<DataStructureField> lstStrt = new ArrayList<>();
        for (int i = 0; i < jarrStructureField.length(); i++) {
            JSONObject jObj = jarrStructureField.getJSONObject(i);
            DataStructureField structureField = new DataStructureField();
            structureField.setStructureId(Long.valueOf(i));
            structureField.setFieldName(jObj.getString("fieldName"));
            if (jObj.has("fieldType")) {
                structureField.setFieldType(jObj.getString("fieldType"));
            }
            structureField.setFieldPos(jObj.getInt("position"));
            lstStrt.add(structureField);
        }
        Collections.sort(lstStrt, new Comparator<DataStructureField>() {
            @Override
            public int compare(DataStructureField o1, DataStructureField o2) {
                return o1.getFieldPos().compareTo(o2.getFieldPos());
            }
        });
        return lstStrt;
    }

}
