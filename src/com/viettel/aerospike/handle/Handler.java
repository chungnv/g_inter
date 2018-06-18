/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.handle;

import com.viettel.aerospike.bo.CdrThreadBO;
import com.viettel.aerospike.bo.MasterThreadBO;
import com.viettel.aerospike.bo.JobsBO;
/**
 *
 * @author QUANGNH
 */
public abstract class Handler {
    public abstract void onHandle(MasterThreadBO object);
    public abstract void onHandleQueryForUpdate(MasterThreadBO object);
    public abstract void onHandle(JobsBO object);
    public abstract void onHandle(CdrThreadBO object);
}
