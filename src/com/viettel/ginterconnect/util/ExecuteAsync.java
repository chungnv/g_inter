package com.viettel.ginterconnect.util;

import com.aerospike.client.*;
import com.aerospike.client.async.*;
import com.aerospike.client.listener.ExecuteListener;
import com.aerospike.client.listener.WriteListener;
import com.aerospike.client.policy.ClientPolicy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecuteAsync {
    private AerospikeClient client;
    private EventLoops eventLoops;
    private final Monitor monitor = new Monitor();
    private final AtomicInteger recordCount = new AtomicInteger();
    private final int recordMax;
    private final int writeTimeout = 50000;
    private final int eventLoopSize;
    private final int concurrentMax;
    private List<Key> lstKey;
    private List<Value[]> lstValues;
    private String packageName;
    private String function;

    public ExecuteAsync(String packeageName, String function, List<Key> lstKey, List<Value[]> lstValues) {
        // Allocate an event loop for each cpu core.
        eventLoopSize = Runtime.getRuntime().availableProcessors();

        // Allow 40 concurrent commands per event loop.
//        concurrentMax = eventLoopSize * 10;
        concurrentMax = 5;

        recordMax = lstKey.size();

        this.lstKey = lstKey;

        this.lstValues = lstValues;

        this.packageName = packeageName;

        this.function = function;
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
            AsyncClientPolicy clientPolicy = new AsyncClientPolicy();
            clientPolicy.eventLoops = eventLoops;
            clientPolicy.maxConnsPerNode = concurrentMax;
            clientPolicy.writePolicyDefault.setTimeout(writeTimeout);
            clientPolicy.user = SystemParam.AEROSPIKE_USERNAME;
            clientPolicy.asyncSelectorTimeout = writeTimeout;
            clientPolicy.asyncSelectorThreads = concurrentMax;
            clientPolicy.password = SystemParam.AEROSPIKE_PASSWORD;
            String s[] = SystemParam.AEROSPIKE_IP.split(",");
            Host[] aeroHost = new Host[s.length];
            for (int i = 0; i < s.length; i++) {
                Host host = new Host(s[i], SystemParam.AEROSPIKE_PORT);
                aeroHost[i] = host;
            }
//            client = new AsyncClient(clientPolicy, SystemParam.getAerospikeIp(), SystemParam.getAerospikePort());
            client = new AsyncClient(clientPolicy, aeroHost);

            try {
                executeBatch();
                monitor.waitTillComplete();
                System.out.println("Records written: " + recordCount.get());
            } finally {
                client.close();
            }
        } finally {
            eventLoops.close();
        }
    }

    private void executeBatch() {
        // Write exactly concurrentMax commands to seed event loops.
        // Distribute seed commands across event loops.
        // A new command will be initiated after each command completion in WriteListener.
        for (int i = 1; i <= concurrentMax && i <= recordMax; i++) {
            EventLoop eventLoop = eventLoops.next();
            execute(eventLoop, new AExecuteListener(eventLoop), i);
        }
    }

    private void execute(EventLoop eventLoop, ExecuteListener listener, int keyIndex) {
        Key key = lstKey.get(keyIndex - 1);
        Value[] values = lstValues.get(keyIndex - 1);
        client.execute(eventLoop, listener, null, key, packageName, function, values);
    }

    private class AExecuteListener implements ExecuteListener {
        private final EventLoop eventLoop;

        public AExecuteListener(EventLoop eventLoop) {
            this.eventLoop = eventLoop;
        }

        @Override
        public void onSuccess(Key key, Object o) {
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
                    execute(eventLoop, this, keyIndex);
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
