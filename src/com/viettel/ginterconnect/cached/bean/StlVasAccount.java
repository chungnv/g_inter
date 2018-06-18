/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.cached.bean;

import java.util.HashMap;

/**
 *
 * @author ubuntu
 */
public class StlVasAccount {
    
    String cdrAccount;
    String partnerCode;
          //service code, prefix, bean
    HashMap<String, HashMap<Long, CommonVasPartnerBean>> mapPrefix = new HashMap<>();
    
    public StlVasAccount(String cdrAccount, String partnerCode) {
        this.cdrAccount = cdrAccount;
        this.partnerCode = partnerCode;
    }

    public String getCdrAccount() {
        return cdrAccount;
    }

    public void setCdrAccount(String cdrAccount) {
        this.cdrAccount = cdrAccount;
    }

    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public HashMap<String, HashMap<Long, CommonVasPartnerBean>> getMapPrefix() {
        return mapPrefix;
    }

    public void setMapPrefix(HashMap<String, HashMap<Long, CommonVasPartnerBean>> mapPrefix) {
        this.mapPrefix = mapPrefix;
    }
    
    
    
}
