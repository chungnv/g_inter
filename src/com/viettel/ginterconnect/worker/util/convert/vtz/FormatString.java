/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.worker.util.convert.vtz;


/**
 *
 * @author chungdq
 */
public class FormatString extends BaseFunction{

    /**
     * Function only support format with type %s
     * return String
     * args[0]: patern
     * args[1 ..]: argument
     * return String
     * 
     */

    @Override
    public Object calculate(Object... args) throws Exception {
        String sReturn = null;
        try{
            if (args.length == 0)
                sReturn = "";
            else if (args.length == 1)
                sReturn = args[0].toString();
            else{
                Object arrs[] = new Object[args.length - 1];
                for (int i = 0; i < arrs.length ; i++)
                    arrs[i] = args[i + 1].toString();
                sReturn = String.format(args[0].toString(), arrs);
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
