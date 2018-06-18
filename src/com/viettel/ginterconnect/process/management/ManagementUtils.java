/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.management;

import com.viettel.ginterconnect.util.SystemParam;
import java.net.URL;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author ubuntu
 */
public class ManagementUtils {

    public static void send(String ip, String threadInfo, Logger logger) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            String URL = "http://" + SystemParam.OAM_HOST + ":" + SystemParam.OAM_PORT;
            config.setServerURL(new URL(URL));
            config.setConnectionTimeout(10 * 1000);
            config.setReplyTimeout(10 * 1000);
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Vector params = new Vector();
            params.addElement(ip);
            params.addElement(threadInfo);
            logger.info("Send info to OAMListener " + params);
            Object res = client.execute("jobprocessor.processThread", params);
            logger.info("respone:  " + res.toString());
        } catch (Exception ex) {
            logger.warn("Error while sending thread info to OAMListener:" + ex.getMessage());
        }
    }

}
