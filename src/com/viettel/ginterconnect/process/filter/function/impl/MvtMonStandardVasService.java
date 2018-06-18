/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function.impl;

import com.viettel.ginterconnect.process.bean.CdrObject;
import com.viettel.ginterconnect.process.filter.function.IFunction;
import org.apache.log4j.Logger;

/**
 *
 * @author ubuntu
 */
public class MvtMonStandardVasService implements IFunction {

    @Override
    public Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger) {
//        select
//        case when b.VAS_SERVICE = 'MI' then 'MI_MON' else b.VAS_SERVICE end VAS_SERVICE 
//        from (
//        select * from vtmoz.d_mon_monthly T
//        where product_id != '10535033' and money > 0) a
//        inner join (
//        select product_id, vas_service, minus_money
//        from vtmoz.r_product_id_mon
//        where minus_money = 'YES') b on a.product_id = b.product_id 
        return object.get("VAS_SERVICE");
    }
    
}
