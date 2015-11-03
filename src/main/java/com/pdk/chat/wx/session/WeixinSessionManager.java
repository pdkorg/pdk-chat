package com.pdk.chat.wx.session;

import com.pdk.chat.dto.User;
import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.AppConfig;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.websocket.manage.ChatManager;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.service.FansService;
import com.pdk.chat.wx.service.WeixinMessageService;
import com.pdk.chat.wx.util.MessageTypeEnum;
import com.pdk.chat.wx.util.WxLocationCache;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kangss on 2015/8/25
 */
public class WeixinSessionManager {

    private static WeixinSessionManager manager = null;
    private Map<String,WeixinMessageSession> map = null;

    private Map<String,User> userMap = null;
    private FansService fansService;

    private WeixinMessageService weixinMessageService;
    private ChatManager chatManager;

    private ExecutorService exec ;

    private long CD = 3600*1000;

    private ChatManager getChatManager() {
        if(chatManager == null)
            chatManager = AppConfig.getBean(ChatManager.class);
        return chatManager;
    }


    private WeixinSessionManager(){
        map = new ConcurrentHashMap<>();
        userMap = new ConcurrentHashMap<>();
        fansService = AppConfig.getBean(FansService.class);
        weixinMessageService = AppConfig.getBean(WeixinMessageService.class);
        createClearSessionThread();
    }

    public static WeixinSessionManager getInstance(){
        if(manager == null){
            synchronized (WeixinSessionManager.class){
                if(manager == null){
                    manager = new WeixinSessionManager();
                }
            }
        }
        return manager;
    }

    public WeixinMessageSession getWeixinMessageSession(String sourceId) throws BusinessException {
        WeixinMessageSession session;
        if(map.containsKey(sourceId) && (session =  map.get(sourceId)).isOpen()){
            return session;
        }
        synchronized (this) {
            if(!map.containsKey(sourceId)) {
                session = createWeixinMessageSession(sourceId);
            } else {
                session = map.get(sourceId);
            }
        }
        return session;
    }

    public boolean hasWeixinMessageSession(String sourceId) throws BusinessException {
        return map.containsKey(sourceId) && map.get(sourceId).isOpen();
    }

    public synchronized void removeWeixinMessageSession(String sourceId) throws BusinessException {
        WeixinMessageSession session = map.remove(sourceId);
        if(session != null) {
            User user =  session.getUser();
            userMap.remove(user.getId());
            getChatManager().removeUser(user);
            session.close();
            session.destroy();
        }

    }

    public synchronized WeixinMessageSession createWeixinMessageSession(String sourceId) throws BusinessException {
        if(map.containsKey(sourceId)) {
            removeWeixinMessageSession(sourceId);
        }
        User user = fansService.queryByOpenid(sourceId);
        if(user == null){
            user = new User();
            user.setSourceId(sourceId);
            user.setName("未注册用户");
            user.setUserType("微信");
        }
        String locationInfo = WxLocationCache.getInstance().removeLocation(sourceId);
        if(!StringUtils.isEmpty(locationInfo))
            user.setLocationInfo(locationInfo);
        WeixinMessageSession session = new WeixinMessageSession();
        session.setStartTime(new Date());
        session.setUser(user);
        map.put(sourceId,session);
        userMap.put(user.getId(),user);
        getChatManager().addUser(user, session);
        return session;
    }

    public User getUser(String userId) throws WeixinBusinessException {
        return userMap.get(userId);
    }


    public String sendMessageToCS(WeixinMessageReceive message) throws BusinessException {
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByWxmsgType(message.getMsgType());
        if(messageTypeEnum == MessageTypeEnum.TYPE_EVENT) {
            return weixinMessageService.handleEvent(message, this);
        } else {
            WeixinMessageSession session = getWeixinMessageSession(message.getFromUserName());
            return weixinMessageService.receiveMessage(message, session);
        }
    }

    @PreDestroy
    public void destroy() {
        exec.shutdownNow();
        map.clear();
        userMap.clear();
    }

    public String sendMessageToWeixin(ChatMessage message) throws BusinessException {
        String openId = getUser(message.getSendToId()).getSourceId();
        return weixinMessageService.sendMessage(message, openId);
    }

    private void createClearSessionThread() {
        exec = Executors.newCachedThreadPool();
        exec.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        long time = System.currentTimeMillis() - (3600 * 24 * 2 * 1000) + CD;
                        for (Map.Entry<String, WeixinMessageSession> entry : map.entrySet())
                            if (entry.getValue().getStartTime().getTime() <= time) {
                                removeWeixinMessageSession(entry.getKey());
                            }
                        Thread.sleep(CD);
                    }
                }catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    ChatLoggerUtil.getInfoLogger().error(e.getMessage(),e);
                } catch (Exception e) {
                    ChatLoggerUtil.getInfoLogger().error(e.getMessage(),e);
                }
            }
        });
    }

    public List<User> listUser() {
        return new ArrayList<>(userMap.values());
    }

 }
