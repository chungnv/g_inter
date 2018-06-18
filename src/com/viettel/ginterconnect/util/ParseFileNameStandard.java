/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.util;

//import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author
 */
public class ParseFileNameStandard {

//    private MessageFormat messageFormat = null;
    private Date timestamp = null;
    private Date fileModifiedTime = null;
    private long fileId = -1;
    private long seq = 0;
    private boolean isSeq = false;
    private boolean isTimestamp = false;
    private boolean isAllFile = false;
    private boolean endAll = false;
    private boolean all = false;
    private Logger logger;

    public Date getFileModifiedTime() {
        return fileModifiedTime;
    }

    public void setFileModifiedTime(Date fileModifiedTime) {
        this.fileModifiedTime = fileModifiedTime;
    }

    public boolean isIsFileId() {
        return isFileId;
    }

    public ParseFileNameStandard(Logger logger) {
        this.logger = logger;
    }

    public ParseFileNameStandard(String patternFileName, Logger log) {
        this.setPatternFileName(patternFileName);
        this.logger = log;
    }

    public void setIsFileId(boolean isFileId) {
        this.isFileId = isFileId;
    }
    private boolean isFileId = false;
    List<FilePatternField> listFields;
    Queue<FilePatternField> arrFields = new LinkedList<FilePatternField>();
    List<String> lstIn = new ArrayList<String>();

