/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.ginterconnect.process.exception;

import java.util.HashMap;
import java.util.Map;

public class StandardException extends RuntimeException {

    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_HIGH = 2;
    public static final int PRIORITY_EXIT = 3;
    public static final int PRIORITY_REJECT_CDR = 4;
    public static final int PRIORITY_BREAK_FLOW = 5;

    private int priority = PRIORITY_LOW;
    private String exceptionCode = null;

    public static final Map<String, StandardException> lsCustomExceptions;

    static {
        lsCustomExceptions = new HashMap<>();
        lsCustomExceptions.put("CACHENOTFOUND", new StandardException("GORA|999 Cache has not been initialized", PRIORITY_EXIT));
        lsCustomExceptions.put("DATANOTFOUND", new StandardException("GORA|998 Data has not been initialized", PRIORITY_EXIT));
        lsCustomExceptions.put("HAVENEW", new StandardException("GORA|997 Have new record", PRIORITY_EXIT));
        lsCustomExceptions.put("SWITCHNOTFOUND", new StandardException("GORA|996 switch has not been initialized", PRIORITY_EXIT));
    }


    public StandardException(String message) {
        super("Have error in filter process.\n" + message);
    }

    public StandardException(String message, int priority) {
        super(message);
        this.priority = priority;
    }

    public StandardException(String message, int priority, String exceptionCode) {
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

    public static StandardException getCustomException(String exname) {
        return lsCustomExceptions.get(exname);
    }

}
