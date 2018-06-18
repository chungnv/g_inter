/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.object;

import com.viettel.ginterconnect.process.bean.DataStructureField;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ubuntu
 */
public class TestRequest implements Serializable {
    
    private String country;
    private String switchType;
    private String cdr;
    private String spliter;
    private List<DataStructureField> structureFilters;
    private List<TestFilterRule> rules;
    private String outputSplitter;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSwitchType() {
        return switchType;
    }

    public void setSwitchType(String switchType) {
        this.switchType = switchType;
    }

    public String getCdr() {
        return cdr;
    }

    public void setCdr(String cdr) {
        this.cdr = cdr;
    }

    public String getSpliter() {
        return spliter;
    }

    public void setSpliter(String spliter) {
        this.spliter = spliter;
    }

    public List<DataStructureField> getStructureFilters() {
        return structureFilters;
    }

    public void setStructureFilters(List<DataStructureField> structureFilters) {
        this.structureFilters = structureFilters;
    }

    public List<TestFilterRule> getRules() {
        return rules;
    }

    public void setRules(List<TestFilterRule> rules) {
        this.rules = rules;
    }

    public String getOutputSplitter() {
        return outputSplitter;
    }

    public void setOutputSplitter(String outputSplitter) {
        this.outputSplitter = outputSplitter;
    }
}
