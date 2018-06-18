/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.filter;

//import com.aerospike.client.query.PredExp;
import com.aerospike.client.query.Filter;
import com.aerospike.client.query.PredExp;
import com.viettel.ginterconnect.process.bean.*;
import com.viettel.ginterconnect.process.exception.FilterException;
import com.viettel.ginterconnect.util.GIClient;
import org.apache.log4j.Logger;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;

/**
 *
 * @version 1.0
 * @since May 9, 2011
 */
public final class GICache {

    //check maploaded
    protected Logger logger = Logger.getLogger(GICache.class);
    
    private Map<String, String> mapLoadedObject = new HashMap<>();
    protected List<Integer> lsLoadedTime = new ArrayList<>();

    private Map<Long, FunctionBO> mapFunction = new HashMap<>();
    private boolean isLoaded = false;
    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = manager.getEngineByName("JavaScript");
    private Map<String, Switchboard> mapSwitchboard = new HashMap<>();
    private Map<String, Map<String, LinkedHashMap<Long, Rule>>> allMapRule = new HashMap<>();
    private Map<String, Map<String, Map<Long, Map<Long, List<SubExpression>>>>> ruleExpression = new HashMap<>();
//    private Map<String, Map<String, List<DataStructureField>>> allMapDataStructureField = new HashMap<>();
    private Map<String, List<DataStructureField>> allMapDataStructureField = new HashMap<>();
    private Map<String, List<DataStructureField>> allMapImportField = new HashMap<>();
    private Map<String, Map<String, Map<Long, List<Result>>>> allMapResult = new HashMap<>();
    private Map<String, Map<String, Map<Long, List<StandardizeField>>>> allMapStandardizeField = new HashMap<>();

    private static GICache filterCache = null;

    public synchronized static GICache getInstance(String switchType, String country, Logger logger) throws Exception {
        if (filterCache == null) {
            filterCache = new GICache(switchType, country);
        }
        return filterCache;
    }

//    public GICache(Logger logger) throws Exception {
//        if (!isLoaded) {
//            this.logger = logger;
//            isLoaded = true;
//        }
//    }

    public GICache(String switchType, String country) throws Exception {
        if (!isLoaded) {
//            loadFieldConfig(switchType, country);
            loadRuleExpression(switchType, country);
            loadResultByRule();
            loadStandardizeByResult();
            loadFunction();
            getSwitchboard("1");
            isLoaded = true;
        }
    }

    //loaded by hour
    private boolean checkCacheLoaded(String name) {
        if (name == null || name.isEmpty() || !mapLoadedObject.containsKey(name)) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int lastDay = day - 1;
        if (day == 1) {
            int month = c.get(Calendar.MONTH);
            if (month == 0) {
                c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 1);
                c.set(Calendar.MONTH, 11);
            } else {
                c.set(Calendar.DAY_OF_MONTH, 0);
            }
            lastDay = c.get(Calendar.DAY_OF_MONTH);
        }
        boolean result = true;
        if (lsLoadedTime == null || lsLoadedTime.isEmpty() || lsLoadedTime.get(0) != lastDay || lsLoadedTime.get(1) == null || lsLoadedTime.get(1) != hour) {
            return false;
        }
        lsLoadedTime.set(0, day);
        lsLoadedTime.set(1, hour);
        return result;
    }

    //---end get-set
    public Object executeJavaScriptFuntion(String functionName, Object object, String inputField, String params, String fieldParams) throws ScriptException, NoSuchMethodException {
        Invocable inv = (Invocable) engine;
        return inv.invokeFunction(functionName, object, inputField, params, fieldParams);
    }

