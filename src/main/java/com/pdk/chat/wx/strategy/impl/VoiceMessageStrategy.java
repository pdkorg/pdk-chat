package com.pdk.chat.wx.strategy.impl;

import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.util.ChatMessageObjectPool;
import com.pdk.chat.util.CommonUtil;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.dto.MediaInfo;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.base.WeixinMessageSend;
import com.pdk.chat.wx.message.receive.ImageMessageReceive;
import com.pdk.chat.wx.message.receive.VoiceMessageReceive;
import com.pdk.chat.wx.strategy.itf.WeixinMessageStrategy;
import com.pdk.chat.wx.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by kangss on 2015/8/24
 */
@Service
public class VoiceMessageStrategy extends BaseMessageStrategy{
    @Autowired
    MediaMessageUtil mediaMessageUtil;
    @Autowired
    WxConfigInfo wxConfigInfo;
    @Override
    public void afterSendToCS(WeixinMessageReceive message) throws WeixinBusinessException {

    }

    @Override
    public void beforeSendToCS(WeixinMessageReceive message) throws WeixinBusinessException {
        VoiceMessageReceive receive;
        if(!(message instanceof VoiceMessageReceive))
            return;
        receive =  (VoiceMessageReceive)  message;
        MediaInfo info = mediaMessageUtil.getMediaPath(receive.getMediaId(), MessageTypeEnum.TYPE_VOICE);
        receive.setMediaUrl(info.getUrlPath());
    }


    @Override
    public ChatMessage transformToCS(WeixinMessageReceive message) throws WeixinBusinessException {
        ChatMessage chatMessage;
        try {
            chatMessage = WeixinMessageUtil.transWeixinMessage2ChatMessage(message);
        } catch (Exception e) {
            throw new WeixinBusinessException("获取微信信息时出现异常",e);
        }
        if(chatMessage == null)
            return null;
        if(message instanceof VoiceMessageReceive) {
            chatMessage.setContent(((VoiceMessageReceive) message).getMediaUrl());
        }
        return chatMessage;
    }

    @Override
    public WeixinMessageSend transformToWX(ChatMessage message) throws WeixinBusinessException {
        return null;
    }

    @Override
    public void afterSendToWX(ChatMessage message) throws WeixinBusinessException {
        ChatLoggerUtil.getChatLogger().info("{} ---- 客服[id:{}, name:{}]向用户[id:{}, name:{}]发送声音消息：{}",
                CommonUtil.formatDate(message.getCreateTime()), message.getFromId(), message.getFromName(),
                message.getSendToId(), message.getSendToName(), message.getContent());
    }

    @Override
    public void beforeSendToWX(ChatMessage message) throws WeixinBusinessException {

    }
}
