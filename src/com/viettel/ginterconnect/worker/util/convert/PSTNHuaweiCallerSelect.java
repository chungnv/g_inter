/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert;

import java.util.Map;

/**
 *
 * @author chungdq
 */
public class PSTNHuaweiCallerSelect extends BaseFunction{

    /**
     * return Digits String
     * args[0]: Direction Ex: O,I,T
     * args[1]: Caller DNSEt Ex: 0,1,2
     * args[2]: Caller Number
     * args[3]: map
     */
    @Override
    public Object calculate(Object... args) throws Exception {
        
        // validate parameter
        if (args.length != 4)
        {
            throw new Exception("Invalid total of parameter");
        }
        
        if (args[0] != null && args[0].toString().equals("O") && args[1] != null && args[3] != null && args[3] instanceof Map)
        {
            if (((Map)args[3]).containsKey(args[1].toString()))
                return ((Map)args[3]).get(args[1].toString()) + args[2].toString();
            else
                return args[2].toString();
        }else{
            return args[2];
        }
    }

}
