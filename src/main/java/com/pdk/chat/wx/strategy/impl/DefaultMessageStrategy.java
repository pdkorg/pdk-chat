package com.pdk.chat.wx.strategy.impl;

import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.base.WeixinMessageSend;
import com.pdk.chat.wx.message.receive.ClickMenuEventMessageReceive;
import com.pdk.chat.wx.message.receive.TextMessageReceive;
import com.pdk.chat.wx.strategy.itf.WeixinMessageStrategy;
import com.pdk.chat.wx.util.WeixinParseUtil;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by kangss on 2015/8/24
 */
@Service
public class DefaultMessageStrategy extends BaseMessageStrategy{


    @Override
    public void afterSendToCS(WeixinMessageReceive message) throws WeixinBusinessException {

    }

    @Override
    public void beforeSendToCS(WeixinMessageReceive message) throws WeixinBusinessException {

    }

    @Override
    public String getResponse(WeixinMessageReceive message) throws WeixinBusinessException {
        TextMessageReceive text = new TextMessageReceive();
        text.setToUserName(message.getFromUserName());
        text.setFromUserName(message.getToUserName());
        text.setCreateTime(String.valueOf(new Date().getTime()));
        text.setMsgType(WeixinParseUtil.TYPE_TEXT);
        text.setContent("暂不支持此功能");
        return WeixinParseUtil.transMsg2Xml(text);
    }

    @Override
    public ChatMessage transformToCS(WeixinMessageReceive message) throws WeixinBusinessException {
        return null;
    }

    @Override
    public WeixinMessageSend transformToWX(ChatMessage message) throws WeixinBusinessException {
        return null;
    }

    @Override
    public void afterSendToWX(ChatMessage message) throws WeixinBusinessException {

    }

    @Override
    public void beforeSendToWX(ChatMessage message) throws WeixinBusinessException {

    }
}
