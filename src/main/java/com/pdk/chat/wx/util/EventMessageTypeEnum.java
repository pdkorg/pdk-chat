package com.pdk.chat.wx.util;

import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.receive.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kangss on 2015/8/25
 */
public enum EventMessageTypeEnum {
    SUBSCRIBE_EVENT(SubscribeEventMessageReceive.class,ChatMessage.class,"subscribe"),
    UNSUBSCRIBE_EVENT(SubscribeEventMessageReceive.class,ChatMessage.class,"unsubscribe"),
    SCAN_EVENT(SubscribeEventMessageReceive.class,ChatMessage.class,"SCAN"),
    VIEW_EVENT(ViewMenuEventMessageReceive.class,ChatMessage.class,"VIEW"),
    LOCATION_EVENT(LocationEventMessageReceive.class,ChatMessage.class,"LOCATION"),
    CLICK_EVENT(ClickMenuEventMessageReceive.class,ChatMessage.class,"CLICK");
    private Class<? extends WeixinMessageReceive> receiveClazz;
    private Class<? extends ChatMessage> chatmsgClazz;
    private String event;
    private static Map<String,EventMessageTypeEnum> eventMap = null;

    EventMessageTypeEnum(Class<? extends WeixinMessageReceive> receiveClazz, Class<? extends ChatMessage> chatmsgClazz, String event){
        this.receiveClazz = receiveClazz;
        this.chatmsgClazz = chatmsgClazz;
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public Class<? extends WeixinMessageReceive> getReceiveClazz() {
        return receiveClazz;
    }

    public Class<? extends ChatMessage> getChatmsgClazz() {
        return chatmsgClazz;
    }

    public static EventMessageTypeEnum getByEvent(String event){
        if(eventMap == null){
            synchronized (EventMessageTypeEnum.class) {
                if(eventMap == null) {
                    eventMap = new HashMap<>();
                    for (EventMessageTypeEnum typeEnum : EventMessageTypeEnum.values()) {
                        eventMap.put(typeEnum.getEvent(), typeEnum);
                    }
                }
            }
        }
        return eventMap.get(event);
    }
}
