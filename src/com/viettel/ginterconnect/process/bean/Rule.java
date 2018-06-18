/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;

/**
 *
 * @version 1.0
 * @since May 9, 2011
 */
@Table(name = "md_rule")
public class Rule {

    @Column(name = "RULE_ID", primaryKey = true)
    private Long ruleId;
    @Column(name = "SWITCH_TYPE")
    private String switchType;
    @Column(name = "RULE_NAME")
    private String ruleName;
    @Column(name = "STATUS")
    private Long status;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ALLOWABLE_EXIT")
    private Long allowableExit;
    @Column(name = "RULE_TYPE")
    private String ruleType;
    @Column(name = "FUNC_CDR_IDEN")
    private Long funcIdentityCdrId;
    @Column(name = "COUNTRY")
    private String country;
    @Column(name = "PRIORITY")
    private Long priority;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getSwitchType() {
        return switchType;
    }

    public void setSwitchType(String switchType) {
        this.switchType = switchType;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAllowableExit() {
        return allowableExit;
    }

    public void setAllowableExit(Long allowableExit) {
        this.allowableExit = allowableExit;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public Long getFuncIdentityCdrId() {
        return funcIdentityCdrId;
    }

    public void setFuncIdentityCdrId(Long funcIdentityCdrId) {
        this.funcIdentityCdrId = funcIdentityCdrId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Long getPriority() {
        return priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
    }
}
