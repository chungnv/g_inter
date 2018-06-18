/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;

/**
 * @author ubuntu
 */
@Table(name = "switchboard")
public class Switchboard {

    @Column(name = "ID", primaryKey = true)
    private String id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "SWITCH_TYPE")
    private String type;
    @Column(name = "VENDER")
    private String vender;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "COUNTRY")
    private String country;
    @Column(name = "STRUCTURE_IN")
    private String structureIn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStructureIn() {
        return structureIn;
    }

    public void setStructureIn(String structureIn) {
        this.structureIn = structureIn;
    }
}

