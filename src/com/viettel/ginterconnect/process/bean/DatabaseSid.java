/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.viettel.ginterconnect.process.bean;

/**
 *
 * @since May 9, 2011
 * @version 1.0
 */
public class DatabaseSid {

    private String url;
//    private String host;
//    private int port;
    private String user;
    private String pass;
//    private String sid;

    public DatabaseSid(String url, String username, String password) {
        this.url = url;
        this.user = username;
        this.pass = password;
    }
    
    public String getPass()
    {
        return pass;
    }

    public void setPass(String pass)
    {
        this.pass = pass;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