    public void setPatternFileName(String patternFileName) {
        //Pattern HW_{YEAR####}{MON##}{DAY##}{HOUR##}{MIN##}{SEC##}{SEQ####}
        listFields = new ArrayList<FilePatternField>();
        //Build message format
        String subStr = "";
        String subPatternString = "";
        String lstIn = "";
        char endFileStr = '*';
        String finishChar = "";
        boolean stopCheckEndless = false;
        if (patternFileName.endsWith("*")) {
            endAll = true;
        }
        try {
            String[] arrTmp = patternFileName.split("\\*\\*");
            String temp = patternFileName;
            int len = 0;
            int fieldType = -1;

            String fixStr = "";
            boolean isEndField = false;
            int j = 0;
            while (j < temp.length()) {
                if (fieldType < 0) {
                    if (temp.charAt(j) == '{') {
                        if (len > 0) {
                            FilePatternField field = new FilePatternField();
                            if (temp.indexOf(fixStr) == 0 && arrFields.size() > 0) {
                                field.setIsStart(false);
                            }
                            field.setFieldType(FilePatternField.FIX_TYPE);
                            field.setLength(len);
                            field.setFixString(fixStr);
                            arrFields.offer(field);
                        }

                        subStr = temp.substring(j);
                        if (subStr.startsWith("{SEQ")) {
                            fieldType = FilePatternField.SEQ_TYPE;
                            isSeq = true;
                            len = 0;
                            subPatternString = "";
                            j = j + 3;
                        } else if (subStr.startsWith("{HSEQ")) {
                            fieldType = FilePatternField.HSEQ_TYPE;
                            isSeq = true;
                            len = 0;
                            subPatternString = "";
                            j = j + 4;
                        } else if (subStr.startsWith("{YEAR")) {
                            fieldType = FilePatternField.YEAR_TYPE;
                            isTimestamp = true;
                            len = 0;
                            j = j + 4;
                        } else if (subStr.startsWith("{MON")) {
                            fieldType = FilePatternField.MON_TYPE;
                            isTimestamp = true;
                            len = 0;
                            subPatternString = "";
                            j = j + 3;
                        } else if (subStr.startsWith("{DAY")) {
                            fieldType = FilePatternField.DAY_TYPE;
                            isTimestamp = true;
                            len = 0;
                            subPatternString = "";
                            j = j + 3;
                        } else if (subStr.startsWith("{HOUR")) {
                            fieldType = FilePatternField.HOUR_TYPE;
                            isTimestamp = true;
                            len = 0;
                            subPatternString = "";
                            j = j + 4;
                        } else if (subStr.startsWith("{MIN")) {
                            fieldType = FilePatternField.MIN_TYPE;
                            isTimestamp = true;
                            len = 0;
                            subPatternString = "";
                            j = j + 3;
                        } else if (subStr.startsWith("{SEC")) {
                            fieldType = FilePatternField.SEC_TYPE;
                            isTimestamp = true;
                            len = 0;
                            subPatternString = "";
                            j = j + 3;
                        } else if (subStr.startsWith("{FILEID")) {
                            fieldType = FilePatternField.FILEID_TYPE;
                            len = 0;
                            subPatternString = "";
                            j = j + 6;
                        } else if (subStr.startsWith("{IN")) {
                            fieldType = FilePatternField.IN_TYPE;
                            len = 0;
                            subPatternString = "";
                            j = j + 2;
                        } else if (subStr.startsWith("{IGNORE")) {
                            fieldType = FilePatternField.IGNORE_TYPE;
                            len = 0;
                            subPatternString = "";
                            j = j + 6;
                        }
                    } else if (temp.charAt(j) == '*') {
                        if (len >= 0) {
                            FilePatternField field = new FilePatternField();
                            if (temp.indexOf(fixStr) == 0 && arrFields.size() > 0) {
                                field.setIsStart(false);
                            }
                            field.setFieldType(FilePatternField.FIX_TYPE);
                            field.setLength(len);
                            field.setFixString(fixStr);
                            arrFields.offer(field);
                        }
//                            all = true;
                        FilePatternField field = new FilePatternField();
                        field.setFieldType(FilePatternField.ALL_TYPE);
//                            field.setLength(len);
//                            field.setFixString(fixStr);
                        arrFields.offer(field);
                        len = 0;
                        fieldType = -1;
                        fixStr = "";
                        subPatternString = "";
                    } else {
                        fixStr += temp.charAt(j);
                        len++;
                    }
                } else {
                    if (temp.charAt(j) == '}') {
                        FilePatternField field = new FilePatternField();
                        if (temp.indexOf(subStr) == 0) {
                            field.setIsStart(false);
                        }
                        switch (fieldType) {
                            case FilePatternField.SEQ_TYPE:
                                field.setFieldType(FilePatternField.SEQ_TYPE);
                                break;
                            case FilePatternField.HSEQ_TYPE:
                                field.setFieldType(FilePatternField.HSEQ_TYPE);
                                break;
                            case FilePatternField.YEAR_TYPE:
                                field.setFieldType(FilePatternField.YEAR_TYPE);
                                break;
                            case FilePatternField.MON_TYPE:
                                field.setFieldType(FilePatternField.MON_TYPE);
                                break;
                            case FilePatternField.DAY_TYPE:
                                field.setFieldType(FilePatternField.DAY_TYPE);
                                break;
                            case FilePatternField.HOUR_TYPE:
                                field.setFieldType(FilePatternField.HOUR_TYPE);
                                break;
                            case FilePatternField.MIN_TYPE:
                                field.setFieldType(FilePatternField.MIN_TYPE);
                                break;
                            case FilePatternField.SEC_TYPE:
                                field.setFieldType(FilePatternField.SEC_TYPE);
                                break;
                            case FilePatternField.IN_TYPE:
                                field.setFieldType(FilePatternField.IN_TYPE);
                                break;
                            case FilePatternField.FILEID_TYPE:
                                field.setFieldType(FilePatternField.FILEID_TYPE);
                                break;
                            case FilePatternField.IGNORE_TYPE:
                                field.setFieldType(FilePatternField.IGNORE_TYPE);
                                break;
                            default:
                                break;
                        }
                        //thangdd add split thread
                        //{YEAR####_1,2,3,4,5,6,7,8,9}: year last by character in (1,2,3,4,5,6,7,8,9)
                        //field.setLength(len);
                        field.setLength(subPatternString.split(FilePatternField.DELIMITER)[0].length());
                        //get last character
                        if (subPatternString.split(FilePatternField.DELIMITER).length > 1) {
                            field.getLstFieldCharacter().addAll(Arrays.asList(subPatternString.split(FilePatternField.DELIMITER)[1].split(",")));
                        }
                        //end thangdd
                        //field.setLength(len);
                        //set field is end
                        if (isEndField) {
                            isEndField = false;
                            field.setIsEndless(true);
                            field.setFinishCharacter(finishChar);
                        }
                        arrFields.offer(field);
                        //index++;
                        len = 0;
                        fieldType = -1;
                        fixStr = "";
                        subPatternString = "";
                        stopCheckEndless = false;
                    } else {
                        len++;
                        //lstIn += temp.charAt(j);
                        endFileStr = temp.charAt(j);

                        if (FilePatternField.DELIMITER.equals("\\" + String.valueOf(endFileStr))) {
                            stopCheckEndless = true;
                        }

                        if (!FilePatternField.PATTERN_CHARACTER.equals(String.valueOf(endFileStr)) && "".equals(finishChar)
                                && !FilePatternField.DELIMITER.equals(String.valueOf(endFileStr)) && !stopCheckEndless) {
                            isEndField = true;
                            finishChar += endFileStr;
                        }
                        subPatternString = subPatternString + temp.charAt(j);
                    }
                }
                j++;
            }
//                String[] arrIn = lstIn.split("\\:");

            if (fieldType == -1) {
                FilePatternField field = new FilePatternField();
                if (temp.indexOf(fixStr) == 0 && arrFields.size() > 0) {
                    field.setIsStart(false);
                }
                field.setFieldType(FilePatternField.FIX_TYPE);
                field.setLength(len);
                field.setFixString(fixStr);
                arrFields.offer(field);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isIsSeq() {
        return isSeq;
    }

    public void setIsSeq(boolean isSeq) {
        this.isSeq = isSeq;
    }

    public boolean isIsTimestamp() {
        return isTimestamp;
    }

    public void setIsTimestamp(boolean isTimestamp) {
        this.isTimestamp = isTimestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public boolean Parse(String fileName) {
        boolean result = true;
        int yy = -1;
        int mm = -1;
        int dd = -1;
        int hh = -1;
        int mi = -1;
        int ss = -1;
        for (int i = 0; i < arrFields.toArray().length; i++) {
            FilePatternField field = (FilePatternField) arrFields.toArray()[i];
//            if ((field.getFieldType() == FilePatternField.FIX_TYPE)
//                    && (field.getFixString() == null || "".equals(field.getFixString()))) {
//                continue;
//            }
            String strRegex = "";
            Pattern pattern;
            Matcher matcher;
            switch (field.getFieldType()) {
                case FilePatternField.ALL_TYPE:
                    all = true;
                    break;
                case FilePatternField.FIX_TYPE:
                    if (all) {
                        if (!fileName.contains(field.getFixString())) {
                            return false;
                        } else {
                            fileName = fileName.substring(fileName.indexOf(field.getFixString()) + field.getLength());
                        }
                        all = false;
                        break;
                    }
                    if (field.isIsStart()) {
                        if (!fileName.startsWith(field.getFixString())) {
                            return false;
                        } else {
                            fileName = fileName.substring(fileName.indexOf(field.getFixString()) + field.getLength());
                        }
                    } else {
                        if (!fileName.startsWith(field.getFixString())) {//ra edit contains --> startsWith
                            return false;
                        } else {
                            fileName = fileName.substring(fileName.indexOf(field.getFixString()) + field.getLength());
                            //fileName = fileName.replace(field.getFixString(), "");
                        }
                    }
                    break;
                case FilePatternField.FILEID_TYPE:
                    if (all) {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }

                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            fileId = Long.parseLong(matcher.group());
                            //add ra
                            String strFileid = ("000000000000000" + fileId).substring((15 + ("" + fileId).length()) - (strRegex.length() / 2));
//                            if (!fileName.startsWith("" + strFileid)) {
//                                return false;
//                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            System.out.println("not parse fileid");
                            return false;
                        }
                        all = false;
                        break;
                    }
                    if (field.isIsStart()) {
                        try {
                            fileId = Long.parseLong(fileName.substring(0, field.getLength()));
                            fileName = fileName.substring(field.getLength());
                        } catch (Exception ex) {
                            return false;
                        }

                    } else {

                        int lengthCheck = 0;
                        if (field.isIsEndless()) {
                            if (fileName.contains(field.getFinishCharacter())) {
                                lengthCheck = fileName.indexOf(field.getFinishCharacter());
                            } else {
                                for (int k = 0; k < fileName.length(); k++) {
                                    if (Character.isDigit(fileName.charAt(k))) {
                                        lengthCheck++;
                                    } else {
                                        if (lengthCheck > 0) {
                                            break;
                                        }
                                    }
                                }
                                //lengthCheck = fileName.length();
                            }
                        } else {
                            lengthCheck = field.getLength();
                        }
                        for (int k = 0; k < lengthCheck; k++) {
                            strRegex += "\\d";
                        }
//                        
//                        for (int k = 0; k < field.getLength(); k++) {
//                            strRegex += "\\d";
//                        }

                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            fileId = Long.parseLong(matcher.group());
                            //add ra
                            String strFileid = ("000000000000000" + fileId).substring((15 + ("" + fileId).length()) - (strRegex.length() / 2));
                            if (!fileName.startsWith("" + strFileid)) {
                                return false;
                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + strFileid.length()/*field.getLength() */);
                            strRegex = "";
                        } else {
                            if (logger != null) {
                                logger.warn("not parse filedi");
                            } else {
                                System.out.println("not parse filedi");
                            }
                            return false;
                        }
                    }
                    //Check last fileId
                    if (field.getLstFieldCharacter().size() > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (fileId + "").endsWith(endSeq)) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                case FilePatternField.IGNORE_TYPE:
                    if (field.isIsStart()) {
                        try {
                            fileName = fileName.substring(field.getLength());
                        } catch (Exception ex) {
                            return false;
                        }
                    } else {
                        if (fileName.length() > field.getLength()) {
                            fileName = fileName.substring(field.getLength());
                        } else {
                            return false;
                        }
                    }
                    //Check last fileId
                    if (field.getLstFieldCharacter().size() > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (fileId + "").endsWith(endSeq)) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                case FilePatternField.SEQ_TYPE:
                    if (all) {
                        int lengthCheck = 0;
                        if (field.isIsEndless()) {
                            if (fileName.contains(field.getFinishCharacter())) {
                                lengthCheck = fileName.indexOf(field.getFinishCharacter());
                            } else {
                                for (int k = 0; k < fileName.length(); k++) {
                                    if (Character.isDigit(fileName.charAt(k))) {
                                        lengthCheck++;
                                    } else {
                                        if (lengthCheck > 0) {
                                            break;
                                        }
                                    }
                                }
                                //lengthCheck = fileName.length();
                            }
                        } else {
                            lengthCheck = field.getLength();
                        }
                        for (int k = 0; k < lengthCheck; k++) {
                            strRegex += "\\d";
                        }
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            seq = Long.parseLong(matcher.group());
                            //add ra
                            String strSeq = ("000000000000000" + seq).substring((15 + ("" + seq).length()) - (strRegex.length() / 2));
//                            if (!fileName.startsWith("" + strSeq)) {
//                                return false;
//                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + strSeq.length()/* field.getLength() */);
                            strRegex = "";
                        } else {
                            if (logger != null) {
                                logger.info("not parse seq");
                            } else {
                                System.out.println("not parse seq");
                            }
                            return false;
                        }
                        all = false;
//                        if (field.getLstFieldCharacter().size() > 0) {
//                            boolean isFilter = false;
//                            for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
//                                String endSeq = field.getLstFieldCharacter().get(index);
//                                if (endSeq != null && endSeq.length() > 0 && (seq + "").endsWith(endSeq)) {
//                                    isFilter = true;
//                                    break;
//                                }
//                            }
//                            if (!isFilter) {
//                                return false;
//                            }
//                        }
//                        break;
                    } else {
                        if (field.isIsStart()) {
                            try {
                                seq = Long.parseLong(fileName.substring(0, field.getLength()));
                                fileName = fileName.substring(field.getLength());
                            } catch (Exception ex) {
                                return false;
                            }
                        } else {
                            int lengthCheck = 0;
                            if (field.isIsEndless()) {
                                if (fileName.contains(field.getFinishCharacter())) {
                                    lengthCheck = fileName.indexOf(field.getFinishCharacter());
                                } else {
                                    for (int k = 0; k < fileName.length(); k++) {
                                        if (Character.isDigit(fileName.charAt(k))) {
                                            lengthCheck++;
                                        } else {
                                            if (lengthCheck > 0) {
                                                break;
                                            }
                                        }
                                    }
                                    //lengthCheck = fileName.length();
                                }
                            } else {
                                lengthCheck = field.getLength();
                            }
                            for (int k = 0; k < lengthCheck; k++) {
                                strRegex += "\\d";
                            }
                            pattern = Pattern.compile(strRegex);
                            matcher = pattern.matcher(fileName);
                            if (matcher.find()) {
                                seq = Long.parseLong(matcher.group());
                                //add ra
                                String strSeq = ("000000000000000" + seq).substring((15 + ("" + seq).length()) - (strRegex.length() / 2));
                                if (!fileName.startsWith("" + strSeq)) {
                                    return false;
                                }
                                fileName = fileName.substring(fileName.indexOf(matcher.group()) + lengthCheck);
                                strRegex = "";
                            } else {
                                if (logger != null) {
                                    logger.info("not parse seq");
                                } else {
                                    System.out.println("not parse seq");
                                }
                                return false;
                            }
                        }
                    }
                    if (field.getLstFieldCharacter().size() > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (seq + "").endsWith(endSeq)) {
                                isFilter = true;
                                break;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                case FilePatternField.HSEQ_TYPE:

                    if (field.isIsStart()) {
                        try {
                            seq = Long.parseLong(fileName.substring(0, field.getLength()), 16);
                            fileName = fileName.substring(field.getLength());
                        } catch (Exception ex) {
                            return false;
                        }
                    } else {
                        int lengthCheck = 0;
                        if (field.isIsEndless()) {
                            if (fileName.contains(field.getFinishCharacter())) {
                                lengthCheck = fileName.indexOf(field.getFinishCharacter());
                            } else {
                                for (int k = 0; k < fileName.length(); k++) {
                                    if (Character.isDigit(fileName.charAt(k))) {
                                        lengthCheck++;
                                    } else {
                                        if (lengthCheck > 0) {
                                            break;
                                        }
                                    }
                                }
//                                if (lengthCheck > 0) {
//                                    lengthCheck--;
//                                }
                                //lengthCheck = fileName.length();
                            }
                        } else {
                            lengthCheck = field.getLength();
                        }
//                        for (int k = 0; k < lengthCheck; k++) {
//                            strRegex += "\\d";
//                        }
                        strRegex = "[0-9A-F]{" + (lengthCheck) + "}";
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            String hseq = matcher.group();
                            seq = Long.parseLong(hseq, 16);
                            //add ra
//                            String strSeq = ("000000000000000" + hseq).substring((15 + ("" + hseq).length()) - (strRegex.length() / 2));
                            if (!fileName.startsWith("" + hseq)) {
                                return false;
                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + hseq.length());
                            strRegex = "";
                        } else {
                            if (logger != null) {
                                logger.info("not parse seq");
                            } else {
                                System.out.println("not parse seq");
                            }
                            return false;
                        }
                    }

                    if (field.getLstFieldCharacter().size() > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (seq + "").endsWith(endSeq)) {
                                isFilter = true;
                                break;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                case FilePatternField.IN_TYPE:
                    if (field.isIsStart()) {
                        try {
                            seq = Long.parseLong(fileName.substring(0, field.getLength()));
                            fileName = fileName.substring(field.getLength());
                        } catch (Exception ex) {
                            return false;
                        }
                    } else {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            seq = Long.parseLong(matcher.group());
                            //add ra
                            String strSeq = ("000000000000000" + seq).substring((15 + ("" + seq).length()) - (strRegex.length() / 2));
                            if (!fileName.startsWith("" + strSeq)) {
                                return false;
                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                    }
                    if (field.getLstFieldCharacter().size() > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (seq + "").endsWith(endSeq)) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                case FilePatternField.SEC_TYPE:
                    if (all) {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }

                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            ss = Integer.parseInt(matcher.group());
                            //add ra
                            String strSS = ("000000000000000" + ss).substring((15 + ("" + ss).length()) - (strRegex.length() / 2));
//                            if (!fileName.startsWith("" + strSS)) {
//                                return false;
//                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex
                                    = "";
                        } else {
                            return false;
                        }
                        all = false;
                        break;
                    }
                    if (field.isIsStart()) {
                        try {
                            ss = Integer.parseInt(fileName.substring(0, field.getLength()));
                            fileName = fileName.substring(field.getLength());
                        } catch (Exception ex) {
                            return false;
                        }
                    } else {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }

                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            ss = Integer.parseInt(matcher.group());
                            //add ra
                            String strSS = ("000000000000000" + ss).substring((15 + ("" + ss).length()) - (strRegex.length() / 2));
                            if (!fileName.startsWith("" + strSS)) {
                                return false;
                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex
                                    = "";
                        } else {
                            return false;
                        }
                    }
                    //Check last sec
                    if (field.getLstFieldCharacter().size() > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (ss + "").endsWith(endSeq)) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                case FilePatternField.MIN_TYPE:
                    if (all) {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            mi = Integer.parseInt(matcher.group());
                            //add ra
//                            String strMI = ("000000000000000" + mi).substring((15 + ("" + mi).length()) - (strRegex.length() / 2));
//                            if (!fileName.startsWith("" + strMI)) {
//                                return false;
//                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                        all = false;
                        break;
                    }
                    if (field.isIsStart()) {
                        try {
                            mi = Integer.parseInt(fileName.substring(0, field.getLength()));
                            fileName
                                    = fileName.substring(field.getLength());
                        } catch (Exception ex) {
                            return false;
                        }

                    } else {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            mi = Integer.parseInt(matcher.group());
                            //add ra
                            String strMI = ("000000000000000" + mi).substring((15 + ("" + mi).length()) - (strRegex.length() / 2));
                            if (!fileName.startsWith("" + strMI)) {
                                return false;
                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                    }
                    //Check last min
                    if (field.getLstFieldCharacter().size() > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (mi + "").endsWith(endSeq)) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                case FilePatternField.HOUR_TYPE:
                    if (all) {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }

                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            hh = Integer.parseInt(matcher.group());
                            //add ra
//                            String strHH = ("000000000000000" + hh).substring((15 + ("" + hh).length()) - (strRegex.length() / 2));
//                            if (!fileName.startsWith("" + strHH)) {
//                                return false;
//                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                        all = false;
                        break;
                    }
                    if (field.isIsStart()) {
                        try {
                            hh = Integer.parseInt(fileName.substring(0, field.getLength()));
                            fileName = fileName.substring(field.getLength());
                        } catch (Exception ex) {
                            return false;
                        }
                    } else {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }

                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            hh = Integer.parseInt(matcher.group());
                            //add ra
                            String strHH = ("000000000000000" + hh).substring((15 + ("" + hh).length()) - (strRegex.length() / 2));
                            if (!fileName.startsWith("" + strHH)) {
                                return false;
                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                    }
                    //Check last hour
                    if (field.getLstFieldCharacter().size() > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (hh + "").endsWith(endSeq)) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                case FilePatternField.MON_TYPE:
                    if (all) {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            mm = Integer.parseInt(matcher.group());
                            //add ra
//                            String strMM = ("000000000000000" + mm).substring((15 + ("" + mm).length()) - (strRegex.length() / 2));
//                            if (!fileName.startsWith("" + strMM)) {
//                                return false;
//                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                        all = false;
                        break;
                    }
                    if (field.isIsStart()) {
                        try {
                            mm = Integer.parseInt(fileName.substring(0, field.getLength()));
                            fileName = fileName.substring(field.getLength());
                        } catch (Exception ex) {
                            return false;
                        }

                    } else {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            mm = Integer.parseInt(matcher.group());
                            //add ra
                            String strMM = ("000000000000000" + mm).substring((15 + ("" + mm).length()) - (strRegex.length() / 2));
                            if (!fileName.startsWith("" + strMM)) {
                                return false;
                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                    }
                    //Check last mon
                    if (field.getLstFieldCharacter().size() > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (mm + "").endsWith(endSeq)) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                case FilePatternField.DAY_TYPE:
                    if (all) {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            dd = Integer.parseInt(matcher.group());
                            //add ra
//                            String strDD = ("000000000000000" + dd).substring((15 + ("" + dd).length()) - (strRegex.length() / 2));
//                            if (!fileName.startsWith("" + strDD)) {
//                                return false;
//                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                        all = false;
                        break;
                    }
                    if (field.isIsStart()) {
                        try {
                            dd = Integer.parseInt(fileName.substring(0, field.getLength()));
                            fileName = fileName.substring(field.getLength());
                        } catch (Exception ex) {
                            return false;
                        }

                    } else {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            dd = Integer.parseInt(matcher.group());
                            //add ra
                            String strDD = ("000000000000000" + dd).substring((15 + ("" + dd).length()) - (strRegex.length() / 2));
                            if (!fileName.startsWith("" + strDD)) {
                                return false;
                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                    }
                    //Check last day
                    if (field.getLstFieldCharacter().size() > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (dd + "").endsWith(endSeq)) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                case FilePatternField.YEAR_TYPE:
                    if (all) {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            yy = Integer.parseInt(matcher.group());
                            //add ra
//                            String strYY = ("000000000000000" + yy).substring((15 + ("" + yy).length()) - (strRegex.length() / 2));
//                            if (!fileName.startsWith("" + strYY)) {
//                                return false;
//                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                        all = false;
                        break;
                    }
                    if (field.isIsStart()) {
                        try {
                            yy = Integer.parseInt(fileName.substring(0, field.getLength()));
                            fileName = fileName.substring(field.getLength());
                        } catch (Exception ex) {
                            return false;
                        }
                    } else {
                        for (int k = 0; k < field.getLength(); k++) {
                            strRegex += "\\d";
                        }
                        pattern = Pattern.compile(strRegex);
                        matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            yy = Integer.parseInt(matcher.group());
                            //add ra
                            String strYY = ("000000000000000" + yy).substring((15 + ("" + yy).length()) - (strRegex.length() / 2));
                            if (!fileName.startsWith("" + strYY)) {
                                return false;
                            }
                            fileName = fileName.substring(fileName.indexOf(matcher.group()) + field.getLength());
                            strRegex = "";
                        } else {
                            return false;
                        }
                    }
                    //Check last year
                    if (field.getLstFieldCharacter().size() > 0 && yy > 0) {
                        boolean isFilter = false;
                        for (int index = 0; index < field.getLstFieldCharacter().size(); index++) {
                            String endSeq = field.getLstFieldCharacter().get(index);
                            if (endSeq != null && endSeq.length() > 0 && (yy + "").endsWith(endSeq)) {
                                isFilter = true;
                            }
                        }
                        if (!isFilter) {
                            return false;
                        }
                    }
                    break;
                default:
                    break;
            }

        }
        if (fileName != null && !"".equals(fileName) && !isAllFile && !endAll) {
            return false;
        }

        if (yy != -1 || mm != -1 || dd != -1 || hh != -1 || mi != -1 || ss != -1) {
            //fix some data is null
            Date curDate = new Date();
            if (ss != -1 && mi == -1) {
                mi = curDate.getMinutes();
            }
            if (mi != -1 && hh == -1) {
                hh = curDate.getHours();
            }
            if (hh != -1 && dd == -1) {
                dd = curDate.getDate();
            }
            if (dd != -1 && mm == -1) {
                mm = curDate.getMonth() + 1;
            }
            if (mm != -1 && yy == -1) {
                yy = 1900 + curDate.getYear();
            }
            if (ss == -1) {
                ss = 0;
            }
            if (mi == -1) {
                mi = 0;
            }
            if (hh == -1) {
                hh = 0;
            }
            if (dd == -1) {
                dd = 0;
            }
            if (mm == -1) {
                mm = 1;
            }
//            if (ss == -1) {
//                ss = curDate.getSeconds();
//            }

            Calendar cal = new GregorianCalendar(yy, mm - 1, dd, hh, mi, ss);
            timestamp = cal.getTime();
        } else {
            timestamp = null;
        }
        return result;
    }

