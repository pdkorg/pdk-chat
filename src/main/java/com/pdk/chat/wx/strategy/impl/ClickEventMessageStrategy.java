package com.pdk.chat.wx.strategy.impl;

import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.receive.ClickMenuEventMessageReceive;
import com.pdk.chat.wx.message.receive.TextMessageReceive;
import com.pdk.chat.wx.session.WeixinSessionManager;
import com.pdk.chat.wx.strategy.itf.WeixinEventStrategy;
import com.pdk.chat.wx.util.WeiXinBooks;
import com.pdk.chat.wx.util.WeixinParseUtil;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by kangss on 2015/9/7
 */
@Service
public class ClickEventMessageStrategy implements WeixinEventStrategy {

    @Override
    public String handleEvent(WeixinMessageReceive message) throws WeixinBusinessException {
        String EventKey = ((ClickMenuEventMessageReceive)message).getEventKey();
        TextMessageReceive text = new TextMessageReceive();
        text.setToUserName(message.getFromUserName());
        text.setFromUserName(message.getToUserName());
        text.setMsgType(WeixinParseUtil.TYPE_TEXT);
        text.setCreateTime(String.valueOf(new Date().getTime()));
        switch(EventKey){
            //服务范围
            case "FWFW":
                text.setContent(WeiXinBooks.getFWFW());
                break;
            //投诉建议
            case "TSJY":
                text.setContent(WeiXinBooks.getTSJY());
                break;
            //怎么下单
            case "UORDER":
                text.setContent(WeiXinBooks.getUOrder());
                break;
            //分享有礼
            case "FXYL":
                text.setContent(WeiXinBooks.getFXYL());
                break;
            default:
                return "";
        }
        return WeixinParseUtil.transMsg2Xml(text);
    }

    @Override
    public void asyncNotify(WeixinMessageReceive message, WeixinSessionManager manager) throws WeixinBusinessException {

    }
}
