/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.main;

import java.util.regex.Pattern;

/**
 *
 * @author ubuntu
 */
public class StopMaster {

    public static void main(String args[]) {
//        MasterThread.getInstance("Master", "Master").stop();
        String s = "201802120000000083,2900001488,123456,0,0,959696473286,1,1,2018/02/12 04:00:55,2018/02/12 04:55:55,0,0,0,0,77,2,35,7,0,1,alarm,GATEWAY,1007,0,0,0,0,0,10.6.3.85,959690001201,2,0,35,,,,dqNssTGY1reFnAKHAXAQHMB2TDHO5CVM8nLXrmzwed5X19A+bjnTzcdDh2W8wN8evcaTUuZovN3DSzC5XeZcx+7e1IRCCiFGQDf4CBk=,,";
        String arr[] = s.split(Pattern.quote(","));
//        String arr[] = s.split(",");
        System.out.println("leng " + arr.length);
    }

}
