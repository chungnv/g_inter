/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.master.get;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public abstract class AbstractFileTransfer {

    private Logger logger;
    
    protected String host;
    protected int port;
    protected String username;
    protected String password;
    protected String processCode;
    protected String remoteDir;

    public abstract boolean connect() throws Exception;

    public abstract void disconnect();

    public abstract boolean isConnected();

    public abstract boolean rename(String remoteDir, String ftpSrcFile, String ftpDesFile) throws Exception;

    public FTPFile[] listFile(final String remoteDir, int timeout) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                FTPFile[] f = listFile(remoteDir);
                return f;
            }
        };
        Future<Object> future = executor.submit(callable);
        try {
            FTPFile[] lstFile = (FTPFile[]) future.get(timeout, TimeUnit.SECONDS);
            logger.info("List files: " + remoteDir + ". Number of files: " + lstFile.length);
            return lstFile;
        } catch (TimeoutException | InterruptedException | ExecutionException timeoutEx) {
            timeoutEx.printStackTrace();
            logger.error(timeoutEx);
            logger.error("Time out when listFiles: " + timeoutEx.getMessage());
            return null;
        } finally {
            future.cancel(true);
            executor.shutdown();
        }
    }

    public abstract FTPFile[] listFile(String remoteDir) throws Exception;
    
    public abstract boolean cd(String dir) throws Exception;
    
    public boolean download(final String remoteDir, final String expectFile,final String desFile, int timeout, final boolean zip) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return (Boolean) download(remoteDir, expectFile, desFile, zip);
            }
        };
        Future<Object> future = executor.submit(callable);
        try {
            return (Boolean) future.get(timeout, TimeUnit.SECONDS);
        } catch (Exception ex) {
            logger.error(ex);
            logger.error("Exception while downloading file: " + ex.getMessage());
            return false;
        } finally {
            future.cancel(true);
            executor.shutdown();
        }
    }
    
    public boolean upload(final String remoteTempDir, final String remoteDir, final String expectFile,final String desFile, int timeout, final boolean zip) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return (Boolean) upload(remoteTempDir, remoteDir, expectFile, desFile, zip);
            }
        };
        Future<Object> future = executor.submit(callable);
        try {
            return (Boolean) future.get(timeout, TimeUnit.SECONDS);
        } catch (Exception ex) {
            logger.error(ex);
            logger.error("Exception while downloading file: " + ex.getMessage());
            return false;
        } finally {
            future.cancel(true);
            executor.shutdown();
        }
    }

    public abstract boolean download(String remoteDir, String expectFile, String desFile, boolean zip) throws Exception;
    
    public abstract boolean upload(String remoteTempDir, String remoteDir, String expectFile, String srcFile, boolean zip) throws Exception;

    public abstract boolean delete(String remoteDir, String ftpSrcFile) throws Exception;

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
    
    public void setParam(String host, int port, String username, 
            String password, String processCode, String remoteDir) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.processCode = processCode;
        this.remoteDir = remoteDir;
    }
    
    protected Long ftpMode = 0L;
    protected Long ftpTransferType = 0L;
    
    public void setModeAndTransferType(Long mode, Long type) {
        this.ftpMode = mode;
        this.ftpTransferType = type;
    }
    
    public void setModeAndTransferType(String mode, String type) {
        long pMode = 0;
        long pTransferType = 0;
        if (mode != null) {
            try {
                pMode = Long.valueOf(mode);
            } catch (Exception ex) {
                ;
            }
        }
        if (type != null) {
            try {
                pTransferType = Long.valueOf(type);
            } catch (Exception ex) {
                ;
            }
        }
        setModeAndTransferType(pMode, pTransferType);
    }

}