//    public synchronized List<DataStructureField> loadFieldConfig(String switchType, String country) throws FilterException {
    public synchronized List<DataStructureField> loadFieldConfig(String structureName) throws FilterException {
        if (mapLoadedObject.containsKey(structureName + "_structureIn")) {
            List<DataStructureField> lstFieldConfig = allMapDataStructureField.get(structureName);
            return lstFieldConfig;
        } else {
            List<DataStructureField> lstFieldConfig = new ArrayList<>();
            List<DataStructureField> allFieldConfig = GIClient.getInstance().getRecordByFilter(DataStructureField.class, Filter.equal("STRUC_NAME", structureName));
            for (DataStructureField fConfig : allFieldConfig) {
                if (fConfig.getSwitchType() != null
                        && fConfig.getStatus().equals("1")) {
                    lstFieldConfig.add(fConfig);
                }
            }

            if (!lstFieldConfig.isEmpty()) {
                Collections.sort(lstFieldConfig, new Comparator<DataStructureField>() {
                    @Override
                    public int compare(DataStructureField o1, DataStructureField o2) {
                        if (o1.getFieldPos() == null && o2.getFieldPos() == null) {
                            return 0;
                        } else if (o1.getFieldPos() == null) {
                            return Integer.MAX_VALUE;
                        } else if (o2.getFieldPos() == null) {
                            return Integer.MIN_VALUE;
                        } else {
                            return o1.getFieldPos().compareTo(o2.getFieldPos());
                        }
                    }
                });
            } else {
                logger.error("Config input field is empty. For structureName: " + structureName);
            }
//            if (allMapDataStructureField.containsKey(country)) {
//                Map<String, List<DataStructureField>> mapDTF = allMapDataStructureField.get(country);
//                mapDTF.put(switchType, lstFieldConfig);
//            } else {
//                Map<String, List<DataStructureField>> mapDTF = new HashMap<>();
//                mapDTF.put(structureName, lstFieldConfig);
                allMapDataStructureField.put(structureName, lstFieldConfig);
//            }
            mapLoadedObject.put(structureName + "_structureIn", structureName + "_structureIn");
            return lstFieldConfig;
        }
    }

    public synchronized List<DataStructureField> loadImportField(String resultName) throws FilterException {
        if (mapLoadedObject.containsKey(resultName + "_Import_DataField")) {
            List<DataStructureField> lstFieldConfig = allMapImportField.get(resultName);
            return lstFieldConfig;
        } else {
//            List<DataStructureField> lstFieldConfig = new ArrayList<DataStructureField>();
            List<DataStructureField> lstFieldConfig = GIClient.getInstance().getRecordByFilter(DataStructureField.class, Filter.equal("STRUC_NAME", resultName));

            if (lstFieldConfig != null && !lstFieldConfig.isEmpty()) {
                Collections.sort(lstFieldConfig, new Comparator<DataStructureField>() {
                    @Override
                    public int compare(DataStructureField o1, DataStructureField o2) {
                        if (o1.getFieldPos() == null && o2.getFieldPos() == null) {
                            return 0;
                        } else if (o1.getFieldPos() == null) {
                            return Integer.MAX_VALUE;
                        } else if (o2.getFieldPos() == null) {
                            return Integer.MIN_VALUE;
                        } else {
                            return o1.getFieldPos().compareTo(o2.getFieldPos());
                        }
                    }
                });
            } else {
                logger.error("Export field is empty. For result: " + resultName);
            }
            allMapImportField.put(resultName, lstFieldConfig);
            mapLoadedObject.put(resultName + "_Import_DataField", resultName + "_Import_DataField");
            return allMapImportField.get(resultName);
        }
    }

    public synchronized Map<Long, Rule> getAllMapRule(String switchType, String country) {
        if (mapLoadedObject.containsKey(switchType + "_" + country + "_Rule")) {
            Map<Long, Rule> lstRule = allMapRule.get(country).get(switchType);
            return lstRule;
        } else {
            loadRuleExpression(switchType, country);
            return allMapRule.get(country).get(switchType);
        }
    }

    public synchronized Map<Long, Map<Long, List<SubExpression>>> loadRuleExpression(String switchType, String country) throws FilterException {
//        mapRule = new HashMap<>();
        if (mapLoadedObject.containsKey(switchType + "_" + country + "_Rule")) {
            Map<Long, Map<Long, List<SubExpression>>> lstRule = ruleExpression.get(country).get(switchType);
            return lstRule;
        } else {
            List<Rule> allRule = GIClient.getInstance().getRecordByFilter(Rule.class, Filter.equal("SWITCH_TYPE", switchType),
                    PredExp.stringBin("COUNTRY"), PredExp.stringValue(country), PredExp.stringEqual());
            List<Rule> lst = new ArrayList<>();
            for (Rule rule : allRule) {
                if (rule.getSwitchType().toUpperCase().equals(switchType.toUpperCase())
                        && rule.getCountry().toUpperCase().equals(country)
                        && rule.getStatus() == 1) {
                    lst.add(rule);
                }
            }
            if (lst.size() < 1) {
                logger.error("No rule. switchtype: " + switchType + " and country: " + country);
                return null;
            }
            Collections.sort(lst, new Comparator<Rule>() {
                @Override
                public int compare(Rule o1, Rule o2) {
                    if (o1.getPriority() == null && o2.getPriority() == null) {
                        return 0;
                    } else if (o1.getPriority() == null) {
                        return Integer.MAX_VALUE;
                    } else if (o2.getPriority() == null) {
                        return Integer.MIN_VALUE;
                    } else {
                        return o1.getPriority().compareTo(o2.getPriority());
                    }
                }
            });
            LinkedHashMap<Long, Map<Long, List<SubExpression>>> mapSubExByRule = new LinkedHashMap<>();
//            Map<Long, Rule> mapRule = new HashMap<>();
            LinkedHashMap<Long, Rule> mapRuletmp = new LinkedHashMap<>();
            for (Rule rule : lst) {
                mapRuletmp.put(rule.getRuleId(), rule);
//                mapRule.put(rule.getRuleId(), rule);
                //load expression
                List<Expression> lstEx = GIClient.getInstance().getRecordByFilter(Expression.class, Filter.equal("RULE_ID", rule.getRuleId() + ""));

                Map<Long, List<SubExpression>> mapExTmp = new HashMap<>();

                for (Expression ex : lstEx) {
                    Map<Long, List<SubExpression>> mapEx = new HashMap<>();
                    //load subEx
                    List<SubExpression> lstSubEx = GIClient.getInstance().getRecordByFilter(SubExpression.class, Filter.equal("EX_ID", ex.getExpressionId() + ""));
                    if (lstSubEx == null || lstSubEx.isEmpty()) {
                        logger.error("Missing SubEx for Expression: " + ex.getExpressionId());
                        return null;
                    }
                    mapExTmp.put(ex.getExpressionId(), lstSubEx);
                }
                mapSubExByRule.put(rule.getRuleId(), mapExTmp);
            }

            if (allMapRule.containsKey(country)) {
                Map<String, LinkedHashMap<Long, Rule>> mapRuleSwitch = allMapRule.get(country);
                mapRuleSwitch.put(switchType, mapRuletmp);
            } else {
                Map<String, LinkedHashMap<Long, Rule>> mapRuleSwitch = new HashMap<>();
                mapRuleSwitch.put(switchType, mapRuletmp);
                allMapRule.put(country, mapRuleSwitch);
            }
//            ruleExpression

            if (ruleExpression.containsKey(country)) {
                Map<String, Map<Long, Map<Long, List<SubExpression>>>> mapDTF = ruleExpression.get(country);
                mapDTF.put(switchType, mapSubExByRule);
            } else {
                Map<String, Map<Long, Map<Long, List<SubExpression>>>> mapDTF = new HashMap<>();
                mapDTF.put(switchType, mapSubExByRule);
                ruleExpression.put(country, mapDTF);
            }
            mapLoadedObject.put(switchType + "_" + country + "_Rule", switchType + "_" + country + "_Rule");
            return ruleExpression.get(country).get(switchType);
        }
    }

    public synchronized Switchboard getSwitchboard(String id) {
        if (mapLoadedObject.containsKey("Switchboard")) {
            return mapSwitchboard.get(id);
        } else {
            List<Switchboard> lst = GIClient.getInstance().getRecordByFilter(Switchboard.class, Filter.equal("STATUS", "1"));
            if (lst == null || lst.isEmpty()) {
                return null;
            }
            for (Switchboard sw : lst) {
                mapSwitchboard.put(sw.getId(), sw);
            }
            mapLoadedObject.put("Switchboard", "Switchboard");
            return mapSwitchboard.get(id);
        }
    }

    public synchronized Map<Long, List<Result>> loadResultByRule() throws FilterException {
//        mapResultByRule = new HashMap<>();
        if (mapLoadedObject.containsKey("Map_Result")) {
            Map<Long, List<Result>> lstResult = allMapResult.get("A").get("B");
            return lstResult;
        } else {
            List<Result> lst = GIClient.getInstance().getRecordByFilter(Result.class, Filter.equal("STATUS", "1"));
            Map<Long, List<Result>> mapResultByRule = new HashMap<>();
            for (Result result : lst) {
                if (mapResultByRule.containsKey(result.getRuleId())) {
                    mapResultByRule.get(result.getRuleId()).add(result);
                } else {
                    List<Result> lstRs = new ArrayList<>();
                    lstRs.add(result);
                    mapResultByRule.put(result.getRuleId(), lstRs);
                }
            }
            if (allMapResult.containsKey("A")) {
                Map<String, Map<Long, List<Result>>> mapDTF = allMapResult.get("A");
                mapDTF.put("B", mapResultByRule);
            } else {
                Map<String, Map<Long, List<Result>>> mapDTF = new HashMap<>();
                mapDTF.put("B", mapResultByRule);
                allMapResult.put("A", mapDTF);
            }
            mapLoadedObject.put("Map_Result", "Map_Result");
            return mapResultByRule;
        }
    }

    public synchronized Map<Long, List<StandardizeField>> loadStandardizeByResult() throws FilterException {
//        mapStandardizebyResult = new HashMap<>();
        if (mapLoadedObject.containsKey("Map_Standardize")) {
            Map<Long, List<StandardizeField>> lstStandardize = allMapStandardizeField.get("A").get("A");
            return lstStandardize;
        } else {
            List<StandardizeField> lstTmp = GIClient.getInstance().getRecordByFilter(StandardizeField.class, Filter.equal("STATUS", "1"));
            Collections.sort(lstTmp, new Comparator<StandardizeField>() {
                @Override
                public int compare(StandardizeField o1, StandardizeField o2) {
                    if (o1.getPriority() == null && o2.getPriority() == null) {
                        return 0;
                    } else if (o1.getPriority() == null) {
                        return Integer.MAX_VALUE;
                    } else if (o2.getPriority() == null) {
                        return Integer.MIN_VALUE;
                    } else {
                        return o1.getPriority().compareTo(o2.getPriority());
                    }
                }
            });
            Map<Long, List<StandardizeField>> mapStandardizebyResult = new HashMap<>();
            for (StandardizeField standardizeField : lstTmp) {
                if (mapStandardizebyResult.containsKey(standardizeField.getResultId())) {
                    mapStandardizebyResult.get(standardizeField.getResultId()).add(standardizeField);
                } else {
                    List<StandardizeField> lst = new ArrayList<>();
                    lst.add(standardizeField);
                    mapStandardizebyResult.put(standardizeField.getResultId(), lst);
                }
            }
            if (allMapStandardizeField.containsKey("A")) {
                Map<String, Map<Long, List<StandardizeField>>> mapDTF = allMapStandardizeField.get("A");
                mapDTF.put("A", mapStandardizebyResult);
            } else {
                Map<String, Map<Long, List<StandardizeField>>> mapDTF = new HashMap<>();
                mapDTF.put("A", mapStandardizebyResult);
                allMapStandardizeField.put("A", mapDTF);
            }
            mapLoadedObject.put("Map_Standardize", "Map_Standardize");
            return mapStandardizebyResult;
        }
    }

    public synchronized Map<Long, FunctionBO> loadFunction() throws FilterException {
        if (mapLoadedObject.containsKey("Map_Function")) {
            return mapFunction;
        } else {
            List<FunctionBO> lst = GIClient.getInstance().getRecordByFilter(FunctionBO.class, Filter.equal("STATUS", "1"));
            for (FunctionBO func : lst) {
                mapFunction.put(func.getFunctionId(), func);
            }
            mapLoadedObject.put("Map_Function", "Map_Function");
            return mapFunction;
        }
    }
}
