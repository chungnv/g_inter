/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author chungdq
 */
public abstract class BaseFunction {
    private static Map<String,BaseFunction> listFunction = null;

    public abstract Object calculate(Object ... args) throws Exception;

    //singleton
    public static BaseFunction getFunction(String pakage, String functionName) throws Exception
    {
        if (listFunction == null)
            listFunction = new HashMap<String, BaseFunction>();

        if (listFunction.containsKey(functionName))
            return listFunction.get(functionName);
        else
        {
            Class functionType = Class.forName(pakage + "." + functionName);
            BaseFunction function = (BaseFunction) functionType.newInstance();
            listFunction.put(pakage + "." + functionName, function);
            return function;
        }
    }

    public static Object getValue(String pakage, String functionName, Object ... args) throws Exception
    {
        BaseFunction function = getFunction(pakage, functionName);
        return function.calculate(args);
    }

    public static void FlushFuctionCache()
    {
        listFunction.clear(); 
    }
}
