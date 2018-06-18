/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 *
 * @author ubuntu
 */
@WebService
public class TestFilterWS {

    @WebMethod
    public String sayHello() {
        return "Hello";
    }
}
