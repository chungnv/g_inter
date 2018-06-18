package com.viettel.ginterconnect.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class GIQueue {

    public static final int SUCCESS = 0;
    public static final int MAX_LIMIT = -1;
    public static final int ERROR = -2;
    private final Queue<Object> queue;
    private final Object notify = new Object();
    private int maxLimit; //MaxLimit <= 0 No limit; > 0 gioi han

    /**
     * Fix ERROR reload
     *
     * @param maxLimit
     */
    public void setMaxLimit(int maxLimit) {
        this.maxLimit = maxLimit;
    }

    /**
     * Constructor
     *
     * @param maxLimit
     */
    public GIQueue(int maxLimit) {
        queue = new LinkedList<>();
        this.maxLimit = maxLimit;
    }

    /**
     *
     * @param object
     * @return 0: Success \n -1:Queue reach limit
     */
    public int enqueue(Object object) {
        synchronized (notify) {
            if (maxLimit > 0 && queue.size() > maxLimit) {
                return MAX_LIMIT;
            }
            queue.add(object);
            notify.notify();
            return SUCCESS;
        }
    }

    /**
     *
     * @return
     */
    public boolean isMaxLimit() {
        synchronized (notify) {
            if (maxLimit > 0) {
                return queue.size() >= maxLimit;
            }
            return false;
        }
    }

    /**
     * Dequeue
     *
     * @return
     * @throws InterruptedException
     */
    public Object dequeue() throws InterruptedException {
        synchronized (notify) {
            if (queue.isEmpty()) {
                notify.wait(500);
            }
            return queue.poll();
        }
    }

    /**
     * Dequeue theo lo
     *
     * @param numberRecord
     * @return
     * @throws InterruptedException
     */
    public ArrayList<Object> dequeue(int numberRecord) throws InterruptedException {
        synchronized (notify) {
            if (queue.isEmpty()) {
                notify.wait(500);
            }
            int max = numberRecord;
            if (max > queue.size()) {
                max = queue.size();
            }
            ArrayList<Object> list = new ArrayList<Object>();
            for (int i = 0; i < max; i++) {
                list.add(queue.poll());

            }
            return list;
        }
    }

    public int getQueueSize() {
        synchronized (notify) {
            return queue.size();
        }
    }
}
