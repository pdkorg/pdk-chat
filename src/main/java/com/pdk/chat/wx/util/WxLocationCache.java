package com.pdk.chat.wx.util;

import com.pdk.chat.util.ChatLoggerUtil;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by kangss on 2015/10/21
 */
public class WxLocationCache {
    private ConcurrentHashMap<String, Location> locationMap;
    private static WxLocationCache cache = null;
    private ExecutorService exec;

    private WxLocationCache() {
        locationMap = new ConcurrentHashMap<>();
        exec = Executors.newSingleThreadExecutor();
        exec.execute(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    try {
                        long curTime = System.currentTimeMillis();
                        for (Map.Entry<String, Location> entry : locationMap.entrySet()) {
                            Location location = entry.getValue();
                            if (curTime - location.getCreateTime() > 7200) {
                                locationMap.remove(entry.getKey());
                            }
                        }
                        TimeUnit.SECONDS.sleep(7200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
                    } catch (Exception e) {
                        ChatLoggerUtil.getInfoLogger().error(e.getMessage(), e);
                    }
                }
            }
        });

    }

    public static WxLocationCache getInstance() {
        if (cache == null) {
            synchronized (WxLocationCache.class) {
                if(cache == null) {
                    cache = new WxLocationCache();
                }
            }
        }
        return cache;
    }

    public String getLocation(String openId) {
        Location location = locationMap.get(openId);
        if (location != null) {
            location.getLocationInfo();
        }
        return null;
    }

    public void putLocation(String openId, String locationInfo) {
        locationMap.put(openId, new Location(locationInfo, System.currentTimeMillis()));
    }

    public String removeLocation(String openId) {
        Location location = locationMap.remove(openId);
        if (location != null)
            return location.getLocationInfo();
        return null;
    }

    public void clear() {
        locationMap.clear();
    }


    private class Location {
        private String locationInfo;
        private long createTime;

        public Location(String locationInfo, long createTime) {
            this.locationInfo = locationInfo;
            this.createTime = createTime;
        }

        public String getLocationInfo() {
            return locationInfo;
        }

        public long getCreateTime() {
            return createTime;
        }
    }

    @PreDestroy
    public void destroy() {
        if (exec != null) {
            exec.shutdownNow();
        }
        clear();

    }
}