    public static String parseDate(String templateName, Date cdrDate) {
        //A{YEAR####}B{MON##}C{DAY##}D{FILEID##########}.txt
//        if (templateName == null || templateName.trim().length() == 0) {
//            return "AAA" + "_" + fileId + ".txt";
//        }
//        Date date = new Date();
        templateName = templateName.substring(templateName.indexOf("{CDRDATE}") + "{CDRDATE}".length());
        Calendar cal = Calendar.getInstance();
        cal.setTime(cdrDate);
        String strReturn = "";
        while (templateName.contains("{")) {
            int i = templateName.indexOf("{");
//            strReturn += templateName.substring(0, i);
            templateName = templateName.substring(i);
            if (templateName.startsWith("{YEAR")) {
                i = 5;
                int ind = templateName.indexOf('}');
//                if ((ind - 4) == i) {
//                    strReturn += cal.get(Calendar.YEAR) + "";
//                }
                String year = "";
                year = (cal.get(Calendar.YEAR) + "").substring(4 - (ind - i));
                strReturn += year;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{MON")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.MONTH) + 1 + "";
//                }
                String month = "";
                if (cal.get(Calendar.MONTH) <= 8) {
                    month = "000" + (cal.get(Calendar.MONTH) + 1);
                } else {
                    month = "00" + (cal.get(Calendar.MONTH) + 1);
                }
                month = month.substring(4 - (ind - i));
                strReturn += month;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{DAY")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String day = "";
                if (cal.get(Calendar.DAY_OF_MONTH) <= 9) {
                    day = "000" + (cal.get(Calendar.DAY_OF_MONTH));
                } else {
                    day = "00" + (cal.get(Calendar.DAY_OF_MONTH));
                }
                day = day.substring(4 - (ind - i));
                strReturn += day;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{HOUR")) {
                i = 5;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String hour = "";
                if (cal.get(Calendar.HOUR_OF_DAY) <= 9) {
                    hour = "000" + (cal.get(Calendar.HOUR_OF_DAY) + 1);
                } else {
                    hour = "00" + (cal.get(Calendar.HOUR_OF_DAY) + 1);
                }
                hour = hour.substring(4 - (ind - i));
                strReturn += hour;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{MIN")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String min = "";
                if (cal.get(Calendar.MINUTE) <= 9) {
                    min = "000" + (cal.get(Calendar.MINUTE) + 1);
                } else {
                    min = "00" + (cal.get(Calendar.MINUTE) + 1);
                }
                min = min.substring(4 - (ind - i));
                strReturn += min;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{SEC")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String sec = "";
                if (cal.get(Calendar.SECOND) <= 9) {
                    sec = "000" + (cal.get(Calendar.SECOND) + 1);
                } else {
                    sec = "00" + (cal.get(Calendar.SECOND) + 1);
                }
                sec = sec.substring(4 - (ind - i));
                strReturn += sec;
                templateName = templateName.substring(ind + 1);
            } else {
                templateName = templateName.substring(1);
            }
        }
        strReturn += templateName;
        return strReturn;
    }
    
    public static String parse(String templateName, long fileId, Date cdrDate) {
        //A{YEAR####}B{MON##}C{DAY##}D{FILEID##########}.txt
        if (templateName == null || templateName.trim().length() == 0) {
            return "AAA" + "_" + fileId + ".txt";
        }
//        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(cdrDate);
        String strReturn = "";
        while (templateName.contains("{")) {
            int i = templateName.indexOf("{");
            strReturn += templateName.substring(0, i);
            templateName = templateName.substring(i);
            if (templateName.startsWith("{YEAR")) {
                i = 5;
                int ind = templateName.indexOf('}');
//                if ((ind - 4) == i) {
//                    strReturn += cal.get(Calendar.YEAR) + "";
//                }
                String year = "";
                year = (cal.get(Calendar.YEAR) + "").substring(4 - (ind - i));
                strReturn += year;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{MON")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.MONTH) + 1 + "";
//                }
                String month = "";
                if (cal.get(Calendar.MONTH) <= 8) {
                    month = "000" + (cal.get(Calendar.MONTH) + 1);
                } else {
                    month = "00" + (cal.get(Calendar.MONTH) + 1);
                }
                month = month.substring(4 - (ind - i));
                strReturn += month;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{DAY")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String day = "";
                if (cal.get(Calendar.DAY_OF_MONTH) <= 9) {
                    day = "000" + (cal.get(Calendar.DAY_OF_MONTH));
                } else {
                    day = "00" + (cal.get(Calendar.DAY_OF_MONTH));
                }
                day = day.substring(4 - (ind - i));
                strReturn += day;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{HOUR")) {
                i = 5;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String hour = "";
                if (cal.get(Calendar.HOUR_OF_DAY) <= 9) {
                    hour = "000" + (cal.get(Calendar.HOUR_OF_DAY) + 1);
                } else {
                    hour = "00" + (cal.get(Calendar.HOUR_OF_DAY) + 1);
                }
                hour = hour.substring(4 - (ind - i));
                strReturn += hour;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{MIN")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String min = "";
                if (cal.get(Calendar.MINUTE) <= 9) {
                    min = "000" + (cal.get(Calendar.MINUTE) + 1);
                } else {
                    min = "00" + (cal.get(Calendar.MINUTE) + 1);
                }
                min = min.substring(4 - (ind - i));
                strReturn += min;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{SEC")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String sec = "";
                if (cal.get(Calendar.SECOND) <= 9) {
                    sec = "000" + (cal.get(Calendar.SECOND) + 1);
                } else {
                    sec = "00" + (cal.get(Calendar.SECOND) + 1);
                }
                sec = sec.substring(4 - (ind - i));
                strReturn += sec;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{FILEID")) {
                i = 7;
                int ind = templateName.indexOf('}');
//                if ((ind - ))
                String fId = "00000000000000000" + fileId;
                strReturn += fId.substring(fId.length() - (ind - i), fId.length());
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{SEQ")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - ))
                String fid = "00000000000000000" + fileId;
                strReturn += fid.substring(fid.length() - (ind - i), fid.length());
                templateName = templateName.substring(ind + 1);
            }
        }
        strReturn += templateName;
        return strReturn;
    }
    
    public static String parse(String templateName, long fileId) {
        //A{YEAR####}B{MON##}C{DAY##}D{FILEID##########}.txt
        if (templateName == null || templateName.trim().length() == 0) {
            return "AAA" + "_" + fileId + ".txt";
        }
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String strReturn = "";
        while (templateName.contains("{")) {
            int i = templateName.indexOf("{");
            strReturn += templateName.substring(0, i);
            templateName = templateName.substring(i);
            if (templateName.startsWith("{YEAR")) {
                i = 5;
                int ind = templateName.indexOf('}');
//                if ((ind - 4) == i) {
//                    strReturn += cal.get(Calendar.YEAR) + "";
//                }
                String year = "";
                year = (cal.get(Calendar.YEAR) + "").substring(4 - (ind - i));
                strReturn += year;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{MON")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.MONTH) + 1 + "";
//                }
                String month = "";
                if (cal.get(Calendar.MONTH) <= 8) {
                    month = "000" + (cal.get(Calendar.MONTH) + 1);
                } else {
                    month = "00" + (cal.get(Calendar.MONTH) + 1);
                }
                month = month.substring(4 - (ind - i));
                strReturn += month;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{DAY")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String day = "";
                if (cal.get(Calendar.DAY_OF_MONTH) <= 9) {
                    day = "000" + (cal.get(Calendar.DAY_OF_MONTH));
                } else {
                    day = "00" + (cal.get(Calendar.DAY_OF_MONTH));
                }
                day = day.substring(4 - (ind - i));
                strReturn += day;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{HOUR")) {
                i = 5;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String hour = "";
                if (cal.get(Calendar.HOUR_OF_DAY) <= 9) {
                    hour = "000" + (cal.get(Calendar.HOUR_OF_DAY) + 1);
                } else {
                    hour = "00" + (cal.get(Calendar.HOUR_OF_DAY) + 1);
                }
                hour = hour.substring(4 - (ind - i));
                strReturn += hour;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{MIN")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String min = "";
                if (cal.get(Calendar.MINUTE) <= 9) {
                    min = "000" + (cal.get(Calendar.MINUTE) + 1);
                } else {
                    min = "00" + (cal.get(Calendar.MINUTE) + 1);
                }
                min = min.substring(4 - (ind - i));
                strReturn += min;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{SEC")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - 2) == i) {
//                    strReturn += cal.get(Calendar.DAY_OF_MONTH) + "";
//                }
                String sec = "";
                if (cal.get(Calendar.SECOND) <= 9) {
                    sec = "000" + (cal.get(Calendar.SECOND) + 1);
                } else {
                    sec = "00" + (cal.get(Calendar.SECOND) + 1);
                }
                sec = sec.substring(4 - (ind - i));
                strReturn += sec;
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{FILEID")) {
                i = 7;
                int ind = templateName.indexOf('}');
//                if ((ind - ))
                String fId = "00000000000000000" + fileId;
                strReturn += fId.substring(fId.length() - (ind - i), fId.length());
                templateName = templateName.substring(ind + 1);
            } else if (templateName.startsWith("{SEQ")) {
                i = 4;
                int ind = templateName.indexOf('}');
//                if ((ind - ))
                String fid = "00000000000000000" + fileId;
                strReturn += fid.substring(fid.length() - (ind - i), fid.length());
                templateName = templateName.substring(ind + 1);
            }
        }
        strReturn += templateName;
        return strReturn;
    }
}
