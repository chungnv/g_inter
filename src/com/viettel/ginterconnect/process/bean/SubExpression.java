/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;
import java.io.Serializable;

/**
 * 
 * @version 1.0
 * @since May 9, 2011
 */
@Table(name = "md_sub_expression")
public class SubExpression implements Serializable {
    @Column(name = "SUB_EX_ID", primaryKey = true)
    private Long subExpressionId;
    @Column(name = "SUB_EX_NAME")
    private String subExpressionName;
    @Column(name = "LEFT_FIELD")
    private String leftField;
    @Column(name = "LEFT_FUNC_ID")
    private Long leftFunctionId;
    @Column(name = "OPERATOR")
    private String operator;
    @Column(name = "CONSTANT")
    private String constant;
    @Column(name = "RIGHT_FUNC_ID")
    private Long rightFunctionId;
    @Column(name = "RIGHT_FIELD")
    private String rightField;
    @Column(name = "IS_DELETE")
    private Long isDelete;
    @Column(name = "EX_ID")
    private Long expressionId;
    @Column(name = "IS_CONSTANT")
    private Long isConstant;
    @Column(name = "SWITCH_TYPE")
    private String switchType;
    @Column(name = "COUNTRY")
    private String country;

    public Long getSubExpressionId() {
        return subExpressionId;
    }

    public void setSubExpressionId(Long subExpressionId) {
        this.subExpressionId = subExpressionId;
    }

    public String getSubExpressionName() {
        return subExpressionName;
    }

    public void setSubExpressionName(String subExpressionName) {
        this.subExpressionName = subExpressionName;
    }

    public String getLeftField() {
        return leftField;
    }

    public void setLeftField(String leftField) {
        this.leftField = leftField;
    }

    public Long getLeftFunctionId() {
        return leftFunctionId;
    }

    public void setLeftFunctionId(Long leftFunctionId) {
        this.leftFunctionId = leftFunctionId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public Long getRightFunctionId() {
        return rightFunctionId;
    }

    public void setRightFunctionId(Long rightFunctionId) {
        this.rightFunctionId = rightFunctionId;
    }

    public String getRightField() {
        return rightField;
    }

    public void setRightField(String rightField) {
        this.rightField = rightField;
    }

    public Long getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Long isDelete) {
        this.isDelete = isDelete;
    }

    public Long getExpressionId() {
        return expressionId;
    }

    public void setExpressionId(Long expressionId) {
        this.expressionId = expressionId;
    }

    public Long getIsConstant() {
        return isConstant;
    }

    public void setIsConstant(Long isConstant) {
        this.isConstant = isConstant;
    }

    public String getSwitchType() {
        return switchType;
    }

    public void setSwitchType(String switchType) {
        this.switchType = switchType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}

