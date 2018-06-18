/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.importer;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.bean.DataStructureField;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class ImportUtils {

    public static void addRecordToTextConfigDB(TextOutputFile out, CdrObject cdrBO, List<DataStructureField> lstImport, Logger logger) throws Exception {
        try {
//            String[] arr = new String[importerFields.size() + 1];
            String[] arr = parseOutputCdr(cdrBO, lstImport, logger);
            if (out != null) {
                out.addRecord(arr);
                out.flush();
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public static String[] parseOutputCdr(CdrObject cdrBO, List<DataStructureField> lstImport, Logger logger) {
        String[] arr = new String[lstImport.size()];
        for (int i = 0; i < lstImport.size(); i++) {
            if (!cdrBO.containsProperty(lstImport.get(i).getFieldName())
                    || cdrBO.get(lstImport.get(i).getFieldName()) == null) {
                arr[i] = "";
                continue;
            }
            Object object = cdrBO.get(lstImport.get(i).getFieldName());
            if (object instanceof Date) {
                if (lstImport.get(i) == null || StringUtils.isEmpty(lstImport.get(i).getFieldFormat())) {
                    logger.error("Output date field missing output format");
                }
                SimpleDateFormat sdf = new SimpleDateFormat(lstImport.get(i).getFieldFormat());
                arr[i] = sdf.format((Date) object);
            } else if (object instanceof Long) {
                arr[i] = ((Long) object).toString();
            } else if (object instanceof Integer) {
                arr[i] = ((Integer) object).toString();
            } else if (object instanceof Double) {
                arr[i] = ((Double) object).toString();
            } else {
                //arr[i] = (String)object;
                arr[i] = cdrBO.get(lstImport.get(i).getFieldName()).toString();
            }
        }
        return arr;
    }

}
