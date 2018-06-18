/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.object;

import com.viettel.ginterconnect.process.bean.DataStructureField;
import com.viettel.ginterconnect.process.bean.StandardizeField;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ubuntu
 */
public class TestResult implements Serializable {

    private Long id;
    private String name;
    private List<DataStructureField> structureOut;
    private List<StandardizeField> standardFields;
    private String cdrOut;
    private String returnCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataStructureField> getStructureOut() {
        return structureOut;
    }

    public void setStructureOut(List<DataStructureField> structureOut) {
        this.structureOut = structureOut;
    }

    public List<StandardizeField> getStandardFields() {
        return standardFields;
    }

    public void setStandardFields(List<StandardizeField> standardFields) {
        this.standardFields = standardFields;
    }

    public String getCdrOut() {
        return cdrOut;
    }

    public void setCdrOut(String cdrOut) {
        this.cdrOut = cdrOut;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
