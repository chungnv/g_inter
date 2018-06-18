/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.process.filter.importer;

import com.viettel.ginterconnect.process.bean.CdrObject;

public interface BaseImporter {
    //insert Record

    public boolean createDataQueue(String threadCode);

    public boolean insertRecord(String ThreadCode, CdrObject recordBO, String fileName, int resultType) throws Exception;

    public ImportQueue terminateInsert(String threadCode);
}
