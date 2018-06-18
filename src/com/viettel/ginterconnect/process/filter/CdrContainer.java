/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.ginterconnect.process.filter;

import com.viettel.ginterconnect.process.bean.CdrObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @since May 9, 2011
 * @version 1.0
 */
public class CdrContainer {

    /**
     * class CdrContainer chua ket qua sau khi filter
     * gom co    + cdrObject filter
     *           + list result
     */
    private List<CdrSaveItem> cdrSaveItem = new ArrayList<>();
    private CdrObject cdrObject;
    private boolean ruleFlag;

    public CdrObject getCdrObject() {
        return cdrObject;
    }

    public void setCdrObject(CdrObject cdrObject) {
        this.cdrObject = cdrObject;
    }

    public List<CdrSaveItem> getCdrSaveItem() {
        return cdrSaveItem;
    }

    public void setCdrSaveItem(List<CdrSaveItem> cdrSaveItem) {
        this.cdrSaveItem = cdrSaveItem;
    }

    public boolean isRuleFlag() {
        return ruleFlag;
    }

    public void setRuleFlag(boolean ruleFlag) {
        this.ruleFlag = ruleFlag;
    }

}

