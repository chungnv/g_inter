/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.ginterconnect.util;

public class ThreadUtil
{

    /**
     * Delay some miliseconds
     * @param time
     */
    public static void delay(Long time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (InterruptedException e)
        {
        // ignore
        }
    }
}
