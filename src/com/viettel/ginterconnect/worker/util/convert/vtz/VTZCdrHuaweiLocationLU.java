/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.vtz;

/**
 *
 * @author chungdq
 *
 * to get location of Huawei MSC and GMSC = LAC + LAI
 */
public class VTZCdrHuaweiLocationLU extends BaseCdrField {

    public VTZCdrHuaweiLocationLU(int length, String description) {
        super(length, description);
    }

    public VTZCdrHuaweiLocationLU() {
        super();
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
//        if (offset == 56582) {
//            System.out.println("here");
//        }
        int iLastOffset = offset + super.getLength();
        StringBuilder valCell = new StringBuilder();

        for (int i = offset; i < iLastOffset; i++) {
            byte h = (byte) ((b[i] & 0xf0) >>> 4);
            if (h < 10) {
                h = (byte) (48 + h);
            } else {
                h = (byte) ((65 + h) - 10);
            }
            byte l = (byte) (b[i] & 0xf);
            if (l < 10) {
                l = (byte) (48 + l);
            } else {
                l = (byte) ((65 + l) - 10);
            }

            valCell.append((char) h);
            valCell.append((char) l);
        }
        String vReturn = valCell.toString();
//        if (vReturn.length() != 16) {
//            return "";
//        } else {

        if (vReturn.length() < 16) {
//            System.out.println("here");
            return "0000000000";
        }

        long numL = Long.parseLong(vReturn.substring(vReturn.length() - 4, vReturn.length()), 16);
        long numH = Long.parseLong(vReturn.substring(vReturn.length() - 12, vReturn.length() - 8), 16);

        String lac = String.valueOf(numH);

        String ci = String.valueOf(numL);

        while (lac.trim().length() > 0 && lac.trim().length() < 5) {
            lac = "0" + lac;
        }
        while (ci.trim().length() > 0 && ci.trim().length() < 5) {
            ci = "0" + ci;
        }

        //   return ("64303" + numH + numL);
        return (lac + ci);
//        }
    }
}
