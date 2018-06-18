/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.queue;

import com.viettel.aerospike.bo.JobsBO;
import java.util.Comparator;

/**
 *
 * @author ubuntu
 */
public class JobComparator implements Comparator<JobsBO> {

    @Override
    public int compare(JobsBO t, JobsBO t1) {
        if (t.getID() < t1.getID()) {
            return -1;
        }
        return 1;
    }
    
}
