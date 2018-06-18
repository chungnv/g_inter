/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author chungdq
 */
public class NCdrHEXByteGroup extends BaseCdrField {

    public NCdrHEXByteGroup(int length, String description) {
        super(length, description);
    }

    public NCdrHEXByteGroup() {
        super();
    }

    @Override
    protected Object unpackDetail(byte[] b, int offset) throws Exception {
        String temp;
        String vstrReturn = "";
        for (int i = super.length - 1; i >= 0; i--) {
            temp = fmHEXByte(b[offset + i]);
            if (temp != null)
                vstrReturn += temp;
            else
                return vstrReturn;
        }
        return Long.parseLong(vstrReturn, 16);
    }

    public String fmHEXByte(byte pbyte) {
        String vstrReturn = "";
        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
        char ch2 = Character.forDigit(pbyte & 0xf, 16);
        vstrReturn = vstrReturn + ch1;
        vstrReturn = vstrReturn + ch2;
        return vstrReturn;
    }
    
//    public static void main(String[] args) throws Exception
//    {
//        
//        String input[] = {"01","12"};
//        byte b[] = new byte[2];
//        for (int i = 0; i < input.length; i++)
//        {
//            b[i] = (byte)Integer.parseInt(input[i], 16);
//        }
//        
//        NCdrHEXByteGroup test = new NCdrHEXByteGroup(2,"");
//        Object thu = test.unpackDetail(b, 0);
//        System.out.println(thu);
//    }
}
