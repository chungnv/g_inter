package com.viettel.ginterconnect.cached.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;

/**
 * Created by hoangsinh on 18/08/2017.
 */
@Table(name = "MVT_PRODUCT")
public class MvtProduct {
    //product_id,vas_service,action_no,add_money,minus_money,description,insert_time
    @Column(name = "PK", primaryKey = true)
    String pk;
    @Column(name = "PRODUCT_ID")
    String productId;
    @Column(name = "VAS_SERVICE")
    String vasService;
    @Column(name = "ACTION_NO")
    String actionNo;
    @Column(name = "ADD_MONEY")
    String addMoney;
    @Column(name = "MINUS_MONEY")
    String minusMoney;
    @Column(name = "DESCRIPTION")
    String description;
    @Column(name = "INSERT_TIME")
    String insertTime;
    @Column(name = "STATUS")
    String status;

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getVasService() {
        return vasService;
    }

    public void setVasService(String vasService) {
        this.vasService = vasService;
    }

    public String getActionNo() {
        return actionNo;
    }

    public void setActionNo(String actionNo) {
        this.actionNo = actionNo;
    }

    public String getAddMoney() {
        return addMoney;
    }

    public void setAddMoney(String addMoney) {
        this.addMoney = addMoney;
    }

    public String getMinusMoney() {
        return minusMoney;
    }

    public void setMinusMoney(String minusMoney) {
        this.minusMoney = minusMoney;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
}
