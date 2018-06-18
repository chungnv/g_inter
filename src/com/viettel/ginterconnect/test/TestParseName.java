/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.test;

import com.viettel.ginterconnect.util.ParseFileNameStandard;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 *
 * @author ubuntu
 */
public class TestParseName {

    public static void main(String[] args) {
        
//        String s = "414096000000568;00959696000007;103.85.107.13;;;mytel;497603;10011430;;20180201035452;310;;16;31260741;;;8000;00;-1;84;103.85.107.8;;10.21.23.104;414F09;00;;;;;;;;1012;;6;;";
//        if (s.endsWith(";")) {
//            s += "0";
//        }
//        String[] arrCdr = s.split(Pattern.quote(";"));
//        arrCdr[arrCdr.length - 1] = "";
//        System.out.println("arr length " + arrCdr.length);
//        System.out.println("val " + arrCdr[arrCdr.length - 1]);
        
        ParseFileNameStandard parseFileName = new ParseFileNameStandard(null);
        parseFileName.setPatternFileName("SMC{YEAR####}{MON##}{DAY##}{SEQ########}.txt.zip");
        if (parseFileName.Parse("SMC2018061815000110.txt.zip")) {
            System.out.println("parse ok");
        } else {
            System.out.println("note");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//        try {
//            sdf.parse("dgd");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        TestParseName t = new TestParseName();
//        HashMap map = new HashMap();
//        t.setMap(map);
//        System.out.println(map.get("test"));
//        HashMap h = new HashMap();
//        Thread1 t1 = new Thread1(h);
//        Thread2 t2 = new Thread2(h);
//        t1.start();
//        t2.start();
    }

    public void setMap(HashMap map) {
//        HashMap<String, String> x = new HashMap();
//        map.put("test", "test");
//        map = x;
    }
}
