/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.object;

import com.viettel.ginterconnect.process.bean.SubExpression;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ubuntu
 */
public class TestExpression implements Serializable {
    
    private String name;
    private List<SubExpression> subExpressions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubExpression> getSubExpressions() {
        return subExpressions;
    }

    public void setSubExpressions(List<SubExpression> subExpressions) {
        this.subExpressions = subExpressions;
    }
}
