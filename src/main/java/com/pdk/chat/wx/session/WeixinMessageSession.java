package com.pdk.chat.wx.session;

import com.pdk.chat.dto.User;
import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.util.AppConfig;
import com.pdk.chat.websocket.manage.ChatManager;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.websocket.msg.ChatMessageSession;
import com.pdk.chat.wx.service.WeixinMessageService;

import java.util.Date;

/**
 * Created by kangss on 2015/8/25
 */
public class WeixinMessageSession implements ChatMessageSession {

    private Date startTime;

    private volatile boolean open = true;

    public Date getStartTime() {
        return startTime;
    }

    private User user;

    private ChatManager chatManager;

    private ChatManager getChatManager() {
        if(chatManager == null)
            chatManager = AppConfig.getBean(ChatManager.class);
        return chatManager;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String receiveMessage( ChatMessage message) throws BusinessException {
        startTime = new Date();
        getChatManager().sendMsgToCS(message);
       return "";
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) throws BusinessException {
        this.user = user;
    }

    @Override
    public void sendMessage(ChatMessage message) throws BusinessException {
        WeixinSessionManager.getInstance().sendMessageToWeixin(message);
    }

    @Override
    public void destroy(){
    }

    @Override
    public synchronized void close() {
        open = false;
    }

    @Override
    public boolean isOpen() {
        return open;
    }
}
