package com.pdk.chat.websocket.msg;

import com.pdk.chat.exception.BusinessException;

/**
 * Created by hubo on 2015/8/25
 */
public interface ChatMessageSession {

    void sendMessage(ChatMessage message) throws BusinessException;

    void destroy();

    void close();

    boolean isOpen();

}
