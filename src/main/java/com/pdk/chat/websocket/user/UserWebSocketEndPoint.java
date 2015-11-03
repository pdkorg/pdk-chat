package com.pdk.chat.websocket.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.dto.User;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.util.WebSocketUtil;
import com.pdk.chat.websocket.manage.ChatManager;
import com.pdk.chat.websocket.msg.ChatMessage;
import org.slf4j.Logger;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Date;

/**
 * Created by hubo on 2015/8/21
 */
public class UserWebSocketEndPoint extends AbstractWebSocketHandler {

    private static final Logger log = ChatLoggerUtil.getChatLogger();

    private ChatManager chatManager;

    public UserWebSocketEndPoint() {
//        log.debug("UserWebSocketEndPoint create");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User user = WebSocketUtil.getUserInfo(session);
        JSONObject msgJson = JSON.parseObject(message.getPayload());
        String content = msgJson.getString("content");
        Date timestamp = msgJson.getDate("createTime");

        ChatMessage msg = new ChatMessage();
        msg.setFromId(user.getId());
        msg.setFromName(user.getName());
        msg.setContent(content);
        msg.setCreateTime(timestamp);

        chatManager.sendMsgToCS(msg);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        User user = WebSocketUtil.getUserInfo(session);
        log.debug("client id: {} name: {} connected, time is {} ", user.getId(), user.getName(), new Date());
        chatManager.addUser(user, new UserWebSocketChatMessageSession(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        User user = WebSocketUtil.getUserInfo(session);
        log.debug("client id: {} name: {} connection close, time is {} ", user.getId(), user.getName(), new Date());
        chatManager.removeUser(user);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        log.debug("client id: {} connection close ....");
        chatManager.removeUser(WebSocketUtil.getUserInfo(session));
    }

    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }


}
