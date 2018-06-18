/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.importer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author ubuntu
 */
public class TextOutputFile {

    private static final String DEFAULT_SEPARATE_CHARACTER = ";";
    private FileWriter outFile = null;
    private PrintWriter out = null;
    private String separateChar = DEFAULT_SEPARATE_CHARACTER;
    private Long fileId = 0L;
    private int fileIdPosition = 0;
    private String filename = null;

    /**
     * Constructor with default separate character
     *
     * @param fileName
     * @throws IOException
     */
    public TextOutputFile(String fileName) throws IOException {
        init(fileName, DEFAULT_SEPARATE_CHARACTER);
    }

    public TextOutputFile(String fileName, boolean append) throws IOException {
        init(fileName, DEFAULT_SEPARATE_CHARACTER, append);
    }

    /**
     * Constructor with separate character
     *
     * @param fileName
     * @param separateChar
     * @throws IOException
     */
    public TextOutputFile(String fileName, String separateChar) throws IOException {
        this.filename = fileName;
        init(fileName, separateChar);
    }

    public TextOutputFile(String fileName, String separateChar, boolean append) throws IOException {
        init(fileName, separateChar, append);
    }

    public void addRecord(String[] record) {
        if (out != null) {
            // build a record to string
            StringBuffer buff = new StringBuffer();

            //add by chungdq 11/10/2008 for replace fileID
            if (fileIdPosition > 0 && fileIdPosition <= record.length) {
                record[fileIdPosition - 1] = fileId.toString();
            }

            for (int i = 0; i < record.length; i++) {
                if (i > 0) {
                    buff.append(separateChar);
                }
                if (record[i] != null) {
                    buff.append(record[i]);
                } else {
                    buff.append("");
                }
            }

            out.println(buff);
        }
    }

    /**
     * Add a record to output file
     *
     * @param record
     */
    public void addRecord(List record) {
        if (out != null) {
            // build a record to string
            StringBuffer buff = new StringBuffer();

            //add by chungdq 11/10/2008 for replace fileID
            if (fileIdPosition > 0 && fileIdPosition <= record.size()) {
                record.set(fileIdPosition - 1, fileId.toString());
            } else if (fileIdPosition > 0 && fileIdPosition - 1 == record.size()) {
                record.add(fileId.toString());
            }

            for (int i = 0; i < record.size(); i++) {
                if (i > 0) {
                    buff.append(separateChar);
                }
                buff.append(record.get(i));
            }

            out.println(buff);
        }
    }

    /*
     * added by ThangDD
     */
    public void addRecord2(String[] record) {
        if (out != null) {
            StringBuffer buff = new StringBuffer();

            for (int i = 0; i < record.length; i++) {
                if (i > 0) {
                    buff.append(separateChar);
                }
                buff.append(record[i] == null ? "" : record[i]);
            }

            out.println(buff);
        }
    }

    public void addRecord2(List<String> record) {
        if (out != null) {
            StringBuffer buff = new StringBuffer();

            for (int i = 0; i < record.size(); i++) {
                if (i > 0) {
                    buff.append(separateChar);
                }
                buff.append(record.get(i));
            }

            out.println(buff);
        }
    }

    /**
     * Close file
     */
    public void close() {
        if (out != null) {
            out.close();
        }
    }

    public String getSeparateChar() {
        return separateChar;
    }

    public void setSeparateChar(String separateChar) {
        this.separateChar = separateChar;
    }

    /**
     * Initialize
     *
     * @param fileName
     * @param separateChar
     * @throws IOException
     */
    private void init(String fileName, String separateChar) throws IOException {
        outFile = new FileWriter(fileName, false);
        out = new PrintWriter(outFile);
        this.separateChar = separateChar;
    }

    private void init(String fileName, String separateChar, boolean append) throws IOException {
        outFile = new FileWriter(fileName, append);
        out = new PrintWriter(outFile);
        this.separateChar = separateChar;
    }

    public synchronized void flush() throws IOException {
        if (out != null) {
            out.flush();
        }
        if (outFile != null) {
            outFile.flush();
        }
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public int getFileIdPosition() {
        return fileIdPosition;
    }

    public void setFileIdPosition(int fileIdPosition) {
        this.fileIdPosition = fileIdPosition;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
