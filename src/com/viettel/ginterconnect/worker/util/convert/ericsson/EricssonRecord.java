/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.ericsson;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author chungdq
 */
public class EricssonRecord
{

    Map<String, Integer> tagMap = new HashMap<String, Integer>();
    Map<String, String> typeMap = new HashMap<String, String>();
    Map<String, Integer> Index = new HashMap<String, Integer>();

    public Map<String, Integer> getIndex() {
        return Index;
    }

    public void setIndex(Map<String, Integer> Index) {
        this.Index = Index;
    }

    public Map<String, Integer> getTagMap() {
        return tagMap;
    }

    public void setTagMap(Map<String, Integer> tagMap) {
        this.tagMap = tagMap;
    }

    public Map<String, String> getTypeMap() {
        return typeMap;
    }

    public void setTypeMap(Map<String, String> typeMap) {
        this.typeMap = typeMap;
    }

}
