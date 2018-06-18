/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.master.get.impl;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.viettel.ginterconnect.master.get.AbstractFileTransfer;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Vector;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author ubuntu
 */
public class SFTP extends AbstractFileTransfer {

    JSch jsch = new JSch();
    Session session = null;

    @Override
    public boolean connect() throws Exception {
        int _retry = 3;
        int connectTime = 0;
        while (connectTime <= _retry) {
            try {
                connectTime++;
                session = jsch.getSession(username, host, 22);
                session.setPassword(password);
                session.setConfig("StrictHostKeyChecking", "no");
                session.connect(60000);
                return true;
            } catch (JSchException jE) {
//                jE.printStackTrace();
                if (connectTime >= _retry) {
                    throw jE;
                } else {
                    Thread.sleep(connectTime + 1000);
                }
            }
        }
        return false;
    }

    @Override
    public void disconnect() {
        try {
            if (session != null && session.isConnected()) {
                session.disconnect();
                getLogger().info("Disconnect sftp successfully");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean isConnected() {
        if (session == null) {
            return false;
        }
        return session.isConnected();
    }

    @Override
    public boolean rename(String remoteDir, String ftpSrcFile, String ftpDesFile) throws IOException, Exception {
        if (session == null || !session.isConnected()) {
            connect();
        }
        try {
            ChannelSftp channelSftp = getChannelSftp();
            channelSftp.cd(remoteDir);
            channelSftp.rename(ftpSrcFile, ftpDesFile);
            return true;
        } catch (JSchException | SftpException je) {
            je.printStackTrace();
            return false;
        }
    }

    @Override
    public FTPFile[] listFile(String remoteDir) throws Exception {
        refresh();
        FTPFile[] arrFile;
        ChannelSftp channelSftp = getChannelSftp();
        Vector<ChannelSftp.LsEntry> v = channelSftp.ls(remoteDir);
        int i = 0;
        arrFile = new FTPFile[v.size()];
        for (ChannelSftp.LsEntry entry : v) {
            FTPFile f = getFTPFileFromEntry(entry);
            arrFile[i] = f;
            i++;
        }
        return arrFile;
    }

    @Override
    public boolean download(String remoteDir, String expectFile, String desFile, boolean zip) throws FileNotFoundException, Exception {
        try {
            refresh();
            ChannelSftp channelSftp = getChannelSftp();
            channelSftp.cd(remoteDir);
            if (zip) {
                BufferedOutputStream fileOutputStream = null;
                GZIPOutputStream zipOutputStream = null;
                try {
                    fileOutputStream = new BufferedOutputStream(new FileOutputStream(desFile + ".zip.tmp"));
                    zipOutputStream = new GZIPOutputStream(fileOutputStream);
                    InputStream inputStream = channelSftp.get(expectFile);
                    long result = IOUtils.copy(inputStream, zipOutputStream);
                    if (result > -1) {
                        zipOutputStream.flush();
                        File f = new File(desFile + ".zip.tmp");
                        if (f.renameTo(new File(desFile + ".zip"))) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return false;
                } finally {
                    if (zipOutputStream != null) {
                        zipOutputStream.flush();
                        zipOutputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                }
            } else {
                channelSftp.get(expectFile, desFile);
                return true;
            }
        } catch (JSchException | SftpException | IOException je) {
            getLogger().error(je);
            getLogger().error("Download file exception: " + je.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String remoteDir, String ftpSrcFile) throws Exception {
        try {
            ChannelSftp channelSftp = getChannelSftp();
            channelSftp.cd(remoteDir);
            channelSftp.rm(ftpSrcFile);
            return true;
        } catch (JSchException | SftpException je) {
            je.printStackTrace();
            throw je;
//            return false;
        }
    }

    private void refresh() throws Exception {
        if (session == null || !session.isConnected()) {
            connect();
        }
    }

    private ChannelSftp getChannelSftp() throws JSchException, Exception {
        refresh();
        Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp channelSftp = (ChannelSftp) channel;
        return channelSftp;
    }

    private Calendar getCalFromSeconds(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time * 1000L);
        return cal;
    }

    private FTPFile getFTPFileFromEntry(ChannelSftp.LsEntry entry) {
        FTPFile f = new FTPFile();
        SftpATTRS attr = entry.getAttrs();
        f.setName(entry.getFilename());
        f.setTimestamp(getCalFromSeconds(attr.getMTime()));
        f.setSize(attr.getSize());
        return f;
    }

    @Override
    public boolean cd(String dir) throws IOException, Exception {
        try {
            ChannelSftp channelSftp = getChannelSftp();
            channelSftp.cd(dir);
            return true;
        } catch (JSchException | SftpException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean upload(String remoteTempDir, String remoteDir, String expectFile, String srcFile, boolean zip) throws Exception {
        ChannelSftp channel = getChannelSftp();
        channel.put(new FileInputStream(srcFile), remoteTempDir + File.separator + expectFile);
        channel.rename(remoteTempDir + File.separator + expectFile, remoteDir + File.separator + expectFile);
        return true;
    }

}
