/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.worker.util.convert.cdrfieldtype;

/**
 *
 * @author Le Thanh Cong
 */
public class CdrPhoneNumber extends CdrDigits
{

    private int lacLength;

    /**
     * Default constructor
     */
    public CdrPhoneNumber()
    {
        super();
    }

    public CdrPhoneNumber(int length, String description)
    {
        super(length, description);
    }

    public CdrPhoneNumber(int length, String description, int lacLength)
    {
        super(length, description);
        this.lacLength = lacLength;
    }

    public int getLacLength()
    {
        return lacLength;
    }

    public void setLacLength(int lacLength)
    {
        this.lacLength = lacLength;
    }

    public String getLACNumber()
    {
        return ((String) getValue()).substring(0, lacLength);
    }

    public String getPhoneNumber()
    {
        return ((String) getValue()).substring(lacLength);
    }

    @Override
    public String toString()
    {
        return getPhoneNumber();
    }
}
