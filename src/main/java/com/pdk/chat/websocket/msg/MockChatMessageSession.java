package com.pdk.chat.websocket.msg;

import com.pdk.chat.exception.BusinessException;

/**
 * Created by hubo on 2015/8/26.
 */
public class MockChatMessageSession implements ChatMessageSession {
    @Override
    public void sendMessage(ChatMessage message) throws BusinessException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void close() {

    }

    @Override
    public boolean isOpen() {
        return false;
    }
}
