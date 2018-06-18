/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.importer;

import com.viettel.ginterconnect.util.ThreadUtil;
import com.viettel.library.dataobject.DbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 *
 * @author chungdq
 */
public class ImporterConnection {

    private Importer importer;

    private Connection connection;

    private PreparedStatement insertStmt;

    private PreparedStatement duplicateStmt;

    private PreparedStatement errorStmt;

    private Date lastUse = new Date();

    public ImporterConnection(Importer importer)
    {
        this.importer = importer;
    }

    public void createConnection() throws Exception
    {
        int count = 0;
        while (connection == null || insertStmt == null || duplicateStmt == null || errorStmt == null)
        {
            try
            {
//                DbConnection dbCon = new DbConnection();
//                dbCon.setConnectionType("thin");
//
//                dbCon.setSid(importer.getDatabaseImport().getSid());
//                dbCon.setUserName(importer.getDatabaseImport().getUser());
//                dbCon.setPassword(importer.getDatabaseImport().getPass());
//                dbCon.setPort(importer.getDatabaseImport().getPort());
//                dbCon.setHost(importer.getDatabaseImport().getHost());
                Class.forName("oracle.jdbc.OracleDriver");
//                connection = dbCon.openConnection();
                connection = DriverManager.getConnection(importer.getDatabaseImport().getUrl(), 
                        importer.getDatabaseImport().getUser(), importer.getDatabaseImport().getPass());

                insertStmt = connection.prepareStatement(importer.getInsertQuery());
                duplicateStmt = connection.prepareStatement(importer.getDuplicateQuery());
                errorStmt = connection.prepareStatement(importer.getErrorQuery());
                lastUse = new Date();
            }
            catch(Exception ex)
            {
                count++;
                //release tat ca moi ket noi neu co the co
                releaseConnection();
                if (count >= 5)
                {
                    throw ex;
                }
                else
                {
                    //Tam dung khoang thoi gian 100s truoc khi connect lai
                    ThreadUtil.delay(1000 * 5L);
                }
            }
        }
    }

    public void releaseConnection()
    {
        try {
            if (insertStmt != null)
                insertStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            if (duplicateStmt != null)
                duplicateStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            if (errorStmt != null)
                errorStmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        try {
            if (connection != null)
                connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        insertStmt = null;
        duplicateStmt = null;
        errorStmt = null;
        connection = null;
        lastUse = null;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public void setConnection(Connection connection)
    {
        this.connection = connection;
    }

    public PreparedStatement getDuplicateStmt()
    {
        return duplicateStmt;
    }

    public void setDuplicateStmt(PreparedStatement duplicateStmt)
    {
        this.duplicateStmt = duplicateStmt;
    }

    public PreparedStatement getErrorStmt()
    {
        return errorStmt;
    }

    public void setErrorStmt(PreparedStatement errorStmt)
    {
        this.errorStmt = errorStmt;
    }

    public PreparedStatement getInsertStmt()
    {
        return insertStmt;
    }

    public void setInsertStmt(PreparedStatement insertStmt)
    {
        this.insertStmt = insertStmt;
    }

    public Date getLastUse()
    {
        return lastUse;
    }

    public void setLastUse(Date lastUse)
    {
        this.lastUse = lastUse;
    }

    public Importer getImporter()
    {
        return importer;
    }

}
