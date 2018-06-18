/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function;

import com.viettel.aerospike.bo.JobsBO;
import com.viettel.ginterconnect.process.exception.FilterException;

/**
 *
 * @author ubuntu
 */
public interface IFilterAspectFunction {
    
    public void filterAspectFunction(JobsBO currentJob, Object mapOutput) throws FilterException;
    
}
