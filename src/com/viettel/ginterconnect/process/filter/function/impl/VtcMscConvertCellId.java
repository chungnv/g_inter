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
public class VtcMscConvertCellId implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        //concat(substr(lpad(conv((substr((substr(T.LOCATION,length(T.LOCATION)-7,8)),1,4)), 16,10), 5, '0'), 1, 2), lpad(conv((substr((substr(T.LOCATION,length(T.LOCATION)-7,8)),5,4)), 16,10), 5, '0')
        //lay 4 ki tu dau tien ==> chuyen sang so decimal, neu length < 5, them so 0 vao dau cho du 5 --> cat lay 2 ki tu dau
        //lay 4 ki tu sau ==> chuyen sang so decimal, lay 5 ki tu dau tien
        //return la concat 2 ket qua tren lai
        if (fieldParams == null || object.get(fieldParams) == null) {
            return null;
        }
//        String cellStr = object.get(fieldParams).toString().replace("45703", "");
        String cellStr = "";
        if (object.get(fieldParams).toString().length() == 8) {
            cellStr = object.get(fieldParams).toString();
        } else if (object.get(fieldParams).toString().length() == 13) {
            cellStr = object.get(fieldParams).toString().substring(5);
        } else {
            return null;
        }
        String firstDec = Long.parseLong(reverse(cellStr.substring(0, 4)), 16) + "";
        String firststr = "00000" + firstDec;
        String first = (firststr.substring(firstDec.length())).substring(0, 2);
        String secondHex = Long.parseLong(reverse(cellStr.substring(4)), 16) + "";
        String secondStr = "00000" + secondHex;
        String second = (secondStr.substring(secondHex.length())).substring(0, 5);
        return first + second;
    }
    
//    public static void main(String[] args) {
//        String s = "4570300C667E0";
//        String cellStr = s.substring(5);
//        String firstDec = Long.parseLong(reverse(cellStr.substring(0, 4)), 16) + "";
//        String firststr = "00000" + firstDec;
//        String first = (firststr.substring(firstDec.length())).substring(0, 2);
//        String secondHex = Long.parseLong(reverse(cellStr.substring(4)), 16) + "";
//        String secondStr = "00000" + secondHex;
//        String second = (secondStr.substring(secondHex.length())).substring(0, 5);
//        System.out.println(first + second);
//    }
    
    private String reverse(String input) {
        if (input.length() != 4) {
            return input;
        }
        
        return new StringBuilder("").append(input.charAt(1)).append(input.charAt(0)).append(input.charAt(3)).append(input.charAt(2)).toString();
    }
    
}
