package com.pdk.chat.wx.util;

import com.pdk.chat.util.ChatMessageObjectPool;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.base.WeixinMessageSend;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;


/**
 * Created by kangss on 2015/8/25
 */
public class WeixinMessageUtil {

    public static ChatMessage transWeixinMessage2ChatMessage(WeixinMessageReceive wxmsg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        MessageTypeEnum typeEnum = MessageTypeEnum.getByWxmsgType(wxmsg.getMsgType());
        if(typeEnum == null)
            return null;
//        Constructor<? extends ChatMessage> constructor = typeEnum.getChatmsgClazz().getConstructor();
        ChatMessage chatmsg = ChatMessageObjectPool.borrow();
        chatmsg.setFromId(wxmsg.getFromUserName());
        chatmsg.setCreateTime(new Date());
        chatmsg.setSendToId(wxmsg.getToUserName());
        chatmsg.setMsgType(typeEnum.getChatmsgType());
        chatmsg.setSource(ChatMessage.SOURCE_WX);
        return chatmsg;
    }

    public static WeixinMessageSend transChatMessage2WeixinMessage(ChatMessage chatmsg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        MessageTypeEnum typeEnum = MessageTypeEnum.getByChatmsgType(chatmsg.getMsgType());
        if(typeEnum == null)
            return null;
        Constructor<? extends WeixinMessageSend> constructor = typeEnum.getSendClazz().getConstructor();
        WeixinMessageSend wxmsg = constructor.newInstance();
        wxmsg.setTouser(chatmsg.getSendToId());
        wxmsg.setMsgtype(typeEnum.getWxmsgType());
        return wxmsg;
    }
}
