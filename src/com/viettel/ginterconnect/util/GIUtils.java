/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.util;

import com.viettel.ginterconnect.process.bean.CdrObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openide.util.Exceptions;

/**
 *
 * @author ubuntu
 */
public class GIUtils {

    public static String extractFlow(String jobFlow, String currentJob) {
        if (jobFlow == null) {
            return null;
        }
        String arrJobFlow[] = jobFlow.split("\\|");
        if (StringUtils.isEmpty(currentJob)) {
            return StringUtils.join(arrJobFlow, "|");
        }
        if (arrJobFlow.length < 2 && arrJobFlow[0].equals(currentJob)) {
            return null;
        }
        if (arrJobFlow[0].toLowerCase().equals(currentJob.toLowerCase())) {
            String newJobFlow[] = new String[arrJobFlow.length - 1];
            System.arraycopy(arrJobFlow, 1, newJobFlow, 0, arrJobFlow.length - 1);
            return StringUtils.join(newJobFlow, "|");
        }
        return null;
    }

    public static String nextStep(String jobFlow, String currentJob) {
        if (jobFlow == null) {
            return null;
        }
        String flow = extractFlow(jobFlow, currentJob);
        if (flow != null) {
            return flow.split("\\|")[0];
        }
        return null;
    }

    public static String genSysDateStr(String format) {
        return (new SimpleDateFormat(format)).format(new Date());
    }
    
    public static String genDateStr(String format, Date date) {
        return (new SimpleDateFormat(format)).format(date);
    }

    public static String genSysDateStr(String format, long addedTime) {
        return (new SimpleDateFormat(format)).format(new Date(System.currentTimeMillis() + addedTime));
    }

    public static Date strToDate(String str, String format) {
        try {
            return (new SimpleDateFormat(format)).parse(str);
        } catch (ParseException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    public static Object coalesce(Object... objects) {
        for (Object o : objects) {
            if (o != null) {
                return o;
            }
        }
        return null;
    }

    public static double coalesceDouble(Object obg, double value) {
        if (obg == null) {
            return value;
        }
        try {
            return Double.parseDouble(obg.toString());
        } catch (Exception nfe) {
            return value;
        }
    }

    public static double coalesceDouble(CdrObject cdr, double value, String... fields) {
        if (cdr == null) {
            return value;
        }
        Double val = 0.0;
        for (String field : fields) {
            val += coalesceDouble(cdr.get(field), value);
        }
        return val;
    }

    public static boolean timeInSleepDuration(String time) {
        if (time == null || "".equals(time.trim())) {
            return false;
        }
        try {
            Date checkTime = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT)).parse(time);
            if ((System.currentTimeMillis() - checkTime.getTime()) > 0) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            System.out.println("Exception: exception while parse master time " + time);
            ex.printStackTrace();
            return true;
        }
    }

    public static boolean greaterSysdateWithGivenTime(String time, long timeDurationInSec) {
        if (time == null || "".equals(time.trim())) {
            return false;
        }
        try {
            Date checkTime = (new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT)).parse(time);
            if ((System.currentTimeMillis() - checkTime.getTime()) > timeDurationInSec * 1000) {
                return true;
            }
            return false;
        } catch (Exception ex) {
            System.out.println("Exception: exception while parse master time " + time);
            ex.printStackTrace();
            return true;
        }
    }

    public static String getIpAddressLstString() {
        String ret = "IPLst";
        String regex = "\\d{1}.*";
        try {
            ArrayList<String> lstHostAddress = new ArrayList();

            java.util.Enumeration e = java.net.NetworkInterface.getNetworkInterfaces();
            java.net.NetworkInterface n;
            while (e.hasMoreElements()) {
                n = (java.net.NetworkInterface) e.nextElement();
                java.util.Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    java.net.InetAddress i = (java.net.InetAddress) ee.nextElement();
                    if (!i.isLoopbackAddress()) {

                        if (i.getHostAddress().matches(regex)) {
                            lstHostAddress.add(i.getHostAddress());
                        }
                    }
                }
            }
            Collections.sort(lstHostAddress);
            for (String ipHost : lstHostAddress) {
                ret = ret + ":" + ipHost;
            }
            return ret.length() <= 40 ? ret : ret.substring(0, 40);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    public static Date getStartTime(String timeSchedule, Date previousDate) {
        String[] tmp = timeSchedule.split(" ");
        String[] time = tmp[0].split(":");
        String[] date = tmp[1].split("/");
        int bonus = Integer.parseInt(tmp[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(previousDate);

        int nextTime = Calendar.SECOND;

        if (!"*".equals(time[0])) {
            nextTime = Calendar.MINUTE;
            calendar1.set(Calendar.SECOND, Integer.parseInt(time[0]));
            if (!"*".equals(time[1])) {
                nextTime = Calendar.HOUR;
                calendar1.set(Calendar.MINUTE, Integer.parseInt(time[1]));
                if (!"*".equals(time[2])) {
                    nextTime = Calendar.DAY_OF_MONTH;
                    calendar1.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[2]));
                    if (!"*".equals(date[0])) {
                        nextTime = Calendar.MONTH;
                        calendar1.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]));
                        if (!"*".equals(date[1])) {
                            nextTime = Calendar.YEAR;
                            calendar1.set(Calendar.MONTH, Integer.parseInt(date[1]) - 1);
                            if (!"*".equals(date[2])) {
                                nextTime = -1;
                                calendar1.set(Calendar.YEAR, Integer.parseInt(date[2]));
                            }
                        }
                    }
                }
            }
        }
        if (nextTime != -1) {
            while (calendar.getTime().after(calendar1.getTime())) {
                calendar1.set(nextTime, calendar1.get(nextTime) + bonus);
            }
        }

        return calendar1.getTime();
    }

    public static int indexOfAny(String input, String[] array) {
        List<String> lst = Arrays.asList(array);
        if (lst.contains(input)) {
            return 1;
        } else {
            return -1;
        }
    }

    public static long getTimeInSec(Date chargingTime, String hour) throws Exception {
        String checkDate = (new SimpleDateFormat("yyyyMMdd").format(chargingTime)) + hour;
        return (new SimpleDateFormat("yyyyMMddHH:mm:ss")).parse(checkDate).getTime() / 1000;
    }

    public static long getIntersec(long start1, long end1, long start2, long end2) {
        long totalRange = Math.max(end1, end2) - Math.min(start1, start2);
        long sumOfRange = (end1 - start1) + (end2 - start2);
        long overlap = 0;
        if (sumOfRange > totalRange) {
            overlap = Math.min(end1, end2) - Math.max(start1, start2);
        }
        return overlap;
    }
    
    public static void extractByCmd(final String filePath, final String extractPath, final Long fileId, final Long isAddFileId, final String password) throws Exception {
        String cmd = "unzip " + filePath + " -d " + extractPath;
        Runtime rt = Runtime.getRuntime();
        Process process = rt.exec(cmd);
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new Exception("Extract error cmd: " + cmd);
        }
    }
    
}
