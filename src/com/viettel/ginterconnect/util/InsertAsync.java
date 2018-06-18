package com.viettel.ginterconnect.util;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.client.Bin;
import com.aerospike.client.Key;
import com.aerospike.client.async.*;
import com.aerospike.client.listener.WriteListener;
import com.aerospike.client.policy.ClientPolicy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class InsertAsync {
    private AerospikeClient client;
    private EventLoops eventLoops;
    private final Monitor monitor = new Monitor();
    private final AtomicInteger recordCount = new AtomicInteger();
    private final int recordMax;
    private final int writeTimeout = 50000;
    private final int eventLoopSize;
    private final int concurrentMax;
    private List<Key> lstKey;
    private List<Bin[]> lstBins;

    public InsertAsync(List<Key> lstKey, List<Bin[]> lstBins) {
        // Allocate an event loop for each cpu core.
        eventLoopSize = Runtime.getRuntime().availableProcessors();

        // Allow 40 concurrent commands per event loop.
        concurrentMax = eventLoopSize * 10;

        recordMax = lstKey.size();

        this.lstKey = lstKey;

        this.lstBins = lstBins;
    }

    public void run() throws AerospikeException {
        EventPolicy eventPolicy = new EventPolicy();
        eventPolicy.minTimeout = writeTimeout;

        // Direct NIO
        eventLoops = new NioEventLoops(eventPolicy, eventLoopSize);

        // Netty NIO
        // EventLoopGroup group = new NioEventLoopGroup(eventLoopSize);
        // eventLoops = new NettyEventLoops(eventPolicy, group);

        // Netty epoll (Linux only)
        // EventLoopGroup group = new EpollEventLoopGroup(eventLoopSize);
        // eventLoops = new NettyEventLoops(eventPolicy, group);

        try {
            ClientPolicy clientPolicy = new ClientPolicy();
            clientPolicy.eventLoops = eventLoops;
            clientPolicy.maxConnsPerNode = concurrentMax;
            clientPolicy.writePolicyDefault.setTimeout(writeTimeout);
            clientPolicy.user = SystemParam.AEROSPIKE_USERNAME;
            clientPolicy.password = SystemParam.AEROSPIKE_PASSWORD;
            client = new AerospikeClient(clientPolicy, SystemParam.AEROSPIKE_IP, SystemParam.AEROSPIKE_PORT);

            try {
                writeRecords();
                monitor.waitTillComplete();
                System.out.println("Records written: " + recordCount.get());
            } finally {
                client.close();
            }
        } finally {
            eventLoops.close();
        }
    }

    private void writeRecords() {
        // Write exactly concurrentMax commands to seed event loops.
        // Distribute seed commands across event loops.
        // A new command will be initiated after each command completion in WriteListener.
        for (int i = 1; i <= concurrentMax && i <= recordMax; i++) {
            EventLoop eventLoop = eventLoops.next();
            writeRecord(eventLoop, new AWriteListener(eventLoop), i);
        }
    }

    private void writeRecord(EventLoop eventLoop, WriteListener listener, int keyIndex) {
        Key key = lstKey.get(keyIndex - 1);
        Bin[] bins = lstBins.get(keyIndex - 1);
        client.put(eventLoop, listener, null, key, bins);
    }

    private class AWriteListener implements WriteListener {
        private final EventLoop eventLoop;

        public AWriteListener(EventLoop eventLoop) {
            this.eventLoop = eventLoop;
        }

        @Override
        public void onSuccess(Key key) {
            try {
                int count = recordCount.incrementAndGet();

                // Stop if all records have been written.
                if (count >= recordMax) {
                    monitor.notifyComplete();
                    return;
                }

                if (count % 10000 == 0) {
                    System.out.println("Records written: " + count);
                }

                // Issue one new command if necessary.
                int keyIndex = concurrentMax + count;
                if (keyIndex <= recordMax) {
                    // Write next record on same event loop.
                    writeRecord(eventLoop, this, keyIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
                monitor.notifyComplete();
            }
        }

        @Override
        public void onFailure(AerospikeException e) {
            e.printStackTrace();
            monitor.notifyComplete();
        }
    }
}
