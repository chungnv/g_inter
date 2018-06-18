/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.importer;

/**
 *
 * @author
 */
public class InsertLog {
    private int success;
    private int imported;
    private int duplicate;
    private int error;
    private String fileName;

    public InsertLog() {
        this.success = 0;
        this.duplicate = 0;
        this.error = 0;
        this.fileName = "";
        this.imported = 0;
    }
    
    public int getDuplicate() {
        return duplicate;
    }

    public void setDuplicate(int duplicate) {
        this.duplicate = duplicate;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getSuccess() {
        return success;
    }

    public InsertLog(int duplicate, int error) {
        this.duplicate = duplicate;
        this.error = error;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getImported() {
        return imported;
    }

    public void setImported(int imported) {
        this.imported = imported;
    }
    
}
