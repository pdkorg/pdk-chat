package com.pdk.chat.wx.strategy.impl;

import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.base.WeixinMessageSend;
import com.pdk.chat.wx.message.receive.TextMessageReceive;
import com.pdk.chat.wx.strategy.itf.WeixinMessageStrategy;
import com.pdk.chat.wx.util.WeiXinBooks;
import com.pdk.chat.wx.util.WeixinParseUtil;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by kangss on 2015/8/24
 */
@Service
public abstract class BaseMessageStrategy implements WeixinMessageStrategy{

    @Override
    public String getResponse(WeixinMessageReceive message) throws WeixinBusinessException {
        int hours =  Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        // 22点以后，9点之前，来信息自动回复
        if (hours >= 22 || hours < 9) {
            TextMessageReceive text = new TextMessageReceive();
            text.setToUserName(message.getFromUserName());
            text.setFromUserName(message.getToUserName());
            text.setMsgType(WeixinParseUtil.TYPE_TEXT);
            text.setContent(WeiXinBooks.getRest());
            text.setCreateTime(String.valueOf(new Date().getTime()));
            return WeixinParseUtil.transMsg2Xml(text);
        }else{
            return null;
        }
    }
}
