package com.pdk.chat.wx.strategy.fatory;


import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.receive.EventMessageReceive;
import com.pdk.chat.wx.strategy.impl.*;
import com.pdk.chat.wx.strategy.itf.WeixinMessageStrategy;
import com.pdk.chat.wx.util.EventMessageTypeEnum;
import com.pdk.chat.wx.util.MessageTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kangss on 2015/8/24
 */
@Component
public class WeixinMessageStrategyFactory {
    @Autowired
    private TextMessageStrategy textMessageStrategy;
    @Autowired
    private ImageMessageStrategy imageMessageStrategy;
    @Autowired
    private VoiceMessageStrategy voiceMessageStrategy;
    @Autowired
    private DefaultMessageStrategy baseMessageStrategy;

    public WeixinMessageStrategy create(WeixinMessageReceive message) {
        return create(MessageTypeEnum.getByWxmsgType(message.getMsgType()));
    }

    public WeixinMessageStrategy create(ChatMessage message) {
        return create( MessageTypeEnum.getByChatmsgType(message.getMsgType()));
    }

    private WeixinMessageStrategy create(MessageTypeEnum typeEnum){
        switch (typeEnum) {
            case TYPE_TEXT:
                return textMessageStrategy;
            case TYPE_VOICE:
                return voiceMessageStrategy;
            case TYPE_IMAGE:
                return imageMessageStrategy;
            case TYPE_LOCATION:
            default:
                return baseMessageStrategy;
        }
    }
}
