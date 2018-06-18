package com.viettel.ginterconnect.cached.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;

/**
 * Created by hoangsinh on 18/08/2017.
 */
@Table(name = "CMR_BTS")
public class BtsCode {
    @Column(name = "BTS_CODE")
    String btsCode;
    @Column(name = "CELL_NAME")
    String cellName;
    @Column(name = "PROVINCE_CODE")
    String provinceCode;
    @Column(name = "CONFIG")
    String config;
    @Column(name = "LAC_CI", primaryKey = true)
    String lacCi;
    @Column(name = "ON_AIR")
    String onAir;
    @Column(name = "CENTER_CODE")
    String centerCode;
    @Column(name = "BTS_TYPE")
    String btsType;
    @Column(name = "POPULATION")
    String population;
    @Column(name = "NETWORK")
    String network;
    @Column(name = "LONG_TITUTE")
    String longTitute;
    @Column(name = "LA_TITUTE")
    String laTitute;

    public String getBtsCode() {
        return btsCode;
    }

    public void setBtsCode(String btsCode) {
        this.btsCode = btsCode;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getLacCi() {
        return lacCi;
    }

    public void setLacCi(String lacCi) {
        this.lacCi = lacCi;
    }

    public String getOnAir() {
        return onAir;
    }

    public void setOnAir(String onAir) {
        this.onAir = onAir;
    }

    public String getCenterCode() {
        return centerCode;
    }

    public void setCenterCode(String centerCode) {
        this.centerCode = centerCode;
    }

    public String getBtsType() {
        return btsType;
    }

    public void setBtsType(String btsType) {
        this.btsType = btsType;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getLongTitute() {
        return longTitute;
    }

    public void setLongTitute(String longTitute) {
        this.longTitute = longTitute;
    }

    public String getLaTitute() {
        return laTitute;
    }

    public void setLaTitute(String laTitute) {
        this.laTitute = laTitute;
    }
}
