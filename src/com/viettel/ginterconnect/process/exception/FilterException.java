/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.ginterconnect.process.exception;

import java.util.HashMap;
import java.util.Map;

public class FilterException extends RuntimeException {

    public static final int PRIORITY_WEAK = 0;
    public static final int PRIORITY_NORMAL = 1;
    public static final int PRIORITY_STRONG = 2;
    public static final int PRIORITY_EXIT = 3;
    public static final int _FAIL = -1; // job will failed everytime it is processed
    public static final int _CONFIG_FAIL = -2; // job need re-configruation

    private int priority = PRIORITY_WEAK;
    private String exceptionCode = null;

    public static final Map<String, FilterException> lsCustomExceptions;

    static {
        lsCustomExceptions = new HashMap<>();
        lsCustomExceptions.put("CACHENOTFOUND", new FilterException("GORA|999 Cache has not been initialized", PRIORITY_EXIT));
        lsCustomExceptions.put("DATANOTFOUND", new FilterException("GORA|998 Data has not been initialized", PRIORITY_EXIT));
        lsCustomExceptions.put("HAVENEW", new FilterException("GORA|997 Have new record", PRIORITY_EXIT));
        lsCustomExceptions.put("SWITCHNOTFOUND", new FilterException("GORA|996 switch has not been initialized", PRIORITY_EXIT));
    }


    public FilterException(String message) {
        super("Have error in filter process.\n" + message);
    }

    public FilterException(String message, int priority) {
        super(message);
        this.priority = priority;
    }

    public FilterException(String message, int priority, String exceptionCode) {
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

    public static FilterException getCustomException(String exname) {
        return lsCustomExceptions.get(exname);
    }

}
