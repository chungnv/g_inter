package com.viettel.ginterconnect.worker.util.convert.stl;

import com.viettel.ginterconnect.worker.util.convert.stl.BaseFunction;

public class FormatString extends BaseFunction {

    public FormatString() {
    }

    @Override
    public Object calculate(Object... args) throws Exception {
        String sReturn = null;
        try {
            if (args.length == 0) {
                sReturn = "";
            } else if (args.length == 1) {
                sReturn = args[0].toString();
            } else {
                Object arrs[] = new Object[args.length - 1];
                for (int i = 0; i < arrs.length; i++) {
                    arrs[i] = args[i + 1].toString();
                }
                sReturn = String.format(args[0].toString(), arrs);
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
        } finally {
            return sReturn;
        }
    }
}
