package com.pdk.chat.websocket.cs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.util.CommonUtil;
import com.pdk.chat.util.WiXinMessageUtil;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.websocket.msg.WebSocketChatMessageAdapter;
import org.slf4j.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Created by hubo on 2015/8/25
 */
public class CSWebSocketChatMessageSession extends WebSocketChatMessageAdapter {

    private static final Logger log = ChatLoggerUtil.getChatLogger();

    public CSWebSocketChatMessageSession(WebSocketSession session) {
        super(session);
    }

    @Override
    protected void sendImageMessage(ChatMessage message) throws BusinessException {
        sendMediaMessage(message);
        log.info("{} ---- 用户[id:{}, name:{}]向客服[id:{}, name:{}]发送图片消息：{}",
                CommonUtil.formatDate(message.getCreateTime()), message.getFromId(), message.getFromName(),
                message.getSendToId(), message.getSendToName(), message.getContent());
    }

    @Override
    protected void sendVoiceMessage(ChatMessage message) throws BusinessException {
        sendMediaMessage(message);
        log.info("{} ---- 用户[id:{}, name:{}]向客服[id:{}, name:{}]发送语音消息：{}",
                CommonUtil.formatDate(message.getCreateTime()), message.getFromId(), message.getFromName(),
                message.getSendToId(), message.getSendToName(), message.getContent());
    }

    private void sendMediaMessage(ChatMessage message) throws BusinessException {
        JSONObject json = new JSONObject();
        json.put("content", message.getContent());
        json.put("fromId", message.getFromId());
        json.put("fromName", message.getFromName());
        json.put("fromHeadImg", message.getFromHeadImg());
        json.put("sendToId", message.getSendToId());
        json.put("sendToName", message.getSendToName());
        json.put("sendHeadImg", message.getSendHeadImg());
        json.put("msgType", message.getMsgType());
        json.put("createTime", CommonUtil.formatDate(message.getCreateTime()));
        sendMessage(json.toJSONString());
    }

    @Override
    protected void sendLocationMessage(ChatMessage message) throws BusinessException {

        JSONObject json = new JSONObject();
        json.put("content", message.getContent());
        json.put("fromId", message.getFromId());
        json.put("fromName", message.getFromName());
        json.put("fromHeadImg", message.getFromHeadImg());
        json.put("sendToId", message.getSendToId());
        json.put("sendToName", message.getSendToName());
        json.put("sendHeadImg", message.getSendHeadImg());
        json.put("msgType", message.getMsgType());
        json.put("createTime", CommonUtil.formatDate(message.getCreateTime()));

        sendMessage(json.toJSONString());

        log.info("{} ---- 用户[id:{}, name:{}]向客服[id:{}, name:{}]发送位置消息：{}",
                CommonUtil.formatDate(message.getCreateTime()), message.getFromId(), message.getFromName(),
                message.getSendToId(), message.getSendToName(), message.getContent());

    }

    public void sendTextMessage(ChatMessage message) throws BusinessException {

        String content = message.getContent();

        content = WiXinMessageUtil.transWx2Msg(CommonUtil.getResourcePath(), content);

        JSONObject json = new JSONObject();
        json.put("content", content);
        json.put("fromId", message.getFromId());
        json.put("fromName", message.getFromName());
        json.put("fromHeadImg", message.getFromHeadImg());
        json.put("sendToId", message.getSendToId());
        json.put("sendToName", message.getSendToName());
        json.put("sendHeadImg", message.getSendHeadImg());
        json.put("msgType", message.getMsgType());
        json.put("createTime", CommonUtil.formatDate(message.getCreateTime()));

        sendMessage(json.toJSONString());

        log.info("{} ---- 用户[id:{}, name:{}]向客服[id:{}, name:{}]发送文字消息：{}",
                CommonUtil.formatDate(message.getCreateTime()), message.getFromId(), message.getFromName(),
                message.getSendToId(), message.getSendToName(), message.getContent());

    }

    public void sendUpdateUserListMessage(ChatMessage message) throws BusinessException {

        JSONObject json = new JSONObject();
        json.put("fromId", message.getFromId());
        json.put("fromName", message.getFromName());
        json.put("fromHeadImg", message.getFromHeadImg());
        json.put("content", message.getContent());
        json.put("msgType", message.getMsgType());

        sendMessage(json.toJSONString());

        log.info("{} ---- 更新客服[id:{}, name:{}]用户列表：用户[id:{}, name:{}]",
                CommonUtil.formatDate(message.getCreateTime()), message.getSendToId(), message.getSendToName(),
                message.getFromId(), message.getFromName());
    }

    @Override
    protected void sendUserNotOnlineMessage(ChatMessage message) throws BusinessException {
        sendMessage(JSON.toJSONString(message));
        log.info("{} ---- 向客服[id:{}, name:{}]发送用户[id:{}, name:{}]下线消息",
                CommonUtil.formatDate(message.getCreateTime()),message.getSendToId(), message.getSendToName(),
                message.getFromId(), message.getFromName());
    }

    @Override
    protected void sendSentToClientErrorMessage(ChatMessage message) throws BusinessException {
        sendMessage(JSON.toJSONString(message));
        log.info("{} ---- 向客服[id:{}, name:{}]发送用户[id:{}, name:{}]信息发送失败消息：{}",
                CommonUtil.formatDate(message.getCreateTime()),message.getSendToId(), message.getSendToName(),
                message.getFromId(), message.getFromName(), message.getContent());
    }

    private void sendMessage(String msg) throws BusinessException {
        try {
            session.sendMessage(new TextMessage(msg));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage(), e);
        }
    }


}
