package com.pdk.chat.websocket.cs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.dto.User;
import com.pdk.chat.util.*;
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
 * 客服客户端接入入口
 * Created by hubo on 2015/8/20
 */
public class CSWebSocketEndPoint extends AbstractWebSocketHandler {

    private static final Logger log = ChatLoggerUtil.getChatLogger();

    private ChatManager chatManager;

    public CSWebSocketEndPoint() {
        log.debug("{} ---- 客服WebSocket启动！", CommonUtil.formatDate(new Date()));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        User cs = WebSocketUtil.getUserInfo(session);
        JSONObject msgJson = JSON.parseObject(message.getPayload());
        String content = msgJson.getString("content");
        Date timestamp = msgJson.getDate("createTime");
        String sendToId = msgJson.getString("sendToId");
        String sendToName = msgJson.getString("sendName");
        Integer msgType = msgJson.getInteger("msgType");

        ChatMessage msg = ChatMessageObjectPool.borrow();
        msg.setFromId(cs.getId());
        msg.setFromName(cs.getName());
        msg.setFromHeadImg(cs.getHeaderImgPath());
        msg.setSendToId(sendToId);
        msg.setSendToName(sendToName);
        msg.setContent(content);
        msg.setCreateTime(timestamp);
        if(msgType != null) {
            msg.setMsgType(msgType);
        }

        chatManager.sendMsgToUser(msg);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.setTextMessageSizeLimit(20480);
        User cs = WebSocketUtil.getUserInfo(session);
        if(!chatManager.addCS(cs, new CSWebSocketChatMessageSession(session))) {
            JSONObject json = new JSONObject();
            json.put("msgType", MessageTypeConstant.TYPE_DUPLICATE_CONNECT);
            json.put("content", "当前客服已经在线,不许重复接入!");
            session.sendMessage(new TextMessage(json.toJSONString()));
            log.debug("已阻止客服重复连接 ：id: {} name: {} ", cs.getId(), cs.getName());
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        User cs = WebSocketUtil.getUserInfo(session);
        //判断是否是重复连接
        if(!((CSWebSocketChatMessageSession)chatManager.getCSMessageSession(cs)).getSession().equals(session)) {
            return;
        }
        chatManager.removeCS(cs);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if(session.isOpen()){
            session.close();
        }
        ChatLoggerUtil.getInfoLogger().info("handleTransportError invoke.....");
        ChatLoggerUtil.getInfoLogger().error(exception.getMessage(), exception);

        chatManager.removeCS(WebSocketUtil.getUserInfo(session));
    }

    public void setChatManager(ChatManager chatManager) {
        this.chatManager = chatManager;
    }

}
