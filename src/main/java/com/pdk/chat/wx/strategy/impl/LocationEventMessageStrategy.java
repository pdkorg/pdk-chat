package com.pdk.chat.wx.strategy.impl;

import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.MessageTypeConstant;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.receive.LocationEventMessageReceive;
import com.pdk.chat.wx.session.WeixinMessageSession;
import com.pdk.chat.wx.session.WeixinSessionManager;
import com.pdk.chat.wx.strategy.itf.WeixinEventStrategy;
import com.pdk.chat.wx.util.BaiduLocationUtil;
import com.pdk.chat.wx.util.WeixinMessageUtil;
import com.pdk.chat.wx.util.WxLocationCache;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by hubo on 15/9/29
 */
@Component
public class LocationEventMessageStrategy implements WeixinEventStrategy {

    private final Map<String, Location> locationMap;

    private final static long TWO_HOUR = 2 * 3600 * 1000;

    private static ExecutorService exec = Executors.newSingleThreadExecutor();

    public LocationEventMessageStrategy() {
        this.locationMap = new ConcurrentHashMap<>();
        exec.execute(new ClearThread());
    }

    @PreDestroy
    public void destroy() {
        exec.shutdownNow();
        locationMap.clear();
    }

    @Override
    public String handleEvent(WeixinMessageReceive message) throws WeixinBusinessException {
        return "";
    }

    @Override
    public void asyncNotify(WeixinMessageReceive message, WeixinSessionManager manager) throws BusinessException {

        String openId = message.getFromUserName();

        if(manager.hasWeixinMessageSession(openId)) {

            String locationInfo;
            LocationEventMessageReceive receive = (LocationEventMessageReceive) message;

            long currTime = System.currentTimeMillis();

            if(locationMap.containsKey(openId) && !expire(openId, currTime)) {
                locationInfo = locationMap.get(openId).getInfo();
            } else {
                locationInfo = BaiduLocationUtil.analyseGPSToLoction(receive.getLatitude(), receive.getLongitude());
                if(locationMap.containsKey(openId)) {
                    Location l = locationMap.get(openId);
                    l.setInfo(locationInfo);
                    l.setCreateTime(currTime);
                }else {
                    locationMap.put(openId, new Location(locationInfo, currTime));
                }
            }

            receive.setLocationInfo(locationInfo);

            ChatMessage chatMessage;
            try {
                chatMessage = WeixinMessageUtil.transWeixinMessage2ChatMessage(message);
            } catch (Exception e) {
                throw new WeixinBusinessException("获取微信信息时出现异常",e);
            }

            if(chatMessage == null) {
                return;
            }

            chatMessage.setContent(locationInfo);

            WeixinMessageSession session = manager.getWeixinMessageSession(message.getFromUserName());

            chatMessage.setFromId(session.getUser().getId());
            chatMessage.setMsgType(MessageTypeConstant.TYPE_LOCATION);

            session.receiveMessage(chatMessage);

        }else{
            if(message instanceof LocationEventMessageReceive) {
                LocationEventMessageReceive receive = (LocationEventMessageReceive) message;
                WxLocationCache.getInstance().putLocation(openId,BaiduLocationUtil.analyseGPSToLoction(receive.getLatitude(), receive.getLongitude()));
            }
        }
    }

    private boolean expire(String openId, long currTime) {

        Location l = locationMap.get(openId);

        return currTime - l.getCreateTime() > TWO_HOUR;

    }

    private class Location {

        private String info;

        private long createTime;

        public Location(String info, long createTime) {
            this.info = info;
            this.createTime = createTime;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

    }

    private class ClearThread implements Runnable {
        @Override
        public void run() {
            while (!Thread.interrupted()) {

                long currTime = System.currentTimeMillis();

                for (String openId : locationMap.keySet()) {
                    if (expire(openId, currTime)) {
                        locationMap.remove(openId);
                    }
                }

                try {
                    TimeUnit.MINUTES.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }
}
