package com.viettel.ginterconnect.cached.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;

/**
 * Created by hoangsinh on 18/08/2017.
 */
@Table(name = "VTC_ISDN_HOME")
public class VtcHome {

    @Column(name = "PK", primaryKey = true)
    String pk;
    @Column(name = "ISDN")
    String isdn;
    @Column(name = "BTS_CODE")
    String btsCode;
    @Column(name = "CENTER_CODE")
    String centerCode;
    @Column(name = "PROVINCE_CODE")
    String provinceCode;
    @Column(name = "PARTITION")
    String partition;
    @Column(name = "MARKET")
    String market;

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public String getBtsCode() {
        return btsCode;
    }

    public void setBtsCode(String btsCode) {
        this.btsCode = btsCode;
    }

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }
}
