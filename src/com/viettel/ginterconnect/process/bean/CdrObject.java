/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.bean;

import java.util.HashMap;

public class CdrObject {

    /**
     * Mot ban ghi se duoc tao thanh 1 CdrObject truoc khi xu li cdrobject nay
     * luu ban ghi trong 1 bien hashmap co dang <String, Object>.
     */
    public static int TYPE_NOT_ERROR = 0;
    public static int TYPE_ERROR_CDR_INVALID = 1;
    public static int TYPE_ERROR_CDR_REJECT = -1;
    public static int TYPE_ERROR_CDR_STANDADIZE_FAIL = 2;
    public static int TYPE_ERROR_CDR_STANDADIZE_EXCEPTION = 3;

    private HashMap<String, Object> hashmap;
    private String sourceCdr;
    private String error;
    private String identifyCdr;
    private int typeError = 0;

    public CdrObject() {
        super();
        hashmap = new HashMap<>();
    }

    public CdrObject(String sourceCdr) {
        super();
        this.sourceCdr = sourceCdr;
        hashmap = new HashMap<>();
    }

    /**
     * Get cac gia tri cua hashtable
     *
     * @param propertiesName gia tri key cho hashmap
     * @return Object object tuong ung trong hashmap co key = propertiesName.
     * @throws Exception
     */
    public Object get(String propertiesName) {
        Object object;
        object = hashmap.get(propertiesName.toUpperCase().trim());
        return object;
    }

    public String getString(String propertiesName) {
        return hashmap.get(propertiesName.toUpperCase().trim()) == null ? ""
                : hashmap.get(propertiesName.toUpperCase().trim()).toString().trim();
    }
    
    public String getString(String propertiesName, String defaultValue) {
        return hashmap.get(propertiesName.toUpperCase().trim()) == null ? defaultValue
                : hashmap.get(propertiesName.toUpperCase().trim()).toString().trim();
    }

    public Double getDouble(String propertiesName) {
        return hashmap.get(propertiesName.toUpperCase().trim()) == null ? 0.0
                : (Double) (hashmap.get(propertiesName.toUpperCase().trim()));
    }

    public Long getLong(String propertiesName) {
        return hashmap.get(propertiesName.toUpperCase().trim()) == null ? null
                : (Long) (hashmap.get(propertiesName.toUpperCase().trim()));
    }

    public Long getLong(String... propertiesName) {
        for (String s : propertiesName) {
            if (hashmap.containsKey(s)) {
                return hashmap.get(s.toUpperCase().trim()) == null ? null
                        : (Long) (hashmap.get(s.toUpperCase().trim()));
            }
        }
        return null;
    }

    /**
     * Thiet lap cac gia tri cho hashmap
     *
     * @param propertiesName gia tri key
     * @param value value tuong ung voi key = propertiesName
     */
    public void set(String propertiesName, Object value) {
        hashmap.put(propertiesName.toUpperCase().trim(), value);
    }

    /**
     * remove key
     *
     * @param propertiesName gia tri key
     */
    public void remove(String propertiesName) {
        hashmap.remove(propertiesName);
    }

    public String getSourceCdr() {
        return sourceCdr;
    }

    public void setSourceCdr(String sourceCdr) {
        this.sourceCdr = sourceCdr;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    /**
     * containsKey: kiem tra xem CdrObject co key = propertiesName ko
     *
     * @param propertiesName key kiem tra
     * @return true neu co key = propertiesName false neu khong co key =
     * propertiesName
     */
    public boolean containsProperty(String propertiesName) {
        if (propertiesName == null || "".equals(propertiesName.trim())) {
            return false;
        }
        return hashmap.containsKey(propertiesName.toUpperCase());
    }

    public int getTypeError() {
        return typeError;
    }

    public void setTypeError(int typeError) {
        this.typeError = typeError;
    }

    public HashMap<String, Object> getHashmap() {
        return hashmap;
    }

    public void setHashmap(HashMap<String, Object> hashmap) {
        this.hashmap = hashmap;
    }

    public String getIdentifyCdr() {
        return identifyCdr;
    }

    public void setIdentifyCdr(String identifyCdr) {
        this.identifyCdr = identifyCdr;
    }
}
