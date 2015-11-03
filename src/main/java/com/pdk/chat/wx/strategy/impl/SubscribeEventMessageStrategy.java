package com.pdk.chat.wx.strategy.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pdk.chat.exception.BusinessException;
import com.pdk.chat.exception.WeixinBusinessException;
import com.pdk.chat.util.ChatLoggerUtil;
import com.pdk.chat.websocket.msg.ChatMessage;
import com.pdk.chat.wx.dto.WeixinFans;
import com.pdk.chat.wx.message.base.WeixinMessageReceive;
import com.pdk.chat.wx.message.base.WeixinMessageSend;
import com.pdk.chat.wx.message.receive.SubscribeEventMessageReceive;
import com.pdk.chat.wx.message.receive.TextMessageReceive;
import com.pdk.chat.wx.service.FansService;
import com.pdk.chat.wx.session.WeixinSessionManager;
import com.pdk.chat.wx.strategy.itf.WeixinEventStrategy;
import com.pdk.chat.wx.strategy.itf.WeixinMessageStrategy;
import com.pdk.chat.wx.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by kangss on 2015/8/29
 */
@Component
public class SubscribeEventMessageStrategy implements WeixinEventStrategy {
    @Autowired
    private FansService fansService;
    @Autowired
    private WeixinUrlInfo weixinUrlInfo;

    @Autowired
    private GetAccessToken getAccessToken;

    private String subscribe(SubscribeEventMessageReceive eventMessage) throws WeixinBusinessException {
        String openid = eventMessage.getFromUserName();
        String returnJson = getWxFansInfo(openid, 0);
        WeixinFans fans = JSONObject.parseObject(returnJson, WeixinFans.class);
        fans.setOpenid(openid);
        int count = fansService.countByOpenid(openid);
        fansService.subscribe(fans, eventMessage.getEventKey());
        TextMessageReceive text = new TextMessageReceive();
        text.setToUserName(eventMessage.getFromUserName());
        text.setFromUserName(eventMessage.getToUserName());
        text.setMsgType(WeixinParseUtil.TYPE_TEXT);
        text.setCreateTime(String.valueOf(new Date().getTime()));
        if( count == 0 )
            text.setContent(WeiXinBooks.getFocus());
        else
            text.setContent("主人，欢迎回来，小跑君等候您多时了……");
        return WeixinParseUtil.transMsg2Xml(text);
    }

    private String getWxFansInfo(String openid, int i){
        try {
            String token = getAccessToken.getAccessToken().getAccess_token();
            String returnJson = HttpSend.send(weixinUrlInfo.getInfo(token, openid));
            JSONObject jobj = JSONObject.parseObject(returnJson);
            Integer errorCode = jobj.getInteger("errcode");
            if(errorCode != null && errorCode.intValue() == 40001){
                getAccessToken.resetAccessToken(token);
                if(i < 2){
                    return getWxFansInfo(openid,i + 1);
                }
            }else if(errorCode != null && errorCode.intValue() != 0){
                ChatLoggerUtil.getInfoLogger().error("关注用户获取用户信息时异常！{}:{}",errorCode.intValue(),jobj.get("errmsg"));
            }
            return returnJson;
        } catch (WeixinBusinessException e) {
            ChatLoggerUtil.getInfoLogger().error("关注用户时异常",e);
            return "";
        }
    }

    private String unsubscribe(SubscribeEventMessageReceive eventMessage) throws WeixinBusinessException {
        fansService.unsubscribe(eventMessage.getFromUserName());
        return "";
    }

    @Override
    public String handleEvent(WeixinMessageReceive message) throws WeixinBusinessException {
        SubscribeEventMessageReceive eventMessage= (SubscribeEventMessageReceive)message;
        String response;
        switch (eventMessage.getEvent()){
            case "subscribe" :
                response = subscribe(eventMessage);
                break;
            case "unsubscribe" :
                response =  unsubscribe(eventMessage);
                break;
            case "SCAN":
            default:
                response = "";
        }
        return response;
    }

    @Override
    public void asyncNotify(WeixinMessageReceive message, final WeixinSessionManager manager) throws BusinessException {

        SubscribeEventMessageReceive eventMessage= (SubscribeEventMessageReceive)message;

        switch (eventMessage.getEvent()) {
            case "subscribe":
//                manager.createWeixinMessageSession(message.getFromUserName());
                break;
            case "unsubscribe":
                manager.removeWeixinMessageSession(message.getFromUserName());
                break;
            case "SCAN":
            default:
                break;
        }

    }
}
