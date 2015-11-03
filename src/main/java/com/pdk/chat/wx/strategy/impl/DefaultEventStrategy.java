package com.pdk.chat.wx.strategy.impl;

import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.base.WeixinMessageSend;
import com.pdk.chat.wx.message.receive.TextMessageReceive;
import com.pdk.chat.wx.session.WeixinSessionManager;
import com.pdk.chat.wx.strategy.itf.WeixinEventStrategy;
import com.pdk.chat.wx.strategy.itf.WeixinMessageStrategy;
import com.pdk.chat.wx.util.WeixinParseUtil;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by kangss on 2015/8/24
 */
@Service
public class DefaultEventStrategy implements WeixinEventStrategy{

    @Override
    public String handleEvent(WeixinMessageReceive message) throws WeixinBusinessException {
        TextMessageReceive text = new TextMessageReceive();
        text.setToUserName(message.getFromUserName());
        text.setFromUserName(message.getToUserName());
        text.setCreateTime(String.valueOf(new Date().getTime()));
        text.setMsgType(WeixinParseUtil.TYPE_TEXT);
        text.setContent("暂不支持此功能");
        return WeixinParseUtil.transMsg2Xml(text);
    }

    @Override
    public void asyncNotify(WeixinMessageReceive message, WeixinSessionManager manager) throws WeixinBusinessException {

    }

}
