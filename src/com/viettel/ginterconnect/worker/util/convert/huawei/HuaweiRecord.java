/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.huawei;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ubuntu
 */
public class HuaweiRecord {

    Map<Integer, Integer> tagMap = new HashMap<>();
    Map<Integer, String> typeMap = new HashMap<>();
    Map<Integer, Integer> Index = new HashMap<>();

    public Map<Integer, Integer> getIndex() {
        return Index;
    }

    public void setIndex(Map<Integer, Integer> Index) {
        this.Index = Index;
    }

    public Map<Integer, Integer> getTagMap() {
        return tagMap;
    }

    public Map<Integer, String> getTypeMap() {
        return typeMap;
    }

    public void setTagMap(Map<Integer, Integer> tagMap) {
        this.tagMap = tagMap;
    }

    public void setTypeMap(Map<Integer, String> typeMap) {
        this.typeMap = typeMap;
    }

}
