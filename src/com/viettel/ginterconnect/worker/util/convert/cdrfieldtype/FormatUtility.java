// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormatUtility.java
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

import java.io.PrintStream;

public class FormatUtility {

    public FormatUtility() {
        mstrDefaultKey = "101010101010101010101010101010101010101010101010";
    }

    public int getLength(byte pbtSrc[], int pintStart) {
        byte vbtLen[] = new byte[2];
        System.arraycopy(pbtSrc, pintStart, vbtLen, 0, 2);
        int vintReturn = getByteValue(vbtLen[1]) * 256 + getByteValue(vbtLen[0]);
        return vintReturn;
    }

    public int getDec(byte pbtSrc[]) {
        int vintReturn = getByteValue(pbtSrc[0]) * 256 + getByteValue(pbtSrc[1]);
        return vintReturn;
    }

    public int getByteValue(byte pbtvalue) {
        return pbtvalue < 0 ? pbtvalue + 256 : pbtvalue;
    }

    public String fmHexWord(byte parrByte[]) {
        String vstrReturn = "";
        vstrReturn = vstrReturn + fmHexByte(parrByte[1]);
        vstrReturn = vstrReturn + fmHexByte(parrByte[0]);
        return vstrReturn;
    }

    public String fmHexByte(byte pbyte) {
        String vstrReturn = "";
        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
        char ch2 = Character.forDigit(pbyte & 0xf, 16);
        vstrReturn = vstrReturn + ch1;
        vstrReturn = vstrReturn + ch2;
        return vstrReturn;
    }

    public String fmBCDByte(byte pbyte) {
        String vstrReturn = "";
        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
        char ch2 = Character.forDigit(pbyte & 0xf, 16);
        vstrReturn = vstrReturn + ch2;
        vstrReturn = vstrReturn + ch1;
        return vstrReturn;
    }

    public String fmBCDByteReverse(byte pbyte) {
        String vstrReturn = "";
        char ch1 = Character.forDigit(pbyte >> 4 & 0xf, 16);
        char ch2 = Character.forDigit(pbyte & 0xf, 16);
        vstrReturn = vstrReturn + ch1;
        vstrReturn = vstrReturn + ch2;
        return vstrReturn;
    }

    public String fmBCDBytes2(byte pbyte[]) {
        String vstrReturn = "";
        for (int i = pbyte.length - 1; i >= 0; i--) {
            vstrReturn = vstrReturn + fmHexByte(pbyte[i]);
        }
        vstrReturn = vstrReturn.replaceAll("f", "");
        vstrReturn = vstrReturn.replaceAll("F", "");
        return vstrReturn;
    }

    public String fmBCDBytes3(byte pbyte[]) {
        String vstrReturn = "";
        for (int i = pbyte.length - 1; i >= 0; i--) {
            vstrReturn = vstrReturn + fmBCDByte(pbyte[i]);
        }
        return vstrReturn;
    }

    public String fmBCDBytes(byte pbyte[]) {
        String vstrReturn = "";
        for (int i = 0; i < pbyte.length; i++) {
            vstrReturn = vstrReturn + fmBCDByte(pbyte[i]);
        }
        vstrReturn = vstrReturn.replaceAll("f", "");
        vstrReturn = vstrReturn.replaceAll("F", "");
        return vstrReturn;
    }

    public String fmBCDDWord(byte pbyte[]) {
        String vstrReturn = "";
        for (int i = 0; i < 4; i++) {
            vstrReturn = vstrReturn + fmHexByte(pbyte[3 - i]);
        }
        return vstrReturn;
    }

    public String fmBCDWord(byte pbyte[]) {
        String vstrReturn = "";
        for (int i = 0; i < 2; i++) {
            vstrReturn = vstrReturn + fmHexByte(pbyte[1 - i]);
        }
        return vstrReturn;
    }

