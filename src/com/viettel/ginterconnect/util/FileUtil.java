/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author mr.X
 */
public class FileUtil {

    /**
     * Fast & simple file copy.
     */
    public static void copy(File source, File dest) throws IOException {
        FileChannel in = null, out = null;
        try {
            in = new FileInputStream(source).getChannel();
            out = new FileOutputStream(dest).getChannel();
            long size = in.size();
            MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);
            out.write(buf);
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Fast & simple file copy.
     */
    public static void copy(String sourceFileName, String destFileName) throws IOException {
        File source = new File(sourceFileName);
        File dest = new File(destFileName);
        copy(source, dest);
    }

    /**
     * Fast & simple file copy.
     */
    public static void copy(String sourceFileName, File dest) throws IOException {
        File source = new File(sourceFileName);
        copy(source, dest);
    }

    /**
     * Fast & simple file copy.
     */
    public static void copy(File source, String destFileName) throws IOException {
        File dest = new File(destFileName);
        copy(source, dest);
    }

    //AnhNT161 added 20140107
    public static boolean moveFileToFolder(String sourcePath, String fileName, String desPath, Logger logger) {
        try {
            File toFile = new File(desPath + File.separator + fileName);
            File fromFile = new File(sourcePath + File.separator + fileName);
            while (fromFile.exists()) {
                if (toFile.exists()) {
                    toFile.delete();
                } else {
                    if (fromFile.renameTo(new File(desPath + File.separator + fileName))) {
                        logger.info(fileName + " was moved from " + sourcePath + " to " + desPath);
                    } else {
                        logger.error(fileName + " was moved error from " + sourcePath + " to " + desPath);
                    }
                }
                fromFile = new File(sourcePath + File.separator + fileName);
            }
            return true;
        } catch (Exception e) {
            logger.error("Error while moving " + fileName + " from " + sourcePath + " to " + desPath + ". Reason: " + e.getMessage());
            return false;
        }
    }

    public static void deleteFolder(String folder) throws Exception {
        File tmp = null;
        try {
            File f = new File(folder);
            if (f.isDirectory()) {
                for (String file : f.list()) {
                    tmp = new File(f.getPath(), file);
                    deleteFolder(tmp.getPath());
                }
                do {
                    f.delete();
                } while (f.exists());
            } else {
                do {
                    f.delete();
                } while (f.exists());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public static void deleteFilesInFolder(String folder) {
        boolean isDeleted = false;
        File f;
        try {
            File fo = new File(folder);
            if (fo.isDirectory()) {
                String[] fileList = fo.list();
                for (String file : fileList) {
                    f = new File(folder, file);
                    if (f.isDirectory()) {
                        deleteFolder(f.getPath());
//                        System.out.println("Deleted folder: " + f.getPath());
                    } else {
                        while (!isDeleted) {
                            isDeleted = f.delete();
//                            System.out.println("Deleted file: " + f.getPath());
                        }
                    }
                }
//                System.out.println("Deleted all files in " + folder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void moveFilesFromFolderToFolder(String sourceFolder, String desFolder, Long fileId) {
        try {
            File sfo = new File(sourceFolder);
            File dfo = new File(desFolder);
            String desFileName = new String();
            if (sfo.isDirectory() && dfo.isDirectory()) {
                String[] fileList = sfo.list();
                for (String file : fileList) {
                    desFileName = file;
                    if (fileId != null) {
                        String fileIdString = Constants.STANDARD_FILEID_PREFIX + fileId.toString();
                        desFileName = fileIdString.substring(fileIdString.length() - Constants.STANDARD_FILEID_LENGTH) + "_" + file;
                    }
                    File toFile = new File(desFolder, desFileName);
                    File fromFile = new File(sourceFolder, file);
                    while (fromFile.exists()) {
                        if (toFile.exists()) {
                            toFile.delete();
                        } else {
                            fromFile.renameTo(toFile);
                        }
                        fromFile = new File(sourceFolder, file);
                    }
//                    System.out.println(file + " was moved from " + sourceFolder + " to " + desFolder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void processFileDir(String path, final String desFolder, final Long fileId, List<String> lstFile) throws Exception {
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) {
            return;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                processFileDir(f.getAbsolutePath(), desFolder, fileId, lstFile);
            } else {
                String desFileName = f.getName();
                lstFile.add(desFileName);
                File toFile = new File(desFolder, desFileName);
                File fromFile = new File(path, desFileName);
                try {
                    FileUtils.moveFile(fromFile, toFile);
                } catch (FileExistsException fee) {}
            }
        }
    }

    public static List<String> moveDecompressFile(String sourceFolder, String desFolder, Long fileId) {
        List<String> lstFileName = new ArrayList<>();
        try {
            File sfo = new File(sourceFolder);
            File dfo = new File(desFolder);
            String desFileName = new String();
            if (sfo.isDirectory() && dfo.isDirectory()) {
                String[] fileList = sfo.list();
                for (String file : fileList) {
                    desFileName = file;
                    lstFileName.add(desFileName);
                    if (fileId != null) {
                        String fileIdString = Constants.STANDARD_FILEID_PREFIX + fileId.toString();
                        desFileName = fileIdString.substring(fileIdString.length() - Constants.STANDARD_FILEID_LENGTH) + "_" + file;
                    }
                    File toFile = new File(desFolder, desFileName);
                    File fromFile = new File(sourceFolder, file);
                    while (fromFile.exists()) {
                        if (toFile.exists()) {
                            toFile.delete();
                        } else {
                            fromFile.renameTo(toFile);
                        }
                        fromFile = new File(sourceFolder, file);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lstFileName;
    }
    //AnhNT161 end

    public static boolean mkdirs(String... dirs) {
        try {
            for (String dir : dirs) {
                File f = new File(dir);
                if (!f.exists()) {
                    if (!f.mkdirs()) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean moveFileJava(String srcDir, String fileName, String desDir, Logger logger) {
        if ("*".equals(fileName)) {
            File dir = new File(srcDir);
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : dir.listFiles()) {
                    File f1 = new File(srcDir + File.separator + file.getName());
                    File f2 = new File(desDir + File.separator + file.getName());
                    try {
                        FileUtils.moveFile(f1, f2);
                    } catch (IOException ex) {
                        logger.error(ex.getMessage(), ex);
                        return false;
                    }
                }
            }
            return true;
        } else {
            File f1 = new File(srcDir + File.separator + fileName);
            File f2 = new File(desDir + File.separator + fileName);
            try {
                FileUtils.moveFile(f1, f2);
                return true;
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
            return false;
        }
    }

    public static boolean moveFileCmd(String srcDir, String fileName, String desDir, Logger logger) {
        Process p = null;
        Runtime run = Runtime.getRuntime();
        try {
            p = run.exec("mv \"" + srcDir + File.separator + fileName + "\" \"" + desDir + "\"");
            InputStream is = p.getErrorStream();
            p.waitFor();
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage(), e);
            logger.error("ERROR.RUNNING.CMD");
        } finally {
            if (p != null) {
                p.destroy();
            }
        }
        return true;
    }

    public static boolean moveAllGrandChildFile(String tempDir, String output) throws Exception {
        File[] childs = new File(tempDir).listFiles();
        if (childs != null) {
            for (File file : childs) {
                if (file.isDirectory()) {
                    File[] grandChilds = file.listFiles();
                    if (grandChilds != null) {
                        for (File grandChild : grandChilds) {
                            try {
                                FileUtils.moveFile(grandChild, new File(output + File.separator + grandChild.getName()));
                            } catch (Exception ex) {
                                if (ex instanceof FileExistsException) {
                                    FileUtils.deleteQuietly(grandChild);
                                } else {
                                    throw ex;
                                }
                            }
                        }
                    }
                    FileUtils.deleteDirectory(file);
                }
            }
        }
        return true;
    }
}
