/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert;

/**
 *
 * @author chungdq
 */
public class EricssonDirection extends BaseFunction{

    /**
     * Function only support format with type %s
     * return String
     * args[0]: record Type
     * args[1]: direction
     * args[2]: MessageTypeIndicator
     * return String of Direction
     * 
     */

    @Override
    public Object calculate(Object... args) throws Exception {
        String sReturn = null;
        try{
            if (args == null || args.length != 3)
            {
                if (args != null && args.length >= 2)
                    return args[1];
                else
                    return "";
            }
            else{
                if (args[0] != null && args[1] != null && args[2] != null && args[2].toString().trim().length() > 0
                        && args[0].toString().equals("2") && args[1].toString().equals("I") && !args[2].toString().equals("0"))
                {
                    sReturn = "R";
                }
                else
                    sReturn = (args[1] == null) ? "" : args[1].toString();
            }
        }
        catch(Exception ex)
        {
            //ex.printStackTrace();
        }
        finally{
            return  sReturn;
        }
    }

}
