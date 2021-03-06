/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.process.filter.function.IFilterAspectFunction;
import com.viettel.ginterconnect.process.exception.FilterException;
import com.viettel.ginterconnect.util.GIClient;
import java.util.Map;

/**
 *
 * @author ubuntu
 */
public class VcrPostFilter implements IFilterAspectFunction {

    @Override
    public void filterAspectFunction(JobsBO currentJob, Object mapOutput) throws FilterException {
        try {
            Map<String, Map<String, Object>> mapNewRevenue = (Map<String, Map<String, Object>>) mapOutput;
            GIClient.getInstance().mergeRevenue(mapNewRevenue, "VCR");
        } catch (Exception ex) {
            throw ex;
        }
    }
    
}
