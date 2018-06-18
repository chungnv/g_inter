/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert;

/**
 *
 * @author chungdq
 */
public class HuaweiPSTNChoiceCalled extends BaseFunction {

    /**
     * return Digits String
     * args[0]: Dialed Number Ex: O,I,T
     * args[1]: Called Number Ex: 0,1,2
     * if Dialed Number start = 178 return Dialed Number
     * else return Called Number
     */
//    private static final String START_CHECK = "178";
    @Override
    public Object calculate(Object... args) throws Exception {

        // validate parameter
        if (args.length != 2) {
            throw new Exception("Invalid total of parameter");
        }

        if (args[0] != null) {
            if (args[0].toString().startsWith("178") || args[0].toString().startsWith("1800") || args[0].toString().startsWith("1900") ||
                    args[0].toString().startsWith("01800") || args[0].toString().startsWith("01900")) {
                return args[0].toString();
            } else {
                return args[1].toString();
            }
        } else {
            return args[1].toString();
        }
    }
}
