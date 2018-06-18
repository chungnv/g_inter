/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.importer;

import com.viettel.ginterconnect.process.bean.DataStructureField;
import com.viettel.ginterconnect.process.bean.Result;
import com.viettel.ginterconnect.process.filter.GICache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 */
public class ImporterFactory {

    private static Map<String, BaseImporter> mapImporter = null;
    private static Map<String, BaseImporter> mapImporterStandardFail = null;

//    public synchronized static Importer getImporterFromDBConfig(String resultName, Result result, String fieldOutSeparate, List<DataStructureField> lstField, boolean isInsertDb, GICache cache) {
    public synchronized static Importer getImporterFromDBConfig(String resultName, Result result, String fieldOutSeparate, List<DataStructureField> lstField, String writeType, GICache cache) {
        if (mapImporter == null) {
            mapImporter = new HashMap<>();
        }

        if (mapImporterStandardFail == null) {
            mapImporterStandardFail = new HashMap<>();
        }
        if (result != null && result.getTypeError() == Result.TYPE_NOT_ERROR) {
            if (mapImporter.containsKey(resultName)) {
                return (Importer) mapImporter.get(resultName);
            } else {
//                Importer importer = new Importer(result, fieldOutSeparate, lstField, isInsertDb, cache);
                Importer importer = new Importer(result, fieldOutSeparate, lstField, cache, writeType);
                mapImporter.put(resultName, importer);
                return importer;
            }
        } else if (result != null && result.getTypeError() == Result.TYPE_ERROR_CDR_STANDADIZE_FAIL) {
            if (mapImporterStandardFail.containsKey(resultName)) {
                return (Importer) mapImporterStandardFail.get(resultName);
            } else {
//                Importer importer = new Importer(result, fieldOutSeparate, lstField, isInsertDb, cache);
                Importer importer = new Importer(result, fieldOutSeparate, lstField, cache, writeType);
                mapImporterStandardFail.put(resultName, importer);
                return importer;
            }
        } else if (result == null) {
            if (mapImporter.containsKey(resultName)) {
                return (Importer) mapImporter.get(resultName);
            } else {
//                Importer importer = new Importer(result, fieldOutSeparate, lstField, isInsertDb, cache);
                Importer importer = new Importer(result, fieldOutSeparate, lstField, cache, writeType);
                mapImporter.put(resultName, importer);
                return importer;
            }
        }
        return null;
    }
}
