package com.pdk.chat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by hubo on 2015/8/28
 */
public class ChatLoggerUtil {

    public static final String CHAT_LOGGER = "com.pdk.chat.websocket.chat";

    public static final String INFO_LOGGER = "com.pdk.chat.websocket.info";

    public static final String WX_LOGGER = "com.pdk.chat.wx";

    public static final String SEND_WX_LOGGER = "com.pdk.chat.send2wx";



    public static Logger getChatLogger() {
        return LoggerFactory.getLogger(CHAT_LOGGER);
    }

    public static Logger getInfoLogger() {
        return LoggerFactory.getLogger(INFO_LOGGER);
    }

    public static Logger getWxLogger() {
        return LoggerFactory.getLogger(WX_LOGGER);
    }

    public static Logger getSendToWxLogger() {
        return LoggerFactory.getLogger(SEND_WX_LOGGER);
    }


}
