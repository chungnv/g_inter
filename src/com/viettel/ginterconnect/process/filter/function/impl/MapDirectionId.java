package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import org.apache.log4j.Logger;

/**
 * Created by hoangsinh on 03/08/2017.
 */
public class MapDirectionId implements IFunction {
    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
        /*case when EVENT_TYPE in('5005','6255','7021','7022','7149') then 1
        when EVENT_TYPE in ('5006','6256','5010') then  2
        when EVENT_TYPE in ('5009','7105')  then 3
        when EVENT_TYPE in ('62', '63', '64', '7023')  then 4
        when EVENT_TYPE in ('5007', '5008','7145')  then 5
        else -1
        end DIRECTION_ID*/
        Long directionId = -1L;
        if (object.containsProperty("EVENT_TYPE") && object.get("EVENT_TYPE") != null) {
            if ("5005".equals(object.get("EVENT_TYPE").toString())
                    || "6255".equals(object.get("EVENT_TYPE").toString())
                    || "7021".equals(object.get("EVENT_TYPE").toString())
                    || "7022".equals(object.get("EVENT_TYPE").toString())
                    || "7149".equals(object.get("EVENT_TYPE").toString())) {
                directionId = 1L;
            } else if ("5006".equals(object.get("EVENT_TYPE").toString())
                    || "6256".equals(object.get("EVENT_TYPE").toString())
                    || "5010".equals(object.get("EVENT_TYPE").toString())) {
                directionId = 2L;
            } else if ("5009".equals(object.get("EVENT_TYPE").toString())
                    || "7105".equals(object.get("EVENT_TYPE").toString())) {
                directionId = 3L;
            } else if ("62".equals(object.get("EVENT_TYPE").toString())
                    || "63".equals(object.get("EVENT_TYPE").toString())
                    || "64".equals(object.get("EVENT_TYPE").toString())
                    || "7023".equals(object.get("EVENT_TYPE").toString())) {
                directionId = 4L;
            } else if ("5007".equals(object.get("EVENT_TYPE").toString())
                    || "5008".equals(object.get("EVENT_TYPE").toString())
                    || "7145".equals(object.get("EVENT_TYPE").toString())) {
                directionId = 5L;
            }
        }
        object.set("DIRECTION_ID", directionId);
        return object;
    }
}
