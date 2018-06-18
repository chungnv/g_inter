/*
 * Copyright (C) 2010 Viettel Telecom. All rights reserved.          
 * VIETTEL PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.viettel.ginterconnect.worker.convert.switchboard;

import com.viettel.ginterconnect.worker.convert.BaseConverter;
import org.apache.log4j.Logger;

public class TestConvert {

    /**
     * constructor
     */
    private TestConvert() {
    }

    /**
     * main
     *
     * @throws Exception when an exception occurs
     * @param args input param
     */
    public static void main(String[] args) throws Exception {
//        Session session = null;
        Object[] params = null;
        BaseConverter converter = null;

//        System.out.println("Path cannal: " + (new File(".")).getCanonicalPath());
//        System.out.println("absolute cannal: " + (new File(".")).getAbsolutePath());
//        System.out.println("absolute cannal: " + (new File(".")).getAbsoluteFile());
        try {
//            session = BaseHibernateDAO.getSessionFactory().openSession();
            //HOMEPHONE
//            converter = (BaseConverter) new OCSHomephoneConverter();
            //GGSN
//            converter = (BaseConverter) new GgsnConverterStream();
//            converter = (BaseConverter) new SgsnChsLogConverter();
            //ADSL
//            converter = (BaseConverter) new BRASConverter();
            //GGSN
//            converter = (BaseConverter) new GgsnConverter_1();
            //Ocs pre
//            converter = (BaseConverter) new VerticalContentConverter();
//            converter = (BaseConverter) new SimpleOCSPreConverter();
//            converter = (BaseConverter) new RefundCDRProcessor();
            //Ocs pos
//            converter = (BaseConverter) new OcsPostConverter();
            //cbs ocs pre
//            converter = (BaseConverter) new CBSHuaweiConverter();
//            Ocs post
//            converter = (BaseConverter) new LogCommandVOCSConverter();
//            converter = (BaseConverter) new LogCommandOCSZTERAPostpaidConverter();
//            converter = (BaseConverter) new RARatingOCSPostConverter();
            //sgsn
//              converter = (BaseConverter) new SgsnConverter();
            //SGSN
//                converter = (BaseConverter) new NokiaMSCConverter();
            //PSTN Huawei
//            converter = (BaseConverter) new HuaweiPSTNCdrBinaryConverter();
            //PSTN Zte
//            converter = (BaseConverter) new ZtePSTNCdrBinaryConverter();
            //MSC Ericsson
//              converter = (BaseConverter) new EricssonMSCConverter();
            //MSC Nokia
//            converter = (BaseConverter) new NokiaMSCConverter();
            //MSC Huawei
//                converter = (BaseConverter) new HuaweiGMSCConverter();
            //VTC GMSC Huawei
//                converter = (BaseConverter) new HuaweiMSCConverter();
            //GMSC Zte
//              converter = (BaseConverter) new ZteGMSCConverter();
            //GMSC Huawei
//            converter = (BaseConverter) new TelemorHuaweiMSCConverter();
//            converter = (BaseConverter) new MytelHuaweiMSCConverter();
            converter = (BaseConverter) new AllHuaweiMSCConverter();
//            converter = (BaseConverter) new VTCHuaweiGMSCConverter();
//                converter = (BaseConverter) new STLHuaweiMSCConverter();
            //SMPP
//              converter = (BaseConverter) new SMPPConverter();
            //IGW
//                converter = (BaseConverter) new ZtePSTNCdrBinaryConverter();
            //BRAS
//                  converter = (BaseConverter) new BRASConverter();
            //MMSC
//                  converter = (BaseConverter) new MMSCConverter();
            //SMSC
//                  converter = (BaseConverter) new SMSCConverter();
            //HLR
//                   converter = (BaseConverter) new HLDDumpConverter();
            //SOFT SWITCH
//                   converter = (BaseConverter) new SoftSwitchConverter();

            //LOG OCS
//            converter = (BaseConverter) new LogCommandOCSZTERAConverter();
//            File f = new File("D:\\Converted\\CDR");
//            File[] filename = f.listFiles();
//            for (int i = 0; i < filename.length; i++) {
//                converter = (BaseConverter) new NokiaMSCConverter();
//            params = new Object[]{"D:\\Converted\\CDR\\POSTPAID", "D:\\Converted\\CDR\\POSTPAID", "D:\\Converted\\CDR\\POSTPAID", "txt", 0L,
//                "D:\\Giangvt5\\QT05_13067_RA_NEW\\12.REFERENCE\\newMediation\\src\\Ocs_GGSN_Pos.properties",
//                "GPRS_897155794_b14829256_08.egsnnSnE.20141008084110.normal", Logger.getLogger("hahahah")};
//            converter = (BaseConverter) new LogHomephoneConverter();
            String folder = "/media/ubuntu/Data/Chung/Project/Java/New-Htds/report/gInterconnect/data/moz";
            String configFile = "/media/ubuntu/Data/Chung/Project/Java/New-Htds/report/gInterconnect/config/moz/Huawei_MSC_Config_MOZ.properties";
//            params = new Object[]{folder, folder, folder, "new.txt", 0L,
//                "/media/ubuntu/New Volume/Chung/Project/Java/New-Htds/report/gInterconnect/config/Huawei_MSC_Config_CAM_VTZ.properties",
//                "HW_MSC11_201708084067.dat", Logger.getLogger("hahahah")};
////                "000000162100697_service.log.1", Logger.getLogger("hahahah")};
//            converter.convert(params);
            converter.convert(folder, folder, folder, "GSMP01201805015715.dat",
                    "hw", Logger.getLogger("hahahah"), configFile);
//            }
//                converter = null;
            Thread.sleep(10);
//            }
        } finally {
//            session.close();
            System.out.println("Finished!");
        }
    }
}
