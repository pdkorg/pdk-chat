package com.pdk.chat.websocket.msg;

import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.util.MessageTypeConstant;
import org.slf4j.Logger;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * Created by hubo on 2015/8/25
 */
public abstract class AbstractWebSocketChatMessageSession implements ChatMessageSession{

    private static final Logger log = ChatLoggerUtil.getChatLogger();

    protected WebSocketSession session;

    public WebSocketSession getSession() {
        return session;
    }

    public AbstractWebSocketChatMessageSession(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void destroy() {
        session = null;
    }

    @Override
    public void close() {
        try {
            session.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean isOpen() {
        return session.isOpen();
    }


    @Override
    public void sendMessage(ChatMessage message) throws BusinessException {
        switch (message.getMsgType()) {
            case MessageTypeConstant.TYPE_TEXT:
                sendTextMessage(message);
                break;
            case MessageTypeConstant.TYPE_IMAGE:
                sendImageMessage(message);
                break;
            case MessageTypeConstant.TYPE_VOICE:
                sendVoiceMessage(message);
                break;
            case MessageTypeConstant.TYPE_EVENT:
                sendEventMessage(message);
                break;
            case MessageTypeConstant.TYPE_LOCATION:
                sendLocationMessage(message);
                break;
            case MessageTypeConstant.TYPE_UPDATE_USER_LIST:
                sendUpdateUserListMessage(message);
                break;
            case MessageTypeConstant.TYPE_USER_NOT_ONLINE:
                sendUserNotOnlineMessage(message);
                break;
            case MessageTypeConstant.TYPE_SEND_CLIENT_ERROR:
                sendSentToClientErrorMessage(message);
                break;
        }
    }

    protected abstract void sendEventMessage(ChatMessage message) throws BusinessException;

    protected abstract void sendLocationMessage(ChatMessage message) throws BusinessException;

    protected abstract void sendUpdateUserListMessage(ChatMessage message) throws BusinessException;

    protected abstract void sendVoiceMessage(ChatMessage message) throws BusinessException;

    protected abstract void sendImageMessage(ChatMessage message) throws BusinessException;

    protected abstract void sendTextMessage(ChatMessage message) throws BusinessException;

    protected abstract void sendUserNotOnlineMessage(ChatMessage message) throws BusinessException;

    protected abstract void sendSentToClientErrorMessage(ChatMessage message) throws BusinessException;
    
}
