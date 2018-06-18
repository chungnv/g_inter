/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.compress;

import com.viettel.ginterconnect.util.Constants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.SevenZipNativeInitializationException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import org.apache.log4j.Logger;
import org.openide.util.Exceptions;

/**
 *
 * @author
 */
public class FileCodecer {

    private Logger logger;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public FileCodecer() {
        try {
            SevenZip.initSevenZipFromPlatformJAR();
        } catch (SevenZipNativeInitializationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    // ubuntu2, 20141013: them password
    public void extract(final String filePath, final String extractPath, final Long fileId, final Long isAddFileId, final String password) throws Exception {
        File input = new File(filePath);
        File output = new File(extractPath);
        if (input.exists() && input.isFile()) {
            extract(input, output, fileId, isAddFileId, password);
        }
    }

    // ubuntu2, 20141013: them password
    public void extractMultiple(final String filePath, final String extractPath, final Long fileId, final Long isAddFileId, final String password) throws Exception {
        File input = new File(filePath);
        File output = new File(extractPath);
        if (input.exists() && input.isFile()) {
            extractMultiple(input, output, fileId, isAddFileId, password);
        }
    }

    // ubuntu2, 20141013: them password
    public void extract(final File input, final File output, final Long fileId, final Long isAddFileId, final String password) throws Exception {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        final String fileName = input.getName().substring(0,
                input.getName().lastIndexOf("."));
        try {
            logger.info("Extracting file " + input.getName());
//            System.out.println("Extracting file " + input.getName());
            randomAccessFile = new RandomAccessFile(input, "r");
            inArchive = SevenZip.openInArchive(null,
                    new RandomAccessFileInStream(randomAccessFile));

            // Getting simple interface of the archive inArchive
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            ExtractOperationResult result;
            for (final ISimpleInArchiveItem item : simpleInArchive
                    .getArchiveItems()) {
                if (!item.isFolder()) {
                    String tmpFileName = null;
                    String tmpItemName = null;
                    if ((isAddFileId != null) && (isAddFileId.equals(Constants.ADD_FILE_ID))) {
                        String fileIdString = Constants.STANDARD_FILEID_PREFIX + fileId.toString();
                        tmpFileName = fileIdString.substring(fileIdString.length() - Constants.STANDARD_FILEID_LENGTH) + "_" + fileName;
                        tmpItemName = fileIdString.substring(fileIdString.length() - Constants.STANDARD_FILEID_LENGTH) + "_" + item.getPath().trim();
                    } else {
                        tmpFileName = fileName;
                        tmpItemName = item.getPath().trim();
                    }
                    final String name = (item.getPath() == null || "".equals(item.getPath().trim()))
                            ? tmpFileName : tmpItemName;
                    ISequentialOutStream outStream = new ISequentialOutStream() {
                        @Override
                        public int write(byte[] data) throws SevenZipException {
                            FileOutputStream fos = null;
                            try {
                                File path = new File(output, name);

                                if (!path.getParentFile().exists()) {
                                    path.getParentFile().mkdirs();
                                }

                                if (!path.exists()) {
                                    path.createNewFile();
                                }
                                fos = new FileOutputStream(path, true);
                                fos.write(data);
                            } catch (IOException e) {
                                logger.error("IOException while extracting " + name, e);
//                                System.out.println("IOException while extracting " + name);
                                throw new SevenZipException("", e);
                            } finally {
                                try {
                                    if (fos != null) {
                                        fos.flush();
                                        fos.close();
                                    }
                                } catch (IOException e) {
                                    logger.error("Could not close FileOutputStream", e);
//                                    System.out.println("Could not close FileOutputStream");
                                }
                            }
                            return data.length;
                        }
                    };
                    // kiem tra password cua file neu co
                    if (password == null || password.isEmpty()) {
                        result = item.extractSlow(outStream);
                    } else {
                        result = item.extractSlow(outStream, password);
                    }

                    if (result != ExtractOperationResult.OK) {
                        logger.info("\t\t Error: " + name);
//                        System.out.println("\t\t Error: " + name);
                        File path = new File(output, name);
                        do {
                            path.delete();
                        } while (path.exists());
                        throw new Exception();
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Error extracting file " + input.getName() + ": " + ex.getMessage());
//            System.out.println("Error extracting file " + input.getName() + ": " + ex.getMessage());
            throw ex;
        } finally {
            if (inArchive != null) {
                inArchive.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        }
    }

    // ubuntu2, 20141013: them password
    public void extractMultiple(final File input, final File output, final Long fileId, final Long isAddFileId, final String password) throws Exception {
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        final String fileName = input.getName().substring(0,
                input.getName().lastIndexOf("."));
        try {
            logger.info("Extracting file " + input.getName());
//            System.out.println("Extracting file " + input.getName());
            randomAccessFile = new RandomAccessFile(input, "r");
            inArchive = SevenZip.openInArchive(null,
                    new RandomAccessFileInStream(randomAccessFile));

            // Getting simple interface of the archive inArchive
            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            ExtractOperationResult result;

            for (final ISimpleInArchiveItem item : simpleInArchive
                    .getArchiveItems()) {
                if (!item.isFolder()) {
                    String tmpFileName = null;
                    String tmpItemName = null;
                    if ((isAddFileId != null) && (isAddFileId.equals(Constants.ADD_FILE_ID))) {
                        String fileIdString = Constants.STANDARD_FILEID_PREFIX + fileId.toString();
                        tmpFileName = fileIdString.substring(fileIdString.length() - Constants.STANDARD_FILEID_LENGTH) + "_" + fileName;
                        tmpItemName = fileIdString.substring(fileIdString.length() - Constants.STANDARD_FILEID_LENGTH) + "_" + item.getPath().trim();
                    } else {
                        tmpFileName = fileName;
                        tmpItemName = item.getPath().trim();
                    }
                    final String name = (item.getPath() == null || "".equals(item.getPath().trim()))
                            ? tmpFileName : tmpItemName;
                    ISequentialOutStream outStream = new ISequentialOutStream() {
                        @Override
                        public int write(byte[] data) throws SevenZipException {
                            FileOutputStream fos = null;
                            try {
                                File path = new File(output, name);

                                if (!path.getParentFile().exists()) {
                                    path.getParentFile().mkdirs();
                                }

                                if (!path.exists()) {
                                    path.createNewFile();
                                }
                                fos = new FileOutputStream(path, true);
                                fos.write(data);
                            } catch (IOException e) {
//                                logger.error("IOException while extracting " + name, e);
//                                System.out.println("IOException while extracting " + name);
                                throw new SevenZipException("", e);
                            } finally {
                                try {
                                    if (fos != null) {
                                        fos.flush();
                                        fos.close();
                                    }
                                } catch (IOException e) {
                                    logger.error("Could not close FileOutputStream", e);
//                                    System.out.println("Could not close FileOutputStream");
                                }
                            }
                            return data.length;
                        }
                    };

                    if (password == null || password.isEmpty()) {
                        result = item.extractSlow(outStream);
                    } else {
                        result = item.extractSlow(outStream, password);
                    }

                    if (result != ExtractOperationResult.OK) {
                        logger.info("\t\t Error: " + name);
//                        System.out.println("\t\t Error: " + name);
                        File path = new File(output, name);
                        do {
                            path.delete();
                        } while (path.exists());
                        throw new Exception();
                    } else {
//                        isDelete = false;
                    }
                }
            }
        } catch (Exception ex) {
//            logger.error("Error extracting file " + input.getName() + ": " + ex.getMessage());
//            System.out.println("Error extracting file " + input.getName() + ": " + ex.getMessage());
            throw ex;
        } finally {
            if (inArchive != null) {
                inArchive.close();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
//            if (!isDelete) {
//                while (!isDelete) {
//                    isDelete = input.delete();
//                    System.out.println("Extract file " + input.getName() + " successfully, delete file!");
//                }
//            }
        }
    }
//    public static void extractFolder(final String fileFolder, final String extractedFolder) throws Exception {
//        File input = new File(fileFolder);
//        File output = new File(extractedFolder);
//        if (!output.exists()) {
//            output.mkdirs();
//        } else if (!output.isDirectory()) {
//            return;
//        }
//        File tmpFolder = new File(extractedFolder, "tmp");
//        if (!tmpFolder.exists()) {
//            tmpFolder.mkdir();
//        }
//        tmpFolder.mkdir();
//        File bakFolder = new File(input, "bak");
//        if (!bakFolder.exists()) {
//            bakFolder.mkdir();
//        }
//        if (input.exists() && input.isDirectory()) {
//            File[] lstFiles = input.listFiles();
//            for (File file : lstFiles) {
//                if (file.isFile() && isCompressedFile(file)) {
//                    if (!file.getName().endsWith(".tar.gz")) {
//                        extract(file, output);
//                    } else {
//                        extract(file, tmpFolder);
//                    }
//                    file.renameTo(new File(bakFolder, file.getName()));
//                }
//            }
//        }
//        //Extract from tmp
//        File[] lstFiles = tmpFolder.listFiles();
//        for (File file : lstFiles) {
//            if (file.isFile() && isCompressedFile(file)) {
//                extract(file, output);
//                file.delete();
//            }
//        }
//        tmpFolder.delete();
//    }
//
//    private static boolean isCompressedFile(File file) {
////        String fileName = file.getName();
////        String suffix = fileName.substring(fileName.lastIndexOf("."));
////        if (lstFileFormat.contains(suffix.toLowerCase())) {
////            return true;
////        }
////        return false;
//        return true;
//    }
}
