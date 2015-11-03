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
import com.pdk.chat.wx.message.send.ImageMessageSend;
import com.pdk.chat.wx.strategy.itf.WeixinMessageStrategy;
import com.pdk.chat.wx.util.MediaMessageUtil;
import com.pdk.chat.wx.util.MessageTypeEnum;
import com.pdk.chat.wx.util.WeixinMessageUtil;
import com.pdk.chat.wx.util.WxConfigInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by kangss on 2015/8/24
 */
@Service
public class ImageMessageStrategy extends BaseMessageStrategy {
    @Autowired
    MediaMessageUtil mediaMessageUtil;
    @Autowired
    WxConfigInfo wxConfigInfo;
    @Override
    public void afterSendToCS(WeixinMessageReceive message) throws WeixinBusinessException {

    }

    @Override
    public void beforeSendToCS(WeixinMessageReceive message) throws WeixinBusinessException {
        ImageMessageReceive receive;
        if(!(message instanceof ImageMessageReceive))
            return;
        receive =  (ImageMessageReceive)  message;
        MediaInfo info = mediaMessageUtil.getMediaPath(receive.getMediaId(), MessageTypeEnum.TYPE_IMAGE);
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
        if(message instanceof ImageMessageReceive) {
            chatMessage.setContent(((ImageMessageReceive) message).getMediaUrl());
        }
        return chatMessage;
    }

    @Override
    public WeixinMessageSend transformToWX(ChatMessage message) throws WeixinBusinessException {

        WeixinMessageSend wxmsg;
        try {
            wxmsg = WeixinMessageUtil.transChatMessage2WeixinMessage(message);
        } catch (Exception e) {
            throw new WeixinBusinessException("获取微信信息时出现异常",e);
        }
        if(wxmsg == null)
            return null;
        if(wxmsg instanceof ImageMessageSend) {
            ((ImageMessageSend)wxmsg).setMedia_id(message.getAttribute("media_id"));
        }
        return wxmsg;
    }

    @Override
    public void afterSendToWX(ChatMessage message) throws WeixinBusinessException {
        ChatLoggerUtil.getChatLogger().info("{} ---- 客服[id:{}, name:{}]向用户[id:{}, name:{}]发送图片消息：{}",
                CommonUtil.formatDate(message.getCreateTime()), message.getFromId(), message.getFromName(),
                message.getSendToId(), message.getSendToName(), message.getContent());
    }

    @Override
    public void beforeSendToWX(ChatMessage message) throws WeixinBusinessException {
        String media_id = mediaMessageUtil.uploadMedia(message.getContent(),MessageTypeEnum.TYPE_IMAGE);
        message.addAttribute("media_id",media_id);
    }
}
