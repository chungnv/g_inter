/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import org.apache.log4j.Logger;

/**
 *
 * @author viettq
 */
public class VtcCellIDRec implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        if (object.containsProperty("CALLING_CELL_ID") && object.get("CALLING_CELL_ID") != null) {
            String cell = object.get("CALLING_CELL_ID").toString();
            String cell1 = "";
            String cell2 = "";
            if (cell.length() > 5) {
                cell1 = cell.substring(0, 2);
                cell2 = cell.substring(cell.length() - 5);
            }
            return cell1.concat(cell2);
        }
        return null;
    }

}
