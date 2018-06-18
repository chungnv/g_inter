/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.util;

//import java.text.MessageFormat;

import java.util.*;

/**
 *
 * @author Le Thanh Cong
 */
public class ParseFileName {

//    private MessageFormat messageFormat = null;
    private Date timestamp = null;
    private long fileId;
    private int seq = 0;
    private boolean isTimestamp = false;
    //added by thangdd7
    private boolean isFileId = false;
    List<FilePatternField> listFields;
    private boolean isSeq = false;

    public void setPatternFileName(String patternFileName) {
        //Pattern HW_{YEAR####}{MON##}{DAY##}{HOUR##}{MIN##}{SEC##}{SEQ####}{TICTAC###}{FILEID####}
        //Modify by ThangDD
        //{YEAR####_1,2,3,4,5,6,7,8,9}: year last by character in (1,2,3,4,5,6,7,8,9).
        //Pattern HW_{YEAR####_1_2_3_4_5_6_7_8_9}{MON##}{DAY##}{HOUR##}{MIN##}{SEC##}{SEQ####}{TICTAC###}{FILEID####}
        String tmp = patternFileName;
        listFields = new ArrayList<FilePatternField>();
        //Build message format

        int len = 0;
        int fieldType = -1;

        String fixStr = "";
        int i = 0;
        String subParttnerString = "";
        while (i < tmp.length()) {
            if (fieldType < 0) {
                if (tmp.charAt(i) == '{') {
                    //Xu ly chuoi fix
                    if (len > 0) {
                        FilePatternField field = new FilePatternField();
                        field.setFieldType(FilePatternField.FIX_TYPE);
                        field.setLength(len);
                        field.setFixString(fixStr);
                        listFields.add(field);
                    }
                    String subStr = tmp.substring(i);
                    if (subStr.startsWith("{SEQ")) {
                        fieldType = FilePatternField.SEQ_TYPE;
                        isSeq = true;
                        len = 0;
                        subParttnerString = "";
                        i = i + 3;
                    } else if (subStr.startsWith("{YEAR")) {
                        fieldType = FilePatternField.YEAR_TYPE;
                        isTimestamp = true;
                        len = 0;
                        subParttnerString = "";
                        i = i + 4;
                    } else if (subStr.startsWith("{MON")) {
                        fieldType = FilePatternField.MON_TYPE;
                        isTimestamp = true;
                        len = 0;
                        subParttnerString = "";
                        i = i + 3;
                    } else if (subStr.startsWith("{DAY")) {
                        fieldType = FilePatternField.DAY_TYPE;
                        isTimestamp = true;
                        len = 0;
                        subParttnerString = "";
                        i = i + 3;
                    } else if (subStr.startsWith("{HOUR")) {
                        fieldType = FilePatternField.HOUR_TYPE;
                        isTimestamp = true;
                        len = 0;
                        subParttnerString = "";
                        i = i + 4;
                    } else if (subStr.startsWith("{MIN")) {
                        fieldType = FilePatternField.MIN_TYPE;
                        isTimestamp = true;
                        len = 0;
                        subParttnerString = "";
                        i = i + 3;
                    } else if (subStr.startsWith("{SEC")) {
                        fieldType = FilePatternField.SEC_TYPE;
                        isTimestamp = true;
                        len = 0;
                        subParttnerString = "";
                        i = i + 3;
                    } else if (subStr.startsWith("{TICTAC")) {
                        fieldType = FilePatternField.TICTAC_TYPE;
                        isTimestamp = true;
                        len = 0;
                        subParttnerString = "";
                        i = i + 6;
                    } else if (subStr.startsWith("{FILEID")) {
                        fieldType = FilePatternField.FILEID_TYPE;
                        isFileId = true;
                        len = 0;
                        subParttnerString = "";
                        i = i + 6;
                    }
                } else {
                    fixStr += tmp.charAt(i);
                    len++;
                }

                //Khong phai fix type nua
            } else {
                if (tmp.charAt(i) == '}') {
                    FilePatternField field = new FilePatternField();
                    switch (fieldType) {
                        case FilePatternField.SEQ_TYPE:
                            field.setFieldType(FilePatternField.SEQ_TYPE);
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

                        case FilePatternField.TICTAC_TYPE:
                            field.setFieldType(FilePatternField.TICTAC_TYPE);
                            break;

                        case FilePatternField.SEC_TYPE:
                            field.setFieldType(FilePatternField.SEC_TYPE);
                            break;
                        case FilePatternField.FILEID_TYPE:
                            field.setFieldType(FilePatternField.FILEID_TYPE);
                            break;
                        default:
                            break;
                    }
                    //{YEAR####_1,2,3,4,5,6,7,8,9}: year last by character in (1,2,3,4,5,6,7,8,9).
                    //field.setLength(len);
                    field.setLength(subParttnerString.split("_")[0].length());
                    if (subParttnerString.split("_").length > 1) {
                        field.lstFieldCharacter.addAll(Arrays.asList(subParttnerString.split("_")[1].split(",")));
                    }
                    listFields.add(field);
                    len = 0;
                    fieldType = -1;
                    fixStr = "";
                    subParttnerString = "";
                } else {
                    len++;
                    subParttnerString = subParttnerString + tmp.charAt(i);
                }
            }
            i++;
        }
        if (fieldType == -1) {
            FilePatternField field = new FilePatternField();
            field.setFieldType(FilePatternField.FIX_TYPE);
            field.setLength(len);
            field.setFixString(fixStr);
            listFields.add(field);
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

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public boolean isIsFileId() {
        return isFileId;
    }

    public void setIsFileId(boolean isFileId) {
        this.isFileId = isFileId;
    }

    public boolean Parse(String fileName) {
        boolean result = true;
        seq = 0;
        timestamp = null;
        int year = 0, mon = 0, day = 1, hour = 0, min = 0, sec = 0, titac = 0;
        fileId = 0L;
        try {
            String tmp = fileName;
            String subStr;
            int pos = 0;
            for (FilePatternField field : listFields) {
                subStr = tmp.substring(pos, pos + field.getLength());
                switch (field.getFieldType()) {
                    case FilePatternField.FIX_TYPE:
                        if (!subStr.equals(field.getFixString())) {
                            return false;
                        }
                        break;
                    case FilePatternField.SEQ_TYPE:
                        try {
                            seq = Integer.parseInt(subStr);
                            //Check last sequence
                            if (field.lstFieldCharacter.size() > 0) {
                                boolean isFilter = false;
                                for (int index = 0; index < field.lstFieldCharacter.size(); index++) {
                                    String endSeq = field.lstFieldCharacter.get(index);
                                    if (endSeq != null && endSeq.length() > 0 && subStr.endsWith(endSeq)) {
                                        isFilter = true;
                                    }
                                }
                                if (!isFilter) {
                                    return false;
                                }
                            }
                        } catch (Exception ex) {
                            return false;
                        }
                        break;
                    case FilePatternField.YEAR_TYPE:
                        try {
                            year = Integer.parseInt(subStr);
                            if (field.getLength() == 2) {
                                if (year > 50) {
                                    year += 1900;
                                } else {
                                    year += 2000;
                                }
                            }
                            //Check last year
                            if (field.lstFieldCharacter.size() > 0) {
                                boolean isFilter = false;
                                for (int index = 0; index < field.lstFieldCharacter.size(); index++) {
                                    String endSeq = field.lstFieldCharacter.get(index);
                                    if (endSeq != null && endSeq.length() > 0 && subStr.endsWith(endSeq)) {
                                        isFilter = true;
                                    }
                                }
                                if (!isFilter) {
                                    return false;
                                }
                            }
                        } catch (Exception ex) {
                            return false;
                        }
                        break;
                    case FilePatternField.MON_TYPE:
                        try {
                            mon = Integer.parseInt(subStr);
                            mon--;

                            //Check last mon
                            if (field.lstFieldCharacter.size() > 0) {
                                boolean isFilter = false;
                                for (int index = 0; index < field.lstFieldCharacter.size(); index++) {
                                    String endSeq = field.lstFieldCharacter.get(index);
                                    if (endSeq != null && endSeq.length() > 0 && subStr.endsWith(endSeq)) {
                                        isFilter = true;
                                    }
                                }
                                if (!isFilter) {
                                    return false;
                                }
                            }
                        } catch (Exception ex) {
                            return false;
                        }
                        break;
                    case FilePatternField.DAY_TYPE:
                        try {
                            day = Integer.parseInt(subStr);
                            //Check last day
                            if (field.lstFieldCharacter.size() > 0) {
                                boolean isFilter = false;
                                for (int index = 0; index < field.lstFieldCharacter.size(); index++) {
                                    String endSeq = field.lstFieldCharacter.get(index);
                                    if (endSeq != null && endSeq.length() > 0 && subStr.endsWith(endSeq)) {
                                        isFilter = true;
                                    }
                                }
                                if (!isFilter) {
                                    return false;
                                }
                            }
                        } catch (Exception ex) {
                            return false;
                        }
                        break;
                    case FilePatternField.HOUR_TYPE:
                        try {
                            hour = Integer.parseInt(subStr);
                            //Check last hour
                            if (field.lstFieldCharacter.size() > 0) {
                                boolean isFilter = false;
                                for (int index = 0; index < field.lstFieldCharacter.size(); index++) {
                                    String endSeq = field.lstFieldCharacter.get(index);
                                    if (endSeq != null && endSeq.length() > 0 && subStr.endsWith(endSeq)) {
                                        isFilter = true;
                                    }
                                }
                                if (!isFilter) {
                                    return false;
                                }
                            }
                        } catch (Exception ex) {
                            return false;
                        }
                        break;
                    case FilePatternField.MIN_TYPE:
                        try {
                            min = Integer.parseInt(subStr);
                            //Check last min
                            if (field.lstFieldCharacter.size() > 0) {
                                boolean isFilter = false;
                                for (int index = 0; index < field.lstFieldCharacter.size(); index++) {
                                    String endSeq = field.lstFieldCharacter.get(index);
                                    if (endSeq != null && endSeq.length() > 0 && subStr.endsWith(endSeq)) {
                                        isFilter = true;
                                    }
                                }
                                if (!isFilter) {
                                    return false;
                                }
                            }
                        } catch (Exception ex) {
                            return false;
                        }
                        break;
                    case FilePatternField.SEC_TYPE:
                        try {
                            sec = Integer.parseInt(subStr);
                            //Check last sec
                            if (field.lstFieldCharacter.size() > 0) {
                                boolean isFilter = false;
                                for (int index = 0; index < field.lstFieldCharacter.size(); index++) {
                                    String endSeq = field.lstFieldCharacter.get(index);
                                    if (endSeq != null && endSeq.length() > 0 && subStr.endsWith(endSeq)) {
                                        isFilter = true;
                                    }
                                }
                                if (!isFilter) {
                                    return false;
                                }
                            }
                        } catch (Exception ex) {
                            return false;
                        }
                        break;

                    case FilePatternField.TICTAC_TYPE:
                        try {
                            titac = Integer.parseInt(subStr);
                            //Check last titac
                            if (field.lstFieldCharacter.size() > 0) {
                                boolean isFilter = false;
                                for (int index = 0; index < field.lstFieldCharacter.size(); index++) {
                                    String endSeq = field.lstFieldCharacter.get(index);
                                    if (endSeq != null && endSeq.length() > 0 && subStr.endsWith(endSeq)) {
                                        isFilter = true;
                                    }
                                }
                                if (!isFilter) {
                                    return false;
                                }
                            }
                        } catch (Exception ex) {
                            return false;
                        }
                        break;


                    case FilePatternField.FILEID_TYPE:
                        try {
                            fileId = Long.parseLong(subStr);
                            //Check last fileId
                            if (field.lstFieldCharacter.size() > 0) {
                                boolean isFilter = false;
                                for (int index = 0; index < field.lstFieldCharacter.size(); index++) {
                                    String endSeq = field.lstFieldCharacter.get(index);
                                    if (endSeq != null && endSeq.length() > 0 && subStr.endsWith(endSeq)) {
                                        isFilter = true;
                                    }
                                }
                                if (!isFilter) {
                                    return false;
                                }
                            }
                        } catch (Exception ex) {
                            return false;
                        }
                        break;
                    default:
                        break;
                }
                pos += field.getLength();
            }
            if (pos != tmp.length()) {
                return false;
            }
        } catch (Exception ex) {
            result = false;
        }
        if (year != 0 || mon != 0 || day != 1 || hour != 0 || min != 0 || sec != 0) {
            //fix some data is null
            Date curDate = new Date();
            if (year == 0) {
                year = curDate.getYear() + 1900;
            }
            Calendar cal = new GregorianCalendar(year, mon, day, hour, min, sec);
            //timestamp = cal.getTime();
            timestamp = new Date(cal.getTime().getTime() + titac);
        } else {
            timestamp = null;
        }
        return result;
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
    
       public static String parse(String templateName, long fileId, long sequence) {
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
                String fid = "00000000000000000" + sequence;
                strReturn += fid.substring(fid.length() - (ind - i), fid.length());
                templateName = templateName.substring(ind + 1);
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
    
    public static String parse(String templateName, long fileId, String str) {
        //A{YEAR####}B{MON##}C{DAY##}D{FILEID##########}.txt
        if (templateName == null || templateName.trim().length() == 0) {
            return "AAA" + "_" + fileId + ".txt";
        }
//        Date date = new Date();
        Date cdrDate = new Date();
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
            } else if (templateName.startsWith("{CDR:")) {
                i = 5;
                int ind = templateName.indexOf('}');
                strReturn += str;
                templateName = templateName.substring(ind + 1);
            }
        }
        strReturn += templateName;
        return strReturn;
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
    
}
