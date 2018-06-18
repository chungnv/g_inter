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
@Table(name = "md_result")
public class Result implements Cloneable {

    public static final int TYPE_NOT_ERROR = 0;
    public static final int TYPE_ERROR_CDR_INVALID = 1;
    public static final int TYPE_ERROR_CDR_STANDADIZE_FAIL = 2;
    public static final int TYPE_ERROR_CDR_STANDADIZE_EXCEPTION = 3;

    @Column(name = "RESULT_ID", primaryKey = true)
    private Long resultId;
    @Column(name = "LOCAL_FOLDER")
    private String localFolder;
    @Column(name = "STATUS")
    private String status;
    @Column(name = "RESULT_NAME")
    private String resultName;
    @Column(name = "RULE_ID")
    private Long ruleId;
    @Column(name = "F_NAME_TEMP")
    private String fileNameTemplate;
    @Column(name = "RESULT_TYPE")
    private String resultType;
    @Column(name = "F_EXTENSION")
    private String fileExtension;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "SEQ_NAME")
    private String seqName;
    @Column(name = "STRUCT_NAME")
    private String dataStructureName;
    @Column(name = "IMP_TAB_ID")
    private String importTableId;
    @Column(name = "SWITCH_TYPE")
    private String switchType;
    @Column(name = "COUNTRY")
    private String country;
    private int typeError;

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getLocalFolder() {
        return localFolder;
    }

    public String getFolderStandardFail() {
        return localFolder + "/STANDARD_FAIL/";
    }

    public void setLocalFolder(String localFolder) {
        this.localFolder = localFolder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getFileNameTemplate() {
        return fileNameTemplate;
    }

    public void setFileNameTemplate(String fileNameTemplate) {
        this.fileNameTemplate = fileNameTemplate;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeqName() {
        return seqName;
    }

    public void setSeqName(String seqName) {
        this.seqName = seqName;
    }

    public String getDataStructureName() {
        return dataStructureName;
    }

    public void setDataStructureName(String dataStructureName) {
        this.dataStructureName = dataStructureName;
    }

    public String getImportTableId() {
        return importTableId;
    }

    public void setImportTableId(String importTableId) {
        this.importTableId = importTableId;
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

    public int getTypeError() {
        return typeError;
    }

    public void setTypeError(int typeError) {
        this.typeError = typeError;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
