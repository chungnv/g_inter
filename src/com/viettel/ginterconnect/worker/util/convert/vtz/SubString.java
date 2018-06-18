/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert.vtz;


/**
 *
 * @author chungdq
 */
public class SubString extends BaseFunction{

    /**
     * return String
     * args[0]: origin String
     * args[1]: start index
     * args[2]: length : optional
     * return start Time
     * 
     */

    @Override
    public Object calculate(Object... args) throws Exception {
        String sReturn = null;
        try{
            if (args[0] == null || args[0].toString().length() == 0)
            {
                sReturn = "";
            }
            else{
                if (args.length == 2)
                {
                    sReturn = args[0].toString().substring(Integer.parseInt(args[1].toString()));
                }else if (args.length >= 3)
                {
                    int startIndex = Integer.parseInt(args[1].toString());
                    try {
                        sReturn = args[0].toString().substring(startIndex, startIndex + Integer.parseInt(args[2].toString()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally{
            return  sReturn;
        }
    }

}
