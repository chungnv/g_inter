/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.cached.StlTimeOffPeakCache;
import com.viettel.ginterconnect.cached.ThreadCached;
import com.viettel.ginterconnect.cached.bean.CommonTimeOffPeak;
import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import com.viettel.ginterconnect.util.GIUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class STLStandardTimeOffPeak implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) throws Exception {
        Long timeOffPeakId = object.getLong(fieldParams);
        if (timeOffPeakId == null || timeOffPeakId < 0) {
            return object;
        }
        try {
            StlTimeOffPeakCache stlTimeOffPeakCache = (StlTimeOffPeakCache) ThreadCached.initCache("StlTimeOffPeakCache", params, "");

            List<CommonTimeOffPeak> lstTimeOffPeak = (List<CommonTimeOffPeak>) stlTimeOffPeakCache.getCached().get(timeOffPeakId);
            //validate
            if (lstTimeOffPeak == null) {
                return object;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
            Date chargingTimeDate = ((Date) object.get("CHARGING_TIME"));
            String dateString = (new SimpleDateFormat("yyyyMMdd")).format(chargingTimeDate);
            long chargingTimeInSec = chargingTimeDate.getTime() / 1000;
            Long duration = object.getLong("DURATION");
            long endTimeInSec = chargingTimeInSec + duration;
            Map<String, Long> mapPeakTime = new HashMap<>();
            for (CommonTimeOffPeak timeOffPeak : lstTimeOffPeak) {
                Date startTime = sdf.parse(dateString + timeOffPeak.getStartHour());
                Date endTime = sdf.parse(dateString + timeOffPeak.getEndHour());
                long intersec = 0;
                if (endTime.after(startTime)) {
                    intersec = calculateIntersec(sdf, chargingTimeInSec, endTimeInSec, timeOffPeak.getStartHour(), timeOffPeak.getEndHour(), dateString);
                } else {
                    if (chargingTimeDate.after(endTime)) {
                        long startTimeOffPeakInSec = ((sdf.parse(dateString + "00:00:00")).getTime() + 24 * 60 * 60 * 1000) / 1000;
                        long endTimeOffPeakInSec = ((sdf.parse(dateString + timeOffPeak.getEndHour())).getTime() + 24 * 60 * 60 * 1000) / 1000;
                        intersec += GIUtils.getIntersec(chargingTimeInSec, endTimeInSec, startTimeOffPeakInSec, endTimeOffPeakInSec);
                        long val = calculateIntersec(sdf, chargingTimeInSec, endTimeInSec, timeOffPeak.getStartHour(), "23:59:59", dateString);
                        if (val > 0) {
                            intersec += val + 1;
                        }
                    } else {
                        intersec += calculateIntersec(sdf, chargingTimeInSec, endTimeInSec, "00:00:00", timeOffPeak.getEndHour(), dateString);
                        long val = calculateIntersec(sdf, chargingTimeInSec, endTimeInSec, timeOffPeak.getStartHour(), "23:59:59", dateString);
                        if (val > 0) {
                            intersec += val + 1;
                        }
                    }
                }
                Long time = mapPeakTime.get(timeOffPeak.getItemType());
                mapPeakTime.put(timeOffPeak.getItemType(), time != null ? (time + intersec) : intersec);
            }
            Iterator<String> ite = mapPeakTime.keySet().iterator();
            while (ite.hasNext()) {
                String key = ite.next();
                object.set(key, mapPeakTime.get(key));
            }
            return object;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static long calculateIntersec(SimpleDateFormat sdf, long chargeStart, long chargeEnd, String start, String end, String dateString) throws Exception {
        long startTimeOffPeakInSec = sdf.parse(dateString + start).getTime() / 1000;
        long endTimeOffPeakInSec = sdf.parse(dateString + end).getTime() / 1000;
        long val = GIUtils.getIntersec(chargeStart, chargeEnd, startTimeOffPeakInSec, endTimeOffPeakInSec);
        return val;
    }

}
