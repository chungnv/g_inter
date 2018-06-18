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
 * @author ubuntu
 */
public class MvtCellId implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //CONCAT(SUBSTR(CELL_ID, 6,1), SUBSTR(CELL_ID,-5)) CELL_ID
        String cellId = object.get("CELL_ID").toString();
        return cellId.substring(5,6) + cellId.substring(cellId.length() - 6, cellId.length() - 1);
    }
    
}
