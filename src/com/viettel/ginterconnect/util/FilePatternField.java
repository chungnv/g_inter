/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ubuntu
 */
public class FilePatternField {

    public static final int FIX_TYPE = -1;
    public static final int SEQ_TYPE = 1;
    public static final int HSEQ_TYPE = 15;
    public static final int YEAR_TYPE = 2;
    public static final int MON_TYPE = 3;
    public static final int DAY_TYPE = 4;
    public static final int HOUR_TYPE = 5;
    public static final int MIN_TYPE = 6;
    public static final int SEC_TYPE = 7;
    public static final int FILEID_TYPE = 8;
    public static final int TICTAC_TYPE = 9;
    public static final int IGNORE_TYPE = 10;
    public static final int IN_TYPE = 11;
    public static final int ALL_TYPE = 12;
    public static final String PATTERN_CHARACTER = "#";
    public static final String DELIMITER = "\\?";


    public int fieldType;
    public int length;
    public String fixString;
    private boolean isStart;
    private boolean isEndless = false;
    private String finishCharacter;
    
    public List<String> lstFieldCharacter = new ArrayList<String>();

    public List<String> getLstFieldCharacter() {
        return lstFieldCharacter;
    }

    public void setLstFieldCharacter(List<String> lstFieldCharacter) {
        this.lstFieldCharacter = lstFieldCharacter;
    }

    public boolean isIsStart() {
        return isStart;
    }

    public void setIsStart(boolean isStart) {
        this.isStart = isStart;
    }

    public String getFixString() {
        return fixString;
    }

    public void setFixString(String fixString) {
        this.fixString = fixString;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isIsEndless() {
        return isEndless;
    }

    public void setIsEndless(boolean isEndless) {
        this.isEndless = isEndless;
    }

    public String getFinishCharacter() {
        return finishCharacter;
    }

    public void setFinishCharacter(String finishCharacter) {
        this.finishCharacter = finishCharacter;
    }
}
