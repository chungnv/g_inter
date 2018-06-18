/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.master.get.impl;

import com.viettel.ginterconnect.master.get.AbstractFileTransfer;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import java.util.zip.*;

/**
 *
 * @author
 */
public class FTP extends AbstractFileTransfer {

    FTPClient ftpClient;

    @Override
    public boolean connect() {
        ftpClient = new FTPClient();
        try {
            int loop = 0;
            boolean connectok = false;
            while (loop < 10) {//thu connect 10 lan
                try {
                    ExecutorService execService = Executors.newCachedThreadPool();
                    Callable<Object> task = new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            ftpClient.connect(host, port);
                            ftpClient.login((username), password);
                            getLogger().info(processCode + ": Login ok");
                            return true;
                        }
                    };
                    Future<Object> future = execService.submit(task);
                    try {
                        connectok = (Boolean) future.get(30, TimeUnit.SECONDS);
                    } catch (TimeoutException timeout) {
                        getLogger().error("Time out while connect to ftpserver: " + timeout.getMessage());
                        connectok = false;
                    } catch (Exception ex) {
                        getLogger().error("Exception while connect to ftpserver: " + ex.getMessage());
                        connectok = false;
                    } finally {
                        future.cancel(true);
                        execService.shutdown();
                    }

                    if (connectok) {
                        break;
                    }
                } catch (Exception excep) {
                    getLogger().warn(processCode + ": " + excep.getMessage());
                    getLogger().info(processCode + ": " + excep.getMessage());
                    getLogger().error(excep);
                    Thread.sleep(2000);//nghi 2s
                }
                getLogger().info(processCode + ": Try connect: " + host + ". time: " + (loop + 1));
                loop++;
            }
            if (!connectok) {
                return false;
            }
            setMode();
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                if (!ftpClient.changeWorkingDirectory(remoteDir)) {
                    getLogger().warn("[Error]:While focus FtpDir " + remoteDir);
                    if (ftpClient != null) {
                        if (ftpClient.isConnected()) {
                            try {
                                ftpClient.disconnect();
                            } catch (IOException iox) {
                                ftpClient = null;
                                return false;
                            }
                        }
                        ftpClient = null;
                        return false;
                    }
                    return false;
                } else {
//                    getLogger().info("set transfer and mode type");
                    setMode();
                }
            } else {
                ftpClient.disconnect();
                ftpClient = null;
                getLogger().warn("FTP server refused connection !");
                return false;
            }
            return true;
        } catch (IOException iox) {
            iox.printStackTrace();
            getLogger().warn("[Error]:" + iox.toString());
            ftpClient = null;
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            getLogger().warn("[Error]:" + ex.toString());
            return false;
        }
    }

    private void setMode() throws Exception {
        if (ftpMode.equals(0L)) {
            ftpClient.setFileTransferMode(FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE);
            ftpClient.enterLocalPassiveMode();
            getLogger().debug("Passive mode " + host);
        } else if (ftpMode.equals(1L)) {
            ftpClient.setFileTransferMode(FTPClient.ACTIVE_LOCAL_DATA_CONNECTION_MODE);
            ftpClient.enterLocalActiveMode();
            getLogger().debug("Active mode " + host);
        } else {
            ftpClient.setFileTransferMode(FTPClient.BLOCK_TRANSFER_MODE);
            getLogger().debug("Block transfer " + host);
        }
        if (ftpTransferType.equals(0L)) {
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            getLogger().debug("File type binary " + host);
        } else {
            ftpClient.setFileType(FTPClient.ASCII_FILE_TYPE);
            getLogger().debug("File type ascii " + host);
        }
    }

    @Override
    public void disconnect() {
        if (ftpClient != null) {
            try {
                ftpClient.disconnect();
                getLogger().debug("ftpClient ngat ket noi thanh cong sau khi fail!");
//                ftpClient = null;
            } catch (IOException ex) {
                getLogger().warn(ex.getMessage());
            }
        }
    }

    @Override
    public FTPFile[] listFile(String remoteDir) throws IOException {
//        FTPFile[] lst = ftpClient.listFiles(remoteDir);
//        disconnect();
//        connect();
        return ftpClient.listFiles(remoteDir);
    }

    @Override
    public boolean download(final String remoteDir, final String expectFile, final String desFile, boolean zip) throws Exception {
        if (ftpClient == null && !ftpClient.isConnected()) {
            connect();
        }
        boolean b = false;
        FileOutputStream fileOutputStream = null;
        try {
            if (zip) {
                fileOutputStream = new FileOutputStream(desFile + ".zip");
                ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
                b = ftpClient.retrieveFile(expectFile, zipOutputStream);
            } else {
                fileOutputStream = new FileOutputStream(desFile);
                b = ftpClient.retrieveFile(expectFile, fileOutputStream);
            }
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        return b;
    }

    @Override
    public boolean isConnected() {
        return ftpClient.isConnected();
    }

    @Override
    public boolean rename(String remoteDir, String ftpSrcFile, String ftpDesFile) throws Exception {
        return ftpClient.rename(ftpSrcFile, ftpDesFile);
    }

    @Override
    public boolean delete(String remoteDir, String ftpSrcFile) throws IOException {
        return ftpClient.deleteFile(ftpSrcFile);
    }

    @Override
    public boolean cd(String dir) throws Exception {
        return ftpClient.changeWorkingDirectory(dir);
    }

    @Override
    public boolean upload(String remoteTempDir, String remoteDir, String expectFile, String srcFile, boolean zip) throws Exception {
        BufferedInputStream buffIn = new BufferedInputStream(new FileInputStream(srcFile));
        if (ftpClient.storeFile(remoteTempDir + File.separator + expectFile, buffIn)) {
            return ftpClient.rename(remoteTempDir + File.separator + expectFile,
                    remoteDir + File.separator + expectFile);
        } else {
            System.out.println("Store file fial:");
        }
        return false;
    }

}
