package com.viettel.ginterconnect.process.bean;

import com.viettel.ginterconnect.annotations.Column;
import com.viettel.ginterconnect.annotations.Table;
import java.io.Serializable;

/**
 * Created by sinhhv on 9/29/2016.
 */
@Table(name = "md_data_structure")
public class DataStructureField implements Serializable {
    @Column(name = "STRUC_ID", primaryKey = true)
    private Long structureId;
    @Column(name = "STRUC_NAME")
    private String structureName;
    @Column(name = "FIELD_NAME")
    private String fieldName;
    @Column(name = "FIELD_TYPE")
    private String fieldType;
    @Column(name = "FIELD_POS")
    private Integer fieldPos;
    @Column(name = "FIELD_FORMAT")
    private String fieldFormat;
    @Column(name = "FIELD_MASK")
    private String fieldMask;
    @Column(name = "DEFAULT_VALUE")
    private String defaultValue;
    private String validRegex;
    @Column(name = "SWITCH_TYPE")
    private String switchType;
    @Column(name = "COUNTRY")
    private String country;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "STATUS")
    private String status;

    public Long getStructureId() {
        return structureId;
    }

    public void setStructureId(Long structureId) {
        this.structureId = structureId;
    }

    public String getStructureName() {
        return structureName;
    }

    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getFieldPos() {
        return fieldPos;
    }

    public void setFieldPos(Integer fieldPos) {
        this.fieldPos = fieldPos;
    }

    public String getFieldFormat() {
        return fieldFormat;
    }

    public void setFieldFormat(String fieldFormat) {
        this.fieldFormat = fieldFormat;
    }

    public String getFieldMask() {
        return fieldMask;
    }

    public void setFieldMask(String fieldMask) {
        this.fieldMask = fieldMask;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getValidRegex() {
        return validRegex;
    }

    public void setValidRegex(String validRegex) {
        this.validRegex = validRegex;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
