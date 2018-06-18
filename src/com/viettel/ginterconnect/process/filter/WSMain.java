/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter;

import com.viettel.ginterconnect.process.bean.Result;
import javax.xml.ws.Endpoint;

/**
 *
 * @author ubuntu
 */
public class WSMain {

    public static void main(String[] args) {
//        Endpoint.publish("http://localhost:9999/ws/hello", new TestFilter());
        Result result = new Result();
        result.setResultId(1L);
        result.setLocalFolder("xxxx");
        result.setStatus("1");
        result.setResultName("name");
        result.setRuleId(22L);
        result.setFileNameTemplate("filenmae temp");
        result.setResultType("Type");
        result.setFileExtension("xxx");
        result.setDescription("description");
        result.setSeqName("abc_seq");
        result.setDataStructureName("myt_vas");
        result.setImportTableId("111");
        result.setSwitchType("VTC");
        
        Result result2 = (Result) result.clone();
        System.out.println("VTC: " + result2.getFileNameTemplate());
    }
}
