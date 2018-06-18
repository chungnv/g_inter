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
public class VtcOcsConvertCellId implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //concat(substr(CALLING_CELL_ID, 1,2), substr(CALLING_CELL_ID,-5))
//        System.outc.println("cell " + fieldParams + " value: " + object.get("CALLING_CELL_ID").toString());
        if (object.get(fieldParams) == null || "".equals(object.get(fieldParams).toString().trim())) {
            return null;
        }
        String cell = object.get(fieldParams).toString();
        if (cell.length() == 10) {
            return cell.substring(0, 2) + cell.substring(cell.length() - 5);
        } else {
            return cell;
        }
    }

    public static void main(String[] args) {
        String cell = "1234567890";
        System.out.println(cell.substring(0, 2) + cell.substring(cell.length() - 5));
    }

}
