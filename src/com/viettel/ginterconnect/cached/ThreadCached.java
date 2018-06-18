package com.viettel.ginterconnect.cached;

import com.viettel.ginterconnect.util.Constants;
import com.viettel.ginterconnect.util.GIUtils;
import com.viettel.mmserver.base.ProcessThreadMX;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.HashMap;
import org.apache.log4j.Logger;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONObject;

/**
 * Created by hoangsinh on 18/08/2017.
 */
public abstract class ThreadCached extends ProcessThreadMX {

    private static ConcurrentHashMap<String, ThreadCached> mapCache = new ConcurrentHashMap<>();
    private final Lock _mutex = new ReentrantLock(true);
    private Map cache = new HashMap<>();
    protected Logger threadCachedLogger = Logger.getLogger(ThreadCached.class);
    private boolean isLoading = true;
    private boolean first = true;

    protected boolean reloadByWS = false;
    protected boolean needReload = false;

    protected String timeSchedule;

    private Date prevDate;

    protected long timeSleep = 100000;
    private TimerTask task;

    private Timer timer = new Timer();
    private String lastReloadTime;
    
    protected String marketId;

    public ThreadCached(String threadName, String description) {
        super(threadName, description);
        timer = new Timer();
        prevDate = new Date();
    }

    private void firstProcess() {
        isLoading = true;
        first = false;
        try {
            _mutex.lock();
            reloadCache(cache);
        } catch (Exception ex) {
            threadCachedLogger.error(ex.getMessage(), ex);
            System.exit(1);
        } finally {
            threadCachedLogger.info(threadName + " finish reload cache....");
            _mutex.unlock();
        }
        isLoading = false;
        if (reloadByWS) {
            needReload = false;
            lastReloadTime = GIUtils.genSysDateStr(Constants.DEFAULT_DATE_FORMAT);
        }
    }

    @Override
    protected void process() {
        threadCachedLogger.info(threadName + " refesh cached ");
        if (!reloadByWS || needReload || first) {
            firstProcess();
        }

        final Object wait = new Object();

        if (timeSchedule != null) {
            threadCachedLogger.info(threadName + " Cache will be reloaded at " + GIUtils.getStartTime(timeSchedule, prevDate));

            if (timer != null) {
                timer.schedule(task = new TimerTask() {
                    @Override
                    public void run() {
                        threadCachedLogger.info(threadName + " run ");
                        synchronized (wait) {
                            wait.notifyAll();
                        }
                    }
                }, GIUtils.getStartTime(timeSchedule, prevDate));
                synchronized (wait) {
                    try {
                        threadCachedLogger.info(threadName + " Waiting for task to complete...");
                        wait.wait();
                    } catch (InterruptedException e) {
                        threadCachedLogger.error(e.getMessage(), e);
                    }
                }
            }
        } else {
            if (timeSleep > 0) {
                sleep(timeSleep);
            } else {
                this.stop();
            }
        }
    }

    public Map getCached() {
        if (isLoading) {
            if (!_mutex.tryLock()) {
                threadCachedLogger.info(threadName + " wait cache refeshing...");
                _mutex.lock();
            }
            _mutex.unlock();
        }
        return cache;
    }

    protected abstract void reloadCache(Map cache);

    protected abstract void parseParams(String params) throws Exception;

    protected void parseReloadFromWeb(JSONObject object) {
        if (object.has("reload_from_web")) {
            if ("1".equals(object.getString("reload_from_web"))) {
                reloadByWS = true;
                CacheManagement.getInstance().addToManager(threadName, this);
            }
        }
    }

    public static synchronized ThreadCached initCache(String threadName, String params, String marketId) throws Exception {

        if (!mapCache.containsKey(threadName + marketId)) {
            Class clazz = Class.forName("com.viettel.ginterconnect.cached." + threadName);
            Constructor contructor = clazz.getConstructor(String.class, String.class);
            ThreadCached newCache = (ThreadCached) contructor.newInstance(new Object[]{threadName + marketId, threadName + marketId});
            mapCache.put(threadName + marketId, newCache);
            newCache.setMarketId(marketId);
            newCache.parseParams(params);
            newCache.start();
            while (newCache.isIsLoading()) {
                newCache.sleep(100);
            }
        }
        return mapCache.get(threadName + marketId);
    }

    public static boolean containsKey(String key) {
        return mapCache != null && mapCache.containsKey(key);
    }

    public boolean isIsLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ex) {
        }
    }

    public boolean isReloadByWS() {
        return reloadByWS;
    }

    public void setReloadByWS(boolean reloadByWS) {
        this.reloadByWS = reloadByWS;
    }

    public boolean isNeedReload() {
        return needReload;
    }

    public void setNeedReload(boolean needReload) {
        this.needReload = needReload;
    }

    public String getLastReloadTime() {
        return lastReloadTime;
    }

    public void setLastReloadTime(String lastReloadTime) {
        this.lastReloadTime = lastReloadTime;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }
}
