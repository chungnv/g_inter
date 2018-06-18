/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.aerospike.processor;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.Record;
import com.aerospike.client.policy.GenerationPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.viettel.aerospike.main.Parameters;
import org.apache.log4j.Logger;

public class Sequence {

    private static Logger logger = Logger.getLogger(Sequence.class);

    public Sequence() {
    }

    public long getSequence(AerospikeClient client, Parameters params) throws Exception {
        long sq = 0;
        while (true) {
            try {
                Key key = new Key(params.getNamespace(), params.getSet(), 1);
                Record userRecord = client.get(null, key);
                sq = userRecord.getLong("SEQUENCE") + 1;
                WritePolicy writePolicy = new WritePolicy();
                writePolicy.generation = userRecord.generation;
                writePolicy.generationPolicy = GenerationPolicy.EXPECT_GEN_EQUAL;
                Bin passwordBin = new Bin("SEQUENCE", sq);
                client.put(writePolicy, key, passwordBin);
                break;
            } catch (AerospikeException ae) {
                if (ae.getMessage().contains("Error Code 3")) {
                    //do nothing;
                } else {
                    logger.error(ae.getMessage(), ae);
                    throw ae;
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage() + " namespace: " + params.getNamespace() + " set: " + params.getSet(), ex);
                throw ex;
            }
        }
        return sq;
    }

}
