/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.merge;

import java.io.FileWriter;

/**
 *
 * @author ubuntu
 */
public class MergeFile {
    
    private FileWriter fileWriter;
    private long numberOfFiles;
    private String filename;
    private String mergingDay;

    public MergeFile(FileWriter writer, int merge, String name) {
        this.fileWriter = writer;
        this.numberOfFiles = merge;
        this.filename = name;
    }
    
    public FileWriter getFileWriter() {
        return fileWriter;
    }

    public void setFileWriter(FileWriter fileWriter) {
        this.fileWriter = fileWriter;
    }

    public long getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }
    
    public long increateNumberOfFile(long count) {
        numberOfFiles += count;
        return numberOfFiles;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMergingDay() {
        return mergingDay;
    }

    public void setMergingDay(String mergingDay) {
        this.mergingDay = mergingDay;
    }
    
}
