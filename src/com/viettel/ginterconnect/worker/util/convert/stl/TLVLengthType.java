/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.worker.util.convert.stl;

/**
 *
 * @author chungdq
 * @version 1.0
 */
public class TLVLengthType {

    /** type - gia tri la bit dau tien cua byte dang xet (=0 or 1).*/
    private int type = 0;
    /** length.*/
    private long length = 0;
    /** lengbits:length cua Tag: Neu type:
     *  = 0: lengbits = gia tri byte dang xet
     *  = 1: lengbits = gia tri byte tiep theo
     */
    private int lengbits = 0;
    /** tagType: TagTitle.*/
    private byte tagType;
    /** tagTypeExtend: byte Tag.*/
    private byte[] tagTypeExtend;
    /** tagTypeInt - integer value of Tag.*/
    private int tagTypeInt = 0;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public void setLengbits(int lengbits) {
        this.lengbits = lengbits;
    }

    public int getLengbits() {
        return lengbits;
    }

    public void setTagType(byte tagType) {
        this.tagType = tagType;
    }

    public byte getTagType() {
        return tagType;
    }

    public byte[] getTagTypeExtend() {
        return tagTypeExtend;
    }

    public void setTagTypeExtend(byte[] tagTypeExtend) {
        this.tagTypeExtend = tagTypeExtend;
    }

    public int getTagTypeInt() {
        return tagTypeInt;
    }

    public void setTagTypeInt(int tagTypeInt) {
        this.tagTypeInt = tagTypeInt;
    }
}
