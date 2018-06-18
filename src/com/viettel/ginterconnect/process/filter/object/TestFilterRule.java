/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ubuntu
 */
public class TestFilterRule implements Serializable {
    
    private String name;
    private List<TestExpression> expressions;
    private List<TestResult> results;
    private String returnCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TestExpression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<TestExpression> expressions) {
        this.expressions = expressions;
    }

    public List<TestResult> getResults() {
        return results;
    }

    public void setResults(List<TestResult> results) {
        this.results = results;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }
    
    public TestFilterRule copyWithoutResult() {
        TestFilterRule tfR = new TestFilterRule();
        tfR.setName(this.name);
        tfR.setResults(new ArrayList<TestResult>());
        tfR.setExpressions(new ArrayList<TestExpression>());
        return tfR;
    }
    
    public HashMap<Long, TestResult> getMapTestResult() {
        HashMap<Long, TestResult> mapTestResult = new HashMap<>();
        if (this.results == null || this.results.isEmpty()) {
            return mapTestResult;
        }
        long i = 0;
        for (TestResult testResult : this.results) {
            i++;
            testResult.setId(i);
            mapTestResult.put(testResult.getId(), testResult);
        }
        return mapTestResult;
    }
}
