package com.pdk.chat.websocket.user;

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
public class UserWebSocketChatMessageSession extends WebSocketChatMessageAdapter {

    private static final Logger log = ChatLoggerUtil.getChatLogger();


    public UserWebSocketChatMessageSession(WebSocketSession session) {
        super(session);
    }

    @Override
    protected void sendTextMessage(ChatMessage message) throws BusinessException {

        String content =  message.getContent();

//        if(WiXinMessageUtil.containsFaceImage(content)) {
            content = WiXinMessageUtil.transMsg2Wx(content);
//        }

        JSONObject json = new JSONObject();
        json.put("id", message.getFromId());
        json.put("name", message.getFromName());
        json.put("content", content);
        json.put("msgType", message.getMsgType());
        json.put("createTime", CommonUtil.formatDate(message.getCreateTime()));

        try {
            session.sendMessage(new TextMessage(json.toJSONString()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(e.getMessage(), e);
        }

        log.info("CS TO User [csId : {}, userId : {}, userName : {}, msg : {}, timestamp : {}]", message.getFromId(),
                message.getSendToId(), message.getSendToName(), message.getContent(), CommonUtil.formatDate(message.getCreateTime()));
    }


}
