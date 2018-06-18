/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.exception;

/**
 *
 * @author
 */
public class ImportQueueException extends Exception {

    public static final int PRIORITY_WEAK = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_STRONG = 2;
    public static final int PRIORITY_EXIT = 3;
    private int priority = PRIORITY_WEAK;
    private String exceptionCode = null;

    public ImportQueueException(String message) {
        super("Have error in import process.\n" + message);
    }

    public ImportQueueException(String message, int priority) {
        super(message);
        this.priority = priority;
    }

    public ImportQueueException(String message, int priority, String exceptionCode) {
        super(message);
        this.priority = priority;
        this.exceptionCode = exceptionCode;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
}
