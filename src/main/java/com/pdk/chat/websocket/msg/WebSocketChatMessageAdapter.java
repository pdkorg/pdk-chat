package com.pdk.chat.websocket.msg;

import com.pdk.chat.exception.BusinessException;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by hubo on 2015/8/29
 */
public class WebSocketChatMessageAdapter extends AbstractWebSocketChatMessageSession {


    public WebSocketChatMessageAdapter(WebSocketSession session) {
        super(session);
    }

    @Override
    protected void sendEventMessage(ChatMessage message) throws BusinessException {

    }

    @Override
    protected void sendLocationMessage(ChatMessage message) throws BusinessException {

    }

    @Override
    protected void sendUpdateUserListMessage(ChatMessage message) throws BusinessException {

    }

    @Override
    protected void sendVoiceMessage(ChatMessage message) throws BusinessException {

    }

    @Override
    protected void sendImageMessage(ChatMessage message) throws BusinessException {

    }

    @Override
    protected void sendTextMessage(ChatMessage message) throws BusinessException {

    }

    @Override
    protected void sendUserNotOnlineMessage(ChatMessage message) throws BusinessException {

    }

    @Override
    protected void sendSentToClientErrorMessage(ChatMessage message) throws BusinessException {

    }

}
