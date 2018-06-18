/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.ginterconnect.process.filter.function;

import com.viettel.ginterconnect.process.bean.CdrObject;
import org.apache.log4j.Logger;

/**
 *
 * @author
 */
public interface IFunction {
    public abstract Object execute(CdrObject object, String inputField, String params, String fieldParams, Object filterCache, Logger logger ) throws Exception;
}
