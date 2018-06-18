/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.master.get;

import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.ParseFileNameStandard;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.hibernate.Session;

/**
 *
 * @author
 */
public class GetFileUtils {

    public static int findExpectedFileMissByIndex(ParseFileNameStandard parseFile, Long missedSeq, Logger logger, final FTPFile[] files) {
        int expectFileIndex = -1;
        if (files == null) {
            return expectFileIndex;
        }
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (parseFile.Parse(fileName)) {
                if (parseFile.isIsSeq() && parseFile.getSeq() == missedSeq) {
                    expectFileIndex = i;
                    break;
                }
            }
        }
        if (expectFileIndex > -1) {
            parseFile.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
        }
        return expectFileIndex;
    }

    public static int findExpectedFileMissByTimeAndSeq(ParseFileNameStandard parseFile, Logger logger, final FTPFile[] files, Long dateMiss, Long seqMiss) {
        int expectFileIndex = -1;
        if (files == null) {
            return expectFileIndex;
        }
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (parseFile.Parse(fileName)) {
                if (parseFile.isIsSeq() && parseFile.isIsTimestamp()
                        && parseFile.getTimestamp().getTime() == dateMiss
                        && parseFile.getSeq() == seqMiss) {
                    expectFileIndex = i;
                    break;
                }
            }
        }
        if (expectFileIndex > -1) {
            parseFile.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
        }
        return expectFileIndex;
    }

    public static int byJustMatchPattern(FTPFile[] files, Date curTimestamp, ParseFileNameStandard parseFileName, Logger logger) {
        int expectFileIndex = -1;
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (parseFileName.Parse(fileName)) {
                expectFileIndex = i;
                break;
            }
        }

        if (expectFileIndex != -1) {
            parseFileName.Parse(files[expectFileIndex].getName());
            parseFileName.setTimestamp(files[expectFileIndex].getTimestamp().getTime());
            parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
        }
        return expectFileIndex;
    }

    public static int byModifiedTimeAndMinSequence(FTPFile[] files, Date curTimestamp, ParseFileNameStandard parseFileName, Long curSeq, Long maxSequence, Logger logger) {
        int expectFileIndex = -1;
        List<Long> afterTimeList = new ArrayList<>();
        List<Date> afterTimeStamp = new ArrayList<>();
        List<Long> afterTimeSeq = new ArrayList<>();
        List<Long> equalTimeList = new ArrayList<>();
        List<Long> equalTimeSeq = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            //read all File has timestamp >= cur TimeStamp to List
            //phai lay file nho nhat
            String fileName = files[i].getName();
            if (parseFileName.Parse(fileName)) {
                if (files[i].getTimestamp() != null && files[i].getTimestamp().getTime() != null) {
                    if (curTimestamp.getTime() < files[i].getTimestamp().getTime().getTime()) {
                        afterTimeList.add(new Long(i));
                        afterTimeStamp.add(files[i].getTimestamp().getTime());
                        afterTimeSeq.add(parseFileName.getSeq());
                    } else if (curTimestamp.getTime() == files[i].getTimestamp().getTime().getTime()) {
                        equalTimeList.add(new Long(i));
                        equalTimeSeq.add(parseFileName.getSeq());
                    }
                }
            }
        }
        //trong nhung file co timestamp = curTimestamp thi lay nhung file co 
        //sequence nho nhat thoa man >= curSeq
        int minSeq = -1;
        for (int i = 0; i < equalTimeSeq.size(); i++) {
            if (equalTimeSeq.get(i).intValue() >= curSeq.intValue()
                    && equalTimeSeq.get(i).longValue() != maxSequence) {
                if (minSeq == -1) {
                    expectFileIndex = equalTimeList.get(i).intValue();
                    minSeq = equalTimeSeq.get(i).intValue();
                    //break;
                } else if (minSeq > equalTimeSeq.get(i).intValue()) {
                    expectFileIndex = equalTimeList.get(i).intValue();
                    minSeq = equalTimeSeq.get(i).intValue();
                }
            }
        }

        //trong truong hop chua tim duoc file can thiet bang cach lay cac file cung thoi gian last modified
        //thi lay file co thoi gian last modified sau curTimeStamp va lay file co sequence nho nhat
        if (minSeq == -1) {
            // Tim kiem sequence co last modfied sau no va seq >= curSeq va be nhat co the
            for (int i = 0; i < afterTimeStamp.size(); i++) {
                if (minSeq == -1) {
                    minSeq = afterTimeSeq.get(i).intValue();
                    expectFileIndex = afterTimeList.get(i).intValue();
                } else if (minSeq > afterTimeSeq.get(i)) {
                    minSeq = afterTimeSeq.get(i).intValue();
                    expectFileIndex = afterTimeList.get(i).intValue();
                }
            }
        }

        if (expectFileIndex != -1) {
            parseFileName.Parse(files[expectFileIndex].getName());
            parseFileName.setTimestamp(files[expectFileIndex].getTimestamp().getTime());
            parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
        }
        return expectFileIndex;
    }

    public static int bySequenceFilenameTime(FTPFile[] files, Date curTimestamp, ParseFileNameStandard parseFileName, Long curSeq, Long maxSequence, Logger logger) {
        int expectFileIndex = -1;
        if (parseFileName.isIsSeq() && parseFileName.isIsTimestamp()) {
            long minSeq = -1;
            Date minTimestamp = null;
            //Tim kiem seq nho nhat thoa man yeu cau
            for (int i = 0; i < files.length; i++) {

                String fileName = files[i].getName();
                if (parseFileName.Parse(fileName)) {
                    if (parseFileName.getTimestamp().getTime() > curTimestamp.getTime() || (parseFileName.getTimestamp().getTime() == curTimestamp.getTime() && parseFileName.getSeq() >= curSeq)) {
                        if (minTimestamp == null || minSeq == -1) {
                            expectFileIndex = i;
                            minSeq = parseFileName.getSeq();
                            minTimestamp = parseFileName.getTimestamp();
                        } else if (parseFileName.getTimestamp().getTime() < minTimestamp.getTime()) {
                            expectFileIndex = i;
                            minSeq = parseFileName.getSeq();
                            minTimestamp = parseFileName.getTimestamp();
                        } else if (parseFileName.getTimestamp().getTime() == minTimestamp.getTime() && parseFileName.getSeq() < minSeq) {
                            expectFileIndex = i;
                            minSeq = parseFileName.getSeq();
                            minTimestamp = parseFileName.getTimestamp();
                        }
                    }
                }
            }
            if (expectFileIndex != -1) {
                parseFileName.setTimestamp(minTimestamp);
                parseFileName.setSeq(minSeq);
                parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
            }
        } else if (parseFileName.isIsSeq() && !parseFileName.isIsTimestamp()) {

            // chi tim theo sequence
            //Tim kiem seq nho nhat ma lon hon hoac bangcurSeq thoa man yeu cau
            //Chi cho phep get theo sequence tang
            long minSeq = -1;
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (parseFileName.Parse(fileName)) {
                    if ((parseFileName.getSeq() >= curSeq) && (parseFileName.getSeq() <= maxSequence)) {
                        if (minSeq == -1) {
                            expectFileIndex = i;
                            minSeq = parseFileName.getSeq();
                        } else if (parseFileName.getSeq() < minSeq) {
                            expectFileIndex = i;
                            minSeq = parseFileName.getSeq();
                        }
                    }
                }
            }
            if (expectFileIndex != -1) {
//                    parseFileName.Parse(files[expectFileIndex].getName());
                parseFileName.setSeq(minSeq);
                parseFileName.setTimestamp(files[expectFileIndex].getTimestamp().getTime());
                parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
            }

        } else if (parseFileName.isIsTimestamp() && !parseFileName.isIsSeq()) {

            Date minTimestamp = null;

            //Tim kiem seq nho nhat thoa man yeu cau
            for (int i = 0; i < files.length; i++) {

                String fileName = files[i].getName();
                if (parseFileName.Parse(fileName)) {
                    if (parseFileName.getTimestamp().getTime() > curTimestamp.getTime()) {
                        if (minTimestamp == null) {
                            expectFileIndex = i;
                            minTimestamp = parseFileName.getTimestamp();
                        } else if (parseFileName.getTimestamp().getTime() < minTimestamp.getTime()) {
                            expectFileIndex = i;
                            minTimestamp = parseFileName.getTimestamp();
                        }
                    }
                }
            }

            if (expectFileIndex != -1) {
                parseFileName.Parse(files[expectFileIndex].getName());
                parseFileName.setTimestamp(minTimestamp);
                parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
            }
        }
        return expectFileIndex;
    }

    public static int byNextSequence(FTPFile[] files, Date curTimestamp, ParseFileNameStandard parseFileName, Long curSeq, Long maxSequence, Logger logger) {
        int expectFileIndex = -1;
        int minSeq = -1;
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (parseFileName.Parse(fileName)) {
                if (parseFileName.getSeq() == curSeq && parseFileName.getSeq() <= maxSequence) {
                    expectFileIndex = i;
                    minSeq = curSeq.intValue();
                    break;
                }
            }
        }

        if (expectFileIndex != -1) {
            parseFileName.setSeq(minSeq);
            parseFileName.setTimestamp(files[expectFileIndex].getTimestamp().getTime());
            parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
        }
        return expectFileIndex;
    }

    public static int bySequenceOnly(FTPFile[] files, Date curTimestamp, ParseFileNameStandard parseFileName, Long curSeq, Long maxSequence, Logger logger) {
        int expectFileIndex = -1;
        if (files == null) {
            return expectFileIndex;
        }
        long minSeq = -1;
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (parseFileName.Parse(fileName)) {
                if (parseFileName.getSeq() >= curSeq && parseFileName.getSeq() <= maxSequence) {
                    if (minSeq == -1) {
                        expectFileIndex = i;
                        minSeq = parseFileName.getSeq();
                    } else if (parseFileName.getSeq() < minSeq) {
                        expectFileIndex = i;
                        minSeq = parseFileName.getSeq();
                    }
                }
            }
        }

        if (expectFileIndex != -1) {
            parseFileName.setSeq(minSeq);
            parseFileName.setTimestamp(files[expectFileIndex].getTimestamp().getTime());
            parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
        }
        return expectFileIndex;
    }

    public static int byNoPriority(FTPFile[] files, Date curTimestamp, ParseFileNameStandard parseFileName, Long curSeq, Long maxSequence, Logger logger, Long getMinSeq) {
        int expectFileIndex = -1;
        if (files == null) {
            return expectFileIndex;
        }
        List<Long> afterTimeList = new ArrayList<>();
        List<Date> afterTimeStamp = new ArrayList<>();
        List<Long> afterTimeSeq = new ArrayList<>();
        List<Long> equalTimeList = new ArrayList<>();
        List<Long> equalTimeSeq = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            //read all File has timestamp >= cur TimeStamp to List
            String fileName = files[i].getName();
            if (parseFileName.Parse(fileName)) {
                if (parseFileName.getTimestamp() != null) {
                    if (curTimestamp.getTime() < parseFileName.getTimestamp().getTime()) {
                        afterTimeList.add(new Long(i));
                        afterTimeStamp.add(parseFileName.getTimestamp());
                        afterTimeSeq.add(parseFileName.getSeq());
                    } else if (curTimestamp.getTime() == parseFileName.getTimestamp().getTime()) {
                        equalTimeList.add(new Long(i));
                        equalTimeSeq.add(parseFileName.getSeq());
                    }
                } //Neu nhu thoi gian lay file ko duoc set trong ten file thi lay last modify
                else {
                    if (files[i].getTimestamp() != null && files[i].getTimestamp().getTime() != null) {
                        if (curTimestamp.getTime() < files[i].getTimestamp().getTime().getTime()) {
                            afterTimeList.add(new Long(i));
                            afterTimeStamp.add(files[i].getTimestamp().getTime());
                            afterTimeSeq.add(parseFileName.getSeq());
                        } else if (curTimestamp.getTime() == files[i].getTimestamp().getTime().getTime()) {
                            equalTimeList.add(new Long(i));
                            equalTimeSeq.add(parseFileName.getSeq());
                        }
                    }
                }
            }
        }

        //Tim kiem Seq gan nhat de tim ra Expected File
        long minSeq = -1;
        Date minTimeStamp = null;

        if (parseFileName.isIsSeq()) // Neu nhu trog template cua file co chua seq
        {
            if (parseFileName.isIsTimestamp()) //Neu nhu trong truong hop co config timeStamp trong ten cua file
            {
                //Tim kiem trong cac file co timestamp = voi cur Time Stamp tim ra seq gan no nhat
                //Tim kiem seq be nhat , be hon max seq va lon hon hoac bang no
                for (int i = 0; i < equalTimeSeq.size(); i++) {
                    if (minSeq == -1) {
                        if (equalTimeSeq.get(i) >= curSeq.intValue()) {
                            expectFileIndex = equalTimeList.get(i).intValue();
                            minSeq = equalTimeSeq.get(i).intValue();
                        }
                    } else {
                        if (equalTimeSeq.get(i).intValue() >= curSeq.intValue() && equalTimeSeq.get(i).intValue() < minSeq) {
                            expectFileIndex = equalTimeList.get(i).intValue();
                            minSeq = equalTimeSeq.get(i).intValue();
                        }
                    }
                }
                //        //Neu khong tim thay trong cac seq lon hon no thi tim seq be nhat
                if (minSeq != -1) {
                    minTimeStamp = curTimestamp;
                }
                // Neu van chua tim thay trong cac gia tri co thoi gian ngang nhau tim trong cac gia tri co thoi gian lon hon
                // Tim kiem thoi gian be nhat
                if (minSeq == -1) {
                    for (int i = 0; i < afterTimeList.size(); i++) {
                        if (minTimeStamp == null) {
                            minTimeStamp = afterTimeStamp.get(i);
                        } else {
                            if (afterTimeStamp.get(i).getTime() < minTimeStamp.getTime()) {
                                minTimeStamp = afterTimeStamp.get(i);
                            }
                        }
                    }
                    if (minTimeStamp != null) {
                        //Tim cac file co thoi gian bang minTimeStamp va seq nho nhat va lon hon hoac bang seq hien tai.
                        for (int i = 0; i < afterTimeStamp.size(); i++) {
                            if (minTimeStamp.getTime() == afterTimeStamp.get(i).getTime()) {
                                if (minSeq == -1L) {
                                    if (afterTimeSeq.get(i) >= curSeq) {
                                        minSeq = afterTimeSeq.get(i).intValue();
                                        expectFileIndex = afterTimeList.get(i).intValue();
                                    }
                                } else {
                                    if (afterTimeSeq.get(i) >= curSeq && minSeq > afterTimeSeq.get(i)) {
                                        minSeq = afterTimeSeq.get(i).intValue();
                                        expectFileIndex = afterTimeList.get(i).intValue();
                                    }
                                }
                            }
                        }
                        //Neu van chua tim duoc thi tim kiem seq nho nhat co cung thoi gian nay
                        if (minSeq == -1) {
                            for (int i = 0; i < afterTimeStamp.size(); i++) {
                                if (minTimeStamp.getTime() == afterTimeStamp.get(i).getTime()) {
                                    if (minSeq == -1) {
                                        if (afterTimeSeq.get(i) < curSeq && afterTimeSeq.get(i) >= getMinSeq) {
                                            minSeq = afterTimeSeq.get(i).intValue();
                                            expectFileIndex = afterTimeList.get(i).intValue();
                                        }
                                    } else {
                                        if (afterTimeSeq.get(i) < curSeq && minSeq > afterTimeSeq.get(i) && afterTimeSeq.get(i) >= getMinSeq) {
                                            minSeq = afterTimeSeq.get(i).intValue();
                                            expectFileIndex = afterTimeList.get(i).intValue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } //trong truong hop nguoc lai khong co config timestamp trong ten file khi do thi can xet uu tien sequence truoc
            else {
                // check truoc cac file co thoi gian trung voi thoi gian hien tai.
                // doi voi cac file co thoi gian trung voi thoi gian hien tai chi lay cac file lien ke ngay sau gan no.
                //tuc la file dung voi curSeq.
//                can sua cho nay
                for (int i = 0; i < equalTimeSeq.size(); i++) {
                    if (equalTimeSeq.get(i).intValue() >= curSeq.intValue()
                            && equalTimeSeq.get(i).longValue() != maxSequence) {
                        expectFileIndex = equalTimeList.get(i).intValue();
                        minSeq = equalTimeSeq.get(i).intValue();
                        break;
                    }
                }

                //trong truong hop chua tim duoc file can thiet bang cach lay cac file cung thoi gian last modified
                //thi lay file co thoi gian last modified sau curTimeStamp
                if (minSeq == -1) {
                    // Tim kiem sequence co last modfied sau no va seq >= curSeq va be nhat co the
                    for (int i = 0; i < afterTimeStamp.size(); i++) {
                        if (minSeq == -1) {
                            if (afterTimeSeq.get(i) >= curSeq) {
                                minSeq = afterTimeSeq.get(i).intValue();
                                expectFileIndex = afterTimeList.get(i).intValue();
                            }
                        } else {
                            if (afterTimeSeq.get(i) >= curSeq && minSeq > afterTimeSeq.get(i)) {
                                minSeq = afterTimeSeq.get(i).intValue();
                                expectFileIndex = afterTimeList.get(i).intValue();
                            }
                        }
                    }
                }

                //Neu van chua tim duoc seq thi tim kiem seq nho hon cursequence va lon nhat co the
                for (int i = 0; i < afterTimeStamp.size(); i++) {
                    if (minSeq == -1) {
                        if (afterTimeSeq.get(i) < curSeq) {
                            minSeq = afterTimeSeq.get(i).intValue();
                            expectFileIndex = afterTimeList.get(i).intValue();
                        }
                    } else {
                        if (afterTimeSeq.get(i) < curSeq && minSeq < afterTimeSeq.get(i)) {
                            minSeq = afterTimeSeq.get(i).intValue();
                            expectFileIndex = afterTimeList.get(i).intValue();
                        }
                    }
                }

            }
        } else //Neu trong truong hop khong chua seq trong template
        {
            //Chi tinh file co thoi gian nho nhat ma lon hon thoi gian hien tai
            for (int i = 0; i < afterTimeList.size(); i++) {
                if (minTimeStamp == null) {
                    if (afterTimeStamp.get(i).getTime() > curTimestamp.getTime()) {
                        minTimeStamp = afterTimeStamp.get(i);
                        expectFileIndex = afterTimeList.get(i).intValue();
                    }
                } else {
                    if (afterTimeStamp.get(i).getTime() > curTimestamp.getTime() && minTimeStamp.getTime() > afterTimeStamp.get(i).getTime()) {
                        minTimeStamp = afterTimeStamp.get(i);
                        expectFileIndex = afterTimeList.get(i).intValue();
                    }
                }
            }
        }
        parseFileName.setSeq(minSeq);
        if (minSeq == -1) {
            parseFileName.setSeq(0);
        }
        if (minSeq != -1 && minSeq > curSeq.intValue()) {
            logger.warn(String.format("No file sequence: " + curSeq));
        }
        if (expectFileIndex != -1) {
            if (minTimeStamp != null) {
                parseFileName.setTimestamp(minTimeStamp);
            } else {
                parseFileName.setTimestamp(files[expectFileIndex].getTimestamp().getTime());
            }
        } else {
            parseFileName.setTimestamp(null);
        }

        if (expectFileIndex != -1) {
            parseFileName.setSeq(minSeq);
            parseFileName.setTimestamp(files[expectFileIndex].getTimestamp().getTime());
            parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
        }
        return expectFileIndex;
    }

    public static int bySequenceAndModifiedTime(FTPFile[] files, Date curTimestamp, ParseFileNameStandard parseFileName, Long curSeq, Long maxSequence, Logger logger) {
        int expectFileIndex = -1;
        Date minTimestamp = null;
        List<Long> equalTimeList = new ArrayList<>();
        List<Long> equalTimeSeq = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            //read all File has timestamp >= cur TimeStamp to List
            //phai lay file nho nhat
            String fileName = files[i].getName();
            if (parseFileName.Parse(fileName)) {
                if (files[i].getTimestamp() != null && files[i].getTimestamp().getTime() != null
                        && files[i].getTimestamp().getTime().getTime() > curTimestamp.getTime()) {
                    if (minTimestamp == null) {
                        minTimestamp = files[i].getTimestamp().getTime();
                    } else if (minTimestamp.getTime() >= files[i].getTimestamp().getTime().getTime()) {
                        minTimestamp = files[i].getTimestamp().getTime();
                    }
                } else if (files[i].getTimestamp() != null && files[i].getTimestamp().getTime() != null
                        && files[i].getTimestamp().getTime().getTime() == curTimestamp.getTime()) {
                    equalTimeList.add(new Long(i));
                    equalTimeSeq.add(parseFileName.getSeq());
                }
            }
        }

        long tmpMinSeq = -1;
        for (int i = 0; i < equalTimeList.size(); i++) {
            if (equalTimeSeq.get(i) >= curSeq) {
                if (tmpMinSeq == -1) {
                    tmpMinSeq = equalTimeSeq.get(i);
                    expectFileIndex = equalTimeList.get(i).intValue();
                    minTimestamp = files[expectFileIndex].getTimestamp().getTime();
                } else if (tmpMinSeq > equalTimeSeq.get(i)) {
                    tmpMinSeq = equalTimeSeq.get(i);
                    expectFileIndex = equalTimeList.get(i).intValue();
                    minTimestamp = files[expectFileIndex].getTimestamp().getTime();
                }
            }
        }

        if (expectFileIndex == -1) {
            if (minTimestamp != null && minTimestamp.getTime() > curTimestamp.getTime()) {
                tmpMinSeq = -1;
                for (int i = 0; i < files.length; i++) {
                    String fileName = files[i].getName();
                    if (parseFileName.Parse(fileName)) {
                        if (files[i].getTimestamp() != null && files[i].getTimestamp().getTime() != null
                                && files[i].getTimestamp().getTime().getTime() == minTimestamp.getTime()
                                && parseFileName.getSeq() >= curSeq) {
                            if (tmpMinSeq == -1) {
                                expectFileIndex = i;
                                tmpMinSeq = parseFileName.getSeq();
                            } else if (tmpMinSeq > parseFileName.getSeq()) {
                                expectFileIndex = i;
                                tmpMinSeq = parseFileName.getSeq();
                            }
                        }
                    }
                }
            }
        }

        if (expectFileIndex != -1) {
            parseFileName.setTimestamp(files[expectFileIndex].getTimestamp().getTime());
            parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
        }
        return expectFileIndex;
    }

    public static int byCheckFileName(FTPFile[] files, Date curTimestamp, ParseFileNameStandard parseFileName, Logger logger, Long switchId) {

        int expectFileIndex = -1;

        Session session = null;
        HashMap<String, List<String>> mapDateFile = new HashMap<>();

        try {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (!parseFileName.Parse(fileName)) {
                    continue;
                }
                //kiem tra xem file nay da duoc get chua bang cach kiem tra trong partition
                //lay modify time cua file
                String strDate = (new SimpleDateFormat("yyyyMMdd")).format(files[i].getTimestamp().getTime());
                List<String> lstGet;
                if (!mapDateFile.containsKey(strDate) || mapDateFile.get(strDate) == null) {
//                    String sql = "select ftp_path from md_getfile_log partition(data" + strDate + ") where switch_Id = " + switchId;
//                    if (session == null) {
//                        session = HibernateUtil.getSessionFactory().openSession();
//                    }
//                    lstGet = session.createSQLQuery(sql).addScalar("ftp_path", Hibernate.STRING).list();
//                    mapDateFile.put(strDate, lstGet);
                } else {
                    lstGet = mapDateFile.get(strDate);
                }

                boolean isGet = false;
//                for (String getfile : lstGet) {
//                    if (getfile.contains(fileName)) {
//                        isGet = true;
//                        break;
//                    }
//                }
                if (!isGet) {
                    expectFileIndex = i;
                    break;
                }
                Thread.sleep(100);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        if (expectFileIndex != -1) {
            parseFileName.Parse(files[expectFileIndex].getName());
            parseFileName.setTimestamp(files[expectFileIndex].getTimestamp().getTime());
            parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
        }
        return expectFileIndex;
    }

    private static int byModifiedTimeOnly(FTPFile[] files, Date curTimestamp, ParseFileNameStandard parseFileName) {
        Date minTimestamp = null;
        int expectFileIndex = -1;
        for (int i = 0; i < files.length; i++) {
            //read all File has timestamp >= cur TimeStamp to List
            //phai lay file nho nhat
            String fileName = files[i].getName();
            if (parseFileName.Parse(fileName)) {
                if (files[i].getTimestamp() != null && files[i].getTimestamp().getTime() != null
                        && files[i].getTimestamp().getTime().getTime() > curTimestamp.getTime()) {
                    if (minTimestamp == null) {
                        expectFileIndex = i;
                        minTimestamp = files[i].getTimestamp().getTime();
                    } else if (minTimestamp.getTime() > files[i].getTimestamp().getTime().getTime()) {
                        expectFileIndex = i;
                        minTimestamp = files[i].getTimestamp().getTime();
                    }
                }
            }
        }
        if (expectFileIndex != -1) {
            parseFileName.Parse(files[expectFileIndex].getName());
            parseFileName.setTimestamp(files[expectFileIndex].getTimestamp().getTime());
            parseFileName.setFileModifiedTime(files[expectFileIndex].getTimestamp().getTime());
        }
        return expectFileIndex;
    }

    public static List<Integer> listMatchPattern(FTPFile[] files, Date curTimestamp, ParseFileNameStandard parseFileName, Logger logger, long batchFile, List<String> blackListFiles) {
        List<Integer> lstMatchedFile = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (parseFileName.Parse(fileName)) {
                if (blackListFiles == null || !blackListFiles.contains(fileName)) {
                    lstMatchedFile.add(i);
                    if (lstMatchedFile.size() == batchFile) {
                        break;
                    }
                }
            }
        }
        return lstMatchedFile;
    }

    public static List<Integer> findListExpectedFile(Long curSeq, Long minSequence, Long maxSequence,
            Long stepSeq, long priorityType, FTPFile[] files,
            Date curTimestamp, ParseFileNameStandard parseFileName, Logger logger, Long switchId, long batchFile) {
        List<Integer> lstMatchedFile = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            String fileName = files[i].getName();
            if (parseFileName.Parse(fileName)) {
                lstMatchedFile.add(i);
                if (lstMatchedFile.size() == batchFile) {
                    break;
                }
            }
        }
        return lstMatchedFile;
    }

    public static List<Integer> findExpectFileIndex(Long curSeq, Long minSequence, Long maxSequence,
            Long stepSeq, long priorityType, FTPFile[] files,
            Date curTimestamp, ParseFileNameStandard parseFileName, Logger logger, Long switchId, long batchFile, List<String> blackListFiles) {

        int expectFileIndex = -1;
        List<Integer> lst = new ArrayList<>();
        if (files == null) {
            return lst;
        }
        if (priorityType == Constants.PRIORITY_MODIFIED_TIME) {
            expectFileIndex = byModifiedTimeOnly(files, curTimestamp, parseFileName);
        } else if (priorityType == Constants.PRIORITY_SEQUENCE_AND_MODIFIED_TIME) {
            //lay theo sequence, check theo ca modifiedTime
            //neu modifiedTime > cur, lay minSeq thoa man >= curSeq
            expectFileIndex = bySequenceAndModifiedTime(files, curTimestamp, parseFileName, curSeq, maxSequence, logger);
        } else if (priorityType == Constants.PRIORITY_JUST_MATCH_NAME) {
            //only use when active delete after get
            return listMatchPattern(files, curTimestamp, parseFileName, logger, batchFile, blackListFiles);
        } else if (priorityType == Constants.PRIORITY_CHECK_FILENAME_FROM_LOG) {
            expectFileIndex = byCheckFileName(files, curTimestamp, parseFileName, logger, switchId);
        } else if (priorityType == Constants.PRIORITY_MODIFIED_TIME_AND_MIN_SEQ) {
            //lay theo sequence, check theo ca modifiedTime
            //neu modifiedTime > curTime, lay file co sequence nho nhat
            expectFileIndex = byModifiedTimeAndMinSequence(files, curTimestamp, parseFileName, curSeq, maxSequence, logger);
        } else if (priorityType == Constants.PRIORITY_NEXT_SEQUENCE) {
            expectFileIndex = byNextSequence(files, curTimestamp, parseFileName, curSeq, maxSequence, logger);
        } else if (priorityType == Constants.PRIORITY_SEQUENCE_AND_FILENAME_TIME) {
            expectFileIndex = bySequenceFilenameTime(files, curTimestamp, parseFileName, curSeq, maxSequence, logger);
        } else if (priorityType == Constants.PRIORITY_SEQUENCE_ONLY) {
            expectFileIndex = bySequenceOnly(files, curTimestamp, parseFileName, curSeq, maxSequence, logger);
        } else {
            expectFileIndex = byNoPriority(files, curTimestamp, parseFileName, curSeq, maxSequence, logger, minSequence);
        }
        lst.add(expectFileIndex);
        return lst;
    }

    public static AbstractFileTransfer buildGetfileClass(String getFileClass, Logger logger,
            String host, int port, String username, String password, String processCode
            , String remoteDir, String ftpMode, String transferMode) throws Exception {
        Class getClass = Class.forName("com.viettel.ginterconnect.master.get.impl." + getFileClass);
        if (getClass.newInstance() instanceof AbstractFileTransfer) {
            AbstractFileTransfer gGetfile = (AbstractFileTransfer) getClass.newInstance();
            gGetfile.setLogger(logger);
            gGetfile.setModeAndTransferType(ftpMode, transferMode);
            gGetfile.setParam(host, port, username, password, processCode, remoteDir);
            return gGetfile;
        } else {
            logger.error("Cannot detect get class");
            return null;
        }
    }

}