    public String getTimeStamp(byte parrByte[]) {
        String vstrDay = fmHexByte(parrByte[3]);
        String vstrMon = fmHexByte(parrByte[4]);
        String vstrSec = fmHexByte(parrByte[0]);
        String vstrMin = fmHexByte(parrByte[1]);
        String vstrHou = fmHexByte(parrByte[2]);
        byte vbtYear[] = new byte[2];
        System.arraycopy(parrByte, 5, vbtYear, 0, 2);
        String vstrYear = fmBCDWord(vbtYear);
        return vstrDay + "/" + vstrMon + "/" + vstrYear + " " + vstrHou + ":" + vstrMin + ":" + vstrSec;
    }

    public byte[] convertHexToByte(String strHex)
            throws Exception {
        String strTemp = "";
        strTemp = strHex.toUpperCase();
        if (strTemp.length() % 2 != 0) {
            throw new Exception("Invalid length");
        }
        int length = strTemp.length() / 2;
        byte keySec[] = new byte[length];
        for (int i = 0; i < length; i++) {
            char ch0 = strTemp.charAt(2 * i);
            char ch1 = strTemp.charAt(2 * i + 1);
            int loByte = "0123456789ABCDEF".indexOf(ch0);
            int hiByte = "0123456789ABCDEF".indexOf(ch1);
            byte lo = Hex2Byte[loByte];
            byte hi = Hex2Byte[hiByte];
            Byte byelo = new Byte(lo);
            Byte byehi = new Byte(hi);
            int inlo = byelo.intValue();
            int inhi = byehi.intValue();
            int value = inlo * 16 + inhi;
            keySec[i] = (byte) value;
        }

        return keySec;
    }

    public String getHexDump(byte data[]) {
        String dump = "";
        try {
            int dataLen = data.length;
            for (int i = 0; i < dataLen; i++) {
                dump = dump + Character.forDigit(data[i] >> 4 & 0xf, 16);
                dump = dump + Character.forDigit(data[i] & 0xf, 16);
            }

        } catch (Throwable t) {
            dump = "Throwable caught when dumping = " + t;
        }
        return dump;
    }

    public void main(String args[]) {
        try {
//            String str = "D78D";
//            System.out.println(fmHexWord(convertHexToByte(str)));
//            System.out.println(fmBCDDWord(convertHexToByte("03000000")));
//            byte[] s = convertHexToByte("24".replaceAll(" ", ""));
//            System.out.println("dfsdfsdf----" + fmBCDByte((byte)36));
//            System.out.println(fmBCDBytes(convertHexToByte("94 71 37 11 60 FF FF FF FF FF".replaceAll(" ", ""))));
//            System.out.println(getTimeStamp(convertHexToByte("22 05 09 16 05 07 20".replaceAll(" ", ""))));
//            System.out.println(getLength(convertHexToByte("2900"), 0));
//            System.out.println(getByteValue((byte) -24));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String fmDataField(byte fieldData[], String vstrDataType) {
        if (vstrDataType.equalsIgnoreCase("BCDs")) {
            return fmBCDBytes(fieldData);
        }
        if (vstrDataType.equalsIgnoreCase("BCDs2")) {
            return fmBCDBytes2(fieldData);
        }
        if (vstrDataType.equalsIgnoreCase("BCDs3")) {
            return fmBCDBytes3(fieldData);
        }
        if (vstrDataType.equalsIgnoreCase("TimeStamp")) {
            return getTimeStamp(fieldData);
        }
        if (vstrDataType.equalsIgnoreCase("BCDw")) {
            return fmBCDWord(fieldData);
        } else {
            return getHexDump(fieldData);
        }
    }

    public String removeStartWith0(String vpstrIn) {
        for (; vpstrIn.startsWith("0") && vpstrIn.length() > 1; vpstrIn = vpstrIn.substring(1));
        return vpstrIn;
    }
    public final String Hex = "0123456789ABCDEF";
    public String mstrDefaultKey;
    public final byte Hex2Byte[] = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
        10, 11, 12, 13, 14, 15
    };
    
    
}

