package com.pdk.chat.wx.util;

import com.pdk.chat.util.MessageTypeConstant;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.base.WeixinMessageSend;
import com.pdk.chat.wx.message.receive.*;
import com.pdk.chat.wx.message.send.ImageMessageSend;
import com.pdk.chat.wx.message.send.LocationMessageSend;
import com.pdk.chat.wx.message.send.TextMessageSend;
import com.pdk.chat.wx.message.send.VoiceMessageSend;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kangss on 2015/8/25
 */
public enum MessageTypeEnum{
    TYPE_TEXT(TextMessageReceive.class,ChatMessage.class,TextMessageSend.class, MessageTypeConstant.TYPE_TEXT, WeixinParseUtil.TYPE_TEXT),
    TYPE_IMAGE(ImageMessageReceive.class,ChatMessage.class, ImageMessageSend.class, MessageTypeConstant.TYPE_IMAGE, WeixinParseUtil.TYPE_IMAGE),
    TYPE_VOICE(VoiceMessageReceive.class,ChatMessage.class, VoiceMessageSend.class,MessageTypeConstant.TYPE_VOICE, WeixinParseUtil.TYPE_VOICE),
    TYPE_LOCATION(LocationMessageReceive.class,ChatMessage.class, LocationMessageSend.class, MessageTypeConstant.TYPE_LOCATION, WeixinParseUtil.TYPE_LOCATION),
    TYPE_EVENT(null,null,null,MessageTypeConstant.TYPE_EVENT, WeixinParseUtil.TYPE_EVENT);

    private Class<? extends WeixinMessageReceive> receiveClazz;
    private Class<? extends WeixinMessageSend> sendClazz;
    private Class<? extends ChatMessage> chatmsgClazz;
    private int chatmsgType;
    private String wxmsgType;

    private static  Map< Class<? extends WeixinMessageReceive>,MessageTypeEnum> wxMap = null;
    private static Map<Class<? extends ChatMessage>,MessageTypeEnum> chatMap = null;
    private static Map<Integer,MessageTypeEnum> chatmsgtypeMap = null;
    private static Map<String,MessageTypeEnum> wxmsgtypeMap = null;
    public Class<? extends WeixinMessageReceive> getReceiveClazz() {
        return receiveClazz;
    }

    public Class<? extends ChatMessage> getChatmsgClazz() {
        return chatmsgClazz;
    }

    public int getChatmsgType() {
        return chatmsgType;
    }

    public String getWxmsgType() {
        return wxmsgType;
    }

    public Class<? extends WeixinMessageSend> getSendClazz() {
        return sendClazz;
    }

    MessageTypeEnum(Class<? extends WeixinMessageReceive> receiveClazz, Class<? extends ChatMessage> chatmsgClazz,Class<? extends WeixinMessageSend> sendClazz, int chatmsgType,String wxmsgType){
        this.receiveClazz = receiveClazz;
        this.chatmsgClazz = chatmsgClazz;
        this.chatmsgType = chatmsgType;
        this.wxmsgType = wxmsgType;
        this.sendClazz = sendClazz;
    }

    public static MessageTypeEnum getByChatmsgType(int chatMsgType){
        if(chatmsgtypeMap == null){
            synchronized (MessageTypeEnum.class) {
                if(chatmsgtypeMap == null) {
                    chatmsgtypeMap = new HashMap<>();
                    for (MessageTypeEnum typeEnum : MessageTypeEnum.values()) {
                        chatmsgtypeMap.put(typeEnum.getChatmsgType(), typeEnum);
                    }
                }
            }
        }
        return chatmsgtypeMap.get(chatMsgType);
    }

    public static MessageTypeEnum getByWxmsgType(String wxmsgType){
        if(wxmsgtypeMap == null){
            synchronized (MessageTypeEnum.class) {
                if(wxmsgtypeMap == null) {
                    wxmsgtypeMap = new HashMap<>();
                    for (MessageTypeEnum typeEnum : MessageTypeEnum.values()) {
                        wxmsgtypeMap.put(typeEnum.getWxmsgType(), typeEnum);
                    }
                }
            }
        }
        return wxmsgtypeMap.get(wxmsgType);
    }

}
