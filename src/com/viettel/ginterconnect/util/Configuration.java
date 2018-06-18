/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.util;

import com.viettel.ginterconnect.worker.util.ParamFileUtil;


/**
 *
 * @author chungdq
 */
public class Configuration {

    //private static final String configPath = "/configuration.properties";

    private ParamFileUtil paramFile = null;

    private static Configuration instance;
    
    private Configuration(String configPath) throws Exception
    {
        paramFile = new ParamFileUtil(configPath);
    }

    public static void loadConfig(String configPath) throws Exception
    {
        instance = new Configuration(configPath);
    }

    public static String getString(String key)
    {
        if (instance.getParamFile().getString(key) != null)
            return instance.getParamFile().getString(key).trim();
        else 
            return null;
    }
    
    public static String getString(String key, String dflt)
    {
        if (instance.getParamFile().getString(key) != null)
            return instance.getParamFile().getString(key).trim();
        else 
            return dflt;
    }

    public ParamFileUtil getParamFile()
    {
        return paramFile;
    }

}
