package com.viettel.ginterconnect.process.main;

import com.aerospike.client.Bin;
import com.viettel.ginterconnect.util.GIClient;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by hoangsinh on 25/08/2017.
 */
public class ImportTextToAerospike {
    public static void main(String[] args) {
        try {
            GIClient client = GIClient.getInstance();
//            client.deleteSet("VTB_ISDN_HOME", client.getNameSpace());
            BufferedReader bfr = new BufferedReader(new FileReader(args[0]));
//            BufferedReader bfr = new BufferedReader(new FileReader("../config/config.cfg"));
            String strRecord = bfr.readLine();
            int count = 0;
            while (strRecord != null) {
                strRecord = strRecord.replaceAll("\"", "");
                String[] record = strRecord.split("\\|");
//                String[] record = strRecord.split(",");
                Bin[] bins = new Bin[record.length];
                bins[0] = new Bin("ISDN", record[0].trim());
                bins[1] = new Bin("BTS_CODE", record[1].trim());
                bins[2] = new Bin("CENTER_CODE", record[2]);
                bins[3] = new Bin("PROVINCE_CODE", record[3]);
                String market = record[4] == null ? "" : record[4];
                if ("PERU".equals(market)) {
                    market = "VTP";
                }
                if ("VTCM".equals(market)) {
                    market = "VCR";
                }
                bins[4] = new Bin("MARKET", market);
                bins[5] = new Bin("PARTITION", record[5].trim());
//                System.out.println(strRecord);
                client.insertToSet(client.getNameSpace(), args[1], bins);
                strRecord = bfr.readLine();
                count++;
                if (count % 10000 == 0) {
                    System.out.println(count);
                }
            }
            System.out.println("total isnert: " + count);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
